package com.ll.TeamSteam.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.NoSuchElementException;

@Controller
@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(value = IllegalArgumentException.class)
    public String IllegalArgumentException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "common/error";
    }

    @ExceptionHandler(value = NoSuchElementException.class)
    public String NoSuchElementException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "common/noChatroomError";
    }

    @ExceptionHandler(value = HttpRequestMethodNotSupportedException.class)
    public String HttpRequestMethodNotSupportedException(Exception e, Model model){
        model.addAttribute("errorMessage",e.getMessage());
        log.info("e.getMessage = {} ", e.getMessage());
        return "common/error";
    }
}