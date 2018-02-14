package com.common;

public class LicensePlate {
    private int xx;
    private int yy;
    private int zz;


    /**
     *
     * @param LicensePlate - XX-YY-ZZ
     */
    public LicensePlate(String LicensePlate) {
        if(LicensePlate.length() != 8)
        this.xx = Integer.parseInt(LicensePlate.substring(0, 1));
        this.yy = Integer.parseInt(LicensePlate.substring(3, 4));
        this.zz = Integer.parseInt(LicensePlate.substring(7, 8));
    }

    public String getLicensePlate() {
        return Integer.toString(this.xx) + "-" + Integer.toString(this.yy) + "-" + Integer.toString(this.zz);
    }
}
