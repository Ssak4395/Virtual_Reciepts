package app_.smartreceipt.digitaldocs.activities;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import app_.smartreceipt.digitaldocs.R;

//import android.support.v7.app.AppCompatActivity;


public class ForgotPasswordActivity extends AppCompatActivity {
    private EditText userEmailInput;
    private Button submit;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forgotpassword);

        userEmailInput = (EditText) findViewById(R.id.editTextTextEmailAddress3);
        userEmailInput.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        submit = (Button) findViewById(R.id.forgot_widget);
        mAuth = FirebaseAuth.getInstance();

        submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String email = userEmailInput.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter your email!", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.sendPasswordResetEmail(email)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(ForgotPasswordActivity.this,
                                            "Check your email to reset your password!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(ForgotPasswordActivity.this,
                                            "Failed to send reset password email!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}

