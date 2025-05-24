package com.example.cameraapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SavedFilterActivity extends AppCompatActivity {
    private GridView gvSavedImages;
    private GalleryAdapter adapter;
    private List<String> savedImagePaths = new ArrayList<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_filter);

        gvSavedImages = findViewById(R.id.gv_saved_images);
        adapter = new GalleryAdapter(this, savedImagePaths);
        gvSavedImages.setAdapter(adapter);

        // 加载已保存的滤镜图片
        loadSavedImages();

        // 图片点击事件
        gvSavedImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 打开图片查看器
                Intent intent = new Intent(SavedFilterActivity.this, ImageViewerActivity.class);
                intent.putExtra("image_path", savedImagePaths.get(position));
                startActivity(intent);
            }
        });
    }

    // 加载已保存的图片
    private void loadSavedImages() {
        // 获取应用保存图片的目录
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

        // 查找所有以"sunglass_"开头的jpg文件
        File[] files = storageDir.listFiles((dir, name) ->
                name.startsWith("sunglass_") && name.endsWith(".jpg"));

        if (files != null) {
            for (File file : files) {
                savedImagePaths.add(file.getAbsolutePath());
            }

            // 更新适配器
            adapter.notifyDataSetChanged();

            if (savedImagePaths.isEmpty()) {
                Toast.makeText(this, "没有找到已保存的滤镜图片", Toast.LENGTH_SHORT).show();
            }
        }
    }
}