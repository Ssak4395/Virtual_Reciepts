package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;

public class GetStartedOne extends AppCompatActivity {
    private Button next;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gettimg_sarted_second);

        //get reference
        setScene();
        linkReceipt();
        linkBack();
    }


    private void setScene() {
        next = findViewById(R.id.buttonW);
        back = findViewById(R.id.button4900);

    }
    private void linkReceipt() {
        final Intent intent = new Intent(this, GetStartedSecond.class);

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetStartedOne.this.startActivity(intent);
            }
        });
    }
    private void linkBack() {
        final Intent intent = new Intent(this, GetStartedActivity.class);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GetStartedOne.this.startActivity(intent);
            }
        });
    }
}
