package app_.smartreceipt.digitaldocs.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptLineItem;


public class RecyclerLineItemAdapter extends RecyclerView.Adapter {

    ArrayList mValues;
    Context mContext;
    private int colorChanger = 0;
    protected ItemListener mListener;

    public RecyclerLineItemAdapter(Context context, ArrayList values, ItemListener itemListener) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView lineitemName;
        public TextView lineitemPrice;
        public RelativeLayout relativeLayout;
        ReceiptLineItem item;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            lineitemName = (TextView) v.findViewById(R.id.line_item_name);
            lineitemPrice = (TextView) v.findViewById(R.id.line_item_price);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutLineItems);
        }

        public void setData(ReceiptLineItem item) {
            this.item = item;
            lineitemName.setText(item.displayItem());
            lineitemPrice.setText(item.getPriceString());
            relativeLayout.setClipToOutline(true);



            if (colorChanger % 2 == 0) {
                relativeLayout.setBackgroundResource(R.drawable.line_item_color_2);
            } else {
                relativeLayout.setBackgroundResource(R.drawable.line_item_color_1);
            }
            colorChanger++;

            if (item.isItemFound()) {
                relativeLayout.setBackgroundResource(R.color.itemFoundColor);
            }
            item.setItemFound(false);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    @Override
    public RecyclerLineItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_lineitem_layout, parent, false);

        return new RecyclerLineItemAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerLineItemAdapter.ViewHolder hold = (RecyclerLineItemAdapter.ViewHolder) holder;
        hold.setData((ReceiptLineItem) mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(ReceiptLineItem item);
    }
}
