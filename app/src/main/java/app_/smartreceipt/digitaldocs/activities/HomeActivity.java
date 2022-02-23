package app_.smartreceipt.digitaldocs.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.utilities.AlertDialogs;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class HomeActivity extends AppCompatActivity {
    private static final boolean AUTO_HIDE = true;
    public static Context context;
    private EditText textInputEmail;
    private EditText textInputPassword;
    private TextView signUpPrompt;
    Button signInPrompt;
    Button signIn;
    FirebaseAuth firebaseAuth;
    private TextView forgotPassword;
    LoadingActivity loadingDialog;
    AlertDialogs alertDialogs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);
        context = getApplicationContext();
        loadingDialog = new LoadingActivity(this);
        alertDialogs = new AlertDialogs(this);

        signIn = findViewById(R.id.SignIn);
        firebaseAuth = FirebaseAuth.getInstance();

        //
        if (firebaseAuth.getCurrentUser() != null) {
            final Intent intent = new Intent(this,DashboardActivity.class);
            HomeActivity.this.startActivity(intent);
            finish();
        }

        setScene();
        explodeSignUpScene();
        explodeForgotPassword();

        signIn.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View view) {
              loadingDialog.startLoadingAnimation();
              handleSignIn(textInputEmail.getText().toString(),textInputPassword.getText().toString());
          }
        });
    }

    private void setScene() {
      textInputEmail = findViewById(R.id.email);
      textInputEmail.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

      textInputPassword = findViewById(R.id.password);
      textInputPassword.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

      signUpPrompt = findViewById(R.id.sign_up);
      signInPrompt = findViewById(R.id.SignIn);
      forgotPassword = findViewById(R.id.forget_widget); //link the sign up page to the forget pw
    }
    
    private void explodeForgotPassword() {
        final Intent intent = new Intent(this, ForgotPasswordActivity.class);

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { //linking needed
                HomeActivity.this.startActivity(intent);
            }
        });
    }
    

    private void explodeSignUpScene() {
        final Intent intent = new Intent(this, SignUpActivity.class);

        signUpPrompt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HomeActivity.this.startActivity(intent);
            }
        });
    }

    public void handleSignIn(String email, String password) {
        final Intent intent = new Intent(this,DashboardActivity.class);

        firebaseAuth.signInWithEmailAndPassword(email.trim(),password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loadingDialog.dismissDialog();
                if(task.isSuccessful()) {
                    if(firebaseAuth.getCurrentUser().isEmailVerified()) {
                        HomeActivity.this.startActivity(intent);
                    } else {
                        Toast.makeText(HomeActivity.this, "Email not verified.", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public static Context getAppContext() {
        return HomeActivity.context;
    }
}