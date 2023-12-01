package com.example.cafe.Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cafe.Entity.Bar;

import java.util.List;

@Dao
public interface BarDao {

    @Insert
    void insertBar(Bar bar);

    @Query("SELECT * FROM bar_table ORDER BY id DESC LIMIT 1")
    Bar getLatestBar();
}
