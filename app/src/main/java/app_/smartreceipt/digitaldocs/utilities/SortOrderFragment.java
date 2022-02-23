package app_.smartreceipt.digitaldocs.utilities;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import app_.smartreceipt.digitaldocs.activities.ReceiptUpdate;
import app_.smartreceipt.digitaldocs.model.ReceiptList;

public class SortOrderFragment extends DialogFragment {

    private String selected = "By Date";

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        final String[] sortOptions = new String[]{"By Date", "Alphabetically", "By Price"};
        int index = ReceiptList.getSorting();
        selected = sortOptions[index];

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick a sorting method:");


        builder.setSingleChoiceItems(sortOptions, index, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                selected = sortOptions[which];
            }
        });


        builder.setPositiveButton("Sort", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ReceiptList.updateSorting(selected);
                ReceiptUpdate pageUpdate = (ReceiptUpdate) getActivity();
                ReceiptList.updateList();
                pageUpdate.setReceiptsList();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //nothing
            }
        });

        return builder.create();
    }
}
