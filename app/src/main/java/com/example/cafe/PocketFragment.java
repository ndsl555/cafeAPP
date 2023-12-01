package com.example.cafe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cafe.Adapter.PocketAdapter;
import com.example.cafe.AsyncTask.AverageValuesAsyncTask;
import com.example.cafe.DB.MyApp;
import com.example.cafe.Dao.CafeShopDao;
import com.example.cafe.Entity.CafeShop;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PocketFragment extends Fragment {

    private static final Map<String, String> CHINESE_CITY_MAPPING = new HashMap<>();

    static {
        CHINESE_CITY_MAPPING.put("hsinchu", "新竹");
        CHINESE_CITY_MAPPING.put("nantou", "南投");
        CHINESE_CITY_MAPPING.put("yunlin", "雲林");
        CHINESE_CITY_MAPPING.put("taitung", "台東");
        CHINESE_CITY_MAPPING.put("taichung", "台中");
        CHINESE_CITY_MAPPING.put("taipei", "台北");
        CHINESE_CITY_MAPPING.put("hualien", "花蓮");
        CHINESE_CITY_MAPPING.put("pingtung", "屏東");
        CHINESE_CITY_MAPPING.put("tainan", "台南");
        CHINESE_CITY_MAPPING.put("taoyuan", "桃園");
        CHINESE_CITY_MAPPING.put("miaoli", "苗栗");
        CHINESE_CITY_MAPPING.put("yilan", "宜蘭");
        CHINESE_CITY_MAPPING.put("chiayi", "嘉義");
        CHINESE_CITY_MAPPING.put("kaohsiung", "高雄");
        CHINESE_CITY_MAPPING.put("kinmen", "金門");
        CHINESE_CITY_MAPPING.put("lienchiang", "連江");
        CHINESE_CITY_MAPPING.put("penghu", "澎湖");
        CHINESE_CITY_MAPPING.put("keelung", "基隆");
        CHINESE_CITY_MAPPING.put("changhua", "彰化");
    }

    private RecyclerView recyclerView;
    private PocketAdapter pocketAdapter;
    private CafeShopDao cafeShopDao;

    private FloatingActionButton floatingActionButton;
    private Spinner citySpinner;
    private SeekBar seekBarWifiPocket, seekBarSeatPocket, seekBarQuietPocket, seekBarTastyPocket, seekBarCheapPocket, seekBarMusicPocket;
    private Button buttonApplyPocket, buttonClearPocket;

    private TextView textViewWifiPocket, textViewSeatPocket, textViewQuietPocket, textViewTastyPocket, textViewCheapPocket, textViewMusicPocket;
    private DrawerLayout drawerLayoutPocket;
    private Set<String> uniqueCities;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pocket, container, false);
        setHasOptionsMenu(true);
        recyclerView = view.findViewById(R.id.recyclerViewPocket);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        pocketAdapter = new PocketAdapter(new ArrayList<>(), getActivity());
        recyclerView.setAdapter(pocketAdapter);
        // 添加分隔線
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        cafeShopDao = MyApp.coffeeDatabase.cafeShopDao();

        floatingActionButton = view.findViewById(R.id.fabClearAllData);
        citySpinner = view.findViewById(R.id.citySpinnerPocket);
        seekBarWifiPocket = view.findViewById(R.id.seekBarWifiPocket);
        seekBarSeatPocket = view.findViewById(R.id.seekBarSeatPocket);
        seekBarQuietPocket = view.findViewById(R.id.seekBarQuietPocket);
        seekBarTastyPocket = view.findViewById(R.id.seekBarTastyPocket);
        seekBarCheapPocket = view.findViewById(R.id.seekBarCheapPocket);
        seekBarMusicPocket = view.findViewById(R.id.seekBarMusicPocket);

        textViewWifiPocket = view.findViewById(R.id.wifivaluePocket);
        textViewSeatPocket = view.findViewById(R.id.seatvaluePocket);
        textViewQuietPocket = view.findViewById(R.id.quietvaluePocket);
        textViewTastyPocket = view.findViewById(R.id.tastyvaluePocket);
        textViewCheapPocket = view.findViewById(R.id.cheapvaluePocket);
        textViewMusicPocket = view.findViewById(R.id.musicvaluePocket);

        buttonApplyPocket = view.findViewById(R.id.buttonApplyPocket);
        buttonClearPocket = view.findViewById(R.id.buttonClearPocket);

        drawerLayoutPocket = view.findViewById(R.id.drawerLayoutPocket);
        // Initialize the set for unique cities
        uniqueCities = new HashSet<>();

        // Set up the city spinner
        setupCitySpinner();

        // Set up SeekBar change listeners
        setupSeekBarChangeListener(seekBarWifiPocket);
        setupSeekBarChangeListener(seekBarSeatPocket);
        setupSeekBarChangeListener(seekBarQuietPocket);
        setupSeekBarChangeListener(seekBarTastyPocket);
        setupSeekBarChangeListener(seekBarCheapPocket);
        setupSeekBarChangeListener(seekBarMusicPocket);

        floatingActionButton = view.findViewById(R.id.fabClearAllData);
        floatingActionButton.setImageResource(R.drawable.baseline_delete_24);

        // Set up click listener for FloatingActionButton
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showConfirmationDialog();
            }
        });

        // Set up click listeners for buttons
        buttonApplyPocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (citySpinner.getSelectedItem() != null && citySpinner.getSelectedItem().toString() != null && !citySpinner.getSelectedItem().toString().isEmpty()) {
                    applyFilters(citySpinner.getSelectedItem().toString());
                    drawerLayoutPocket.closeDrawers();
                } else {
                    // Handle the case when nothing is selected in the spinner
                    // You may show a toast or perform other actions based on your requirement
                }
            }
        });

        buttonClearPocket.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearFilters();
                drawerLayoutPocket.closeDrawers();
            }
        });

        // Add any other setup code you need
        // Load all cafes without filters
        new LoadCafeTask(cafeShopDao).execute();
        return view;
    }
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.option_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_item_show_average) {
            showAverageDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private Map<String, Float> averageValues;

    // 在 PocketFragment 中修改 showAverageDialog 方法
    private void showAverageDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("名單中各項平均");

        View dialogView = getLayoutInflater().inflate(R.layout.habbit, null);
        builder.setView(dialogView);

        // 尋找 RatingBar 並設定其值
        RatingBar ratingBarWifi = dialogView.findViewById(R.id.personalratingBarWifi);
        RatingBar ratingBarSeat = dialogView.findViewById(R.id.personalratingBarSeat);
        RatingBar ratingBarQuiet = dialogView.findViewById(R.id.personalratingBarQuiet);
        RatingBar ratingBarTasty = dialogView.findViewById(R.id.personalratingBarTasty);
        RatingBar ratingBarCheap = dialogView.findViewById(R.id.personalratingBarCheap);
        RatingBar ratingBarMusic = dialogView.findViewById(R.id.personalratingBarMusic);

        // 設定 RatingBar 的值為資料庫中的平均值（使用 AsyncTask）
        new AverageValuesAsyncTask(cafeShopDao, new AverageValuesAsyncTask.AverageValuesListener() {
            @Override
            public void onAverageValuesReceived(Map<String, Float> values) {
                averageValues = values;

                ratingBarWifi.setRating(averageValues.get("wifi"));
                ratingBarSeat.setRating(averageValues.get("seat"));
                ratingBarQuiet.setRating(averageValues.get("quiet"));
                ratingBarTasty.setRating(averageValues.get("tasty"));
                ratingBarCheap.setRating(averageValues.get("cheap"));
                ratingBarMusic.setRating(averageValues.get("music"));
            }
        }).execute();

        builder.setPositiveButton("確定", null);

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


    // Function to set up the city spinner
    private void setupCitySpinner() {
        // Retrieve unique cities from the database
        new RetrieveCitiesTask(cafeShopDao).execute();
    }

    // AsyncTask to retrieve unique cities from the database
    private class RetrieveCitiesTask extends AsyncTask<Void, Void, List<String>> {
        private CafeShopDao cafeShopDao;

        public RetrieveCitiesTask(CafeShopDao cafeShopDao) {
            this.cafeShopDao = cafeShopDao;
        }

        @Override
        protected List<String> doInBackground(Void... voids) {
            return cafeShopDao.getUniqueCities();
        }

        @Override
        protected void onPostExecute(List<String> cities) {
            super.onPostExecute(cities);

            // Update the set of unique cities
            uniqueCities.clear();
            uniqueCities.addAll(cities);

            // Update the city spinner with unique cities
            updateCitySpinner();
        }
    }

    // Function to update the city spinner with unique cities
    private void updateCitySpinner() {
        List<String> chineseCities = new ArrayList<>();
        for (String englishCity : uniqueCities) {
            String chineseCity = CHINESE_CITY_MAPPING.get(englishCity.toLowerCase());
            chineseCities.add(chineseCity != null ? chineseCity : englishCity);
        }

        ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, chineseCities);
        cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        citySpinner.setAdapter(cityAdapter);
    }
    // Function to set up SeekBar change listener
    private void setupSeekBarChangeListener(SeekBar seekBar) {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // Handle SeekBar value change, you can apply filtering here if needed
                // Filter the list based on the search query and SeekBar values
                if (citySpinner.getSelectedItem() != null && citySpinner.getSelectedItem().toString() != null && !citySpinner.getSelectedItem().toString().isEmpty()) {
                    applyFilters(citySpinner.getSelectedItem().toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
    }

    // Function to apply filters to the RecyclerView
    private void applyFilters(String cityFilter) {
        // Retrieve values from SeekBars and other filter criteria
        int wifiValue = seekBarWifiPocket.getProgress();
        int seatValue = seekBarSeatPocket.getProgress();
        int quietValue = seekBarQuietPocket.getProgress();
        int tastyValue = seekBarTastyPocket.getProgress();
        int cheapValue = seekBarCheapPocket.getProgress();
        int musicValue = seekBarMusicPocket.getProgress();
        textViewWifiPocket.setText("網路:" + wifiValue);
        textViewSeatPocket.setText("座位:" + seatValue);
        textViewQuietPocket.setText("安靜:" + quietValue);
        textViewTastyPocket.setText("咖啡:" + tastyValue);
        textViewCheapPocket.setText("便宜:" + cheapValue);
        textViewMusicPocket.setText("音樂:" + musicValue);

        // 获取中文城市名称对应的英文城市名称
        String englishCityFilter = null;
        for (Map.Entry<String, String> entry : CHINESE_CITY_MAPPING.entrySet()) {
            if (entry.getValue().equals(cityFilter)) {
                englishCityFilter = entry.getKey();
                break;
            }
        }

        // 如果找不到对应的英文城市名称，使用原始城市名称
        if (englishCityFilter == null) {
            englishCityFilter = cityFilter;
        }


        // Implement your filtering logic here and update the RecyclerView accordingly
        new FilterCafeTask(cafeShopDao, wifiValue, seatValue, quietValue, tastyValue, cheapValue, musicValue, englishCityFilter, "").execute();
    }

    // AsyncTask to filter cafe data based on criteria
    private class FilterCafeTask extends AsyncTask<Void, Void, List<CafeShop>> {
        private CafeShopDao cafeShopDao;
        private int wifiValue, seatValue, quietValue, tastyValue, cheapValue, musicValue;
        private String cityFilter, searchQuery;

        public FilterCafeTask(CafeShopDao cafeShopDao, int wifiValue, int seatValue, int quietValue, int tastyValue, int cheapValue, int musicValue, String cityFilter, String searchQuery) {
            this.cafeShopDao = cafeShopDao;
            this.wifiValue = wifiValue;
            this.seatValue = seatValue;
            this.quietValue = quietValue;
            this.tastyValue = tastyValue;
            this.cheapValue = cheapValue;
            this.musicValue = musicValue;
            this.cityFilter = cityFilter;
            this.searchQuery = searchQuery;
        }

        @Override
        protected List<CafeShop> doInBackground(Void... voids) {
            // Implement filtering logic in the DAO or here
            return cafeShopDao.filterCafes(wifiValue, seatValue, quietValue, tastyValue, cheapValue, musicValue, cityFilter, searchQuery);
        }

        @Override
        protected void onPostExecute(List<CafeShop> filteredCafes) {
            super.onPostExecute(filteredCafes);

            // Update the RecyclerView adapter with the filtered data
            pocketAdapter.setData(filteredCafes);
            Log.d("RecyclerView", "Number of items: " + filteredCafes.size());

            // Ensure UI updates are done on the main thread
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // Update other UI elements here
                }
            });
        }
    }

    // Function to clear filters and reset the RecyclerView
    private void clearFilters() {
        // Implement logic to clear filters and reset the RecyclerView
        // For example, you can reset SeekBars, clear search query, etc.
        seekBarWifiPocket.setProgress(0);
        seekBarSeatPocket.setProgress(0);
        seekBarQuietPocket.setProgress(0);
        seekBarTastyPocket.setProgress(0);
        seekBarCheapPocket.setProgress(0);
        seekBarMusicPocket.setProgress(0);
        citySpinner.setSelection(0);

        // Reload all cafes without filters
        new LoadCafeTask(cafeShopDao).execute();
    }

    // AsyncTask to load cafe data from the database
    private class LoadCafeTask extends AsyncTask<Void, Void, List<CafeShop>> {
        private CafeShopDao cafeShopDao;

        public LoadCafeTask(CafeShopDao cafeShopDao) {
            this.cafeShopDao = cafeShopDao;
        }

        @Override
        protected List<CafeShop> doInBackground(Void... voids) {
            return cafeShopDao.getAllCafes();
        }

        @Override
        protected void onPostExecute(List<CafeShop> cafes) {
            super.onPostExecute(cafes);

            // Update the RecyclerView adapter with the loaded data
            pocketAdapter.setData(cafes);
        }
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("確定清除所有資料?")
                .setPositiveButton("確定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "確定", clear all data
                        clearAllData();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked "取消", do nothing or handle as needed
                    }
                });
        // Create and show the AlertDialog
        builder.create().show();
    }

    // Function to clear all data in cafe_table
    private void clearAllData() {
        // Implement logic to clear all data in the cafe_table
        // For example, you can use an AsyncTask to perform the database operation in the background
        new ClearAllDataAsyncTask(cafeShopDao).execute();
        pocketAdapter.clear();
    }

    // AsyncTask to clear all data in the background
    private static class ClearAllDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private CafeShopDao cafeShopDao;

        public ClearAllDataAsyncTask(CafeShopDao cafeShopDao) {
            this.cafeShopDao = cafeShopDao;
        }

        @Override
        protected Void doInBackground(Void...voids) {
            // Perform the operation to clear all data
            cafeShopDao.deleteAll();
            return null;
        }
    }
}
