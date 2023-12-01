package com.example.cafe.DB;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.cafe.Dao.BarDao;
import com.example.cafe.Dao.CafeShopDao;
import com.example.cafe.Entity.Bar;
import com.example.cafe.Entity.CafeShop;

@Database(entities = {CafeShop.class, Bar.class}, version = 2)
public abstract class CoffeDatabase extends RoomDatabase {
    public abstract CafeShopDao cafeShopDao();
    public abstract BarDao barDao();
    // 可以添加其他配置和方法，比如下面这个单例模式
    private static volatile CoffeDatabase INSTANCE;

    public static CoffeDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (CoffeDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    CoffeDatabase.class, "coffe_database")
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
