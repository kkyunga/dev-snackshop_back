package org.back.devsnackshop_back.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jcraft.jsch.*;
import lombok.extern.slf4j.Slf4j;
import org.back.devsnackshop_back.dto.SshSession;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.InputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class SshWebSocketHandler extends TextWebSocketHandler
{
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final Map<String,SshSession> sessions = new ConcurrentHashMap<>();


    @Override
    public void afterConnectionEstablished(WebSocketSession session){
        log.info("웹소켓 연결됨"+session.getId());
    }



    private void handleConnect(WebSocketSession session, JsonNode json) throws JSchException {
        try{
        String host = json.get("host").asText();
        int port = json.get("port").asInt();
        String username = json.get("username").asText();
        String password = json.get("password").asText();


        JSch jSch = new JSch();
        Session sshSession = jSch.getSession(username,host,port);
        sshSession.setPassword(password);

        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking","no");
        sshSession.setConfig(config);


        sshSession.setConfig(config);
        sshSession.connect(30000);


        Channel channel = sshSession.openChannel("shell");
        ((ChannelShell)channel).setPtyType("xterm");

        PipedInputStream pis = new PipedInputStream();
        PipedOutputStream pos = new PipedOutputStream(pis);
        channel.setInputStream(pis);

        InputStream inputStream = channel.getInputStream();
        channel.connect(3000);

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
    }catch (Exception e){
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
        String type = json.get("type").asText();

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
