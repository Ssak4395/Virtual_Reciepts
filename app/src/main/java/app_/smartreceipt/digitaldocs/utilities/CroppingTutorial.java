package app_.smartreceipt.digitaldocs.utilities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.activities.GetStartedActivity;
import app_.smartreceipt.digitaldocs.model.User;


public class CroppingTutorial {
    private Activity activity;
    private AlertDialog alertDialog;

    public CroppingTutorial(Activity activity) {
        this.activity = activity;
        startDialog();
    }

    private void startDialog() {
        //Declare a builder to start the dialog activity creation
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        //Declare inflater object, to explode layout on screen
        LayoutInflater layoutInflater = activity.getLayoutInflater();
        View v = layoutInflater.inflate(R.layout.cropping_tutorial,null);

        CheckBox checkBox = v.findViewById(R.id.checkbox_tutorial);

        Button closeButton = v.findViewById(R.id.tutorial_close_button);
        linkClose(closeButton, checkBox);

        TextView getStarted = v.findViewById(R.id.get_started_guid);
        linkGetStarted(getStarted);

        builder.setView(v);
        //builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }

    private void linkGetStarted(TextView getStarted) {
        getStarted.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GetStartedActivity.class);
                activity.startActivity(intent);
            }
        });
    }

    private void linkClose(Button close, CheckBox checkBox) {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.setNeedsCroppingTutorial(!checkBox.isChecked());
                alertDialog.dismiss();
            }
        });
    }
}
