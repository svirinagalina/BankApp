package ru.katacademy.notification.test;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DockerPingTest {

    @Test
    void dockerShouldBeAccessible() {
        try (GenericContainer<?> container = new GenericContainer<>("alpine:3.18")
                .withCommand("sleep", "1")) {
            container.start();
            assertTrue(container.isRunning());
        }
    }
}
