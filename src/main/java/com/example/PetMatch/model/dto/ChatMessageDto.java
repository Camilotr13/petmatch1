package com.example.PetMatch.model.dto;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatMessageDto implements Serializable {

    private String sender;      // max 50 chars
    private String receiver;    // max 50 chars
    private String content;     // max 1000 chars
    private String timestamp;   // ISO 8601: yyyy-MM-dd'T'HH:mm:ss
}
