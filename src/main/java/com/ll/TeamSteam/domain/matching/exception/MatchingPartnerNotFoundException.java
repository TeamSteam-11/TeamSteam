package com.ll.TeamSteam.domain.matching.exception;

public class MatchingPartnerNotFoundException extends IllegalArgumentException {
    public MatchingPartnerNotFoundException(String message) {
        super(message);
    }
}
