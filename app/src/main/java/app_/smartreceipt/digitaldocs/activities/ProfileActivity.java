package app_.smartreceipt.digitaldocs.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptList;


/**
 * Class displaying user data from what they signed up, accessing it from the firebase firestore
 */
public class ProfileActivity extends AppCompatActivity {
     private Button exit;

     /**
      * FOR NAVIGATION BAR ON BOTTOM
      */
     private ImageButton camera;
     private  ImageButton chart;
     private ImageButton settings;
     private ImageButton receipt;
     private ImageButton search;

     /**
      * For the top part of profile page
      */
     private ImageView profileImage;  //for displaying the profile pic
     private Button changeProfilePic; //when user wants to upload their profile pic

     private TextView email,last;
     private Button update;
     private Bitmap bitmap; //for uploading Profile Pic
     private TextView firstName,LastName;

     FirebaseAuth fAuth;
     FirebaseFirestore fStore;
     StorageReference storageReference; //Firebase object for uploading Profile pic to FB


     private String userID;

     @Override
     protected void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          this.setContentView(R.layout.profile_page);
          setContentView(R.layout.profile_page);
          setScene(); //continues setup below
          linkCamera();
          linkChart();
          linkReceipt();
          linkSetting();
          linkSearch();
          linkExit();
          linkUpdate();

          fAuth = FirebaseAuth.getInstance();
          fStore = FirebaseFirestore.getInstance();
          storageReference= FirebaseStorage.getInstance().getReference(); //use to upload image to Firebase system


          StorageReference profileRef= storageReference.child("users/"+ fAuth.getCurrentUser().getUid() + "/profile.jpg"); //use the uploaded image in Firebase system to display

          profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
               @Override
               public void onSuccess(Uri uri) {
                    Glide.with(getApplicationContext()).load(uri.toString()).into(profileImage);
                    //Picasso.get().load(uri.toString()).into(profileImage); //ensure that all users access ONLY their own pics
               }
          });

          userID = fAuth.getCurrentUser().getUid();

          firstName = findViewById(R.id.editfirstName);
          LastName = findViewById(R.id.editLastName);

          DocumentReference documentReference = fStore.collection("users").document(userID);
          documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
               @Override
               public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                    if (e != null) {
                         return;
                    }
                    firstName.setText(documentSnapshot.getString("First Name"));
                    LastName.setText(documentSnapshot.getString("Last Name"));
               }
          });


          //whenever the user clicks on the + button
          changeProfilePic.setOnClickListener(new View.OnClickListener() {
               public void onClick(View v) {
                    //open gallery
                    Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(openGalleryIntent, 1000);

                    Intent intent = new Intent();
                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1000);
               }
          });
     }

     //setting profile picture.
     @Override
     protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
          super.onActivityResult(requestCode, resultCode, data);

          if (data == null) {
               return;
          }

          Uri filePath = data.getData(); //gets the Profile Pic path
          uploadImageToFirebase(filePath); //upload to Firebase
     }


     private void uploadImageToFirebase(Uri imageUri) { //for uploading the Profile Picture to Firebase
          //displaying progress dialog while image is uploading
          final ProgressDialog progressDialog = new ProgressDialog(this);
          progressDialog.setTitle("Uploading");
          progressDialog.show();

          //getting the storage reference
          final StorageReference fileRef = storageReference.child("users/" + fAuth.getCurrentUser().getUid()+ "/profile.jpg");

          fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
               @Override
               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                         @Override
                         public void onSuccess(Uri uri) {
                              Picasso.get().load(uri).into(profileImage);

                              progressDialog.dismiss();
                              //displaying success toast
                              Toast.makeText(getApplicationContext(), "File Uploaded ", Toast.LENGTH_LONG).show();
                         }
                    });
               }
          }).addOnFailureListener(new OnFailureListener() {
               @Override
               public void onFailure(@NonNull Exception e) {
                    Toast.makeText(ProfileActivity.this, "Failed image upload.", Toast.LENGTH_SHORT).show();
               }
          });
     }

     private void linkCamera() {
          final Intent intent = new Intent(this, CameraActivity.class);

          camera.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }
     private void linkSearch() {
          final Intent intent = new Intent(this, SearchActivity.class);

          search.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }

     private void linkUpdate() {
          final Intent intent = new Intent(this, ProfileUpdateActivity.class);

          update.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }

     private void linkChart() {
          final Intent intent = new Intent(this, ChartActivity.class);

          chart.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }

     private void linkSetting() {
          final Intent intent = new Intent(this, SettingsActivity.class);

          settings.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }

     private void linkReceipt() {
          final Intent intent = new Intent(this, ReceiptActivity.class);

          receipt.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ProfileActivity.this.startActivity(intent);
               }
          });
     }

     private void linkExit() {
          final Intent intent = new Intent(this, HomeActivity.class);

          exit.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                    ReceiptList.clearReceipts();
                    fAuth.signOut();
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    ProfileActivity.this.startActivity(intent);
                    finish();
               }
          });
     }

     private void setScene() {


          profileImage= findViewById((R.id.profilePic)); //add widget name
          changeProfilePic = findViewById((R.id.changeProfilePic)); //button

          update = findViewById(R.id.button);
          exit = findViewById(R.id.LOGOUTT);
          camera = findViewById(R.id.done);
          chart = findViewById(R.id.receipt_chart_profile);
          settings = findViewById(R.id.ee);
          receipt = findViewById(R.id.aa);
          search = findViewById(R.id.bb);

     }

}
