package com.volcano.holsansys.ui.manage;

public class Manage {

    private String recordTime;
    private String recordName;
    private String recordIf;

    public Manage(String recordTime, String recordName, String recordIf) {
        this.recordTime = recordTime;
        this.recordName = recordName;
        this.recordIf =recordIf;
    }

    public String getRecordTime() {
        return recordTime;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getRecordIf() {
        return recordIf;
    }

    public void setRecordTime(String recordTime) {
        this.recordTime = recordTime;
    }

    public void setRecordName(String recordName) {
        this.recordName = recordName;
    }

    public void setRecordIf(String recordIf) {
        this.recordIf = recordIf;
    }
}
