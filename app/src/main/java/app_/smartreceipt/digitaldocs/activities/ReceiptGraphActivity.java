package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.utilities.ReceiptPopup;
import app_.smartreceipt.digitaldocs.utilities.RecyclerViewAdapter;
import app_.smartreceipt.digitaldocs.utilities.SortOrderFragment;

public class ReceiptGraphActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener, ReceiptUpdate, ReceiptPopup.DeleteListener {
    private Button back;

    private ImageButton sortOption;
    private ArrayList<ReceiptEntity> dateReceipt;

    private  RecyclerViewAdapter adapter;

    RecyclerView recyclerView;
    TextView dateText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.date_view_page);
        setContentView(R.layout.date_view_page);
        setup();

        dateReceipt = (ArrayList<ReceiptEntity>) getIntent().getExtras().getSerializable("receipt");
        dateText.setText(getIntent().getExtras().getString("text"));

        //Show most recent receipts in application and then update with server
        setReceiptsList();
    }

    private void setup() {
        setScene();
        linkBack();
        linkSort();
    }


    public void setReceiptsList() {
        if (adapter == null) {
            ArrayList<ReceiptEntity> arrayList = dateReceipt;

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
        popUpClass.showPopupWindow(recyclerView, item, this, this, this);
    }

    /**
     * Links each button to its id
     */

    private void setScene() {

        recyclerView = findViewById(R.id.recyclerView_chart);
        dateText = findViewById(R.id.textView_date);

        //other buttons
        sortOption = findViewById(R.id.receipt_filter_date);

        //Default toolbar setup
        back = findViewById(R.id.chart_back_button);
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

    private void linkBack() {

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent openMainActivity = new Intent(ReceiptGraphActivity.this, ChartActivity.class);
                openMainActivity.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                startActivityIfNeeded(openMainActivity, 0);
            }
        });
    }

    @Override
    public void onItemDelete(ReceiptEntity receipt) {
        dateReceipt.remove(receipt);
        setReceiptsList();
    }
}
