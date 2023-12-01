package com.example.cafe;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cafe.Adapter.CafeAdapter;
import com.example.cafe.DataClass.Cafe;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class SearchFragment extends Fragment {
    // 城市名稱的中英文映射
    private static final Map<String, String> CITY_MAPPING = new HashMap<>();

    static {
        CITY_MAPPING.put("hsinchu", "新竹");
        CITY_MAPPING.put("nantou", "南投");
        CITY_MAPPING.put("yunlin", "雲林");
        CITY_MAPPING.put("taitung","台東");
        CITY_MAPPING.put("taichung", "台中");
        CITY_MAPPING.put("taipei", "台北");
        CITY_MAPPING.put("hualien", "花蓮");
        CITY_MAPPING.put("pingtung", "屏東");
        CITY_MAPPING.put("tainan", "台南");
        CITY_MAPPING.put("taoyuan", "桃園");
        CITY_MAPPING.put("miaoli", "苗栗");
        CITY_MAPPING.put("yilan", "宜蘭");
        CITY_MAPPING.put("chiayi", "嘉義");
        CITY_MAPPING.put("kaohsiung", "高雄");
        CITY_MAPPING.put("kinmen", "金門");
        CITY_MAPPING.put("lienchiang", "連江");
        CITY_MAPPING.put("penghu", "澎湖");
        CITY_MAPPING.put("keelung", "基隆");
        CITY_MAPPING.put("changhua", "彰化");
    }
    private ArrayList<String> uniqueCities = new ArrayList<>();
    private Spinner citySpinner;
    private static final String API_URL = "https://cafenomad.tw/api/v1.2/cafes";
    private ArrayList<Cafe> cafes = new ArrayList<>();
    private ArrayList<Cafe> filteredCafes = new ArrayList<>();
    private RecyclerView recyclerView;
    private CafeAdapter cafeAdapter;
    private SeekBar wifiSlider, seatSlider, quietSlider, tastySlider, cheapSlider, musicSlider;
    private Button filterButton, clearButton;
    private DrawerLayout drawerLayout;
    private ProgressBar progressBar;

    private TextView tv1, tv2, tv3, tv4, tv5, tv6;

    private int wifiProgress, seatProgress, quietProgress, tastyProgress, cheapProgress, musicProgress;

    private FetchCafeDataTask fetchCafeDataTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        progressBar = view.findViewById(R.id.progressBar);

        Drawable drawable = progressBar.getIndeterminateDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.ave_color), PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(drawable);

        fetchCafeDataTask = new FetchCafeDataTask();
        fetchCafeDataTask.execute();

        SearchView searchView = view.findViewById(R.id.searchView);
        searchView.setQueryHint("快速搜尋...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchCafesByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchCafesByName(newText);
                return true;
            }
        });

        recyclerView = view.findViewById(R.id.recyclerView);
        cafeAdapter = new CafeAdapter(cafes, getContext());
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(cafeAdapter);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        drawerLayout = view.findViewById(R.id.drawerLayout);

        wifiSlider = view.findViewById(R.id.seekBarWifi);
        seatSlider = view.findViewById(R.id.seekBarSeat);
        quietSlider = view.findViewById(R.id.seekBarQuiet);
        tastySlider = view.findViewById(R.id.seekBarTasty);
        cheapSlider = view.findViewById(R.id.seekBarCheap);
        musicSlider = view.findViewById(R.id.seekBarMusic);

        filterButton = view.findViewById(R.id.buttonApply);
        clearButton = view.findViewById(R.id.buttonClear);

        tv1 = view.findViewById(R.id.wifivalue);
        tv2 = view.findViewById(R.id.seatvalue);
        tv3 = view.findViewById(R.id.quietvalue);
        tv4 = view.findViewById(R.id.tastyvalue);
        tv5 = view.findViewById(R.id.cheapvalue);
        tv6 = view.findViewById(R.id.musicvalue);

        citySpinner = view.findViewById(R.id.citySpinner);

        setUpSeekBarListeners();
        setUpButtonListeners();

        return view;
    }

    private void searchCafesByName(String query) {
        ArrayList<Cafe> searchedCafes = new ArrayList<>();

        for (Cafe cafe : cafes) {
            if (cafe.getName().toLowerCase().contains(query.toLowerCase())) {
                searchedCafes.add(cafe);
            }
        }

        cafeAdapter.setData(searchedCafes);
        cafeAdapter.notifyDataSetChanged();
    }

    private void setUpSeekBarListeners() {
        wifiSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                wifiProgress = progress;
                tv1.setText("網路:" + String.valueOf(wifiProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }
        });

        seatSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seatProgress = progress;
                tv2.setText("座位:"+String.valueOf(seatProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            // 其他兩個方法保持不變
        });
        quietSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                quietProgress = progress;
                tv3.setText("安靜:"+String.valueOf(quietProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            // 其他兩個方法保持不變
        });
        tastySlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tastyProgress = progress;
                tv4.setText("咖啡:"+String.valueOf(tastyProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            // 其他兩個方法保持不變
        });
        cheapSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                cheapProgress = progress;
                tv5.setText("便宜:"+String.valueOf(cheapProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            // 其他兩個方法保持不變
        });
        musicSlider.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicProgress = progress;
                tv6.setText("音樂:"+String.valueOf(musicProgress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                // Add any additional behavior as needed
            }

            // 其他兩個方法保持不變
        });

        // 設置其他 SeekBar 的監聽器
    }

    private void setUpButtonListeners() {
        filterButton.setOnClickListener(v -> {
            applyFilters();
            drawerLayout.closeDrawers();
        });

        clearButton.setOnClickListener(v -> {
            clearFilters();
            drawerLayout.closeDrawers();
        });
    }

    private void applyFilters() {
        String selectedCity = uniqueCities.get(citySpinner.getSelectedItemPosition());
        ArrayList<Cafe> newFilteredCafes = new ArrayList<>();

        for (Cafe cafe : cafes) {
            if (cafe.getCity().equals(selectedCity) &&
                    Double.parseDouble(cafe.getWifi()) >= Double.parseDouble(String.valueOf(wifiProgress)) &&
                    Double.parseDouble(cafe.getSeat()) >= Double.parseDouble(String.valueOf(seatProgress)) &&
                    Double.parseDouble(cafe.getQuiet()) >= Double.parseDouble(String.valueOf(quietProgress)) &&
                    Double.parseDouble(cafe.getTasty()) >= Double.parseDouble(String.valueOf(tastyProgress)) &&
                    Double.parseDouble(cafe.getCheap()) >= Double.parseDouble(String.valueOf(cheapProgress)) &&
                    Double.parseDouble(cafe.getMusic()) >= Double.parseDouble(String.valueOf(musicProgress))) {
                newFilteredCafes.add(cafe);
            }
        }

        Toast.makeText(requireContext(), "找到" + newFilteredCafes.size() + "家", Toast.LENGTH_SHORT).show();
        cafeAdapter.setData(newFilteredCafes);
        cafeAdapter.notifyDataSetChanged();
    }

    private void clearFilters() {
        wifiSlider.setProgress(0);
        seatSlider.setProgress(0);
        quietSlider.setProgress(0);
        tastySlider.setProgress(0);
        cheapSlider.setProgress(0);
        musicSlider.setProgress(0);

        cafeAdapter.setData(cafes);
        cafeAdapter.notifyDataSetChanged();

        citySpinner.setSelection(0);
    }

    private class FetchCafeDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(API_URL)
                    .build();

            try {
                Response response = client.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            //CITY_MAPPING.clear();  // Clear previous mappings
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (isAdded() &&result != null) {
                try {
                    JSONArray cafesArray = new JSONArray(result);
                    System.out.println(cafesArray.length());
                    Set<String> citySet = new HashSet<>();

                    for (int i = 0; i < cafesArray.length(); i++) {
                        JSONObject cafeObject = cafesArray.getJSONObject(i);
                        String city = cafeObject.getString("city");
                        citySet.add(city);
                        CITY_MAPPING.put(city.toLowerCase(), getCityChineseName(city.toLowerCase()));

                    }

                    uniqueCities.clear();
                    uniqueCities.addAll(citySet);

                    ArrayAdapter<String> cityAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, uniqueCities) {
                        @Override
                        public View getView(int position, View convertView, ViewGroup parent) {
                            View view = super.getView(position, convertView, parent);
                            String cityName = getItem(position);
                            String displayText = CITY_MAPPING.get(cityName.toLowerCase());
                            ((TextView) view).setText(displayText);
                            return view;
                        }

                        @Override
                        public View getDropDownView(int position, View convertView, ViewGroup parent) {
                            View view = super.getDropDownView(position, convertView, parent);
                            String cityName = getItem(position);
                            String displayText = CITY_MAPPING.get(cityName.toLowerCase());
                            ((TextView) view).setText(displayText);
                            return view;
                        }
                    };

                    cityAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(cityAdapter);
                    citySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                            String selectedCity = uniqueCities.get(position);
                            // 在這裡你可以更新 RecyclerView 中的數據
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parentView) {
                        }
                    });

                    for (int i = 0; i < cafesArray.length(); i++) {
                        JSONObject cafeObject = cafesArray.getJSONObject(i);
                        String id = cafeObject.getString("id");
                        String name = cafeObject.getString("name");
                        String city = cafeObject.getString("city");
                        String wifi = cafeObject.getString("wifi");
                        String seat = cafeObject.getString("seat");
                        String quiet = cafeObject.getString("quiet");
                        String tasty = cafeObject.getString("tasty");
                        String cheap = cafeObject.getString("cheap");
                        String music = cafeObject.getString("music");
                        String address = cafeObject.getString("address");
                        String latitude = cafeObject.getString("latitude");
                        String longitude = cafeObject.getString("longitude");
                        String url = cafeObject.getString("url");
                        String limitedTime = cafeObject.getString("limited_time");
                        String socket = cafeObject.getString("socket");
                        String standingDesk = cafeObject.getString("standing_desk");
                        String mrt = cafeObject.getString("mrt");
                        String openTime = cafeObject.getString("open_time");

                        Cafe cafe = new Cafe(id, name, city, wifi, seat, quiet, tasty, cheap, music, address,
                                latitude, longitude, url, limitedTime, socket, standingDesk, mrt, openTime);
                        cafes.add(cafe);
                    }

                    cafeAdapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        // 在 FetchCafeDataTask 中加入獲取中文城市名稱的方法
        private String getCityChineseName(String englishName) {
            return CITY_MAPPING.get(englishName.toLowerCase());
        }
        @Override
        protected void onCancelled() {
            // 在任務被取消時的處理
        }
    }
}
