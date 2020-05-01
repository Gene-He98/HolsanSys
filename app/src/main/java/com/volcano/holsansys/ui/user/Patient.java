package com.volcano.holsansys.ui.user;

public class Patient {
    private String patientName;
    private String patientAge;
    private String patientSex;
    private String patientAddress;
    private String patientBloodType;
    private String patientMedicalHistory;
    private String patientAllergy;
    private String location;

    public Patient(String patientName, String location_patient) {
        this.patientName = patientName;
        this.location = location_patient;
    }

    public Patient(String patientName,String patientAge ,String patientSex,String patientAddress,
                   String patientBloodType,String patientMedicalHistory,String patientAllergy,
                   String recentDrugRecord, String location) {
        this.patientName = patientName;
        this.patientAge = patientAge;
        this.patientSex = patientSex;
        this.patientAddress = patientAddress;
        this.patientBloodType = patientBloodType;
        this.patientMedicalHistory = patientMedicalHistory;
        this.patientAllergy = patientAllergy;
        this.location = location;
    }


    public String getName() {
        return patientName;
    }

    public String getLocation() {
        return location;
    }

    public void setName(String name_patient) {
        this.patientName = name_patient;
    }

    public void setLocaction(String location_patient) {
        this.location = location_patient;
    }

}
