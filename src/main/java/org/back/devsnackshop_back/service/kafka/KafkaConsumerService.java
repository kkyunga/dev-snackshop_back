package org.back.devsnackshop_back.service.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
    private final SimpMessagingTemplate messagingTemplate;

    @KafkaListener(topics = "db-changes", groupId = "monitor-group")
    public void listen(String message) {
        messagingTemplate.convertAndSend("/topic/monitor", message);
    }
}
