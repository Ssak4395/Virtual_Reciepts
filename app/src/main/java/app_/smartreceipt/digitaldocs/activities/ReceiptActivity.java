package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.utilities.ReceiptPopup;
import app_.smartreceipt.digitaldocs.utilities.RecyclerViewAdapter;
import app_.smartreceipt.digitaldocs.utilities.SortOrderFragment;

public class ReceiptActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener, ReceiptUpdate {
    private ImageButton camera;
    private ImageButton settings;
    private ImageButton receipt;
    private ImageButton search;
    private ImageButton chart;

    private ImageButton sortOption;

    private RecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    TextView noScansText;
    LottieAnimationView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.receipt_page);
        setContentView(R.layout.receipt_page);

        setup();

        //Show most recent receipts in application and then update with server
        setReceiptsList();
        ReceiptList.updateReceiptsList(this);

        returnFromEdit();
    }

    private void returnFromEdit() {

        ConstraintLayout constraintLayout = findViewById(R.id.receipt_page);
        ReceiptUpdate update = this;
        constraintLayout.post(new Runnable() {
            @Override
            public void run() {
                if (getIntent().getExtras() != null && getIntent().getExtras().getSerializable("receipt") != null && !isFinishing()) {
                    ReceiptEntity receipt = (ReceiptEntity) getIntent().getExtras().getSerializable("receipt");
                    ReceiptPopup popUpClass = new ReceiptPopup();
                    popUpClass.showPopupWindow(recyclerView, receipt, update, getApplicationContext());
                }
            }
        });
    }

    private void setup() {
        setScene();
        linkCamera();
        linkReceipt();
        linkSetting();
        linkSearch();
        linkSort();
        linkChart();
    }


    public void setReceiptsList() {
        ArrayList<ReceiptEntity> arrayList = ReceiptList.getReceipts();

        if (arrayList.size() > 0) {
            noScansText.setVisibility(View.GONE);
            image.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noScansText.setVisibility(View.VISIBLE);
            image.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }


        if (adapter == null) {
            adapter = new RecyclerViewAdapter(this, arrayList, this);
            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(ReceiptEntity item) {
        ReceiptPopup popUpClass = new ReceiptPopup();
        popUpClass.showPopupWindow(recyclerView, item, this, this);
    }

    /**
     * Links each button to its id
     */

    private void setScene() {

        recyclerView = findViewById(R.id.recyclerView);
        noScansText = findViewById(R.id.noScansText);
        image = findViewById(R.id.pic);

        //other buttons
        sortOption = findViewById(R.id.receipt_filter);


        //Default toolbar setup
        camera = findViewById(R.id.camera_widget1);
        chart = findViewById(R.id.receipt_chart);
        settings = findViewById(R.id.setting_widget1);
        receipt = findViewById(R.id.receipt_widget1);
        search = findViewById(R.id.search_widget1);
    }

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiptActivity.this.startActivity(intent);
            }
        });
    }

    private void linkSort() {
        sortOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new SortOrderFragment().show(getSupportFragmentManager(), "sortorderDialog");
            }
        });
    }

    /**
     * Accessing the camera from the receipt page
     */

    private void linkCamera() {
        final Intent intent = new Intent(this, CameraActivity.class);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiptActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the search page from the receipt page
     */

    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiptActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the setting page from the receipt receipt page
     */

    private void linkSetting() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             ReceiptActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the receipt page from the receipt page
     */

    private void linkReceipt() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ReceiptActivity.this.startActivity(intent);
            }
        });
    }
}
