package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptLineItem;
import app_.smartreceipt.digitaldocs.model.ReceiptList;

public class EditLineItemActivity extends AppCompatActivity {
    private EditText lineitemName;
    private EditText lineitemPrice;

    private Button saveButton;

    private ReceiptLineItem editLineItem;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_lineitem_layout);

        lineitemName = findViewById(R.id.line_item_name_edit);
        lineitemName.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        lineitemPrice = findViewById(R.id.line_item_price_edit);
        lineitemPrice.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        saveButton = findViewById(R.id.lineitem_save_button);

        editLineItem = (ReceiptLineItem) getIntent().getExtras().getSerializable("line_item");

        lineitemName.setText(editLineItem.getItem());
        lineitemPrice.setText((editLineItem.getPrice() + ""));

        linkSaveButton();
        linkDeleteButton();
    }

    private void linkDeleteButton() {
        ImageButton delete = findViewById(R.id.line_item_delete);

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiptEntity receipt = editLineItem.getReceiptOwner();
                ArrayList<ReceiptLineItem> lineItems = (ArrayList<ReceiptLineItem>) receipt.getLineItems();

                lineItems.remove(editLineItem);

                saveAndClose();
            }
        });
    }

    private void linkSaveButton() {

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //name check
                String companyName = lineitemName.getText().toString();

                if (companyName.equals("")) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid Line Item. please provide a name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                editLineItem.setItem(companyName);

                //price check and round
                double priceText;
                try {
                    priceText = Double.parseDouble(lineitemPrice.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid price! Please use numbers only!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //rounding to 2 decimal places
                priceText = Math.round(priceText * 100.0) / 100.0;

                editLineItem.setPrice(priceText);

                //end of price check

                saveAndClose();
            }
        });
    }

    public void saveAndClose() {
        Intent intent = new Intent(this, ReceiptActivity.class);
        ReceiptEntity receipt = editLineItem.getReceiptOwner();

        ReceiptList.deleteReceipt(receipt, null);
        ReceiptList.addReceipt(receipt);


        Bundle bundle = new Bundle();
        bundle.putSerializable("receipt", receipt);
        intent.putExtras(bundle);

        startActivity(intent);
    }
}
