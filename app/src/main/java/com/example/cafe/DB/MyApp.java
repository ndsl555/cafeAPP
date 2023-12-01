package com.example.cafe.DB;

import android.app.Application;
import androidx.room.Room;

public class MyApp extends Application {

    public static CoffeDatabase coffeeDatabase;

    @Override
    public void onCreate() {
        super.onCreate();

        // 在應用程式啟動時初始化你的資料庫
        coffeeDatabase = Room.databaseBuilder(getApplicationContext(), CoffeDatabase.class, "coffe_database")
                .fallbackToDestructiveMigration()  // 若發生 schema 變更，則使用破壞性遷移
                .build();
    }
}
