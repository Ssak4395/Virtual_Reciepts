package app_.smartreceipt.digitaldocs.utilities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.Tag;

public class RecyclerTagAdapter extends RecyclerView.Adapter {

    ArrayList mValues;
    Context mContext;
    protected ItemListener mListener;
    boolean mEditable;

    public RecyclerTagAdapter(Context context, ArrayList values, ItemListener itemListener, boolean editable) {
        mValues = values;
        mContext = context;
        mListener = itemListener;
        mEditable = editable;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView tagName;
        public ImageButton tagDelete;
        public RelativeLayout relativeLayout;
        public CardView cardView;
        public Tag item;

        public ViewHolder(View v) {
            super(v);

            v.setOnClickListener(this);
            tagName = (TextView) v.findViewById(R.id.tag_name);
            relativeLayout = (RelativeLayout) v.findViewById(R.id.relativeLayoutTag);
            cardView = v.findViewById(R.id.cardViewTag);

            if (mEditable) {
                tagDelete = (ImageButton) v.findViewById(R.id.tag_delete);
                tagDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onItemDelete(item);
                    }
                });
            }
        }

        public void setData(Tag item) {
            this.item = item;
            tagName.setText(item.getName());

            if (item.isSelected()) {
                cardView.setCardBackgroundColor(mContext.getResources().getColor(R.color.tagSelected));
                if (mEditable) {
                    tagDelete.setBackgroundColor(mContext.getResources().getColor(R.color.tagSelected));
                } else {
                    item.setSelected(false);
                }
            }

            relativeLayout.setClipToOutline(true);
        }


        @Override
        public void onClick(View view) {
            if (mListener != null) {
                mListener.onItemClick(item);
            }
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (mEditable) {
            view = LayoutInflater.from(mContext).inflate(R.layout.recycler_edit_tag_layout, parent, false);
        } else {
            view = LayoutInflater.from(mContext).inflate(R.layout.recycler_tag_layout, parent, false);
        }

        return new RecyclerTagAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerTagAdapter.ViewHolder hold = (RecyclerTagAdapter.ViewHolder) holder;
        hold.setData((Tag) mValues.get(position));
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public interface ItemListener {
        void onItemClick(Tag item);
        void onItemDelete(Tag item);
    }
}
