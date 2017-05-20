package com.example.gul_h.letsrun;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by Swashy on 2017-05-17.
 */

public class HeartRateEntity extends TableServiceEntity {

    private Double heartRate;
    private String type = "H";
    private String userName;
    private String date;

    public HeartRateEntity(String user, String row ){
        this.partitionKey = user;
        this.rowKey = row;

    }
    public HeartRateEntity (){}


    public Double getHeartRate() {
        return heartRate;
    }

    public void setHeartRate(Double heartRate) {
        this.heartRate = heartRate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}