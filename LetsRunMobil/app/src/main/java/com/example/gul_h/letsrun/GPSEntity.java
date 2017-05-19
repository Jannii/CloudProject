package com.example.gul_h.letsrun;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by gul_h on 2017-05-09.
 */

public class GPSEntity extends TableServiceEntity {

    Double latitude;
    Double longitude;
    String ID;
    String type = "G";
    String userName;

    public GPSEntity(String user, String row ){
        this.partitionKey = user;
        this.rowKey = row;

    }
    public GPSEntity (){}


    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
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


}
