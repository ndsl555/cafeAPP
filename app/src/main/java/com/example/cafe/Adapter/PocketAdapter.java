package com.example.cafe.Adapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cafe.Entity.CafeShop;
import com.example.cafe.R;
import com.example.cafe.AsyncTask.RemoveCafeAsyncTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PocketAdapter extends RecyclerView.Adapter<PocketAdapter.PocketViewHolder> {

    private ArrayList<CafeShop> cafeShops;
    private Context context;

    // 添加此方法以更新 adapter 中的數據

    public void clear() {
        cafeShops.clear();
        notifyDataSetChanged();
    }
    public void setData(List<CafeShop> cafeShops) {
        this.cafeShops = new ArrayList<>(cafeShops);
        notifyDataSetChanged();
    }

    public PocketAdapter(ArrayList<CafeShop> cafeShops, Context context) {
        this.cafeShops = cafeShops;
        this.context = context;
    }

    @NonNull
    @Override
    public PocketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_pocket, parent, false);
        return new PocketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PocketViewHolder holder, int position) {
        CafeShop cafeShop = cafeShops.get(position);
        holder.bind(cafeShop);

        // Add click listener to the item view
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open a custom Dialog here
                showCafeShopDetailDialog(cafeShop);
            }
        });
    }

    @Override
    public int getItemCount() {
        return cafeShops.size();
    }

    private void showCafeShopDetailDialog(CafeShop cafeShop) {
        // Create a dialog builder
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(cafeShop.getName()); // Set the title of the dialog

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
        TextView limitedTimeTextView = dialogView.findViewById(R.id.textViewLimitedTime);
        TextView socketTextView = dialogView.findViewById(R.id.textViewSocket);
        TextView standingDeskTextView = dialogView.findViewById(R.id.textViewStandingDesk);
        TextView mrtTextView = dialogView.findViewById(R.id.textViewMrt);
        TextView openTimeTextView = dialogView.findViewById(R.id.textViewOpenTime);

        // Set the values of RatingBars based on cafe attributes
        wifiRatingBar.setRating(cafeShop.getWifi());
        seatRatingBar.setRating(cafeShop.getSeat());
        quietRatingBar.setRating(cafeShop.getQuiet());
        tastyRatingBar.setRating(cafeShop.getTasty());
        cheapRatingBar.setRating(cafeShop.getCheap());
        musicRatingBar.setRating(cafeShop.getMusic());

        // Set the values of TextViews based on cafe attributes
        addressTextView.setText(cafeShop.getAddress());
        limitedTimeTextView.setText(cafeShop.getLimitedTime());
        socketTextView.setText(cafeShop.getSocket());
        standingDeskTextView.setText(cafeShop.getStandingDesk());
        mrtTextView.setText(cafeShop.getMrt());
        openTimeTextView.setText(cafeShop.getOpenTime());

        // Create and show the dialog
        builder.setNeutralButton("確定", (dialog, which) -> dialog.dismiss());

        builder.setPositiveButton("從口袋名單移除", (dialog, which) -> {
            // Remove button clicked, implement your logic to remove the cafe data from the database
            // 點擊移除按鈕，後台執行數據庫操作
            new RemoveCafeAsyncTask(cafeShop).execute();
            cafeShops.remove(cafeShop);
            notifyDataSetChanged();
            Toast.makeText(context, "將"+cafeShop.getName()+"從口袋名單移除", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        // Create the button for navigation
        builder.setNegativeButton("導航", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println(cafeShop.getLatitude()+cafeShop.getLongitude());
                // Handle navigation button click
                navigateToCafe(String.valueOf(cafeShop.getLatitude()), String.valueOf(cafeShop.getLongitude()));
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

    public static class PocketViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView,pocketAveTextview,pocketMrtTextview,pocketTimeTextview;

        public PocketViewHolder(@NonNull View itemView) {
            super(itemView);
            pocketAveTextview=itemView.findViewById(R.id.PocketAveTextView);
            pocketMrtTextview=itemView.findViewById(R.id.PocketMrtTextView);
            pocketTimeTextview=itemView.findViewById(R.id.PocketTimeTextView);
            nameTextView = itemView.findViewById(R.id.textViewCafeNamePocket);
        }

        public void bind(CafeShop cafeShop) {
            float ave=(cafeShop.getCheap()+ cafeShop.getMusic()+cafeShop.getQuiet()+ cafeShop.getTasty()+ cafeShop.getWifi()+cafeShop.getSeat())/6;

            String originalString = cafeShop.getName();
            if(cafeShop.getName().length()>12){
                String stringWithNewLine = originalString.substring(0, 13) + '\n' + originalString.substring(13);
                originalString=stringWithNewLine;
            }
            // 设置默认状态，确保在不符合条件时清除文本和背景
            pocketMrtTextview.setText("");
            pocketMrtTextview.setBackground(null);
            // 设置默认状态，确保在不符合条件时清除文本和背景
            pocketTimeTextview.setText("");
            pocketTimeTextview.setBackground(null);


            if((!cafeShop.getMrt().trim().isBlank())&&(!cafeShop.getMrt().trim().isEmpty())){
                System.out.println(cafeShop.getMrt());
                pocketMrtTextview.setText("捷運");
            }
            if(Objects.equals(cafeShop.getLimitedTime(), "yes")){
                pocketTimeTextview.setText("限時");
                //searchTimeTextview.setText(cafe.getLimitedTime());
            }
            nameTextView.setText(originalString);


            nameTextView.setText(cafeShop.getName());
            pocketAveTextview.setText(String.valueOf(Math.round(ave * 10.0) / 10.0));
            // Add other bindings as needed
        }
    }
}
