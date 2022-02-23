package app_.smartreceipt.digitaldocs.utilities;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpHandler {

    FirebaseAuth firebaseAuth;
    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    String uid;
    boolean isSuccessful;

    public SignUpHandler() {
        firebaseAuth = FirebaseAuth.getInstance();
    }

    public boolean signUp(final String Email, String Password, final String FirstName, final String LastName) {
        firebaseAuth.createUserWithEmailAndPassword(Email.trim(),Password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                isSuccessful = true;
                                uid = firebaseAuth.getUid();
                                DocumentReference documentReference = firebaseFirestore.collection("users").document(uid);
                                Map<String, Object> users = new HashMap<>();
                                users.put("First Name", FirstName);
                                users.put("Last Name", LastName);
                                users.put("Email", Email);
                                documentReference.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                    }
                                });
                            } else {
                                task.getException().printStackTrace();
                            }
                        }
                    });
                } else {
                    try {
                        throw task.getException();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        return isSuccessful;
    }

}
