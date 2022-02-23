package app_.smartreceipt.digitaldocs.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import app_.smartreceipt.digitaldocs.R;


public class AlgorithmFailedDialog {
    private Activity activity;
    private AlertDialog alertDialog;
    private String alertText;

    public AlgorithmFailedDialog(Activity activity, String text) {
        this.activity = activity;
        alertText = text;
        startDialog();
    }

    private void startDialog() {
        //Declare a builder to start the dialog activity creation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //Declare inflater object, to explode layout on screen
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.algorithm_failed_dialog,null);

        TextView crashText = v.findViewById(R.id.crash_text);

        Button closeButton = v.findViewById(R.id.close_button);
        linkClose(closeButton);


        crashText.setText(alertText);
        builder.setView(v);
        //builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void linkClose(Button close) {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
    }
}
