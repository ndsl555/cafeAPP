package com.example.cafe;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.cafe.DB.CoffeDatabase;
import com.example.cafe.DB.MyApp;
import com.example.cafe.Dao.BarDao;
import com.example.cafe.Entity.Bar;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.oned.Code39Writer;

import java.util.concurrent.Executors;

public class BarcodeFragment extends Fragment {

    private Switch brightnessSwitch;
    private CoffeDatabase coffeeDatabase;
    private BarDao barDao;
    private ImageView imageView;
    private FloatingActionButton fab;
    private TextView codenum;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_barcode, container, false);

        coffeeDatabase = MyApp.coffeeDatabase;
        barDao = coffeeDatabase.barDao();

        codenum = root.findViewById(R.id.code_num);
        imageView = root.findViewById(R.id.image_barcode);
        fab = root.findViewById(R.id.floatingActionButton);

        fab.setOnClickListener(view -> showInputDialog());
        brightnessSwitch = root.findViewById(R.id.light);

        brightnessSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adjustBrightness(true);
            } else {
                adjustBrightness(false);
            }
        });

        displayLatestBarcode();
        return root;
    }

    private void adjustBrightness(boolean enable) {
        WindowManager.LayoutParams layoutParams = getActivity().getWindow().getAttributes();
        layoutParams.screenBrightness = enable ? 1.0f : -1.0f;
        getActivity().getWindow().setAttributes(layoutParams);
    }

    private void showInputDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("輸入字串");

        final EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("確定", (dialog, which) -> {
            String barcodeData = input.getText().toString();
            if (!barcodeData.isEmpty()) {
                // 如果barcodeData前面有斜杠，则删除斜杠
                if (barcodeData.startsWith("/")) {
                    barcodeData = barcodeData.substring(1); // 从索引1开始截取，即删除第一个字符（斜杠）
                }
                barcodeData = barcodeData.toUpperCase();
                generateBarcodeAndSave(barcodeData);
            } else {
                Toast.makeText(requireContext(), "未輸入", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("取消", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void generateBarcodeAndSave(String barcodeData) {
        Executors.newSingleThreadExecutor().execute(() -> {
            Bar newBar = new Bar(barcodeData);
            barDao.insertBar(newBar);

            new Handler(Looper.getMainLooper()).post(() -> {
                displayLatestBarcode();
                Toast.makeText(requireContext(), "已保存", Toast.LENGTH_SHORT).show();
            });
        });
    }

    private void displayLatestBarcode() {
        Executors.newSingleThreadExecutor().execute(() -> {
            Bar latestBar = barDao.getLatestBar();
            if (latestBar != null) {
                String barcodeData = latestBar.getBarcodeData();
                Bitmap barcodeBitmap = generateCode39Barcode('/' + barcodeData);
                codenum.setText('/' + barcodeData);

                new Handler(Looper.getMainLooper()).post(() -> imageView.setImageBitmap(barcodeBitmap));
            }
        });
    }

    private Bitmap generateCode39Barcode(String data) {
        Code39Writer writer = new Code39Writer();
        BitMatrix bitMatrix = writer.encode(data, BarcodeFormat.CODE_39, 700, 200);
        int width = bitMatrix.getWidth();
        int height = bitMatrix.getHeight();

        Bitmap barcodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(barcodeBitmap);

        Paint paint = new Paint();
        paint.setColor(Color.parseColor("#754C00"));

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (bitMatrix.get(x, y)) {
                    canvas.drawPoint(x, y, paint);
                }
            }
        }

        return barcodeBitmap;
    }
}
