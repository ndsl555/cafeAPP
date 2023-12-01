package com.example.cafe.Entity;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "bar_table")
public class Bar {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String barcodeData;

    public Bar(String barcodeData) {
        this.barcodeData = barcodeData;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getBarcodeData() {
        return barcodeData;
    }
}


