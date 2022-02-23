
package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;

public class GetStartedThree extends AppCompatActivity {

    private Button exit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.pagesix);
        setScene();
        linkReceipt();

    }

    private void setScene() {
        exit = findViewById(R.id.button419);

    }
    private void linkReceipt() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        exit.setOnClickListener(view -> GetStartedThree.this.startActivity(intent));
    }
}

