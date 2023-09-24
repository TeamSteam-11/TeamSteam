package com.ll.TeamSteam.global.error;

import com.ll.TeamSteam.domain.chatMessage.error.ChatMessageOverContent;
import com.ll.TeamSteam.domain.chatRoom.exception.CanNotEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.KickedUserEnterException;
import com.ll.TeamSteam.domain.chatRoom.exception.NoChatRoomException;
import com.ll.TeamSteam.domain.chatRoom.exception.NotInChatRoomException;
import com.ll.TeamSteam.domain.dm.exception.NoDmException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import java.util.NoSuchElementException;

@Controller
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public String illegalArgumentException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public String noSuchElementException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public String httpRequestMethodNotSupportedException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }

    @ExceptionHandler(value = HttpClientErrorException.BadRequest.class)
    public String httpClientErrorExceptionBadRequest(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }

    // 커스텀
    @ExceptionHandler(value = KickedUserEnterException.class)
    public String kickedUserEnterException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/kickedError";
    }

    @ExceptionHandler(value = NullPointerException.class)
    public String nullPointException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }


    // 커스텀
    @ExceptionHandler(value = NoChatRoomException.class)
    public String noChatRoomException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/noChatroomError";
    }

    // 커스텀
    @ExceptionHandler(value = CanNotEnterException.class)
    public String canNotEnterException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/canNotEnterChatRoom";
    }

    @ExceptionHandler(value = NotInChatRoomException.class)
    public String notInChatRoomException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/notInChatRoom";
    }

    @ExceptionHandler(value = NoDmException.class)
    public String noDmException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/noChatroomError";
    }

    @ExceptionHandler(value = ChatMessageOverContent.class)
    public String chatMessageOverContent(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "error/commonError";
    }

}