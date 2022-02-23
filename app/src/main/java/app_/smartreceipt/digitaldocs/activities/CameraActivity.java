package app_.smartreceipt.digitaldocs.activities;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.User;
import app_.smartreceipt.digitaldocs.utilities.AlgorithmFailedDialog;
import app_.smartreceipt.digitaldocs.utilities.ImageTutorial;

//This is the camara class

public class CameraActivity extends AppCompatActivity {
    private ImageButton camera;
    private  ImageButton chart;
    private ImageButton settings;
    private ImageButton receipt;
    private ImageButton search;
    private LinearLayout accessGallery;
    private String ABN;
    private String totalDouble;
    private String companyName;
    private LoadingActivity loadingDialog;
    byte[] byteArray;
    Context context = this;
    private static final int PICK_IMAGE = 1;
    private static final int PERMISSION_CODE = 1000;
    private static final int IMAGE_CAPTURE_CODE = 1001;
    ImageView mCaptureBtn;
    String encodedBase64 ="";
    Uri image_uri;
    Bitmap image;
    final static String guid =  "411e2117-1fe8-4876-a8a8-5e3150e22eda";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestPermission();
        setContentView(R.layout.camera_display);
        final Activity activity = this;

        //check if there was a crash on algorithm and display dialog
        checkForCrash();

        setScene();
        linkCamera();
        linkChart();
        linkReceipt();
        linkSetting();
        linkSearch();

        accessGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent gallery = new Intent();
                gallery.setType("image/*");
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(gallery,"Select a picture"),PICK_IMAGE);
            }
        });

        mCaptureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (User.needsImageTutorial()) {
                    new ImageTutorial(activity);
                    return;
                }

                openCamera();
            }
        });
    }

    private void checkForCrash() {
        Intent intent = getIntent();
        if (intent.hasExtra("error")) {
            String errorMessage = intent.getExtras().getString("error");
            new AlgorithmFailedDialog(this, errorMessage);
        }
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
        }
    }

    public void openCamera() {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From the Camera");
        //this is where the image URI is saved for now to be used later
        image_uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri);
        startActivityForResult(cameraIntent, IMAGE_CAPTURE_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //this method is called, when user pressed allowed or deny from permission request group
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera();
                } else {
                    Toast.makeText(this, "Permission denied...", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
               if (requestCode == PICK_IMAGE) {
                   image_uri = data.getData();
               }
                Intent intent = new Intent(this,imageTaken.class );
                intent.putExtra("uri",image_uri);
                if (requestCode == PICK_IMAGE) {
                    intent.putExtra("isCamera", false);
                } else {
                    intent.putExtra("isCamera", true);
                }
                startActivity(intent);
        }
    }

    private void setScene() {
        camera = findViewById(R.id.camera_widget2);
        chart = findViewById(R.id.receipt_chart_camera);
        settings = findViewById(R.id.setting_widget2);
        receipt = findViewById(R.id.receipt_widget2);
        search = findViewById(R.id.search_widget2);

        accessGallery = findViewById(R.id.accessGallery);
        mCaptureBtn = findViewById(R.id.capture_image_btn);
        loadingDialog = new LoadingActivity(this);
    }



    private void linkCamera() {
        final Intent intent = new Intent(this, CameraActivity.class);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.this.startActivity(intent);
            }
        });
    }

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.this.startActivity(intent);
            }
        });
    }
    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.this.startActivity(intent);
            }
        });
    }

    private void linkSetting() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.this.startActivity(intent);
            }
        });
    }

    private void linkReceipt() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraActivity.this.startActivity(intent);
            }
        });
    }
}
