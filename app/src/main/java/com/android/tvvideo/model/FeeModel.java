package com.android.tvvideo.model;

/**
 * Created by yangfengyuan on 16/8/23.
 */
public class FeeModel {

    String inHospitalNum;

    String date;

    String kind;

    String name;

    String specifications;

    String unit;

    String unitPrice;

    String doExamRoom;

    String safestKind;

    String quality;

    String price;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDoExamRoom() {
        return doExamRoom;
    }

    public void setDoExamRoom(String doExamRoom) {
        this.doExamRoom = doExamRoom;
    }

    public String getInHospitalNum() {
        return inHospitalNum;
    }

    public void setInHospitalNum(String inHospitalNum) {
        this.inHospitalNum = inHospitalNum;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getQuality() {
        return quality;
    }

    public void setQuality(String quality) {
        this.quality = quality;
    }

    public String getSafestKind() {
        return safestKind;
    }

    public void setSafestKind(String safestKind) {
        this.safestKind = safestKind;
    }

    public String getSpecifications() {
        return specifications;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(String unitPrice) {
        this.unitPrice = unitPrice;
    }
}
