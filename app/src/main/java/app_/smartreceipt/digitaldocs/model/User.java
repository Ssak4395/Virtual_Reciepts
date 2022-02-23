package app_.smartreceipt.digitaldocs.model;

import android.app.Activity;
import android.content.Context;

import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import app_.smartreceipt.digitaldocs.activities.HomeActivity;

public class User {

    private static FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private static FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private static String userID = fAuth.getCurrentUser().getUid();
    private static DocumentReference userReference = firebaseFirestore.collection("users").document(userID);
    private static Context context = HomeActivity.getAppContext();

    private static boolean needsCroppingTutorial;
    private static boolean needsImageTutorial;

    public static void checkFirebaseForTutorial(Activity activity) {
        userReference.addSnapshotListener(activity, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    return;
                }
                try {
                    needsCroppingTutorial =  documentSnapshot.getBoolean("Cropping Tutorial Boolean");
                } catch (NullPointerException exception) {
                    setNeedsCroppingTutorial(true);
                }

                try {
                    needsImageTutorial =  documentSnapshot.getBoolean("Image Tutorial Boolean");
                } catch (NullPointerException exception) {
                    setNeedsImageTutorial(true);
                }
            }
        });
    }

    public static void setNeedsCroppingTutorial(boolean update) {
        needsCroppingTutorial = update;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Cropping Tutorial Boolean", update);
        userReference.update(map);
    }

    public static void setNeedsImageTutorial(boolean update) {
        needsCroppingTutorial = update;
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("Image Tutorial Boolean", update);
        userReference.update(map);
    }

    public static boolean needsCroppingTutorial() {
        return needsCroppingTutorial;
    }

    public static boolean needsImageTutorial() {
        return needsImageTutorial;
    }

    public static void updateUser() {
        firebaseFirestore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        userReference = firebaseFirestore.collection("users").document(userID);
    }

    //everything below is not used yet, might be good to transition to this at some point



    public static void saveData() throws IOException {
        FileOutputStream outputStream = context.getApplicationContext().openFileOutput("User", Context.MODE_PRIVATE);
        outputStream.write(userID.getBytes());
        outputStream.close();
    }

    public static void loadData() throws IOException {
        FileInputStream fis = context.openFileInput("User");
        Scanner scanner = new Scanner(fis);
        scanner.useDelimiter("\\Z");

        if(scanner.hasNext()) {
            userID = scanner.next();
        } else {
            userID = "";
        }

        scanner.close();
    }

    public static void updateContext(Context applicationContext) {
        context = applicationContext;
    }

    public static void setUserID(String UID) {
        userID = UID;
        try {
            //saveData();
        } catch (Exception e) {
        }
    }

    public static String getUserID() {
        return userID;
    }
}
