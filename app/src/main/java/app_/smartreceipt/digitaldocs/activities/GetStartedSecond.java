
package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;

public class GetStartedSecond  extends AppCompatActivity {
    private Button next;
    private Button back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.gettiing_started_step3);

        //get reference
        setScene();
        linkReceipt();
        linkBack();
    }



    private void setScene() {
        next = findViewById(R.id.buttonWWW);
        back = findViewById(R.id.button467);
    }

    private void linkReceipt() {
        final Intent intent = new Intent(this, GetStartedThree.class);

        next.setOnClickListener(view -> GetStartedSecond.this.startActivity(intent));
    }

    private void linkBack() {
        final Intent intent = new Intent(this, GetStartedOne.class);

        back.setOnClickListener(view -> GetStartedSecond.this.startActivity(intent));
    }
}

