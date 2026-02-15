package org.back.devsnackshop_back.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/kafka")
public class ChatController {
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/test")
    public String testSend() {
        String testMessage = "테스트 데이터" + System.currentTimeMillis();
        messagingTemplate.convertAndSend("/topic/monitor", testMessage);
        return "메시지 발송 성공: " + testMessage;
    }
}
