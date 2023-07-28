package com.ll.TeamSteam.domain.chatRoom.exception;

public class CanNotEnterException extends RuntimeException{
    public CanNotEnterException(String message) {
        super(message);
    }
}