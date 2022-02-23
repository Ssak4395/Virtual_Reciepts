package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;


public class GetStartedActivity extends AppCompatActivity {
    private Button next;
    private Button back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.get_started_layout);


        //get reference
        setScene();
        linkReceipt();
        linkback();
    }


    private void setScene() {
        next = findViewById(R.id.w);
        back = findViewById(R.id.button4888);


    }

    private void linkReceipt() {
        final Intent intent = new Intent(this, GetStartedOne.class);

       next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               GetStartedActivity.this.startActivity(intent);
            }
        });
    }

    private void linkback() {
        final Intent intent = new Intent(this, SettingsActivity.class);

       back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetStartedActivity.this.startActivity(intent);
            }
        });
    }
}