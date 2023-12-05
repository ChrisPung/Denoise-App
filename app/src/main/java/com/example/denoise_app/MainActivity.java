package com.example.denoise_app;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BlendModeColorFilter;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.provider.MediaStore;
import android.util.Log;
import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.denoise_app.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import org.opencv.android.Camera2Renderer;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int MAX_KERNEL_LENGTH = 31;
    Button select, camera, Avg_Filter, Brighten;
    ImageView imageView;
    Bitmap bitmap;
    Mat mat;
    int SELECT_CODE = 100;
    int CAMERA_CODE = 101;
    int CAMERA_PERMISSION = 102;
    int AVG_CODE = 103;
    int BRIGHT_CODE = 104;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OpenCVLoader.initDebug();   // Needed for app to function.
        getPermission();            // Gets Camera Permission access.

        // Finds IDs of buttons or image in activity_main.xml.
        camera = findViewById(R.id.camera);
        select = findViewById(R.id.select);
        imageView = findViewById(R.id.imageView);

        Avg_Filter = findViewById(R.id.Avg_Filter);

        Brighten = findViewById(R.id.Brighten);

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, SELECT_CODE);
            }
        });

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_CODE);
            }
        });

        Avg_Filter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, AVG_CODE);
            }
        });

        Brighten.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, BRIGHT_CODE);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == SELECT_CODE && data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);

                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if(requestCode == CAMERA_CODE && data != null){
            bitmap = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);

            mat = new Mat();
            Utils.bitmapToMat(bitmap, mat);

        }
        if(requestCode == AVG_CODE && data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);

                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);

                // Insert change code here.
                for (int i = 1; i < MAX_KERNEL_LENGTH; i = i + 2) {
                    Imgproc.blur(mat, mat, new Size(i, i), new Point(-1, -1));
                }

                Utils.matToBitmap(mat, bitmap);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(requestCode == BRIGHT_CODE && data != null){
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), data.getData());
                imageView.setImageBitmap(bitmap);

                mat = new Mat();
                Utils.bitmapToMat(bitmap, mat);

                // Insert change code here.

                int Contrast = 2;
                int Brightness = 15;

                Core.convertScaleAbs(mat, mat, Contrast, Brightness);

                Utils.matToBitmap(mat, bitmap);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    void getPermission(){
        if(checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == CAMERA_PERMISSION && grantResults.length > 0){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                getPermission();
            }
        }
    }
}