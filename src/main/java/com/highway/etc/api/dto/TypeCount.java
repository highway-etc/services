package com.highway.etc.api.dto;

public class TypeCount {

    private String type;
    private long count;

    public TypeCount() {
    }

    public TypeCount(String type, long count) {
        this.type = type;
        this.count = count;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
