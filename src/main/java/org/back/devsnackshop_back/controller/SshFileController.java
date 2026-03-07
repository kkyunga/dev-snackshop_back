package org.back.devsnackshop_back.controller;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.RequiredArgsConstructor;
import org.back.devsnackshop_back.entity.AttachmentEntity;
import org.back.devsnackshop_back.entity.UserOsInstanceEntity;
import org.back.devsnackshop_back.repository.AttachmentRepository;
import org.back.devsnackshop_back.repository.UserOsInstanceRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.InputStream;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ssh/files")
public class SshFileController {

    private final UserOsInstanceRepository userOsInstanceRepository;
    private final AttachmentRepository attachmentRepository;

    @GetMapping("/{serverId}")
    public ResponseEntity<List<Map<String, String>>> listFiles(
            @PathVariable Long serverId,
            @RequestParam(defaultValue = "/") String path) {

        UserOsInstanceEntity server = userOsInstanceRepository.findById(serverId)
                .orElseThrow(() -> new RuntimeException("서버를 찾을 수 없습니다."));

        JSch jsch = new JSch();
        Session session = null;
        ChannelSftp channel = null;

        try {
            session = jsch.getSession(server.getUsername(), server.getIpAddress(), Integer.parseInt(String.valueOf(server.getPortNumber())));
            session.setConfig("StrictHostKeyChecking", "no");

            if (server.getPassword().isEmpty() && server.getAttachmentId() != null) {
                AttachmentEntity attachment = attachmentRepository.findById(server.getAttachmentId())
                        .orElseThrow();
                String keyPath = attachment.getFilePath() + File.separator + attachment.getStoredFileName();
                jsch.addIdentity(keyPath);
            } else {
                session.setPassword(server.getPassword());
            }

            session.connect(10000);
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect(5000);

            Vector<ChannelSftp.LsEntry> entries = channel.ls(path);
            List<Map<String, String>> result = new ArrayList<>();

            for (ChannelSftp.LsEntry entry : entries) {
                String name = entry.getFilename();
                if (name.equals(".") || name.equals("..")) continue;

                Map<String, String> item = new HashMap<>();
                item.put("name", name);
                item.put("path", path.endsWith("/") ? path + name : path + "/" + name);
                item.put("type", entry.getAttrs().isDir() ? "directory" : "file");
                result.add(item);
            }

            result.sort(Comparator.comparing(m -> m.get("type"), Comparator.reverseOrder()));
            return ResponseEntity.ok(result);

        } catch (Exception e) {
            return ResponseEntity.status(500).build();
        } finally {
            if (channel != null) channel.disconnect();
            if (session != null) session.disconnect();
        }
    }
}
