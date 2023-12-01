package com.example.cafe.Dao;

// CafeShopDao.java
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.cafe.Entity.CafeShop;

import java.util.ArrayList;
import java.util.List;

@Dao
public interface CafeShopDao {


    @Query("SELECT * FROM cafe_shop_table WHERE wifi >= :wifiValue AND seat >= :seatValue AND quiet >= :quietValue AND tasty >= :tastyValue AND cheap >= :cheapValue AND music >= :musicValue AND city = :cityFilter AND (name LIKE '%' || :searchQuery || '%' OR address LIKE '%' || :searchQuery || '%')")
    List<CafeShop> filterCafesWithSearch(int wifiValue, int seatValue, int quietValue, int tastyValue, int cheapValue, int musicValue, String cityFilter, String searchQuery);

    @Query("SELECT * FROM cafe_shop_table WHERE wifi >= :wifiValue AND seat >= :seatValue AND quiet >= :quietValue AND tasty >= :tastyValue AND cheap >= :cheapValue AND music >= :musicValue AND city = :cityFilter AND name LIKE '%' || :searchQuery || '%'")
    List<CafeShop> filterCafes(int wifiValue, int seatValue, int quietValue, int tastyValue, int cheapValue, int musicValue, String cityFilter, String searchQuery);


    // 新增方法：獲取所有唯一的城市列表
    @Query("SELECT DISTINCT city FROM cafe_shop_table")
    List<String> getUniqueCities();

    @Query("SELECT * FROM cafe_shop_table")
    List<CafeShop> getAllCafes();

    @Insert
    void insert(CafeShop cafeShop);

    // 新增方法：計算wifi的平均值
    @Query("SELECT AVG(wifi) FROM cafe_shop_table")
    double getAverageWifi();

    // 新增方法：計算seat的平均值
    @Query("SELECT AVG(seat) FROM cafe_shop_table")
    double getAverageSeat();

    // 新增方法：計算quiet的平均值
    @Query("SELECT AVG(quiet) FROM cafe_shop_table")
    double getAverageQuiet();

    // 新增方法：計算tasty的平均值
    @Query("SELECT AVG(tasty) FROM cafe_shop_table")
    double getAverageTasty();

    // 新增方法：計算cheap的平均值
    @Query("SELECT AVG(cheap) FROM cafe_shop_table")
    double getAverageCheap();

    // 新增方法：計算music的平均值
    @Query("SELECT AVG(music) FROM cafe_shop_table")
    double getAverageMusic();


    @Query("DELETE FROM cafe_shop_table")
    void deleteAll();

    @Query("DELETE FROM cafe_shop_table WHERE name = :name")
    void deleteByName(String name);
    // Add other necessary queries
}
