package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.utilities.DeveloperNamesPopup;


public class SettingsActivity extends AppCompatActivity {
    private ImageButton camera;
    private ImageButton chart;
    private ImageButton search;
    private ImageButton settings;
    private ImageButton receipt;

    private LinearLayout legal;
    private LinearLayout help;
    private LinearLayout profile;
    private LinearLayout getStarted;


    private TextView versionCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.setting_page);

        //get reference
        setScene();

        //page items
        linkProfile();
        linkHelp();
        linkLegal();
        linkGetStarted();
        linkVersion();

        //tool bar items
        linkReceipt();
        linkChart();
        linkCamera();
        linkSearch();
        linkSetting();
    }

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Linking to the camera page from settings page
     */

    private void linkCamera() {
        final Intent intent = new Intent(this, CameraActivity.class);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SettingsActivity.this.startActivity(intent);
            }
        });
    }

    private void linkVersion() {

        versionCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeveloperNamesPopup popUpClass = new DeveloperNamesPopup();
                popUpClass.showPopupWindow(v);
            }
        });
    }

    /**
     * Linking to the legal page from the settings page
     */

    private void linkLegal() {
        final Intent intent = new Intent(this, LegalActivity.class);

        legal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Linking to the help page from the settings page
     */

    private void linkHelp() {
        final Intent intent = new Intent(this, HelpActivity.class);

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Links to the profile page from the setting page
     */

    private void linkProfile() {
        final Intent intent = new Intent(this, ProfileActivity.class);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               SettingsActivity.this.startActivity(intent);
            }
        });
    }

    private void linkGetStarted() {
        final Intent intent = new Intent(this, GetStartedActivity.class);

        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingsActivity.this.startActivity(intent);
            }
        });
    }
    /**
     * Links to the setting page from the setting page
     */

    private void linkSetting() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Links to the receipt page from the settings page
     */

    private void linkReceipt() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Links the setting page to the search bar
     */
    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

       search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SettingsActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Links each button with its id
     */

    private void setScene() {
        legal = findViewById(R.id.legal_button);
        help = findViewById(R.id.help_button);
        profile = findViewById(R.id.account_button);
        getStarted = findViewById(R.id.get_started_button);
        versionCode = findViewById(R.id.version_number);

        camera = findViewById(R.id.camera_setting);
        settings = findViewById(R.id.setting_setting);
        receipt = findViewById(R.id.receipt_setting);
        search = findViewById(R.id.search_setting);
        chart = findViewById(R.id.receipt_chart_settings);
    }

}
