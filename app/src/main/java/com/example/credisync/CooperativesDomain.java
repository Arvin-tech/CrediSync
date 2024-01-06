package com.example.credisync;

public class CooperativesDomain {
    private String coopName;
    private String coopAddress;
    private String picAddress;
    private String miniPicAddress;

    public CooperativesDomain(String coopName, String coopAddress, String picAddress, String miniPicAddress) {
        this.coopName = coopName;
        this.coopAddress = coopAddress;
        this.picAddress = picAddress;
        this.miniPicAddress = miniPicAddress;
    }

    public String getCoopName() {
        return coopName;
    }

    public void setCoopName(String coopName) {
        this.coopName = coopName;
    }

    public String getCoopAddress() {
        return coopAddress;
    }

    public void setCoopAddress(String coopAddress) {
        this.coopAddress = coopAddress;
    }

    public String getPicAddress() {
        return picAddress;
    }

    public void setPicAddress(String picAddress) {
        this.picAddress = picAddress;
    }

    public String getMiniPicAddress() {
        return miniPicAddress;
    }

    public void setMiniPicAddress(String miniPicAddress) {
        this.miniPicAddress = miniPicAddress;
    }
}
