package app_.smartreceipt.digitaldocs.activities;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;


public class IndividualReceipt extends AppCompatActivity   {

    private ImageView imageView;
    private ImageView noPreview;
    private TextView noPreviewText;
    private Button back;

    private Uri imageUri;

    private ReceiptEntity receipt;
    private LoadingActivity loadingDialog;

    FirebaseAuth fAuth;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.seperate_receipt_page);

        setup();

        receipt = (ReceiptEntity) getIntent().getExtras().getSerializable("receipt");

        fAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        StorageReference profileRef= storageReference.child("users/" + fAuth.getCurrentUser().getUid() + "/receipts/" + receipt.getID() + ".png");

        loadingDialog.startLoadingAnimation();

        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide.with(getApplicationContext()).load(uri.toString()).listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        noPreview.setVisibility(View.GONE);
                        noPreviewText.setVisibility(View.GONE);
                        loadingDialog.dismissDialog();
                        imageUri = uri;
                        return false;
                    }
                }).into(imageView);
                //Picasso.get().load(uri).into(imageView); //ensure that all users access ONLY their own pics
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                loadingDialog.dismissDialog();
            }
        });

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(android.content.Intent.ACTION_VIEW);
                intent.setDataAndType(imageUri, "image/png");
                startActivity(intent);
            }
        });
    }

    private void setup() {
        imageView = findViewById(R.id.receipt_preview);
        noPreview = findViewById(R.id.no_preview);
        back = findViewById(R.id.back_taken);
        noPreviewText = findViewById(R.id.no_preview_text);

        loadingDialog = new LoadingActivity(this, "Downloading...");

        linkBack();
    }

    private void linkBack() {
        final Intent intent = new Intent(this, ReceiptActivity.class);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IndividualReceipt.this.startActivity(intent);
            }
        });
    }


}
