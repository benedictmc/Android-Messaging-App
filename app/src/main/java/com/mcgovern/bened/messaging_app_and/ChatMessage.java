package com.mcgovern.bened.messaging_app_and;

import android.widget.TextView;

import java.util.Date;

/**
 * Created by bened on 26/08/2017.
 */

public class ChatMessage {
    //Declartion of chat components
    private String Message;
    private String Sender;
    private long Time;
    private String UID;

    public ChatMessage(String Message, String Sender, String UID){
        this.Message = Message;
        this.Sender = Sender;
        this.UID = UID;

        Time = new Date().getTime();

    }
    public ChatMessage(){

    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String Message) {
        this.Message = Message;
    }

    public String getSender() {
        return Sender;
    }

    public void setSender(String Sender) {
        this.Sender = Sender;
    }

    public long getTime() {
        return Time;
    }

    public void setTime(long Time) {
        this.Time = Time;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

}

