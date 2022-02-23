package app_.smartreceipt.digitaldocs.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptLineItem;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.utilities.RecyclerLineItemAdapter;

/**
 * Displays the data collected from the receipt
 */

public class ImageTakenActivity extends AppCompatActivity {

    private TextView totalPriceText;
    private TextView dateText;
    private TextView companyNameString;
    private String companyName;
    private String totalPrice;
    private Context mContext = this;
    private String[] items;
    //private ListView listView;
    private Button confermationCloseButton;
    private ImageButton editButton;
    private TextView no_line_items;
    private RecyclerView recyclerView;

    private TextView dateNotFound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_on_image_taken);

        dateText = findViewById(R.id.date_added);
        companyNameString = findViewById(R.id.companyName);
        totalPriceText = findViewById(R.id.totalPrice);
        no_line_items = findViewById(R.id.no_line_items_new);
        recyclerView = findViewById(R.id.recyclerViewLineItemsnew);
        dateNotFound = findViewById(R.id.date_not_found);
        dateNotFound.setVisibility(View.GONE);

        //listView = findViewById(R.id.lineView);

        Intent intent1 = getIntent();
        totalPrice = intent1.getExtras().getString("total");


        String date = intent1.getExtras().getString("date");// THIS IS THE DATE IN STRING FORM FOR EXAMPLE DD/MM/YY

        if(date.equals(""))
        {
            SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
            date = df.format(new Date());
            dateNotFound.setVisibility(View.VISIBLE);
        } else {
            String[] splitDate = date.split("/");
            int repYear = Integer.parseInt(splitDate[2]);
            int repMonth = Integer.parseInt(splitDate[1]);
            int repDay = Integer.parseInt(splitDate[0]);

            if (repYear < 100) {
                repYear += 2000;
            }

            date = repYear + "/" + String.format("%02d", repMonth) + "/" + String.format("%02d", repDay);
        }


        //TO MARC, THIS IS LIST OF THE RECIEPT LINE ITEMS INDEX 0 contains the ITEM NAMES index 1 contains the ITEM PRICES.
        ArrayList<ArrayList<String>> list = (ArrayList<ArrayList<String>>)  intent1.getExtras().get("list");
        companyName = intent1.getExtras().getString("company");

        if (intent1.getExtras().get("abn").equals("Algorithm could not detect a valid ABN")) {
            companyName = "Unknown Company";
        }


        StringBuilder niceStoreName = new StringBuilder();

        boolean convertNext = true;
        for (char ch : companyName.toCharArray()) {
            if (Character.isSpaceChar(ch)) {
                convertNext = true;
            } else if (convertNext) {
                ch = Character.toTitleCase(ch);
                convertNext = false;
            } else {
                ch = Character.toLowerCase(ch);
            }
            niceStoreName.append(ch);
        }

        double totalLineItemPrice = 0;

        //create line item list
        ArrayList<ReceiptLineItem> itemList = new ArrayList<>();
        for(int i = 0; i < list.get(0).size(); i++) {
            try {
                String itemName = list.get(0).get(i);
                String itemPriceString = list.get(1).get(i);
                double itemPrice = Double.parseDouble(list.get(1).get(i));
                totalLineItemPrice += itemPrice;

                if (!itemPriceString.contains(".") && i + 1 < list.get(0).size()) {
                    //i ++;
                    //itemName += " " + list.get(0).get(i);
                    //itemPrice = Double.parseDouble(list.get(1).get(i));
                }

                itemList.add(new ReceiptLineItem(itemName,itemPrice));
            } catch (Exception ignored) {}
        }

        if (Double.parseDouble(totalPrice) == -1.0) {
            totalPrice = totalLineItemPrice + "";
        }

        //Create the receipt and upload it to the database.
        ReceiptEntity newReceipt = new ReceiptEntity(niceStoreName.toString(), date, Double.parseDouble(totalPrice));
        newReceipt.setLineItems(itemList);

        Uri uri = (Uri) intent1.getExtras().get("uri");
        ReceiptList.addForFirstTime(newReceipt, uri);

        companyNameString.setText(newReceipt.getStoreName());
        dateText.setText(newReceipt.getFormattedDate());
        totalPriceText.setText(newReceipt.getTotalPriceStirng());

        ArrayList<ReceiptLineItem> lineItems = (ArrayList<ReceiptLineItem>) newReceipt.getLineItems();

        if (lineItems.size() <= 0) {
            no_line_items.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            no_line_items.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        try {
            RecyclerLineItemAdapter adapter = new RecyclerLineItemAdapter(this, lineItems, null);
            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(this, 1, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        } catch (Exception e){
            e.printStackTrace();
        }

        //link buttons
        linkCloseButton();
        linkEditButton(newReceipt);
    }

    private void linkCloseButton() {
        final Intent intent = new Intent(this, ReceiptActivity.class);
        confermationCloseButton = findViewById(R.id.reciept_close);

        confermationCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageTakenActivity.this.startActivity(intent);
            }
        });
    }

    private void linkEditButton(ReceiptEntity newReceipt) {
        final Intent intent = new Intent(this, EditReceiptActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("receipt", newReceipt);
        intent.putExtras(bundle);

        editButton = findViewById(R.id.photo_take_edit);
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageTakenActivity.this.startActivity(intent);
            }
        });
    }
}