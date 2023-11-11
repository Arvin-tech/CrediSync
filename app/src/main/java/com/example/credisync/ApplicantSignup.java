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
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class ApplicantSignup extends AppCompatActivity {

    protected final String TAG = this.getClass().getName(); //tag argument is the tag of the message,  msg argument is the message to display on the Logcat
    protected static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^"+
                    "(?=.*[0-9])" + //at least 1-digit
                    "(?=.*[a-z])" + //at least 1 lower case letter
                    "(?=.*[A-Z])" + //at least 1 upper case letter
                    "(?=.*[@#$%^&+=])" + //at least 1 special character
                    "(?=\\S+$)" + //no whitespaces
                    ".{8,}" + //at least 8 characters
                    "$");

    protected FirebaseFirestore db; //cloud fire store
    protected EditText emailTxt, passwordTxt, confirmPassTxt; //variables for edit texts
    protected AppCompatButton signUp;
    protected TextView login, terms; //already have an account? click me and terms and conditions
    protected TextInputLayout emailInputLayout, passwordInputLayout, confirmInputLayout; //to output the error and set input limits
    protected String  inputtedEmail, inputtedPassword, inputtedConfirmPassword; //variable for inputted values
    protected Map<String, Object> user; //used to save user details
    protected Calendar calendar = Calendar.getInstance();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //year month date -- hour minute seconds
    protected String currentDate = simpleDateFormat.format(calendar.getTime()); //or currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    protected int accountNumber = generateAccountNumber();
    protected int redColor = Color.parseColor("#FF0000"); // Set the error text color to red
    protected ImageView checkmarkLength, lowercaseCheck, uppercaseCheck, digitCheck, specialCheck;
    protected CheckBox privacyPolicy;
    protected boolean valid = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_signup);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        login.setPaintFlags(login.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        db = FirebaseFirestore.getInstance(); //initialize firebase fire store

        findViewById(); //reference to ui elements
        textWatchers(); //input text watchers
        inputListeners(); //input listeners

        emailTxt.addTextChangedListener(signupTextWatcher);
        passwordTxt.addTextChangedListener(signupTextWatcher);
        confirmPassTxt.addTextChangedListener(signupTextWatcher);

        //click next button
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtedEmail = emailTxt.getText().toString().trim();
                inputtedPassword = passwordTxt.getText().toString().trim();
                inputtedConfirmPassword = confirmPassTxt.getText().toString().trim();

                if(validateEmail(inputtedEmail) && validatePassword(inputtedPassword)
                        && validateConfirmPass(inputtedPassword, inputtedConfirmPassword)){
                    redirectStep2(inputtedEmail, inputtedPassword, inputtedConfirmPassword); //pass email, password, and confirm pass data to 2nd step
                }else{

                }
            }
        });

        //already have an account? click me
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectLoginActivity();
            } //proceeds to login activity
        });

        //checkbox
        privacyPolicy.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String emailInput = emailTxt.getText().toString().trim();
                String passwordInput = passwordTxt.getText().toString().trim();
                String confirmPassInput = confirmPassTxt.getText().toString().trim();

                if (isChecked) {
                    signUp.setEnabled(false);
                    signUp.setBackgroundResource(R.drawable.button_disabled);// disable because we also need to validate if the email, password and confirm pass is empty or not

                    if(!emailInput.isEmpty() && !passwordInput.isEmpty() && !confirmPassInput.isEmpty()){
                        signUp.setEnabled(true);
                        signUp.setBackgroundResource(R.drawable.button_bg); //yellow if enabled
                    }
                    else{
                        signUp.setEnabled(false);
                        signUp.setBackgroundResource(R.drawable.button_disabled); //gray disabled
                    }
                } else {

                    signUp.setEnabled(false);
                    signUp.setBackgroundResource(R.drawable.button_disabled); // Disable the button when the checkbox is unchecked
                }
            }
        });

        //terms and conditions
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), PrivacyPolicy.class);
                startActivity(intent);
            }
        });
    }

    //text watcher for email , password and confirm pass input
    protected TextWatcher signupTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String emailInput = emailTxt.getText().toString().trim();
            String passwordInput = passwordTxt.getText().toString().trim();
            String confirmPassInput = confirmPassTxt.getText().toString().trim();

            if(!emailInput.isEmpty() && !passwordInput.isEmpty() && !confirmPassInput.isEmpty() && privacyPolicy.isChecked()){
                signUp.setEnabled(true);
                signUp.setBackgroundResource(R.drawable.button_bg); //yellow if enabled
            }
            else{
                signUp.setEnabled(false);
                signUp.setBackgroundResource(R.drawable.button_disabled); //gray disabled
            }

        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //login details input listeners
    protected void inputListeners() {
        emailInputListener(inputtedEmail);
        passwordInputListener(inputtedPassword);
        confirmPassInputListener(inputtedPassword, inputtedConfirmPassword);
    }

    //generate an applicant id number
    protected int generateAccountNumber(){
        final int minCode = 10000000; // 8-digit numbers start from 100000
        final int maxCode = 99999999; // 8-digit numbers end at 999999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    //text watchers
    protected void textWatchers(){
        emailTxt.addTextChangedListener(new EmailTextWatcher());
        passwordTxt.addTextChangedListener(new PasswordTextWatcher());
        confirmPassTxt.addTextChangedListener(new ConfirmPasswordTextWatcher());
    }

    //set status bar color
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    //create users collection
    protected void createUser(final String email, final String password) {
        // Check if the email already exists in the collection
        db.collection("Users")
                .document(email) // Use the email as the document ID
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Email already taken", Toast.LENGTH_SHORT).show(); //duplicate input
                            emailTxt.setText(null); //clear duplicate input
                        } else {
                            // Email not duplicated, create the user document
                            user = new HashMap<>();
                            user.put("accountNumber", accountNumber);
                            user.put("email", email);
                            user.put("password", password);
                            user.put("dateRegistered", currentDate);
                            user.put("lastLogin", "not logged in"); //not working as intended

                            // Set the document ID as the inputted email
                            db.collection("Users")
                                    .document(email)
                                    .set(user)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Log.d(TAG, "DocumentSnapshot added with ID: " + email);
                                            redirectLoginActivity();
                                            //Toast.makeText(Signup.this, "Successfully Added " + name, Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(getApplicationContext(), "Error adding user " + e, Toast.LENGTH_SHORT).show();
                                            Log.d(TAG, "Error adding document", e);
                                        }
                                    });
                        }
                    }
                });
    }

    //reference to ui elements
    protected void findViewById(){

        //inputs
        emailTxt = (EditText) findViewById(R.id.emailEditTxt);
        passwordTxt = (EditText) findViewById(R.id.passwordEditTxt);
        confirmPassTxt = (EditText) findViewById(R.id.passwordConfirmEditTxt);
        signUp = (AppCompatButton) findViewById(R.id.buttonSignup);
        login = (TextView) findViewById(R.id.textViewLogin);
        privacyPolicy = (CheckBox) findViewById(R.id.agreeCheckbox);
        terms = (TextView) findViewById(R.id.termsTxt);

        //checkmarks
        checkmarkLength = findViewById(R.id.charactersCheck);
        lowercaseCheck = findViewById(R.id.lowercaseCheck);
        uppercaseCheck = findViewById(R.id.uppercaseCheck);
        digitCheck = findViewById(R.id.digitCheck);
        specialCheck = findViewById(R.id.specialCheck);
    }

    protected void redirectLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); //redirect to home after signup
        startActivity(intent);
        finish(); //close signup activity
    }

    protected void redirectStep2(String email, String password, String confirm){
        Intent intent = new Intent(getApplicationContext(), ApplicantSignupStep2.class); //proceed to step 2
        intent.putExtra("email", email); //send to step 2
        intent.putExtra("password", password); //send to step 2
        intent.putExtra("confirm", confirm); //send to step 2
        startActivity(intent);

        //send this email also to otp verification activity
        Intent otpIntent = new Intent(getApplicationContext(), SignupVerification.class);
        otpIntent.putExtra("email", email);
        startActivity(otpIntent);
    }

    //validators
    protected Boolean validateEmail(String email){
        emailInputLayout = findViewById(R.id.emailLayout);
        emailTxt = emailInputLayout.getEditText();

        if(email.isEmpty()){
            emailInputLayout.setError("Email Required!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInputLayout.setError("Invalid email!"); //red gihapon
            return false;
        }
        else{
            emailInputLayout.setError(null); //no more  red
            return true;
        }
    }

    protected Boolean validatePassword(String password){
        passwordInputLayout = findViewById(R.id.passwordLayout);
        passwordTxt = passwordInputLayout.getEditText();

        if(password.isEmpty()){
            passwordInputLayout.setError("Password Required!");
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(password).matches()){
            passwordInputLayout.setError("Password too weak! Please choose a strong password"); //red
            return false;
        }
        else if (password.length() < 8 || password.length() > 15) {
            //passwordTxt.requestFocus();
            passwordInputLayout.setError("Password must be between 8-15 characters long!"); //red
            return false;
        }
        else {
            passwordInputLayout.setError(null); //no more red
            return true;
        }
    }

    protected Boolean validateConfirmPass(String password, String confirm){
        confirmInputLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPassTxt = confirmInputLayout.getEditText();

        if(confirm.isEmpty()){
            confirmInputLayout.setError("Confirm Password Required!");
            return false;
        }
        else if(!password.equals(confirm)){
            //confirmPassTxt.requestFocus();
            confirmInputLayout.setError("Passwords do not match!"); //red gihapon
            return false;
        }
        else{
            confirmInputLayout.setError(null); //no more red
            return true;
        }
    }

    //input listeners
    protected Boolean emailInputListener(String email){

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
                    emailInputLayout.setError(null); //display no error: triggers when email edit text input not empty
                    valid = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputtedEmail = emailTxt.getText().toString().trim();
                if(inputtedEmail.isEmpty()){
                    emailInputLayout.setError(null);  //when user leaves the edit text empty
                    valid = false;
                }
            }

        });
        emailInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
        return valid;
    }

    protected Boolean passwordInputListener(String password) {

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
                    passwordInputLayout.setError(null); //triggers when name edit text input not empty
                    valid = false;
                }

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputtedPassword = passwordTxt.getText().toString().trim();
                if(inputtedPassword.isEmpty()){
                    passwordInputLayout.setError(null);
                    valid = false;
                }
            }
        });
        passwordInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
        return valid;
    }

    protected Boolean confirmPassInputListener(String password, String confirm){

        confirmInputLayout = findViewById(R.id.confirmPasswordLayout);
        confirmPassTxt = confirmInputLayout.getEditText();
        assert confirmPassTxt != null;

        confirmPassTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedConfirmPassword = confirmPassTxt.getText().toString().trim();
                if (!inputtedConfirmPassword.isEmpty()){
                    confirmInputLayout.setError(null); //triggers when name edit text input not empty
                    valid = false;
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                inputtedConfirmPassword = confirmPassTxt.getText().toString().trim();
                if(inputtedConfirmPassword.isEmpty()){
                    confirmInputLayout.setError(null);
                    valid = false;
                }
            }
        });
        confirmInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
        return valid;
    }

    //text watcher for email
    private class EmailTextWatcher implements TextWatcher{
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

    //text watcher for password
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
            // Check password criteria and update checkmarks visibility
            updatePasswordStrength(password);
        }
    }

    //text watcher for confirm password
    private class ConfirmPasswordTextWatcher implements TextWatcher {
        private static final int MAX_CONFIRM_PASSWORD_LENGTH = 15;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String confirmPass = editable.toString().trim();
            if (confirmPass.length() > MAX_CONFIRM_PASSWORD_LENGTH) {
                editable.replace(0, editable.length(), confirmPass.substring(0, MAX_CONFIRM_PASSWORD_LENGTH));
            }
        }
    }

    //for checkmarks
    private void updatePasswordStrength(String password) {
        // update checkmarks visibility here, set ImageView visibility based on whether the criteria are met:
        checkmarkLength.setVisibility(password.length() >= 8 && password.length() <= 15 ? View.VISIBLE : View.GONE);
        lowercaseCheck.setVisibility(Pattern.compile("[a-z]").matcher(password).find() ? View.VISIBLE : View.GONE);
        uppercaseCheck.setVisibility(Pattern.compile("[A-Z]").matcher(password).find() ? View.VISIBLE : View.GONE);
        digitCheck.setVisibility(Pattern.compile("[0-9]").matcher(password).find() ? View.VISIBLE : View.GONE);
        specialCheck.setVisibility(Pattern.compile("[!@#$%^&*(),.?\":{}|<>]").matcher(password).find() ? View.VISIBLE : View.GONE);
    }


}