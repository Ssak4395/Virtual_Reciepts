package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.model.Tag;
import app_.smartreceipt.digitaldocs.utilities.RecyclerTagAdapter;

public class EditReceiptActivity extends AppCompatActivity implements RecyclerTagAdapter.ItemListener {

    private EditText companyNameEdit;
    private EditText dateEdit;
    private EditText priceEdit;
    private EditText tagEdit;

    private Button saveButton;

    private ReceiptEntity editReceipt;

    private RecyclerView recyclerView;
    private ArrayList<Tag> tags;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_receipt_activity);

        companyNameEdit = findViewById(R.id.companyNameEdit);
        companyNameEdit.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        dateEdit = findViewById(R.id.dateEdit);
        dateEdit.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        priceEdit = findViewById(R.id.totalPriceEdit);
        priceEdit.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        tagEdit = findViewById(R.id.tagEdit);
        tagEdit.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        saveButton = findViewById(R.id.receipt_save_button);

        editReceipt = (ReceiptEntity) getIntent().getExtras().getSerializable("receipt");

        companyNameEdit.setText(editReceipt.getStoreName());
        dateEdit.setText(editReceipt.getDate());
        priceEdit.setText(editReceipt.getTotalPrice() + "");

        tags = (ArrayList<Tag>) editReceipt.getTags();
        recyclerView = findViewById(R.id.recyclerLayoutTagEdit);

        setRecyclerView();


        tagEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = s.toString();

                if(input.endsWith("\n")) {
                    input = input.replace("\n", "").trim();

                    for (Tag t : tags) {
                        if (t.getName().equals(input)) {
                            tagEdit.setText("");
                            Toast.makeText(getApplicationContext(), "Already added", Toast.LENGTH_SHORT).show();
                        } else if (tags.size() >= 3) {
                            Toast.makeText(getApplicationContext(), "Maximum number of tags reached", Toast.LENGTH_SHORT).show();
                        }
                    }
                    tags.add(new Tag(input, tags.size()));
                    tagEdit.setText("");
                    setRecyclerView();
                } else if (input.contains("\n")) {
                    int index = input.indexOf("\n");
                    tagEdit.setText(input.replace("\n", ""));
                    tagEdit.setSelection(index);
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });


//        tagEdit.setOnKeyListener(new View.OnKeyListener() {
//            @Override
//            public boolean onKey(View v, int keyCode, KeyEvent event) {
//                System.out.println(v);
//                System.out.println(keyCode);
//                System.out.println(event);
//                Tag selected = null;
//                if (tags.size() > 0) {
//                    selected = tags.get(tags.size() - 1);
//                }
//
//                if ((event.getAction() == KeyEvent.ACTION_DOWN) && ((keyCode == KeyEvent.KEYCODE_ENTER) || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
//                    String name = tagEdit.getText().toString();
//                    for (Tag t : tags) {
//                        if (t.getName().equals(name)) {
//                            tagEdit.setText("");
//                            return false;
//                        } else if (tags.size() >= 3) {
//                            Toast.makeText(getApplicationContext(), "Maximum number of tags reached", Toast.LENGTH_SHORT).show();
//                            return false;
//                        }
//                    }
//                    tags.add(new Tag(name, tags.size()));
//                    tagEdit.setText("");
//                    setRecyclerView();
//                    return false;
//                } else if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_DEL)) {
//                    if (tagEdit.getText().toString().equals("")) {
//                        if (selected == null) {
//                            return false;
//                        }
//
//                        if (selected.isSelected()) {
//                            onItemDelete(selected);
//                        } else {
//                            selected.setSelected(true);
//                            setRecyclerView();
//                        }
//                        return false;
//                    }
//                }
//                if (selected != null && selected.isSelected()) {
//                    selected.setSelected(false);
//                    setRecyclerView();
//                }
//                return false;
//            }
//        });
        linkSaveButton();
    }

    private void setRecyclerView() {
        RecyclerTagAdapter adapter = new RecyclerTagAdapter(this, tags, this, true);
        recyclerView.setAdapter(adapter);

        LinearLayoutManager manager = new LinearLayoutManager(this, GridLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(manager);
    }

    private void linkSaveButton() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //name check
                String companyName = companyNameEdit.getText().toString();

                if (companyName.equals("")) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid Company Name. please provide a name!", Toast.LENGTH_SHORT).show();
                    return;
                }
                editReceipt.setStoreName(companyName);


                // Date checks, to ensure date has the right format
                String newDate = dateEdit.getText().toString();
                String[] date_test = newDate.split("/");
                if (date_test.length != 3|| !(date_test[0].length() == 4 && date_test[1].length() == 2 && date_test[2].length() == 2)) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid date! Please use yyyy/mm/dd!", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    int year = Integer.parseInt(date_test[0]);
                    int month = Integer.parseInt(date_test[1]);
                    int day = Integer.parseInt(date_test[2]);
                    if (year > Calendar.getInstance().get(Calendar.YEAR) || year < 1900 || month > 12 || month < 0 || day > 31 || day < 0) {
                        Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid date! Please use real dates!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid date! Please use numbers only!", Toast.LENGTH_SHORT).show();
                    return;
                }

                editReceipt.setDate(newDate);
                //end of date check

                //price check and round
                double priceText;
                try {
                    priceText = Double.parseDouble(priceEdit.getText().toString());
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext().getApplicationContext(), "Invalid price! Please use numbers only!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //rounding to 2 decimal places
                priceText = Math.round(priceText * 100.0) / 100.0;

                editReceipt.setTotalPrice(priceText);

                editReceipt.setTags(tags);

                ReceiptList.deleteReceipt(editReceipt, null);

                ReceiptList.addReceipt(editReceipt);

                Bundle bundle = new Bundle();
                bundle.putSerializable("receipt", editReceipt);
                intent.putExtras(bundle);

                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(Tag item) {
    }

    @Override
    public void onItemDelete(Tag item) {
        for (Tag tag : tags) {
            if (tag == item) {
                tags.remove(tag);
                setRecyclerView();
                return;
            }
        }
    }
}

