package com.example.cafe;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.cafe.DataClass.Cafe;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class NearFragment extends Fragment {

    private static final String API_URL = "https://cafenomad.tw/api/v1.2/cafes";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private ArrayList<Cafe> cafes = new ArrayList<>();
    private MapView mapView;
    private GoogleMap googleMap;
    private ProgressBar progressBar;
    private LatLng currentLatLng;  // 新增LatLng類別變數

    private FetchCafeDataTask fetchCafeDataTask;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_near, container, false);
        progressBar = view.findViewById(R.id.progressBarNear);
        Drawable drawable = progressBar.getIndeterminateDrawable();
        drawable.setColorFilter(getResources().getColor(R.color.ave_color), PorterDuff.Mode.SRC_IN);
        progressBar.setIndeterminateDrawable(drawable);
        fetchCafeDataTask = new FetchCafeDataTask();
        fetchCafeDataTask.execute();
        mapView = view.findViewById(R.id.mapViewNear);
        mapView.onCreate(savedInstanceState);

        mapView.getMapAsync(map -> {
            googleMap = map;
            requestLocationPermission();
        });

        return view;
    }

    private void requestLocationPermission() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            showCurrentLocation();
        }
    }

    private void showCurrentLocation() {
        LocationManager locationManager = (LocationManager) requireContext().getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

                if (lastKnownLocation != null) {

                    currentLatLng = new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 14));
                    // 添加自己的標記，顏色為綠色
                    /*googleMap.addMarker(new MarkerOptions()
                            .position(currentLatLng)
                            .title("您的位置").visible(true)
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
                    // 创建 BitmapDescriptor 对象
                    BitmapDescriptor icon = BitmapDescriptorFactory.fromResource(R.drawable.baseline_emoji_people_24);
// 在地图上添加标记
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(currentLatLng)
                            .icon(icon)
                            .title("Your Marker Title");
                    googleMap.addMarker(markerOptions);
*/

                    int vectorDrawableResourceId = R.drawable.baseline_emoji_people_24;

// 获取矢量图标
                    Drawable vectorDrawable = ContextCompat.getDrawable(getContext(), vectorDrawableResourceId);

// 将矢量图标转换为位图
                    BitmapDescriptor icon = getMarkerIconFromDrawable(vectorDrawable);

// 在地图上添加标记
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(currentLatLng)
                            .icon(icon)
                            .title("Your Marker Title");
                    googleMap.addMarker(markerOptions);

                    addMarkersToMap(googleMap, cafes, currentLatLng);
                } else {
                    Toast.makeText(requireContext(), "無法獲取目前位置", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    // 辅助方法
    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void addMarkersToMap(GoogleMap googleMap, ArrayList<Cafe> cafes, LatLng currentLatLng) {
        int dis=1000;
        if (currentLatLng != null) {
            LatLng centerLatLng = currentLatLng;
            for (Cafe cafe : cafes) {
                LatLng cafeLatLng = new LatLng(Double.parseDouble(cafe.getLatitude()), Double.parseDouble(cafe.getLongitude()));

                float[] distanceResult = new float[1];
                Location.distanceBetween(centerLatLng.latitude, centerLatLng.longitude, cafeLatLng.latitude, cafeLatLng.longitude, distanceResult);

                // 如果距離小於400米，則顯示標記
                if (distanceResult[0] <= dis) {
                    MarkerOptions markerOptions = new MarkerOptions().position(cafeLatLng).title(cafe.getName());

                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE));

                    Marker marker = googleMap.addMarker(markerOptions);


                    // 設置自定義InfoWindow
                    marker.setTag(cafe);
                    googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                        @Override
                        public View getInfoWindow(Marker marker) {
                            return null;
                        }

                        @Override
                        public View getInfoContents(Marker marker) {
                            View view = getLayoutInflater().inflate(R.layout.marker_info_window, null);

                            Cafe cafe = (Cafe) marker.getTag();
                            TextView titleTextView = view.findViewById(R.id.tvCafeName);
                            ImageView navigateImageView = view.findViewById(R.id.ivNavigation);

                            titleTextView.setText("店名：" + cafe.getName());

                            return view;
                        }
                    });

                    // 設置InfoWindow點擊事件
                    googleMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
                        @Override
                        public void onInfoWindowClick(Marker marker) {
                            if (marker.getPosition().latitude != currentLatLng.latitude && marker.getPosition().longitude != currentLatLng.longitude) {
                                startNavigation(marker.getPosition().latitude, marker.getPosition().longitude);
                            }
                        }
                    });
                }
            }

            // 添加Circle標記表示400米範圍
            CircleOptions circleOptions = new CircleOptions()
                    .center(centerLatLng)
                    .radius(dis)  // 以米為單位
                    .strokeWidth(0f)
                    .fillColor(0x55FFFF00);  // 半透明藍色填充色
            googleMap.addCircle(circleOptions);
        }
    }

    // 添加以下方法以啟動導航
    private void startNavigation(double latitude, double longitude) {
        // 使用Intent啟動地圖應用程序，導航到選定的位置
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps");

        if (mapIntent.resolveActivity(requireContext().getPackageManager()) != null) {
            startActivity(mapIntent);
        } else {
            Toast.makeText(requireContext(), "未找到地圖應用程序", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                showCurrentLocation();
            } else {
                Toast.makeText(requireContext(), "需要位置權限以顯示地圖", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private class FetchCafeDataTask extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            try {
                // 連接 API 並取得 JSON 字串，這裡使用你原本的方法
                return fetchDataFromApi(API_URL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (isAdded() && result != null) {
                try {
                    JSONArray cafesArray = new JSONArray(result);

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

                    addMarkersToMap(googleMap, cafes, currentLatLng);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        protected void onCancelled() {
            // 在任務被取消時的處理
        }
    }

    private String fetchDataFromApi(String apiUrl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(apiUrl)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            return response.body().string();
        }
    }
}