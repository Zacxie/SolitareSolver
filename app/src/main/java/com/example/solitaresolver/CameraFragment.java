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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class CameraFragment extends Fragment implements View.OnClickListener {

    private static final int pic_id = 123;
    String currentPhotoPath;

    public Bitmap edgeDetectionBitmap;
    private Button edgeDetectionButton, openCameraButton;
    private ImageView imageView;
    Bitmap photo;
    BitmapFactory.Options bmOptions = new BitmapFactory.Options();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_camera, container, false);

        imageView = root.findViewById(R.id.imageView4);

        openCameraButton = root.findViewById(R.id.openCameraButton);
        edgeDetectionButton = root.findViewById(R.id.button2);
        edgeDetectionButton.setOnClickListener(this);

        openCameraButton.setOnClickListener(this);
        OpenCVLoader.initDebug();

        // Permissions for Android 6+
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.CAMERA},
                1);

        return root;
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
                    Toast.makeText(getActivity(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id) {
            int targetW = imageView.getWidth();
            int targetH = imageView.getHeight();

            // Get the dimensions of the bitmap

            bmOptions.inJustDecodeBounds = true;

            BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            photo = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
            imageView.setImageBitmap(photo);

        }
    }


    public void edgeDetection(View v) {
        Mat Rgba = new Mat();
        Mat grayMat = new Mat();
        final List<MatOfPoint> points = new ArrayList<>();
        final Mat hierarchy = new Mat();



        bmOptions.inDither = false;
        bmOptions.inSampleSize = 4;

        int width = photo.getWidth();
        int height = photo.getHeight();

        edgeDetectionBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);

        //Edge detection
        Utils.bitmapToMat(photo, Rgba);

        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_RGB2BGR);
        Imgproc.cvtColor(Rgba, grayMat, Imgproc.COLOR_RGB2GRAY);
        Imgproc.GaussianBlur(Rgba, grayMat, new Size(13,13), 0);
        Imgproc.Canny(Rgba, grayMat, 100, 80);
        //Imgproc.dilate(Rgba, grayMat, new Mat(), new Point(-1, -1), 2);



        Imgproc.findContours(grayMat, points, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE);

        for(int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
            MatOfPoint matOfPoint = points.get(idx);
            Rect rect = Imgproc.boundingRect(matOfPoint);
            Imgproc.rectangle(Rgba, rect.tl(), rect.br(), new Scalar(255, 0, 0),2);
        }

        Utils.matToBitmap(Rgba, edgeDetectionBitmap);

        System.out.println(points.toString());
        imageView.setImageBitmap(edgeDetectionBitmap);
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(getContext().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getContext(),
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, pic_id);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v == edgeDetectionButton) {
            edgeDetection(v);
        }
        if (v == openCameraButton) {
            dispatchTakePictureIntent();


        }
    }

}