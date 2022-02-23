package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import app_.smartreceipt.digitaldocs.R;

public class SignUpConfirmationActivity extends AppCompatActivity {

    /**
     * The display which is going to be show when sign up is confirmed
     */
    Button proceed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.confirmation);
        setContentView(R.layout.confirmation);
        proceed = findViewById(R.id.Proceed);

        proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                final Intent intent = new Intent(SignUpConfirmationActivity.this, HomeActivity.class);
                SignUpConfirmationActivity.this.startActivity(intent);
            }
        });

    }

}