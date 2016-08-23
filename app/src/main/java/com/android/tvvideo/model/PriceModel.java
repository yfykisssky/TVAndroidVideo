package com.android.tvvideo.model;

/**
 * Created by yangfengyuan on 16/8/23.
 */
public class PriceModel {

    String clinicalKind;

    String clinicalName;

    String getKind;

    String getName;

    String unit;

    String price;

    public String getClinicalKind() {
        return clinicalKind;
    }

    public void setClinicalKind(String clinicalKind) {
        this.clinicalKind = clinicalKind;
    }

    public String getClinicalName() {
        return clinicalName;
    }

    public void setClinicalName(String clinicalName) {
        this.clinicalName = clinicalName;
    }

    public String getGetKind() {
        return getKind;
    }

    public void setGetKind(String getKind) {
        this.getKind = getKind;
    }

    public String getGetName() {
        return getName;
    }

    public void setGetName(String getName) {
        this.getName = getName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
