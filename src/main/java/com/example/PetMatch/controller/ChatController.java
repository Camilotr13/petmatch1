package com.example.PetMatch.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.messaging.handler.annotation.MessageExceptionHandler;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import com.example.PetMatch.model.dto.ChatMessageDto;

@Controller
public class ChatController {

    private static final int MAX_SENDER_LENGTH = 50;
    private static final int MAX_RECEIVER_LENGTH = 50;
    private static final int MAX_CONTENT_LENGTH = 1000;
    private static final DateTimeFormatter ISO_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

    @MessageMapping("/send")
    @SendTo("/topic/messages")
    public ChatMessageDto sendMessage(ChatMessageDto message) {
        validateMessage(message);

        if (message.getTimestamp() == null || message.getTimestamp().isBlank()) {
            message.setTimestamp(LocalDateTime.now().format(ISO_FORMATTER));
        }

        return message;
    }

    @MessageExceptionHandler
    @SendToUser("/queue/errors")
    public String handleException(IllegalArgumentException ex) {
        return ex.getMessage();
    }

    private void validateMessage(ChatMessageDto message) {
        if (message.getSender() == null || message.getSender().isBlank()) {
            throw new IllegalArgumentException("El campo 'sender' es obligatorio");
        }
        if (message.getSender().length() > MAX_SENDER_LENGTH) {
            throw new IllegalArgumentException(
                    "El campo 'sender' no puede exceder " + MAX_SENDER_LENGTH + " caracteres");
        }
        if (message.getReceiver() == null || message.getReceiver().isBlank()) {
            throw new IllegalArgumentException("El campo 'receiver' es obligatorio");
        }
        if (message.getReceiver().length() > MAX_RECEIVER_LENGTH) {
            throw new IllegalArgumentException(
                    "El campo 'receiver' no puede exceder " + MAX_RECEIVER_LENGTH + " caracteres");
        }
        if (message.getContent() == null || message.getContent().isBlank()) {
            throw new IllegalArgumentException("El campo 'content' es obligatorio");
        }
        if (message.getContent().length() > MAX_CONTENT_LENGTH) {
            throw new IllegalArgumentException(
                    "El campo 'content' no puede exceder " + MAX_CONTENT_LENGTH + " caracteres");
        }
    }
}
