package com.lightweightapp.emailservice.model;

public class EmailMessageModel {

    public static String emailMessage(String receipientName)
    {
        String message = "Welcome to message queue " + receipientName;
        return message;
    }
}
