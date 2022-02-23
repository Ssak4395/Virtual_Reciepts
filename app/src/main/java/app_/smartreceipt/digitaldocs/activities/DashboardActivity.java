package app_.smartreceipt.digitaldocs.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.model.ReceiptEntity;
import app_.smartreceipt.digitaldocs.model.ReceiptList;
import app_.smartreceipt.digitaldocs.model.User;
import app_.smartreceipt.digitaldocs.services.algorithm.algorithms;
import app_.smartreceipt.digitaldocs.utilities.ReceiptPopup;
import app_.smartreceipt.digitaldocs.utilities.RecyclerViewAdapter;


public class DashboardActivity extends AppCompatActivity implements RecyclerViewAdapter.ItemListener, ReceiptUpdate {
    private ImageButton camera;
    private ImageButton chart;
    private ImageButton settings;
    private ImageButton receipt;
    private ImageButton search;
    private LottieAnimationView animationView;

    private ImageView mainImage;
    private TextView dashboeardText, welcomeText;
    private RecyclerView recyclerView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard_page);
        ReceiptList.updateContext(getApplicationContext());
        ReceiptList.updateUser();
        User.updateUser();
        User.checkFirebaseForTutorial(this);

        setWelcomeText();
        setScene();
        linkCamera();
        linkChart();
        linkReceipt();
        linkSetting();
        linkSearch();

        loadReceipts();
    }


    private void setScene() {
        camera = findViewById(R.id.camera_widget);
        chart = findViewById(R.id.receipt_chart_dashboard);
        settings = findViewById(R.id.setting_one);
        receipt = findViewById(R.id.receipt_widget);
        search = findViewById(R.id.search_widget);

        mainImage = findViewById(R.id.Main_Activity);
        dashboeardText = findViewById(R.id.dashboeardText);
        recyclerView = findViewById(R.id.recyclerViewDashboard);
        recyclerView.setVisibility(View.GONE);

        animationView=findViewById(R.id.Main_Activity);
        animationView.playAnimation();
    }

    private void setWelcomeText() {
        welcomeText = findViewById(R.id.welcomeText);
        FirebaseAuth fAuth = FirebaseAuth.getInstance();
        FirebaseFirestore fStore = FirebaseFirestore.getInstance();
        String userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);

        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {

            }
        });

    }

    private void loadReceipts() {
        try {
            ReceiptList.loadData();
        } catch (Exception e) {
            e.printStackTrace();
        }
        setReceiptsList();
        ReceiptList.updateReceiptsList(this);
    }

    public void setReceiptsList() {
        ArrayList<ReceiptEntity> recentReceipts = new ArrayList<ReceiptEntity>();
        int numberOfReceiptsToShow = 6;

        ArrayList<ReceiptEntity> receipts = ReceiptList.getReceipts();
        if (receipts.size() <= 0) {
            mainImage.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            dashboeardText.setText("No scans saved yet, go add one!");
            return;
            
        } else {
            mainImage.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            dashboeardText.setText("Your most recent scans!");
        }

        if (receipts.size() < numberOfReceiptsToShow) {
            numberOfReceiptsToShow = receipts.size();
        }

        for (int i = 0; i < numberOfReceiptsToShow; i++) {
            recentReceipts.add(receipts.get(i));
        }

        RecyclerViewAdapter adapter = new RecyclerViewAdapter(this, recentReceipts, this);
        recyclerView.setAdapter(adapter);

        GridLayoutManager manager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(manager);
    }

    @Override
    public void onItemClick(ReceiptEntity item) {
        algorithms algorithm = new algorithms();

        ReceiptPopup popUpClass = new ReceiptPopup();
        popUpClass.showPopupWindow(recyclerView, item, this, this);
    }

    private void linkCamera() {
        final Intent intent = new Intent(this, CameraActivity.class);

        camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardActivity.this.startActivity(intent);
            }
        });
    }

    private void linkChart() {
        final Intent intent = new Intent(this, ChartActivity.class);

        chart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardActivity.this.startActivity(intent);
            }
        });
    }
    private void linkSearch() {
        final Intent intent = new Intent(this, SearchActivity.class);

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardActivity.this.startActivity(intent);
            }
        });
    }

    private void linkSetting() {
        final Intent intent = new Intent(this, SettingsActivity.class);

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardActivity.this.startActivity(intent);
            }
        });
    }

    private void linkReceipt() {
        final Intent intent = new Intent(this, ReceiptActivity.class);

        receipt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashboardActivity.this.startActivity(intent);
            }
        });
    }
}
