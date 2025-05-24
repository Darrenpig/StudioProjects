package com.example.cameraapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;
import com.google.mlkit.vision.face.FaceLandmark;
import java.io.File;
import java.util.List;

public class FaceDetectionActivity extends AppCompatActivity {
    private ImageView ivFullImage;
    private Bitmap originalBitmap;
    private String photoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        ivFullImage = findViewById(R.id.iv_full_image);

        // 获取照片路径
        photoPath = getIntent().getStringExtra("photo_uri");
        if (photoPath == null) {
            Toast.makeText(this, "照片路径为空", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        // 加载原始图片
        loadImage();

        // 进行人脸检测
        detectFaces();
    }

    // 加载原始图片
    private void loadImage() {
        try {
            // 修改URI解析方式
            Uri uri = Uri.parse(photoPath);
            InputStream inputStream;
            if (photoPath.startsWith("file://")) {
                // 处理file://开头的URI
                inputStream = new FileInputStream(new File(uri.getPath()));
            } else {
                // 处理content://开头的URI
                inputStream = getContentResolver().openInputStream(uri);
            }
            
            if (inputStream != null) {
                originalBitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
                ivFullImage.setImageBitmap(originalBitmap);
            } else {
                Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show();
                finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "加载图片失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // 进行人脸检测
    private void detectFaces() {
        // 配置人脸检测器
        FaceDetectorOptions options =
                new FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_ALL)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                        .build();

        FaceDetector detector = FaceDetection.getClient(options);

        InputImage image = InputImage.fromBitmap(originalBitmap, 0);

        detector.process(image)
                .addOnSuccessListener(new com.google.android.gms.tasks.OnSuccessListener<List<Face>>() {
                    @Override
                    public void onSuccess(List<Face> faces) {
                        // 处理检测到的人脸
                        processFaces(faces);
                    }
                })
                .addOnFailureListener(new com.google.android.gms.tasks.OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // 处理检测失败的情况
                        Toast.makeText(FaceDetectionActivity.this, "人脸检测失败", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // 处理检测到的人脸
    private void processFaces(List<Face> faces) {
        if (faces.isEmpty()) {
            Toast.makeText(this, "未检测到人脸", Toast.LENGTH_SHORT).show();
            return;
        }

        // 加载墨镜图片
        Bitmap sunglassesBitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.sunglasses)).getBitmap();

        // 创建一个可变的Bitmap用于绘制
        Bitmap resultBitmap = originalBitmap.copy(originalBitmap.getConfig(), true);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();

        for (Face face : faces) {
            // 获取左右眼的关键点
            FaceLandmark leftEyeLandmark = face.getLandmark(FaceLandmark.LEFT_EYE);
            FaceLandmark rightEyeLandmark = face.getLandmark(FaceLandmark.RIGHT_EYE);

            if (leftEyeLandmark != null && rightEyeLandmark != null) {
                PointF leftEye = leftEyeLandmark.getPosition();
                PointF rightEye = rightEyeLandmark.getPosition();

                // 计算人脸宽度
                float faceWidth = rightEye.x - leftEye.x;

                // 调整墨镜的大小和位置
                Matrix matrix = new Matrix();
                matrix.postScale(faceWidth / sunglassesBitmap.getWidth(), faceWidth / sunglassesBitmap.getWidth());
                matrix.postTranslate(leftEye.x - sunglassesBitmap.getWidth() / 2, leftEye.y - sunglassesBitmap.getHeight() / 2);

                // 绘制墨镜
                canvas.drawBitmap(sunglassesBitmap, matrix, paint);
            }
        }

        // 显示结果图片
        ivFullImage.setImageBitmap(resultBitmap);
    }
}