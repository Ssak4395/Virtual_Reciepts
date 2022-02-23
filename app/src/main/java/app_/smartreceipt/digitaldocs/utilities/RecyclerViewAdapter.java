
package app_.smartreceipt.digitaldocs.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.Tag;


public class RecyclerViewAdapter extends RecyclerView.Adapter {

    ArrayList mValues;
    Context mContext;
    protected ItemListener mListener;

    public RecyclerViewAdapter(Context context, ArrayList values, ItemListener itemListener) {

        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, RecyclerTagAdapter.ItemListener {

        public TextView receiptStoreName;
        public TextView receiptDate;
        public TextView receiptItemNumber;
        public TextView receiptTotalPrice;
        public RelativeLayout relativeLayout;
        private RecyclerView recyclerViewTag;
        public ImageView companyImage;
        ReceiptEntity item;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            receiptStoreName = (TextView) v.findViewById(R.id.receipt_store_name);
            receiptDate = (TextView) v.findViewById(R.id.receipt_date);
            receiptItemNumber = (TextView) v.findViewById(R.id.receipt_total_items);
            receiptTotalPrice = (TextView) v.findViewById(R.id.receipt_total_price);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayout);
            companyImage = (ImageView) v.findViewById(R.id.receipt_store_image);
            recyclerViewTag = (RecyclerView) v.findViewById(R.id.recyclerLayoutTagReceipt);
            recyclerViewTag.setVisibility(View.GONE);

        }

        public void setData(ReceiptEntity item) {
            this.item = item;
            receiptStoreName.setText(item.getStoreName());
            receiptDate.setText(item.getFormattedDate());

            int numOfItems;
            try {
                numOfItems = item.getLineItems().size();
            } catch (Exception e) {
                numOfItems = 0;
            }
            if (numOfItems > 1) {
                receiptItemNumber.setText((numOfItems + " items"));
            } else if (numOfItems == 1) {
                receiptItemNumber.setText((numOfItems + " item"));
            } else {
                receiptItemNumber.setText("No items found");
            }

            receiptTotalPrice.setText(item.getTotalPriceStirng());
            companyImage.setImageResource(item.getImagePath());
            relativeLayout.setClipToOutline(true);

            ArrayList<Tag> tags = (ArrayList<Tag>) item.getTags();
            ArrayList<Tag> first = new ArrayList<>();
            if(tags.size() > 0) {
                first.add(tags.get(0));
            }

            RecyclerTagAdapter tagAdapter = new RecyclerTagAdapter(mContext, first, this, false);
            recyclerViewTag.setAdapter(tagAdapter);

            LinearLayoutManager tagManager = new LinearLayoutManager(mContext, GridLayoutManager.HORIZONTAL, false);
            recyclerViewTag.setLayoutManager(tagManager);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }

        @Override
        public void onItemClick(Tag item) {

        }

        @Override
        public void onItemDelete(Tag item) {
            //no delete functionality on this page
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //change this for other layout
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_receipt_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder hold = (ViewHolder) holder;
        hold.setData((ReceiptEntity) mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(ReceiptEntity item);
    }
}
