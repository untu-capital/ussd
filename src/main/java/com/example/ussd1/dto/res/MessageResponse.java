package com.example.ussd1.dto.res;

import com.fasterxml.jackson.annotation.JsonFormat;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;

@XmlRootElement(name = "messageResponse")
public class MessageResponse {

    private String transactionTime;
     private String transactionID;
    private String sourceNumber;
    private String destinationNumber;
    private String message;
    private String stage;
    private String channel;
    private String applicationTransactionID;
    private String transactionType;

    // Getters and setters
    @XmlElement
    public String getTransactionTime() {
        return transactionTime;
    }

    public void setTransactionTime(String transactionTime) {
        this.transactionTime = transactionTime;
    }

    @XmlElement
    public String getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(String transactionID) {
        this.transactionID = transactionID;
    }

    @XmlElement
    public String getSourceNumber() {
        return sourceNumber;
    }

    public void setSourceNumber(String sourceNumber) {
        this.sourceNumber = sourceNumber;
    }

    @XmlElement
    public String getDestinationNumber() {
        return destinationNumber;
    }

    public void setDestinationNumber(String destinationNumber) {
        this.destinationNumber = destinationNumber;
    }

    @XmlElement
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @XmlElement
    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    @XmlElement
    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    @XmlElement
    public String getApplicationTransactionID() {
        return applicationTransactionID;
    }

    public void setApplicationTransactionID(String applicationTransactionID) {
        this.applicationTransactionID = applicationTransactionID;
    }

    @XmlElement
    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }
}
