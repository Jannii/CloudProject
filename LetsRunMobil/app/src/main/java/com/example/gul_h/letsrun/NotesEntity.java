package com.example.gul_h.letsrun;

import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by Swashy on 2017-05-17.
 */

public class NotesEntity extends TableServiceEntity {

    private String theNote;
    private String type = "N";
    private String userName;
    private String date;

    public NotesEntity(String user, String row) {
        this.partitionKey = user;
        this.rowKey = row;
    }

    public NotesEntity() {
    }


    public String getTheNote() {
        return theNote;
    }

    public void setTheNote(String theNote) {
        this.theNote = theNote;
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
