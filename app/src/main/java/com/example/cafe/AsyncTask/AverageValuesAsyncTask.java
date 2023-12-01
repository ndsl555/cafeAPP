package com.example.cafe.AsyncTask;

import android.os.AsyncTask;
import com.example.cafe.Dao.CafeShopDao;
import java.util.HashMap;
import java.util.Map;

public class AverageValuesAsyncTask extends AsyncTask<Void, Void, Map<String, Float>> {

    private CafeShopDao cafeShopDao;
    private AverageValuesListener listener;

    public AverageValuesAsyncTask(CafeShopDao cafeShopDao, AverageValuesListener listener) {
        this.cafeShopDao = cafeShopDao;
        this.listener = listener;
    }

    @Override
    protected Map<String, Float> doInBackground(Void... voids) {
        Map<String, Float> averageValues = new HashMap<>();
        averageValues.put("wifi", (float) cafeShopDao.getAverageWifi());
        averageValues.put("seat", (float) cafeShopDao.getAverageSeat());
        averageValues.put("quiet", (float) cafeShopDao.getAverageQuiet());
        averageValues.put("tasty", (float) cafeShopDao.getAverageTasty());
        averageValues.put("cheap", (float) cafeShopDao.getAverageCheap());
        averageValues.put("music", (float) cafeShopDao.getAverageMusic());
        return averageValues;
    }

    @Override
    protected void onPostExecute(Map<String, Float> averageValues) {
        if (listener != null) {
            listener.onAverageValuesReceived(averageValues);
        }
    }

    // 新增一個介面用於回調
    public interface AverageValuesListener {
        void onAverageValuesReceived(Map<String, Float> averageValues);
    }
}
