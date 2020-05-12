package com.volcano.holsansys.ui.manage;

public class Medicine {

	private String medicineName;//药品名
	private String medicineAnotherName;//药品别称
	private String usage;//用途
	private String dosage;//用法用量
	private String cautions;//注意事项
	private String validity;//有效期

	public Medicine(String medicineName, String medicineAnotherName, String validity) {
		this.medicineName = medicineName;
		this.medicineAnotherName = medicineAnotherName;
		this.validity =validity;
	}
	
	public String getMedicineName() {
		return medicineName;
	}
	public void setMedicineName(String medicineName) {
		this.medicineName = medicineName;
	}
	public String getMedicineAnotherName() {
		return medicineAnotherName;
	}
	public void setMedicineAnotherName(String medicineAnotherName) {
		this.medicineAnotherName = medicineAnotherName;
	}
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	public String getDosage() {
		return dosage;
	}
	public void setDosage(String dosage) {
		this.dosage = dosage;
	}
	public String getCautions() {
		return cautions;
	}
	public void setCautions(String cautions) {
		this.cautions = cautions;
	}
	public String getValidity() {
		return validity;
	}
	public void setValidity(String validity) {
		this.validity = validity;
	}

}
