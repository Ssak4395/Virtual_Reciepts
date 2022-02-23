package app_.smartreceipt.digitaldocs.utilities;

import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import app_.smartreceipt.digitaldocs.R;

public class DeveloperNamesPopup {

    public void showPopupWindow(final View view) {

        LayoutInflater inflater = (LayoutInflater) view.getContext().getSystemService(view.getContext().LAYOUT_INFLATER_SERVICE);
        final View popupView = inflater.inflate(R.layout.developer_list_popup, null);

        int width = LinearLayout.LayoutParams.MATCH_PARENT;
        int height = LinearLayout.LayoutParams.MATCH_PARENT;

        boolean focusable = true;

        final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

        TextView linkJess = popupView.findViewById(R.id.jess_link);
        linkJess.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkNabeera = popupView.findViewById(R.id.nabeera_link);
        linkNabeera.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkJack = popupView.findViewById(R.id.jack_link);
        linkJack.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkSadmna = popupView.findViewById(R.id.sadman_link);
        linkSadmna.setMovementMethod(LinkMovementMethod.getInstance());

        TextView linkMarc = popupView.findViewById(R.id.marc_link);
        linkMarc.setMovementMethod(LinkMovementMethod.getInstance());

        Button close = popupView.findViewById(R.id.developer_close_button);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
    }
}
