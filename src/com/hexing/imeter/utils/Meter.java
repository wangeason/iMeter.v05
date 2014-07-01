package com.hexing.imeter.utils;

public class Meter {
	
	private String meterNumber;
	private String customerNumber;
	private String address;
	private String energyActive;
	private String latitude;
	private String longtitude;
	
	public Meter(String meterNumber, String customerNumber, String address, String energyActive,String latitude, String longtitude) {
		super();
		this.meterNumber = meterNumber;
		this.customerNumber = customerNumber;
		this.energyActive = energyActive;
		this.latitude = latitude;
		this.longtitude = longtitude;
	}
	
	public String getMeterNumber() {
		return meterNumber;
	}

	public void setMeterNumber(String MeterNumber) {
		this.meterNumber = MeterNumber;
	}

	public String getCustomerNumber() {
		return customerNumber;
	}

	public void setCustomerNumber(String customerNumber) {
		this.customerNumber = customerNumber;
	}
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getEnergyActive() {
		return energyActive;
	}

	public void setEnergyActive(String energyActive) {
		this.energyActive = energyActive;
	}
	
	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	
	public String getLongtitude() {
		return longtitude;
	}

	public void setLongtitude(String longtitude) {
		this.longtitude = longtitude;
	}
/*
	@Override
	public String toJson() {
		return "Meter [MeterNumber=" + MeterNumber + ", Model=" + Model + ", EnergyActive=" + EnergyActive
				+ "]";
	}*/
}

