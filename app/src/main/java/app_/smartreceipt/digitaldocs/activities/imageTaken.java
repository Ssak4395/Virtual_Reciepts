package app_.smartreceipt.digitaldocs.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.services.vision.v1.Vision;
import com.google.api.services.vision.v1.VisionRequestInitializer;
import com.google.api.services.vision.v1.model.AnnotateImageRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesRequest;
import com.google.api.services.vision.v1.model.BatchAnnotateImagesResponse;
import com.google.api.services.vision.v1.model.EntityAnnotation;
import com.google.api.services.vision.v1.model.Feature;
import com.google.api.services.vision.v1.model.Image;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.User;
import app_.smartreceipt.digitaldocs.services.AbnSearchWSHttpGet;
import app_.smartreceipt.digitaldocs.services.ImageResizer;
import app_.smartreceipt.digitaldocs.services.algorithm.algorithms;
import app_.smartreceipt.digitaldocs.utilities.CroppingTutorial;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class imageTaken extends AppCompatActivity {

    private ImageView imageView;
    private Bitmap  original;
    private Button process;
    private Button closeButton;

    private Uri originalUri;
    private String ABN = "";
    private String purchaseDate;
    private String totalDouble = "";
    private String companyName = "";
    private LoadingActivity loadingDialog;
    private ArrayList<ArrayList<String>> lineAndPrice;
    final static String guid =  "411e2117-1fe8-4876-a8a8-5e3150e22eda";
    double lat = 0;
    double lon = 0;
    Context context = this;

    public Intent intent;

    public boolean movedOn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_taken);
        movedOn = false;
        ABN = "";
        totalDouble = "";
        companyName = "";
        imageView = findViewById(R.id.preview);
        originalUri = (Uri) getIntent().getExtras().get("uri");
        loadingDialog = new LoadingActivity(this);
        intent = new Intent(this, CameraActivity.class);

        try {
            resolveBitmap();
            imageView.setImageBitmap(original);
        } catch (IOException e) {
            e.printStackTrace();
        }
        process = findViewById(R.id.process);
        process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              explodeCropperActivity();
            }
        });

        closeButton = findViewById(R.id.back_image_taken);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent openMainActivity = new Intent(imageTaken.this, CameraActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
                finish();
            }
        });

        if (User.needsCroppingTutorial()) {
            new CroppingTutorial(this);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        // Trigger the initial hide() shortly after the activity has been
        // created, to briefly hint to the user that UI controls
        // are available.

    }

    private void resolveBitmap() throws IOException {
        original = MediaStore.Images.Media.getBitmap(getContentResolver(), originalUri);
    }

    public void explodeCropperActivity()
    {
        Uri uri = (Uri) getIntent().getExtras().get("uri");
        CropImage.activity(uri).start(this);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bmpfinal = null;

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                try {
                    bmpfinal = MediaStore.Images.Media.getBitmap(getContentResolver(),resultUri);
                    imageView.setImageBitmap(bmpfinal);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    ByteArrayOutputStream stream2 = new ByteArrayOutputStream();

                    if (Objects.equals(getIntent().getExtras().get("isCamera"), true)) {
                        CameraManager manager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
                        int orientation = 0;
                        try {
                            String cameraId = manager.getCameraIdList()[0];
                            CameraCharacteristics characteristics = manager.getCameraCharacteristics(cameraId);
                            orientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }

                        Matrix matrix = new Matrix();
                        switch(orientation) {
                            case 90:
                                matrix.postRotate(90);
                                original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                                break;
                            case 180:
                                matrix.postRotate(180);
                                original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                                break;
                            case 270:
                                matrix.postRotate(270);
                                original = Bitmap.createBitmap(original, 0, 0, original.getWidth(), original.getHeight(), matrix, true);
                                break;
                            case 0:
                            default:
                        }
                    }

                    // TO DEVELOPERS IF THE IMAGE ISNT PARSING
                    original.compress(Bitmap.CompressFormat.JPEG,ImageResizer.resolveCompressionRawImage(originalUri,getApplicationContext()), stream);
                    bmpfinal.compress(Bitmap.CompressFormat.JPEG,40,stream2);

                    imageView.setImageBitmap(bmpfinal);
                    byte[] byteArray = stream.toByteArray();
                    byte[] byteArray2 = stream2.toByteArray();

                    tryCloudVisionAPI cloudVision = new tryCloudVisionAPI();
                    cloudVision.execute(byteArray,byteArray2);

                    loadingDialog.startLoadingAnimation();
                    Handler handler = new Handler();
                    handler.postDelayed(() -> {
                        if ( cloudVision.getStatus() == AsyncTask.Status.RUNNING )
                            cloudVision.cancel(true);
                            if (!movedOn) {
                                crashHandler("Algorithm Timeout");
                            }
                    }, 40000 );

                    byteArray = null;
                    byteArray2 = null;
                    System.gc();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
            }
        }
    }

    public class tryCloudVisionAPI extends AsyncTask<byte[],String,String> {
        @Override
        protected String doInBackground(byte[]... bytes) {
            Vision.Builder visionBuilder = new Vision.Builder(
                    new NetHttpTransport(),
                    new AndroidJsonFactory(),
                    null);

            visionBuilder.setVisionRequestInitializer(
                    new VisionRequestInitializer("AIzaSyD2p2Yc95RZ01oFNI9ox9yE2BhXR5X3rTw"));

            Vision vision = visionBuilder.build();

            Image image = new Image();
            image.encodeContent(bytes[0]);
            Image imageCropped = new Image();
            imageCropped.encodeContent(bytes[1]);
            Feature desiredFeature = new Feature();
            desiredFeature.setType("TEXT_DETECTION");

            List<EntityAnnotation> totalAbn = new ArrayList<>();
            List<EntityAnnotation> lineItems = new ArrayList<>();

            AnnotateImageRequest request = new AnnotateImageRequest();
            request.setImage(image);
            AnnotateImageRequest request2 = new AnnotateImageRequest();
            request2.setImage(imageCropped);
            request2.setFeatures(Arrays.asList(desiredFeature));
            request.setFeatures(Arrays.asList(desiredFeature));
            BatchAnnotateImagesRequest batchRequest = new BatchAnnotateImagesRequest();
            batchRequest.setRequests(Arrays.asList(request,request2));
            BatchAnnotateImagesResponse batchResponse = null;
            try {
                System.out.println("try batch response");
                batchResponse = vision.images().annotate(batchRequest).execute();
                totalAbn = batchResponse.getResponses().get(0).getTextAnnotations();
                lineItems = batchResponse.getResponses().get(1).getTextAnnotations();

            } catch (IOException e) {
                //This error occures when there is no internet connection as much as I know
                crashHandler("No Internet Connection");
                e.printStackTrace();
            }

            algorithms algorithms = new algorithms();

            try {
                totalDouble = Double.toString(algorithms.startTotalStrategy(totalAbn));
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                 ABN = algorithms.startABNStrategy(totalAbn);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                lineAndPrice = algorithms.generateLineItems(lineItems);
            } catch (IOException e) {
                e.printStackTrace();
            }

            try{
                purchaseDate = algorithms.resolveTotalDate(totalAbn);
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (!ABN.equals("Algorithm could not detect a valid ABN")) {
                try {
                    companyName = AbnSearchWSHttpGet.searchByABN(guid,ABN,false).getOrganisationName();
//                    if(ABN.equals("88000014675"))
//                    {
//                         lat = (Double) algorithms.getWooliesLatLongCoords(totalAbn).get("lat");
//                         lon = (Double) algorithms.getWooliesLatLongCoords(totalAbn).get("long");
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return totalDouble;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            loadingDialog.dismissDialog();

            Intent intent1 = new Intent(context,ImageTakenActivity.class);
            intent1.putExtra("total", totalDouble);
            intent1.putExtra("company", companyName);
            intent1.putExtra("list", lineAndPrice);
            intent1.putExtra("abn", ABN);
            intent1.putExtra("date",purchaseDate);
            intent1.putExtra("lat", lat);
            intent1.putExtra("long", lon);
            intent1.putExtra("uri", originalUri);
            startActivity(intent1);
            movedOn = true;
            finish();
        }
    }

    public void crashHandler(String s) {
        loadingDialog.dismissDialog();
        intent.putExtra("error", s);
        imageTaken.this.startActivity(intent);
        movedOn = true;
        finish();
    }
}