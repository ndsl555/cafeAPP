package com.example.cafe.AsyncTask;

import android.os.AsyncTask;

import com.example.cafe.DB.MyApp;
import com.example.cafe.Dao.CafeShopDao;
import com.example.cafe.Entity.CafeShop;

public class RemoveCafeAsyncTask extends AsyncTask<Void, Void, Void> {
    private CafeShop cafeShop;

    public RemoveCafeAsyncTask(CafeShop cafeShop) {
        this.cafeShop = cafeShop;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        // 在這裡執行從數據庫中刪除咖啡店的操作
        CafeShopDao cafeShopDao = MyApp.coffeeDatabase.cafeShopDao();
        cafeShopDao.deleteByName(cafeShop.getName());
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);
        // 执行完数据库操作后的任何UI更新
        // 可以通過適當的方式通知UI，例如使用回調或EventBus
    }
}