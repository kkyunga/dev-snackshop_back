package org.back.devsnackshop_back.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.SshSession;
import org.back.devsnackshop_back.entity.AttachmentEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.AttachmentRepository;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.File;
import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
@RequiredArgsConstructor
public class SshWebSocketHandler extends TextWebSocketHandler
{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String,SshSession> sessions = new ConcurrentHashMap<>();
    private final Map<String, Long> sessionServerIds = new ConcurrentHashMap<>(); // websocketSessionId → serverId
    private final UserOsInstanceRepository userOsInstanceRepository;
    private final AttachmentRepository attachmentRepository;




    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("웹소켓 연결됨"+session.getId());
        String query = session.getUri().getQuery();
        if(query!=null)
        {
            for(String param : query.split("&"))
            {
                String[] params = param.split("=");
                if(params.length==2 && "serverId".equals(params[0])){
                    sessionServerIds.put(session.getId(),Long.parseLong(params[1]));
                    break;
                }
            }
        }
    }



    private void handleConnect(WebSocketSession session, JsonNode json) throws JSchException {
        try {
            Long serverId = sessionServerIds.get(session.getId());
            if (serverId == null) {
                sendMessage(session, Map.of("type", "error", "message", "serverId 없음"));
                return;
            }

            UserOsInstanceEntity userOsInstanceEntity = userOsInstanceRepository.findById(serverId)
                    .orElseThrow(() -> new RuntimeException("서버없음" + serverId));

            log.info("서버 조회 완료: host={}, port={}, username={}, attachmentId={}",
                    userOsInstanceEntity.getIpAddress(),
                    userOsInstanceEntity.getPortNumber(),
                    userOsInstanceEntity.getUsername(),
                    userOsInstanceEntity.getAttachmentId());


            String host = userOsInstanceEntity.getIpAddress();
            int port = Integer.parseInt("" + userOsInstanceEntity.getPortNumber());
            String username = userOsInstanceEntity.getUsername();
            String password = userOsInstanceEntity.getPassword();
            Optional<AttachmentEntity> attachmentEntityOptoinal = Optional.empty();
            if (userOsInstanceEntity.getAttachmentId() != null) {
                attachmentEntityOptoinal = attachmentRepository.findById(userOsInstanceEntity.getAttachmentId());
            }
            boolean isAttachPasswordFile = attachmentEntityOptoinal.isPresent();
            String authType = "password";
            if (isAttachPasswordFile && (password == null || password.isEmpty())) {
                authType = "pemFile";
            }
            log.info("authType={}", authType);

            JSch jSch = new JSch();
            Session sshSession = jSch.getSession(username, host, port);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            sshSession.setConfig(config);

            if ("pemFile".equals(authType)) {
                AttachmentEntity attachment = attachmentEntityOptoinal.get();
                String keyPath = attachment.getFilePath() + File.separator + attachment.getStoredFileName();
                jSch.addIdentity(keyPath);
            } else {
                sshSession.setPassword(password);
            }

            sshSession.connect(30000);

            Channel channel = sshSession.openChannel("shell");
            ((ChannelShell) channel).setPtyType("xterm");

            PipedInputStream pis = new PipedInputStream();
            PipedOutputStream pos = new PipedOutputStream(pis);
            channel.setInputStream(pis);
            channel.connect(10000);

            InputStream inputStream = channel.getInputStream();

            SshSession sshSessionObj = new SshSession(sshSession, channel, pos, inputStream);
            sessions.put(session.getId(), sshSessionObj);

            sendMessage(session, Map.of("type", "connected"));

            new Thread(() -> {
                try {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        String output = new String(buffer, 0, len, "UTF-8");
                        sendMessage(session, Map.of("type", "data", "data", output));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            sendMessage(session, Map.of("type", "error", "message", e.getMessage()));
        }
    }
    private void handleInput(WebSocketSession session, String input) {
        SshSession sshSession = sessions.get(session.getId());
        if (sshSession != null) {
            try {
                sshSession.outputStream.write(input.getBytes());
                sshSession.outputStream.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    private void sendMessage(WebSocketSession session, Map<String, Object> data) {
        try {
            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(data)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws JsonProcessingException {
        JsonNode json = objectMapper.readTree(message.getPayload());
        JsonNode typeNode = json.get("type");
        if (typeNode == null) return;

        String type = typeNode.asText();

        switch (type) {
            case "connect":
                try {
                    handleConnect(session, json);
                } catch (JSchException e) {
                    e.printStackTrace();
                    sendMessage(session, Map.of("type", "error", "message", e.getMessage()));
                }
                break;
            case "input":
                handleInput(session, json.get("data").asText());
                break;
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        SshSession sshSession = sessions.remove(session.getId());
        if (sshSession != null) {
            sshSession.close();
        }
        System.out.println("WebSocket 연결 종료: " + session.getId());
    }

}
