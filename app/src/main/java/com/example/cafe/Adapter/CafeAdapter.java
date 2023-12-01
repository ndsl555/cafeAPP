package com.example.cafe.Adapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cafe.DataClass.Cafe;
import com.example.cafe.Dao.CafeShopDao;
import com.example.cafe.Entity.CafeShop;
import com.example.cafe.AsyncTask.InsertCafeShopTask;
import com.example.cafe.DB.MyApp;
import com.example.cafe.R;

import java.util.ArrayList;
import java.util.Objects;

public class CafeAdapter extends RecyclerView.Adapter<CafeAdapter.CafeViewHolder> {

    private ArrayList<Cafe> cafes;
    private Context context;

    public void setData(ArrayList<Cafe> cafes) {
        this.cafes = cafes;
    }

    public CafeAdapter(ArrayList<Cafe> cafes, Context context) {
        this.cafes = cafes;
        this.context = context;
    }

    @NonNull
    @Override
    public CafeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cafe, parent, false);
        return new CafeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CafeViewHolder holder, int position) {
        Cafe cafe = cafes.get(position);
        holder.bind(cafe);

        // Add click listener to the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a custom Dialog here
                showCafeDetailDialog(cafe);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cafes.size();
    }

    private void showCafeDetailDialog(Cafe cafe) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(cafe.getName()); // Set the title of the dialog

        // Inflate the custom dialog layout
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_cafe_detail, null);
        builder.setView(dialogView);

        // Find the RatingBars in the custom dialog layout
        RatingBar wifiRatingBar = dialogView.findViewById(R.id.ratingBarWifi);
        RatingBar seatRatingBar = dialogView.findViewById(R.id.ratingBarSeat);
        RatingBar quietRatingBar = dialogView.findViewById(R.id.ratingBarQuiet);
        RatingBar tastyRatingBar = dialogView.findViewById(R.id.ratingBarTasty);
        RatingBar cheapRatingBar = dialogView.findViewById(R.id.ratingBarCheap);
        RatingBar musicRatingBar = dialogView.findViewById(R.id.ratingBarMusic);
        // Find the TextViews in the custom dialog layout
        TextView addressTextView = dialogView.findViewById(R.id.textViewAddress);
        //TextView urlTextView = dialogView.findViewById(R.id.textViewUrl);
        TextView limitedTimeTextView = dialogView.findViewById(R.id.textViewLimitedTime);
        TextView socketTextView = dialogView.findViewById(R.id.textViewSocket);
        TextView standingDeskTextView = dialogView.findViewById(R.id.textViewStandingDesk);
        TextView mrtTextView = dialogView.findViewById(R.id.textViewMrt);
        TextView openTimeTextView = dialogView.findViewById(R.id.textViewOpenTime);
        // Set the values of RatingBars based on cafe attributes
        wifiRatingBar.setRating(Float.valueOf(cafe.getWifi()));
        seatRatingBar.setRating(Float.valueOf(cafe.getSeat()));
        quietRatingBar.setRating(Float.valueOf(cafe.getQuiet()));
        tastyRatingBar.setRating(Float.valueOf(cafe.getTasty()));
        cheapRatingBar.setRating(Float.valueOf(cafe.getCheap()));
        musicRatingBar.setRating(Float.valueOf(cafe.getMusic()));
        // Set the values of TextViews based on cafe attributes
        addressTextView.setText(cafe.getAddress());
        //urlTextView.setText(cafe.getUrl());
        limitedTimeTextView.setText(cafe.getLimitedTime());
        socketTextView.setText(cafe.getSocket());
        standingDeskTextView.setText(cafe.getStandingDesk());
        mrtTextView.setText(cafe.getMrt());

        openTimeTextView.setText(cafe.getOpenTime());
        // Create and show the dialog

        // Create and set the buttons for the dialog
        builder.setNeutralButton("確定", (dialog, which) -> {
            // Cancel button clicked, close the dialog
            dialog.dismiss();
        });

        builder.setPositiveButton("加入口袋名單", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Save button clicked, implement your logic to save the cafe name to the Room database
                // Save button clicked, implement your logic to save the cafe data to the ZoomDB
                CafeShopDao cafeShopDao = MyApp.coffeeDatabase.cafeShopDao();

                CafeShop cafeShop = new CafeShop();
                cafeShop.setName(cafe.getName());
                cafeShop.setCity(cafe.getCity());
                cafeShop.setWifi(Float.parseFloat(cafe.getWifi()));
                cafeShop.setSeat(Float.parseFloat(cafe.getSeat()));
                cafeShop.setQuiet(Float.parseFloat(cafe.getQuiet()));
                cafeShop.setTasty(Float.parseFloat(cafe.getTasty()));
                cafeShop.setCheap(Float.parseFloat(cafe.getCheap()));
                cafeShop.setMusic(Float.parseFloat(cafe.getMusic()));
                cafeShop.setLatitude(Double.parseDouble(String.valueOf(cafe.getLatitude())));
                cafeShop.setLongitude(Double.parseDouble(String.valueOf(cafe.getLongitude())));
                cafeShop.setAddress(cafe.getAddress());
                cafeShop.setSocket(cafe.getSocket());
                cafeShop.setLimitedTime(cafe.getLimitedTime());
                cafeShop.setStandingDesk(cafe.getStandingDesk());
                cafeShop.setMrt(cafe.getMrt());
                cafeShop.setOpenTime(cafe.getOpenTime());
                // 设置其他字段

                // Insert the CafeShop data into the ZoomDB
                new InsertCafeShopTask(cafeShopDao).execute(cafeShop);
                Toast.makeText(context, "將"+cafe.getName()+"加入口袋名單", Toast.LENGTH_SHORT).show();
                //cafeShopDao.insert(cafeShop);
                dialog.dismiss();
            }
        });

        // Create the button for navigation
        builder.setNegativeButton("導航", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Handle navigation button click
                navigateToCafe(cafe.getLatitude(), cafe.getLongitude());
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void navigateToCafe(String latitude, String longitude) {
        // Create an Intent to open a map app with the specified latitude and longitude
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage("com.google.android.apps.maps"); // Specify the package to use Google Maps
        if (mapIntent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(mapIntent);
        } else {
            // Handle the case where Google Maps is not installed
            Toast.makeText(context, "Google Maps is not installed", Toast.LENGTH_SHORT).show();
        }
    }

    public static class CafeViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView,searchAveTextview,searchMrtTextview,searchTimeTextview;

        public CafeViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.textViewCafeName);
            searchMrtTextview=itemView.findViewById(R.id.SearchMrtTextView);
            searchTimeTextview=itemView.findViewById(R.id.SearchTimeTextView);
            searchAveTextview=itemView.findViewById(R.id.SearchAveTextView);
        }

        public void bind(Cafe cafe) {
            float ave= Float.parseFloat(cafe.getCheap())+ Float.parseFloat(cafe.getMusic())+Float.parseFloat(cafe.getQuiet())+Float.parseFloat(cafe.getTasty())+ Float.parseFloat(cafe.getWifi())+Float.parseFloat(cafe.getSeat());
            String originalString = cafe.getName();
            if(cafe.getName().length()>12){
                String stringWithNewLine = originalString.substring(0, 13) + '\n' + originalString.substring(13);
                originalString=stringWithNewLine;
            }
            // 设置默认状态，确保在不符合条件时清除文本和背景
            searchMrtTextview.setText("");
            searchMrtTextview.setBackground(null);
            // 设置默认状态，确保在不符合条件时清除文本和背景
            searchTimeTextview.setText("");
            searchTimeTextview.setBackground(null);


            if((!cafe.getMrt().trim().isBlank())&&(!cafe.getMrt().trim().isEmpty())){
                System.out.println(cafe.getMrt());
                searchMrtTextview.setText("捷運");
            }
            if(Objects.equals(cafe.getLimitedTime(), "yes")){
                searchTimeTextview.setText("限時");
                //searchTimeTextview.setText(cafe.getLimitedTime());
            }
            nameTextView.setText(originalString);
            searchAveTextview.setText(String.valueOf(Math.round((ave/6) * 10.0) / 10.0));
            // Add other bindings as needed
        }
    }
}
