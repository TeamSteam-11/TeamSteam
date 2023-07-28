package com.ll.TeamSteam.domain.chatRoom.exception;

public class NotInChatRoomException extends RuntimeException{
    public NotInChatRoomException(String message) {
        super(message);
    }
}