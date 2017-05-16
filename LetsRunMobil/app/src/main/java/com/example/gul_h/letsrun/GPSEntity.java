package com.example.gul_h.letsrun;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by gul_h on 2017-05-09.
 */

public class GPSEntity extends TableServiceEntity {


    public GPSEntity(String user, String row ){
        this.partitionKey = user;
        this.rowKey = row;

    }
    public GPSEntity (){}

        String latitude;
        String longitude;

        String ID;
        String type = "G";
        String userName;


    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
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

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }
}
