package com.example.cameraapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class ImageViewerActivity extends AppCompatActivity {
    private ImageView ivFullImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ivFullImage = findViewById(R.id.iv_full_image);

        Intent intent = getIntent();
        String imagePath = intent.getStringExtra("image_path");

        if (imagePath != null) {
            // 获取屏幕尺寸作为目标尺寸
            int targetWidth = getResources().getDisplayMetrics().widthPixels;
            int targetHeight = getResources().getDisplayMetrics().heightPixels;

            // 加载并缩放图片
            Bitmap bitmap = decodeSampledBitmapFromFile(imagePath, targetWidth, targetHeight);
            ivFullImage.setImageBitmap(bitmap);
        } else {
            finish();
        }
    }
    private Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {
        // 第一次解析用于获取图片尺寸，不分配内存
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // 计算缩放比例
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // 第二次解析图片，使用计算出的缩放比例
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // 原始图片尺寸
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            // 计算图片高度和宽度与目标尺寸的比例
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // 选择最小的比例作为inSampleSize值，这样可以保证最终图片尺寸
            // 同时大于或等于目标尺寸
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }

}