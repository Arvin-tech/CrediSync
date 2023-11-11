package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.regex.Pattern;

public class ResetPassword extends AppCompatActivity {

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
    protected TextView title, prompt; //reset pass and strong pass texts
    protected TextInputLayout newPassLayout, verifyPassLayout; //text input layouts
    protected EditText newPassTxt, verifyPassTxt; //edit texts
    protected AppCompatButton resetPassButton;
    protected FirebaseFirestore db;
    protected String inputtedNewPass, inputtedVerifyPass;
    protected String userEmail;
    protected int redColor = Color.parseColor("#FF0000"); // Set the error text color to red
    protected ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        findViewById(); //reference to ui elements
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        db = FirebaseFirestore.getInstance(); //initialize fire store
        inputTextWatchers(); //new pass and verify pass text watchers
        inputListeners(); //new pass and verify pass input listeners

        //click reset button
        resetPassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtedNewPass = newPassTxt.getText().toString().trim();
                inputtedVerifyPass = verifyPassTxt.getText().toString().trim();
                if(validNewPassword(inputtedNewPass) && validVerifyPass(inputtedNewPass, inputtedVerifyPass)){
                    getUserDetails();
                    updatePassword(inputtedVerifyPass);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please enter again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //click back button icon
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent); //back to login ui
            }
        });
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected void findViewById(){
        title = findViewById(R.id.titleTxt);
        prompt = findViewById(R.id.promptUserTxt);
        newPassTxt = findViewById(R.id.newPasswordEditTxt);
        verifyPassTxt = findViewById(R.id.verifyPasswordEditTxt);
        resetPassButton = findViewById(R.id.buttonResetPassword);
        back = findViewById(R.id.backImage);
    }

    protected void inputTextWatchers(){
        newPassTxt.addTextChangedListener(new ResetPassword.NewPasswordTextWatcher());
        verifyPassTxt.addTextChangedListener(new ResetPassword.VerifyPasswordTextWatcher());
    }

    protected void inputListeners(){
        newPasswordInputListener();
        verifyPasswordInputListener();
    }

    //grab email from forgot password activity using bundle extras
    protected void getUserDetails(){
        // Get the email from the Intent extras
        Bundle extras = getIntent().getExtras();
        if(extras != null){
            userEmail = extras.getString("email");
            db.collection("Users").whereEqualTo("email", userEmail)
                    .limit(1)
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                        @Override
                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                            if (!queryDocumentSnapshots.isEmpty()) {
                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                String storedPassword = documentSnapshot.getString("password"); //get the value of password field in firebase (this will be old password)
                                updatePassword(storedPassword); //update the old pass thru reset
                            } else {
                                Toast.makeText(ResetPassword.this, "User with this email does not exist.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ResetPassword.this, "Error checking user existence: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    //update password , query to firebase
    protected void updatePassword(String storedPassword){
        String newPassword = inputtedVerifyPass; //the inputted password at verify pass edit text is the new password
        db.collection("Users")
                .whereEqualTo("email", userEmail)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
                            DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                            String documentId = documentSnapshot.getId();
                            // Update the 'password' field for the user document with the new password
                            db.collection("Users").document(documentId)
                                    .update("password", newPassword)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            redirectLoginActivity(); //back to login if reset password done
                                            Toast.makeText(ResetPassword.this, "Password updated successfully!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ResetPassword.this, "Error updating password: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            // Handle failure if needed
                                        }
                                    });
                        } else {
                            // User with the provided email does not exist in Firestore
                            Toast.makeText(ResetPassword.this, "User with this email does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ResetPassword.this, "Error checking user existence: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //if back button icon is clicked
    protected void redirectLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); //back to login
        startActivity(intent);
    }

    protected Boolean validNewPassword(String password){
        newPassLayout = findViewById(R.id.newPassLayout);
        newPassTxt = newPassLayout.getEditText();

        if(password.isEmpty()){
            newPassTxt.requestFocus(); //cursor here
            newPassLayout.setError("Field Required!"); //red
            return false;
        }
        else if (!PASSWORD_PATTERN.matcher(password).matches()){
            newPassLayout.setError("Password too weak"); //red gihapon
            return false;
        }
        else if (password.length() < 8 || password.length() > 15) {
            newPassTxt.requestFocus(); //cursor here
            newPassLayout.setError("Password must be between 8 and 15 characters!"); //red gihapon
            return false;
        }
        else {
            newPassLayout.setError(null); //no more red
            return true;
        }
    }

    protected Boolean validVerifyPass(String password, String verify){
        verifyPassLayout = findViewById(R.id.verifyPassLayout);
        verifyPassTxt = verifyPassLayout.getEditText();

        if(verify.isEmpty()){
            verifyPassTxt.requestFocus(); //cursor here
            verifyPassLayout.setError("Verify your password"); //red
            return false;
        } else if(!password.equals(verify)){
            verifyPassTxt.requestFocus();
            verifyPassLayout.setError("Passwords do not match!"); //red gihapon
            return false;
        }
        else{
            verifyPassLayout.setError(null); //no more red
            return true;
        }
    }

    protected void newPasswordInputListener(){
        newPassLayout = findViewById(R.id.newPassLayout);
        newPassTxt = newPassLayout.getEditText();
        assert newPassTxt != null;
        newPassTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedNewPass = newPassTxt.getText().toString().trim();
                if (!inputtedNewPass.isEmpty()){
                    newPassLayout.setError(null); //triggers when new pass edit text input is not empty
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        newPassLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    protected void verifyPasswordInputListener(){
        verifyPassLayout = findViewById(R.id.verifyPassLayout);
        verifyPassTxt = verifyPassLayout.getEditText();
        assert verifyPassTxt != null;
        verifyPassTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedVerifyPass = verifyPassTxt.getText().toString().trim();
                if (!inputtedVerifyPass.isEmpty()){
                    verifyPassLayout.setError(null); //triggers when new pass edit text input is not empty
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        verifyPassLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    private static class NewPasswordTextWatcher implements TextWatcher {
        private static final int MAX_NEW_PASSWORD_LENGTH = 15;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String newPassword = editable.toString().trim();
            if (newPassword.length() > MAX_NEW_PASSWORD_LENGTH) {
                editable.replace(0, editable.length(), newPassword.substring(0, MAX_NEW_PASSWORD_LENGTH));
            }
        }
    }

    private static class VerifyPasswordTextWatcher implements TextWatcher {
        private static final int MAX_VERIFY_PASSWORD_LENGTH = 15;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String verifyPass = editable.toString().trim();
            if (verifyPass.length() > MAX_VERIFY_PASSWORD_LENGTH) {
                editable.replace(0, editable.length(), verifyPass.substring(0, MAX_VERIFY_PASSWORD_LENGTH));
            }
        }
    }

}