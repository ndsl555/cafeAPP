package com.example.cafe.DataClass;

// Cafe.java
public class Cafe {
    private String id;
    private String name;

    private String city;
    private String wifi;
    private String seat;
    private String quiet;
    private String tasty;
    private String cheap;
    private String music;
    private String address;
    private String latitude;
    private String longitude;
    private String url;
    private String limitedTime;
    private String socket;
    private String standingDesk;
    private String mrt;
    private String openTime;

    // 構造函數
    public Cafe(String id, String name,String city, String wifi, String seat, String quiet, String tasty, String cheap,
                String music, String address, String latitude, String longitude, String url,
                String limitedTime, String socket, String standingDesk, String mrt, String openTime) {
        this.id = id;
        this.name = name;
        this.city = city;
        this.wifi = wifi;
        this.seat = seat;
        this.quiet = quiet;
        this.tasty = tasty;
        this.cheap = cheap;
        this.music = music;
        this.address = address;
        this.latitude = latitude;
        this.longitude = longitude;
        this.url = url;
        this.limitedTime = limitedTime;
        this.socket = socket;
        this.standingDesk = standingDesk;
        this.mrt = mrt;
        this.openTime = openTime;
    }



    // Getter 和 Setter 方法
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWifi() {
        return wifi;
    }

    public void setWifi(String wifi) {
        this.wifi = wifi;
    }

    public String getSeat() {
        return seat;
    }

    public void setSeat(String seat) {
        this.seat = seat;
    }

    public String getQuiet() {
        return quiet;
    }

    public void setQuiet(String quiet) {
        this.quiet = quiet;
    }

    public String getTasty() {
        return tasty;
    }

    public void setTasty(String tasty) {
        this.tasty = tasty;
    }

    public String getCheap() {
        return cheap;
    }

    public void setCheap(String cheap) {
        this.cheap = cheap;
    }

    public String getMusic() {
        return music;
    }

    public void setMusic(String music) {
        this.music = music;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getLimitedTime() {
        return limitedTime;
    }

    public void setLimitedTime(String limitedTime) {
        this.limitedTime = limitedTime;
    }

    public String getSocket() {
        return socket;
    }

    public void setSocket(String socket) {
        this.socket = socket;
    }

    public String getStandingDesk() {
        return standingDesk;
    }

    public void setStandingDesk(String standingDesk) {
        this.standingDesk = standingDesk;
    }

    public String getMrt() {
        return mrt;
    }

    public void setMrt(String mrt) {
        this.mrt = mrt;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }
}
