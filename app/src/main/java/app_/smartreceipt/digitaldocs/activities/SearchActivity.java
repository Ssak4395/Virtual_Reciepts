package app_.smartreceipt.digitaldocs.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptLineItem;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.model.Tag;
import app_.smartreceipt.digitaldocs.utilities.ReceiptPopup;
import app_.smartreceipt.digitaldocs.utilities.RecyclerViewAdapter;
import app_.smartreceipt.digitaldocs.utilities.SortOrderFragment;

public class SearchActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener, ReceiptUpdate, ReceiptPopup.DeleteListener {
    private ImageButton camera;
    private ImageButton chart;
    private ImageButton settings;
    private ImageButton receipt;
    private ImageButton searchPage;

    private ImageButton searchSorting;

    private EditText editText;
    private RecyclerView recyclerView;
    private TextView noSearchFoundText;
    private LottieAnimationView animationView;

    private RecyclerViewAdapter adapter;

    private ArrayList<ReceiptEntity> foundReceipts = new ArrayList<ReceiptEntity>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.search_page);
        setContentView(R.layout.search_page);

        setup();

        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputText = editText.getText().toString().toLowerCase();
                search(inputText);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void search(String searchTerm) {
        ArrayList<ReceiptEntity> allReceipts = ReceiptList.getReceipts();
        foundReceipts.clear();

        for (ReceiptEntity receipt : allReceipts) {
            String storeName = receipt.getStoreName().toLowerCase();
            String date = receipt.getDate().toLowerCase();
            String price = receipt.getTotalPriceStirng().toLowerCase();


            if (storeName.contains(searchTerm) || date.contains(searchTerm) || price.contains(searchTerm)) {
                addReceipt(receipt);
            }

            for (ReceiptLineItem lineItem : receipt.getLineItems()) {
                lineItem.setItemFound(false);
                String lineItemName = lineItem.getItem().toLowerCase();
                String lineItemPrice = lineItem.getPriceString();
                if (lineItemName.contains(searchTerm) || lineItemPrice.contains(searchTerm)) {
                    lineItem.setItemFound(true);
                    addReceipt(receipt);
                }
            }

            for (Tag tag : receipt.getTags()) {
                tag.setSelected(false);
                if (tag.getName().toLowerCase().contains(searchTerm)) {
                    tag.setSelected(true);
                    addReceipt(receipt);
                }
            }
        }

        if (searchTerm.equals("")) {
            foundReceipts.clear();
            noSearchFoundText.setText("Enter a search term!");
        } else {
            noSearchFoundText.setText("Nothing Found!");
        }

        setReceiptsList();
    }

    private void addReceipt(ReceiptEntity receipt) {
        if (!foundReceipts.contains(receipt)) {
            ReceiptList.addToArray(foundReceipts, receipt);
            //foundReceipts.add(receipt);
        }
    }

    public void setReceiptsList() {
        ArrayList<ReceiptEntity> transfer = new ArrayList<ReceiptEntity>();
        transfer.addAll(foundReceipts);
        foundReceipts.clear();

        for (ReceiptEntity receiptEntity : transfer) {
            ReceiptList.addToArray(foundReceipts, receiptEntity);
        }

        if (foundReceipts.size() > 0) {
            noSearchFoundText.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            noSearchFoundText.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }

        animationView.setVisibility(View.VISIBLE);
        if (foundReceipts.size() >= 3) {
            animationView.setVisibility(View.GONE);
        }

        if (adapter == null) {
            adapter = new RecyclerViewAdapter(this, foundReceipts, this);
            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        } else {
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(ReceiptEntity item) {
        forceHide(this, editText);
        ReceiptPopup popUpClass = new ReceiptPopup();
        popUpClass.showPopupWindow(recyclerView, item, this, this, this);
    }


    private void setup() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setScene();
        linkCamera();
        linkChart();
        linkReceipt();
        linkSetting();
        linkSearch();
        linkSort();
    }

    private void setScene() {
        //Default toolbar setup
        camera = findViewById(R.id.camera_widget_search);
        chart = findViewById(R.id.receipt_chart_search);
        settings = findViewById(R.id.setting_widget_search);
        receipt = findViewById(R.id.receipt_widget_search);
        searchPage = findViewById(R.id.search_widget_search);

        searchSorting = findViewById(R.id.receipt_search_filter);

        editText = findViewById(R.id.search_text);
        editText.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        recyclerView = findViewById(R.id.recyclerViewSearch);
        noSearchFoundText = findViewById(R.id.no_search_found_text);
        animationView = findViewById(R.id.search_animation);
    }

    private void linkSort() {
        searchSorting.setOnClickListener(new View.OnClickListener() {
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
                SearchActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the search page from the receipt page
     */

    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

        searchPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.startActivity(intent);
            }
        });
    }

    /**
     * Accessing the profile page from the receipt page
     */

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SearchActivity.this.startActivity(intent);
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
                SearchActivity.this.startActivity(intent);
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
                SearchActivity.this.startActivity(intent);
            }
        });
    }

    public static void forceHide(@NonNull Activity activity, @NonNull EditText editText) {
        if (activity.getCurrentFocus() == null || !(activity.getCurrentFocus() instanceof EditText)) {
            editText.requestFocus();
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    @Override
    public void onItemDelete(ReceiptEntity receipt) {
        foundReceipts.remove(receipt);
        setReceiptsList();
    }
}






