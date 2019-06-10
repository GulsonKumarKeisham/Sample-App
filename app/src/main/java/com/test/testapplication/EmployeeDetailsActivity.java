package com.test.testapplication;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EmployeeDetailsActivity extends AppCompatActivity {

    private static final int REQUEST_TAKE_PHOTO = 9001;
    private static final int PERMISSIONS_MULTIPLE_REQUEST = 9002;
    private ImageView captureImgView;
    private String currentPhotoPath, timeStamp;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employee_details);

        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        //receiving particular employee data as string array object
        String[] details = getIntent().getStringArrayExtra("employee_details");

        TextView employeeNameTv = findViewById(R.id.employeeNameTv);
        TextView empId = findViewById(R.id.empId);
        TextView designation = findViewById(R.id.designation);
        TextView city = findViewById(R.id.city);
        TextView joinedDate = findViewById(R.id.joinedDate);
        TextView salary = findViewById(R.id.salary);

        //set values
        if (details[0] != null && !TextUtils.isEmpty(details[0]))
        employeeNameTv.setText(details[0]); //if employee name is not null and empty
        if (details[3] != null && !TextUtils.isEmpty(details[3]))
        empId.setText(details[3]); //if employee id name is not null and empty
        if (details[1] != null && !TextUtils.isEmpty(details[1]))
        designation.setText(details[1]); //if designation name is not null and empty
        if (details[2] != null && !TextUtils.isEmpty(details[2]))
        city.setText(details[2]); //if city name is not null and empty
        if (details[4] != null && !TextUtils.isEmpty(details[4]))
        joinedDate.setText(details[4]); //if joined date name is not null and empty
        if (details[5] != null && !TextUtils.isEmpty(details[5]))
        salary.setText(details[5]); //if salary name is not null and empty

        captureImgView = findViewById(R.id.captureImgView);
        Button cameraBtn = findViewById(R.id.cameraBtn);
        cameraBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                initCamera();
            }
        });
    }

    @Override
    public void onBackPressed() {
        finishAfterTransition();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            finishAfterTransition();
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * check permission in initCamera method
     */

    private void initCamera(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {

                if (ActivityCompat.shouldShowRequestPermissionRationale
                        (this, Manifest.permission.READ_EXTERNAL_STORAGE) ||
                        ActivityCompat.shouldShowRequestPermissionRationale
                                (this, Manifest.permission.CAMERA)) {

                    Snackbar.make(findViewById(android.R.id.content),
                            "Please Grant Permissions to capture photo",
                            Snackbar.LENGTH_INDEFINITE).setAction("ENABLE",
                            new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    ActivityCompat.requestPermissions(EmployeeDetailsActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                                            PERMISSIONS_MULTIPLE_REQUEST);
                                }
                            }).show();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.CAMERA},
                            PERMISSIONS_MULTIPLE_REQUEST);
                }
            }else{
                dispatchTakePictureIntent();
            }
        }else{
            dispatchTakePictureIntent();
        }
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
                ex.printStackTrace();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.example.android.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_TAKE_PHOTO && resultCode == RESULT_OK) {
            galleryAddPic();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSIONS_MULTIPLE_REQUEST) {
            boolean isPerpermissionForAllGranted = false;
            if (grantResults.length > 0 && permissions.length==grantResults.length) {
                for (int i = 0; i < permissions.length; i++){
                    isPerpermissionForAllGranted = grantResults[i] == PackageManager.PERMISSION_GRANTED;
                }
                Log.e("value", "Permission Granted.");
            }
            if(isPerpermissionForAllGranted){
                dispatchTakePictureIntent();
            }else {
                Snackbar.make(findViewById(android.R.id.content),
                        "Permissions deny! Unable to capture photo.",
                        Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault()).format(new Date());
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

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(currentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
        setPic();
    }

    /**
     * decode image as bitmap from path
     * draw text over bitmap image
     * display bitmap in imageView
     */
    private void setPic() {
        // Get the dimensions of the View

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        int resizeScale = 1, max_size = 480;

        if (bmOptions.outHeight > max_size
                || bmOptions.outWidth > max_size) {
            resizeScale = (int) Math.pow(2, (int) Math.round(Math.log(max_size/ (double) Math.max(bmOptions.outHeight, bmOptions.outWidth)) / Math.log(0.5)));
        }

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = resizeScale;

        Bitmap bitmap = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        android.graphics.Bitmap.Config bitmapConfig =
                bitmap.getConfig();
        // set default bitmap config if none
        if(bitmapConfig == null) {
            bitmapConfig = android.graphics.Bitmap.Config.ARGB_8888;
        }
        // resource bitmaps are imutable,
        // so we need to convert it to mutable one
        bitmap = bitmap.copy(bitmapConfig, true);

        //draw text on the image
        Canvas canvas = new Canvas(bitmap);
        // new anti-alised Paint
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        // text color - #3D3D3D
        paint.setColor(Color.WHITE);
        // text size in pixels
        float scale = getResources().getDisplayMetrics().density;
        paint.setTextSize((int) (10 * scale));
        // text shadow
        paint.setShadowLayer(1f, 0f, 1f, Color.RED);

        // draw text to the Canvas center
        Rect bounds = new Rect();
        paint.getTextBounds(timeStamp, 0, timeStamp.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width())/2;
        int y = (bitmap.getHeight() + bounds.height())/2;

        canvas.drawText(timeStamp, x, y, paint);

        captureImgView.setImageBitmap(bitmap);
        captureImgView.setAdjustViewBounds(true);
    }
}
