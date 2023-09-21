package com.ll.TeamSteam.domain.chatMessage.error;

public class ChatMessageOverContent extends RuntimeException{

    public ChatMessageOverContent(String message) {
        super(message);
    }
}
