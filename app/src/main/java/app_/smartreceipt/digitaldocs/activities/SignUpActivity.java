package app_.smartreceipt.digitaldocs.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import app_.smartreceipt.digitaldocs.R;
import app_.smartreceipt.digitaldocs.utilities.AlertDialogs;
import app_.smartreceipt.digitaldocs.utilities.EmailValidator;
import app_.smartreceipt.digitaldocs.utilities.PasswordValidator;
import app_.smartreceipt.digitaldocs.utilities.SignUpHandler;

public class SignUpActivity extends AppCompatActivity {
    EditText editEmail;
    EditText editPassword;
    EditText editFirstName;
    EditText editLastName;
    Button signUp;
    LoadingActivity loadingDialog;
    AlertDialogs alertDialogs;
    SignUpHandler signUpHandler;
    PasswordValidator passwordValidator;
    EmailValidator emailValidator;

    TextView passwordHint;

    LinearLayout passwordChecker;

    ImageView minCharCheck;
    ImageView upperCharCheck;
    ImageView lowerCharCheck;
    ImageView numberCheck;
    ImageView specialCharCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__up_);

        setUp(); //setup scene & get references

        passwordHintLogic(); //Deal with password hint logic

        linkSignUpButton(); //Deal with pressing signup button
    }

    private void setUp() {
        loadingDialog = new LoadingActivity(this);
        alertDialogs = new AlertDialogs(this);
        signUpHandler = new SignUpHandler();
        passwordValidator = new PasswordValidator();
        emailValidator = new EmailValidator();
        editEmail = findViewById(R.id.editEmail);
        editEmail.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        editPassword = findViewById(R.id.editPassInput);
        editPassword.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        signUp = findViewById(R.id.sign_up_button);
        editFirstName = findViewById(R.id.editFirstName);
        editFirstName.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        editLastName = findViewById(R.id.editLastName);
        editLastName.getBackground().setColorFilter(Color.parseColor("#9AA28D"), PorterDuff.Mode.SRC_IN);

        passwordHint = findViewById(R.id.password_hint);

        passwordChecker = findViewById(R.id.password_checker);
        passwordChecker.setVisibility(View.GONE);

        minCharCheck = findViewById(R.id.min_char_check);
        upperCharCheck = findViewById(R.id.upper_case_check);
        lowerCharCheck = findViewById(R.id.lower_case_check);
        numberCheck = findViewById(R.id.number_check);
        specialCharCheck = findViewById(R.id.special_char_check);
    }

    private void passwordHintLogic() {

        editPassword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    editEmail.setVisibility(View.GONE);
                    editLastName.setVisibility(View.GONE);
                    passwordChecker.setVisibility(View.VISIBLE);
                } else {
                    passwordChecker.setVisibility(View.GONE);
                    editEmail.setVisibility(View.VISIBLE);
                    editLastName.setVisibility(View.VISIBLE);
                }
            }
        });

        editPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //nothing, ignored
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                //update password checks
                String password = s.toString();


                if (s.length() >= 8) {
                    minCharCheck.setImageResource(R.drawable.baseline_correct_24);
                } else {
                    minCharCheck.setImageResource(R.drawable.baseline_incorect_24);
                }

                if (password.matches(".*[A-Z].*")) {
                    upperCharCheck.setImageResource(R.drawable.baseline_correct_24);
                } else {
                    upperCharCheck.setImageResource(R.drawable.baseline_incorect_24);
                }

                if (password.matches(".*[a-z].*")) {
                    lowerCharCheck.setImageResource(R.drawable.baseline_correct_24);
                } else {
                    lowerCharCheck.setImageResource(R.drawable.baseline_incorect_24);
                }

                if (password.matches(".*\\d.*")) {
                    numberCheck.setImageResource(R.drawable.baseline_correct_24);
                } else {
                    numberCheck.setImageResource(R.drawable.baseline_incorect_24);
                }

                if (password.matches(".*\\W.*")) {
                    specialCharCheck.setImageResource(R.drawable.baseline_correct_24);
                } else {
                    specialCharCheck.setImageResource(R.drawable.baseline_incorect_24);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //nothing, ignored
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText && v.getId() == R.id.editPassInput) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }

    private void linkSignUpButton() {
        signUpHandler = new SignUpHandler();
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean validEmail;
                boolean validPassword;
                if (editEmail.getText().toString().isEmpty()) {
                    validEmail = false;
                } else {
                    validEmail = emailValidator.validate(editEmail.getText().toString());
                }
                if (editPassword.getText().toString().isEmpty()) { // avoids null pointer exception in validate method
                    validPassword = false;
                } else {
                    validPassword = passwordValidator.validate(editPassword.getText().toString());
                }
                int duration = Toast.LENGTH_LONG;
                if (!validEmail) { // invalid email displays error message
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid email";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                if (!validPassword) { // invalid password displays error message
                    Context context = getApplicationContext();
                    CharSequence text = "Invalid password, please use at least: 1 Upper case, 1 lower case, 1 digit and 1 Symbol";
                    Toast toast = Toast.makeText(context, text, duration);
                    toast.show();
                }
                if (validEmail && validPassword) { // if both are valid we do our normal sign-up routine
                    signUpHandler.signUp(editEmail.getText().toString(),editPassword.getText().toString(),
                            editFirstName.getText().toString(),editLastName.getText().toString()
                    );
                    loadingDialog.startLoadingAnimation();
                    //Create new thread to dismiss the loading dialog
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            loadingDialog.dismissDialog();
                            //Go Back to sign in page
                            reloadDashboard();
                        }
                    },2000);
                }
            }
        });
    }

    public void reloadDashboard() {
        final Intent intent = new Intent(this,SignUpConfirmationActivity.class);
        this.startActivity(intent);
    }
}