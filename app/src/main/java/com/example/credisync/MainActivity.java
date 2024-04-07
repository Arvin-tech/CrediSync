package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    protected final String TAG = this.getClass().getName(); //tag argument is the tag of the message,  msg argument is the message to display on the Logcat
    protected FirebaseFirestore db; //cloud fire store
    protected AppCompatButton loginButton;
    protected TextInputLayout emailInputLayout, passwordInputLayout;
    protected EditText emailTxt, passwordTxt; //email and password input
    protected TextView forgotPassTxt; //don't have an account yet? && reset password
    protected String inputtedEmail, inputtedPassword;
    protected Calendar calendar = Calendar.getInstance();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    protected String currentDate = simpleDateFormat.format(calendar.getTime()); //or currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    protected String userEmail; //used for updating last login activity
    protected int redColor = Color.parseColor("#FF0000"); // Set the error text color to red
    protected TextView signupTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        db = FirebaseFirestore.getInstance(); //initialize firebase fire store

        findViewById(); //reference to ui elements method call
        textWatchers(); //text watchers method call
        emailInputListener(); //input listener for email address edit text
        passwordInputListener(); //input listener for password edit text

        signupTxt.setPaintFlags(signupTxt.getPaintFlags()|Paint.UNDERLINE_TEXT_FLAG); //underline join now text view

        //login process
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //store user input to global variables : inputtedEmail and inputtedPassword
                inputtedEmail = emailTxt.getText().toString().trim();
                inputtedPassword = passwordTxt.getText().toString().trim();

                //validations
                if(validEmail(inputtedEmail) && validPassword(inputtedPassword)){
                    loginUser();
                }
            }
        });

        //forgot password?
        forgotPassTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), ForgotPassword.class);
                startActivity(intent);
                finish(); //so they wont have to use back button instead they will use the back button provided in forgot pass activity
            }
        });

        //don't have an account? Signup
        signupTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), UserSignupPrompt.class);
                startActivity(intent); //redirect to user signup prompt
            }
        });
    }

    //set the status bar color
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    //reference to ui elements
    protected void findViewById(){
        emailTxt = (EditText) findViewById(R.id.emailEditTxt); //email address edit text
        passwordTxt = (EditText) findViewById(R.id.passwordEditTxt); //password edit text
        forgotPassTxt = (TextView) findViewById(R.id.forgotPasswordTxt); //forgot password text redirect to forgot pass activity
        loginButton = (AppCompatButton) findViewById(R.id.buttonLogin); //login button
        signupTxt = (TextView) findViewById(R.id.textViewSignup); //signup textview redirect to signup activity
    }

    //redirect to applicant's home activity
    protected void redirectHomeActivity(String email){
        Intent intent = new Intent(getApplicationContext(), ApplicantHome.class);
        startActivity(intent);
    }

    protected void textWatchers(){
        emailTxt.addTextChangedListener(new MainActivity.EmailTextWatcher());
        passwordTxt.addTextChangedListener(new MainActivity.PasswordTextWatcher());
    }

    //query to firebase
    protected void loginUser() {
        db.collection("Users")
                .whereEqualTo("User_EmailAddress", inputtedEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            // User with the provided email found
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String storedPassword = documentSnapshot.getString("User_Password"); //get the string value of password field
                            if (inputtedPassword.equals(storedPassword)) {
                                finish();
                                redirectHomeActivity(inputtedEmail);
                                Toast.makeText(MainActivity.this, "Login successful", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show(); // Password doesn't match
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Incorrect email or password", Toast.LENGTH_SHORT).show(); // User with the provided email not found
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "Error logging in " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //boolean validators
    protected Boolean validEmail(String email){
        emailInputLayout = findViewById(R.id.emailLayout);
        emailTxt = findViewById(R.id.emailEditTxt);

        if(email.isEmpty()){
            //emailTxt.requestFocus();
            emailInputLayout.setError("Email Required!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailTxt.requestFocus();
            emailInputLayout.setError("Invalid email!");
            return false;
        }
        else{
            emailInputLayout.setError(null);
            return true;
        }
    }

    protected Boolean validPassword(String password){
        passwordInputLayout = findViewById(R.id.passwordLayout);
        passwordTxt = findViewById(R.id.passwordEditTxt);

        if(password.isEmpty()){
            //passwordTxt.requestFocus();
            passwordInputLayout.setError("Password Required!");
            return false;
        }
        else {
            passwordInputLayout.setError(null);
            return true;
        }
    }

    //input listeners
    protected void emailInputListener(){
        emailInputLayout = findViewById(R.id.emailLayout);
        emailTxt = emailInputLayout.getEditText();
        assert emailTxt != null;
        emailTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedEmail = emailTxt.getText().toString().trim();
                if(!inputtedEmail.isEmpty()){
                    emailInputLayout.setError(null); //tri
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    protected void passwordInputListener() {
        passwordInputLayout = findViewById(R.id.passwordLayout);
        passwordTxt = passwordInputLayout.getEditText();
        assert passwordTxt != null;
        passwordTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedPassword = passwordTxt.getText().toString().trim();
                if(!inputtedPassword.isEmpty()){
                    passwordInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        passwordInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    private class EmailTextWatcher implements TextWatcher {
        private static final int MAX_EMAIL_LENGTH = 30;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String email = editable.toString().trim();
            if (email.length() > MAX_EMAIL_LENGTH) {
                editable.replace(0, editable.length(), email.substring(0, MAX_EMAIL_LENGTH));
            }
        }
    }

    private class PasswordTextWatcher implements TextWatcher {
        private static final int MAX_PASSWORD_LENGTH = 15;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String password = editable.toString().trim();
            if (password.length() > MAX_PASSWORD_LENGTH) {
                editable.replace(0, editable.length(), password.substring(0, MAX_PASSWORD_LENGTH));
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

}