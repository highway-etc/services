package com.highway.etc.api.dto;

public class ProvinceCount {

    private String province;
    private long count;

    public ProvinceCount() {
    }

    public ProvinceCount(String province, long count) {
        this.province = province;
        this.count = count;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
