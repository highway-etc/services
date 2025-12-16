package com.highway.etc.model;

import java.time.LocalDateTime;

public class TrafficRecord {

    private Long id;
    private LocalDateTime gcsj;
    private String xzqhmc;
    private Integer adcode;
    private String kkmc;
    private Integer stationId;
    private String fxlx;
    private String hpzl;
    private String hphmMask;
    private String clppxh;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getGcsj() {
        return gcsj;
    }

    public void setGcsj(LocalDateTime gcsj) {
        this.gcsj = gcsj;
    }

    public String getXzqhmc() {
        return xzqhmc;
    }

    public void setXzqhmc(String xzqhmc) {
        this.xzqhmc = xzqhmc;
    }

    public Integer getAdcode() {
        return adcode;
    }

    public void setAdcode(Integer adcode) {
        this.adcode = adcode;
    }

    public String getKkmc() {
        return kkmc;
    }

    public void setKkmc(String kkmc) {
        this.kkmc = kkmc;
    }

    public Integer getStationId() {
        return stationId;
    }

    public void setStationId(Integer stationId) {
        this.stationId = stationId;
    }

    public String getFxlx() {
        return fxlx;
    }

    public void setFxlx(String fxlx) {
        this.fxlx = fxlx;
    }

    public String getHpzl() {
        return hpzl;
    }

    public void setHpzl(String hpzl) {
        this.hpzl = hpzl;
    }

    public String getHphmMask() {
        return hphmMask;
    }

    public void setHphmMask(String hphmMask) {
        this.hphmMask = hphmMask;
    }

    public String getClppxh() {
        return clppxh;
    }

    public void setClppxh(String clppxh) {
        this.clppxh = clppxh;
    }
}
