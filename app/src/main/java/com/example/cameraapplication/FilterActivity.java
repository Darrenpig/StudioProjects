package com.example.cameraapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FilterActivity extends AppCompatActivity {
    private ImageView ivPreview;
    private Button btnSave, btnBack;
    private SeekBar sbIntensity;
    private Bitmap originalBitmap;
    private String photoPath;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);

        // 获取照片路径
        photoPath = getIntent().getStringExtra("photo_uri");
        if (photoPath == null) {
            Toast.makeText(this, "照片路径为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 初始化UI
        ivPreview = findViewById(R.id.iv_preview);
        btnSave = findViewById(R.id.btn_save);
        btnBack = findViewById(R.id.btn_back);
        sbIntensity = findViewById(R.id.sb_intensity);

        // 加载原始图片
        loadImage();

        // 返回按钮
        btnBack.setOnClickListener(v -> finish());

        // 保存按钮
        btnSave.setOnClickListener(v -> applyFilterAndSave());

        // 滤镜强度调整
        sbIntensity.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 实时预览滤镜效果
                applyFilter(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }

    // 加载原始图片
    private void loadImage() {
        try {
            File file = new File(Uri.parse(photoPath).getPath());
            originalBitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            ivPreview.setImageBitmap(originalBitmap);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加载图片失败", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 应用滤镜效果
    private void applyFilter(int intensity) {
        if (originalBitmap == null) return;

        // 简单实现：调整亮度作为示例滤镜效果
        Bitmap filteredBitmap = adjustBrightness(originalBitmap, intensity);
        ivPreview.setImageBitmap(filteredBitmap);
    }

    // 调整亮度的滤镜效果
    private Bitmap adjustBrightness(Bitmap src, int brightness) {
        // 将亮度百分比转换为实际调整值 (-100 到 100)
        float brightnessFactor = brightness / 50.0f - 1.0f;

        // 创建一个相同尺寸的可变位图
        Bitmap result = src.copy(src.getConfig(), true);

        // 简单滤镜：调整亮度
        for (int y = 0; y < result.getHeight(); y++) {
            for (int x = 0; x < result.getWidth(); x++) {
                int pixel = result.getPixel(x, y);

                // 获取RGB分量
                int alpha = (pixel >> 24) & 0xff;
                int red = (pixel >> 16) & 0xff;
                int green = (pixel >> 8) & 0xff;
                int blue = pixel & 0xff;

                // 调整亮度
                red = Math.min(255, Math.max(0, (int) (red + red * brightnessFactor)));
                green = Math.min(255, Math.max(0, (int) (green + green * brightnessFactor)));
                blue = Math.min(255, Math.max(0, (int) (blue + blue * brightnessFactor)));

                // 重新组合像素
                result.setPixel(x, y, (alpha << 24) | (red << 16) | (green << 8) | blue);
            }
        }

        return result;
    }

    // 应用滤镜并保存
    private void applyFilterAndSave() {
        try {
            // 应用最终滤镜效果
            Bitmap finalBitmap = adjustBrightness(originalBitmap, sbIntensity.getProgress());

            // 保存图片到相册
            String savedPath = saveImageToGallery(finalBitmap);

            if (savedPath != null) {
                Toast.makeText(this, "图片已保存到相册", Toast.LENGTH_SHORT).show();

                // 跳转到已保存页面
                Intent intent = new Intent(this, SavedFilterActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "保存失败", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "处理图片时出错", Toast.LENGTH_SHORT).show();
        }
    }

    // 保存图片到系统相册
    private String saveImageToGallery(Bitmap bitmap) {
        // 保存到公共图片目录
        String fileName = "sunglass_" + System.currentTimeMillis() + ".jpg";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File imageFile = new File(storageDir, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();

            // 通知系统刷新相册
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri contentUri = Uri.fromFile(imageFile);
            mediaScanIntent.setData(contentUri);
            sendBroadcast(mediaScanIntent);

            return imageFile.getAbsolutePath();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
