package com.example.cafe.Entity;

// CafeShop.java

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "cafe_shop_table")
public class CafeShop {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String name;
    private String city;
    private float wifi;
    private float seat;
    private float quiet;
    private float tasty;
    private float cheap;
    private float music;
    private String address;
    private double latitude;
    private double longitude;
    private String url;
    private String limitedTime;
    private String socket;
    private String standingDesk;
    private String mrt;
    private String openTime;

    // Getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public float getWifi() {
        return wifi;
    }

    public float getSeat() {
        return seat;
    }

    public float getQuiet() {
        return quiet;
    }

    public float getTasty() {
        return tasty;
    }

    public float getCheap() {
        return cheap;
    }

    public float getMusic() {
        return music;
    }

    public String getAddress() {
        return address;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getUrl() {
        return url;
    }

    public String getLimitedTime() {
        return limitedTime;
    }

    public String getSocket() {
        return socket;
    }

    public String getStandingDesk() {
        return standingDesk;
    }

    public String getMrt() {
        return mrt;
    }

    public String getOpenTime() {
        return openTime;
    }

    // Setters
    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setWifi(float wifi) {
        this.wifi = wifi;
    }

    public void setSeat(float seat) {
        this.seat = seat;
    }

    public void setQuiet(float quiet) {
        this.quiet = quiet;
    }

    public void setTasty(float tasty) {
        this.tasty = tasty;
    }

    public void setCheap(float cheap) {
        this.cheap = cheap;
    }

    public void setMusic(float music) {
        this.music = music;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setLimitedTime(String limitedTime) {
        this.limitedTime = limitedTime;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public void setStandingDesk(String standingDesk) {
        this.standingDesk = standingDesk;
    }

    public void setMrt(String mrt) {
        this.mrt = mrt;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }
}
