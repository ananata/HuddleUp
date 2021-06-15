package com.example.huddleup.auth.and.database;

import android.os.Parcelable;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message implements Serializable {
    private String subject, message, toUserUid;
    private long dateTimeSent;
    private String dateSent;

    // TODO: Add teamName parameter (if null, sender will automatically be HuddleUp)
    // TODO: Read about

    // Required empty constructor
    public Message() { }

    /**
     * Parameterized constructor
     * Pass "" (empty string) to fromUser if message is sent from app
     * @param subject String
     * @param message String
     * @param toUser String
     */
    public Message(String subject, String message, String toUser) {
        this.subject = subject;
        this.message = message;
        this.toUserUid = toUser;
        // Automatically sets the date and time object was created
        this.dateTimeSent = new Date().getTime();
        this.dateSent = new SimpleDateFormat("MMMM dd, yyyy", Locale.US).format(new Date()) + "";
    }

    /**
     * Parameterized constructor
     * Pass "" (empty string) to fromUser if message is sent from app
     * @param subject String
     * @param message String
     */
    public Message(String subject, String message, String dateSent, long timeSent) {
        this.subject = subject;
        this.message = message;
        this.dateSent = dateSent;
        this.dateTimeSent = timeSent;
    }


    /*====================
     * Add getters and setters
     *====================*/

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getToUserUid() {
        return toUserUid;
    }

    public void setToUserUid(String toUserUid) {
        this.toUserUid = toUserUid;
    }

    public String getDateSent() {
        return dateSent;
    }

    public void setDateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public long getDateTimeSent() { return dateTimeSent; }

    public void setDateTimeSent(long timeSent) {this.dateTimeSent = timeSent; }

}
