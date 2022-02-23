package app_.smartreceipt.digitaldocs.utilities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.activities.EditLineItemActivity;
import app_.smartreceipt.digitaldocs.activities.EditReceiptActivity;
import app_.smartreceipt.digitaldocs.activities.IndividualReceipt;
import app_.smartreceipt.digitaldocs.activities.ReceiptUpdate;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptLineItem;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.model.Tag;

public class ReceiptPopup implements RecyclerLineItemAdapter.ItemListener, RecyclerTagAdapter.ItemListener {

    Context context;

    DeleteListener deleteListener;

    public void showPopupWindow(final View view, final ReceiptEntity item, final ReceiptUpdate receiptView, final Context context, final DeleteListener deleteListener) {
        this.deleteListener = deleteListener;
        showPopupWindow(view, item, receiptView, context);
    }



    //PopupWindow display method
    public void showPopupWindow(final View view, final ReceiptEntity item, final ReceiptUpdate receiptView, final Context context) {
        this.context = context;


        //Create a View object yourself through inflater
        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        View popupView;
        if (item.getImagePath() != 0) {
            popupView = inflater.inflate(R.layout.receipt_popup_image, null);
            ImageView store_image = popupView.findViewById(R.id.receipt_popup_image);
            store_image.setImageResource(item.getImagePath());
        } else {
            popupView = inflater.inflate(R.layout.receipt_popup, null);
        }

        //Specify the length and width through constants
        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        //Make Inactive Items Outside Of PopupWindow
        boolean focusable = true;

        //Create a window with our parameters
        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        //Set the location of the window on the screen
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        //Initialize the elements of our window, install the handler


        TextView store_name = popupView.findViewById(R.id.store_text);
        TextView date_text = popupView.findViewById(R.id.date_text);
        RecyclerView recyclerView = popupView.findViewById(R.id.recyclerViewLineItems);
        RecyclerView recyclerViewTag = popupView.findViewById(R.id.recyclerLayoutTagPopup);
        TextView total_text = popupView.findViewById(R.id.total_text);
        TextView no_line_items = popupView.findViewById(R.id.no_line_items);

        store_name.setText(item.getStoreName());
        date_text.setText(item.getFormattedDate());

        ArrayList<Tag> tags = (ArrayList<Tag>) item.getTags();

        RecyclerTagAdapter tagAdapter = new RecyclerTagAdapter(context, tags, this, false);
        recyclerViewTag.setAdapter(tagAdapter);

        LinearLayoutManager tagManager = new LinearLayoutManager(context, GridLayoutManager.HORIZONTAL, false);
        recyclerViewTag.setLayoutManager(tagManager);

        ArrayList<ReceiptLineItem> arrayList = (ArrayList<ReceiptLineItem>) item.getLineItems();

        if (arrayList.size() <= 0) {
            no_line_items.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            no_line_items.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
        }

        try {
            RecyclerLineItemAdapter adapter = new RecyclerLineItemAdapter(context, arrayList, this);
            recyclerView.setAdapter(adapter);

            GridLayoutManager manager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);
            recyclerView.setLayoutManager(manager);
        } catch (Exception e){
        }



        total_text.setText("Total: " + item.getTotalPriceStirng());

        Button buttonClose = popupView.findViewById(R.id.messageButton);
        buttonClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

        final Intent a  = new Intent(context, IndividualReceipt.class);
        ImageButton i = popupView.findViewById(R.id.access);
        i.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("receipt", item);
                a.putExtras(bundle);
                context.startActivity(a);
            }
        });

        ImageButton location = popupView.findViewById(R.id.location);
        //removed for now
        location.setVisibility(View.GONE);
        location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        ImageButton buttonDelete = popupView.findViewById(R.id.receipt_delete);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ReceiptList.deleteReceipt(item, receiptView);
                popupWindow.dismiss();

                if (deleteListener != null) {
                    deleteListener.onItemDelete(item);
                }
            }
        });


        final Intent intent = new Intent(context, EditReceiptActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("receipt", item);
        intent.putExtras(bundle);

        ImageButton buttonEdit = popupView.findViewById(R.id.receipt_edit);
        buttonEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(intent);
            }
        });

        //Handler for clicking on the inactive zone of the window
        popupView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                popupWindow.dismiss();
                return true;
            }
        });
    }

    @Override
    public void onItemClick(ReceiptLineItem item) {
        final Intent intent = new Intent(context, EditLineItemActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("line_item", item);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    @Override
    public void onItemClick(Tag item) {

    }

    @Override
    public void onItemDelete(Tag item) {
    }

    public interface DeleteListener {
        void onItemDelete(ReceiptEntity receipt);
    }
}