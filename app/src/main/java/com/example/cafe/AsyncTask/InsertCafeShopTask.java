package com.example.cafe.AsyncTask;

import android.os.AsyncTask;

import com.example.cafe.Dao.CafeShopDao;
import com.example.cafe.Entity.CafeShop;

public class InsertCafeShopTask extends AsyncTask<CafeShop, Void, Void> {

    private CafeShopDao cafeShopDao;

    public InsertCafeShopTask(CafeShopDao cafeShopDao) {
        this.cafeShopDao = cafeShopDao;
    }

    @Override
    protected Void doInBackground(CafeShop... cafeShops) {
        cafeShopDao.insert(cafeShops[0]);
        return null;
    }
}