package com.example.gul_h.letsrun;

/**
 * Created by Swashy on 2017-05-17.
 */

import com.microsoft.azure.storage.table.TableServiceEntity;


import com.microsoft.azure.storage.table.TableServiceEntity;

/**
 * Created by gul_h on 2017-05-09.
 */

public class StepCounterEntity extends TableServiceEntity {

    String steps;
    String type = "S";
    String userName;

    public StepCounterEntity(String user, String row ){
        this.partitionKey = user;
        this.rowKey = row;

    }
    public StepCounterEntity (){}

    public String getSteps() {
        return steps;
    }

    public void setSteps(String steps) {
        this.steps = steps;
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

