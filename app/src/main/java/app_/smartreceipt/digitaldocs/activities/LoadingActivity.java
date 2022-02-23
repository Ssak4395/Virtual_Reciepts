package app_.smartreceipt.digitaldocs.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import app_.smartreceipt.digitaldocs.R;


public class LoadingActivity {
    private Activity activity;
    private AlertDialog alertDialog;
    private String alertText;

    LoadingActivity(Activity activity, String text) {
        this.activity = activity;
        alertText = text;
    }

    LoadingActivity(Activity activity) {
        this.activity = activity;
        alertText = "Processing...";
    }

    void startLoadingAnimation() {
        //Declare a builder to start the dialog activity creation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //Declare inflater object, to explode layout on screen
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.custom_dialog,null);
        TextView dialogText = v.findViewById(R.id.dialog_text);
        if (alertText != null) {
            dialogText.setText((alertText + "\n\n Please be patient"));
        }
        builder.setView(v);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    void dismissDialog() {
        alertDialog.dismiss();
    }
}
