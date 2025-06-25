package com.nathaniel.bookbackend.library.events;

import com.nathaniel.bookbackend.common.events.UserActivityEvent;
import lombok.Getter;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

@Component
public class TestKafkaListener {

    @Getter
    private final List<UserActivityEvent> receivedEvents = new ArrayList<>();
    private CountDownLatch latch = new CountDownLatch(1);

    @KafkaListener(topics = "user-activity", groupId = "test-group")
    public void receive(UserActivityEvent event) {
        receivedEvents.add(event);
        latch.countDown();
    }

    public void resetLatch(int count) {
        latch = new CountDownLatch(count);
    }

    public CountDownLatch getLatch() {
        return latch;
    }

    public void clear() {
        receivedEvents.clear();
        resetLatch(1);
    }
}
