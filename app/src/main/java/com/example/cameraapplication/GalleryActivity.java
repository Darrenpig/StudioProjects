package com.example.cameraapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    private GridView gvImages;
    private GalleryAdapter adapter;
    private List<String> imagePaths = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        gvImages = findViewById(R.id.gv_images);
        adapter = new GalleryAdapter(this, imagePaths);
        gvImages.setAdapter(adapter);

        // 获取所有图片
        loadImages();

        // 图片点击事件
        // 图片点击事件
        gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 打开人脸检测页面，传入图片路径
                String imagePath = imagePaths.get(position);
                Intent intent = new Intent(GalleryActivity.this, FaceDetectionActivity.class);
                // 使用 Uri.fromFile 创建 URI
                Uri imageUri = Uri.fromFile(new File(imagePath));
                intent.putExtra("photo_uri", imageUri.toString());
                startActivity(intent);
            }
        });
    }

    // 加载图片
    private void loadImages() {
        // 定义查询的字段
        String[] projection = {MediaStore.Images.Media.DATA};

        // 按日期降序排序，最新的图片在前面
        String sortOrder = MediaStore.Images.Media.DATE_ADDED + " DESC";

        // 查询外部存储中的图片
        Cursor cursor = getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                projection,
                null,
                null,
                sortOrder
        );

        if (cursor != null) {
            while (cursor.moveToNext()) {
                // 获取图片路径
                String imagePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA));
                imagePaths.add(imagePath);
            }
            cursor.close();

            // 更新适配器
            adapter.notifyDataSetChanged();

            if (imagePaths.isEmpty()) {
                Toast.makeText(this, "没有找到图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}