package com.github.sinalarimi81.consumerservice.common;

public enum RequestStatus {
    PENDING("pending"),
    FAILURE("failure"),
    READY("ready"),
    DONE("done");

    private final String literal;

    RequestStatus(String literal) {
        this.literal = literal;
    }

    public String getLiteral() {
        return literal;
    }
}
