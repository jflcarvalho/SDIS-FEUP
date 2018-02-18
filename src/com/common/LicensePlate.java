package com.common;

public class LicensePlate {
    private String full;
    private String[] elements;

    public LicensePlate(String full) {
      this.full = full;
      this.elements = full.split("-");
    }

    public String getFull() {
        return this.full;
    }

    public int hashCode() {
      return this.full.length();
    }

    public boolean equals(Object object) {
      return ((LicensePlate) object).full.equals(this.full);
    }
}
