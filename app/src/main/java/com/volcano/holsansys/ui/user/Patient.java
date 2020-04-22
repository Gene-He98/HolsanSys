package com.volcano.holsansys.ui.user;

public class Patient {
    private String name_patient;
    private String situation_patient;
    private String location_patient;

    public Patient(String name_patient, String situation_patient,
                        String location_patient) {
        this.name_patient = name_patient;
        this.situation_patient = situation_patient;
        this.location_patient = location_patient;
    }

    public String getName() {
        return name_patient;
    }

    public String getSituation() {
        return situation_patient;
    }

    public String getLocation() {
        return location_patient;
    }

    public void setName(String name_patient) {
        this.name_patient = name_patient;
    }

    public void setSituation(String situation_patient) {
        this.situation_patient = situation_patient;
    }

    public void setLocaction(String location_patient) {
        this.location_patient = location_patient;
    }

}
