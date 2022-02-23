package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;


/**
 * class that will display all of the help information
 */

public class HelpActivity extends AppCompatActivity {

    Button emailButton;
    Button balckButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.help_page);
        setContentView(R.layout.help_page);

        setup();
        linkEmail();
        linkBack();
    }

    private void setup() {
        emailButton = findViewById(R.id.emailButton);
        balckButton = findViewById(R.id.backButton);
    }

    private void linkEmail() {
        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("plain/text");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[] { "admin@riddles.tech" });
                intent.putExtra(Intent.EXTRA_SUBJECT, "Support");
                startActivity(Intent.createChooser(intent, ""));
            }
        });
    }

    private void linkBack() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        balckButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HelpActivity.this.startActivity(intent);
            }
        });
    }
}
