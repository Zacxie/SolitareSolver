package com.example.solitaresolver;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int pic_id = 123;
    String currentPhotoPath;

    public Bitmap edgeDetectionBitmap;
    private Button gallaryButton, edgeDetectionButton, openCameraButton;
    private ImageView imageView;
    Bitmap photo;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        imageView = findViewById(R.id.imageView4);
        gallaryButton = findViewById(R.id.button);
        openCameraButton = findViewById(R.id.openCameraButton);
        edgeDetectionButton = findViewById(R.id.button2);
        edgeDetectionButton.setOnClickListener(this);
        gallaryButton.setOnClickListener(this);
        openCameraButton.setOnClickListener(this);
        OpenCVLoader.initDebug();


        // Permissions for Android 6+
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.CAMERA},
                1);


    }


    @Override
    public void onResume() {
        super.onResume();


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted.
                } else {
                    // permission denied.
                    Toast.makeText(MainActivity.this, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }


    public void openGallary(View v) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW/targetW, photoH/targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            imageView.setImageBitmap(bitmap);

        }
    }

    public void edgeDetection(View v) {
        Mat Rgba = new Mat();
        Mat grayMat = new Mat();

        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inDither = false;
        o.inSampleSize = 4;

        int width = photo.getWidth();
        int height = photo.getHeight();

        edgeDetectionBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        //Edge detection
        Utils.bitmapToMat(photo, Rgba);
        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.Canny(Rgba, grayMat, 100, 80);

        Utils.matToBitmap(grayMat, edgeDetectionBitmap);

        imageView.setImageBitmap(edgeDetectionBitmap);
    }

    @Override
    public void onClick(View v) {
        if (v == gallaryButton) {
            openGallary(v);
        }
        if (v == edgeDetectionButton) {
            edgeDetection(v);
        }
        if (v == openCameraButton) {

            dispatchTakePictureIntent();


        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, pic_id);
            }
        }
    }


}
