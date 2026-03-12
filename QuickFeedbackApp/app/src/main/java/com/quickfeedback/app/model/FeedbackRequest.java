package com.quickfeedback.app.model;

public class FeedbackRequest {

    private String name;
    private String email;
    private String message;

    public FeedbackRequest(String name, String email, String message) {
        this.name = name;
        this.email = email;
        this.message = message;
    }

    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getMessage() { return message; }
}
