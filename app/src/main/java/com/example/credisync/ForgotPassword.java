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
import android.util.Patterns;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Properties;
import java.util.Random;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class ForgotPassword extends AppCompatActivity {

    protected final String TAG = this.getClass().getName(); //tag argument is the tag of the message,  msg argument is the message to display on the Logcat
    protected FirebaseFirestore db; //cloud fire store
    protected EditText emailInputTxt, codeInputTxt;
    protected TextInputLayout emailInputLayout, codeInputLayout;
    protected AppCompatButton resetButton, enterCode;
    protected TextView prompt, title;
    protected ProgressBar progressBar;
    protected String inputtedEmail, inputtedCode;
    protected int code = generateCode(); //get the generated code
    protected int redColor = Color.parseColor("#FF0000"); // Set the error text color to red
    protected ImageView back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color
        db = FirebaseFirestore.getInstance(); //initialize firebase fire store
        findViewById();

        emailInputTxt.addTextChangedListener(new EmailTextWatcher());
        codeInputTxt.addTextChangedListener(new CodeTextWatcher());
        emailInputListener();
        codeInputListener();

        //send code to email (Email me)
        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtedEmail = emailInputTxt.getText().toString().trim();
                if(validEmail(inputtedEmail)){
                    checkIfUserExist(inputtedEmail);
                    sendResetCode(inputtedEmail);
                }
            }
        });

        //after send code button is hidden, the enter code button appears
        enterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtedCode = codeInputTxt.getText().toString();
                //Convert the entered code to an integer
                int enteredCode;
                try {
                    enteredCode = Integer.parseInt(inputtedCode);
                } catch (NumberFormatException e) {
                    Toast.makeText(ForgotPassword.this, "Invalid input!", Toast.LENGTH_SHORT).show();
                    return;
                }
                //verify if the entered code matches the generated code
                if (enteredCode == code) {
                    redirectResetPasswordActivity(inputtedEmail);
                    finish();
                } else {
                    Toast.makeText(ForgotPassword.this, "Reset code is incorrect!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //back button icon
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                redirectLoginActivity();
            }
        });

    }

    //set status bar color
    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    //reference to ui elements
    private void findViewById() {
        //reference to ui elements (parent layout)
        back = findViewById(R.id.backImage);
        prompt = findViewById(R.id.promptUserTxt); //reset message
        title = findViewById(R.id.titleTxt);

        //InputsFrameLayout
        emailInputTxt = findViewById(R.id.emailEditTxt); //email address input
        codeInputTxt = findViewById(R.id.codeEditTxt);

        //ButtonsFrameLayout
        resetButton = findViewById(R.id.buttonReset); //send code to email button
        enterCode = findViewById(R.id.buttonEnterCode); //enter reset code button
        progressBar = findViewById(R.id.progressBarReset); //progress bar
    }

    //used to validate email address input
    protected Boolean validEmail(String email){
        emailInputLayout = findViewById(R.id.emailLayout);
        emailInputTxt = findViewById(R.id.emailEditTxt);

        if(email.isEmpty()){
            emailInputTxt.requestFocus();
            emailInputLayout.setError("Email Required!");
            return false;
        }
        else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInputTxt.requestFocus();
            emailInputLayout.setError("Invalid email!");
            return false;
        }
        else{
            emailInputLayout.setError(null);
            return true;
        }
    }

    // to get document id or document = document snapshot, if field in a document QuerySnapshot queryDocumentSnapshots
    protected void checkIfUserExist(String email){
        db.collection("Users")
                .document(email)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Toast.makeText(ForgotPassword.this, "Please check your email", Toast.LENGTH_SHORT).show();
                            resetPassword();

                        } else {
                            // User with the provided email does not exist in Firestore
                            emailInputLayout.setError("We can't find a user with the email you provided!");
                            //Toast.makeText(ForgotPassword.this, "User with this email does not exist.", Toast.LENGTH_SHORT).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(ForgotPassword.this, "Error checking user existence: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //redirect to login if back button icon is clicked
    protected void redirectLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class); //back to login
        startActivity(intent);
    }

    //redirect to reset password activity
    protected void redirectResetPasswordActivity(String email){
        Intent intent = new Intent(getApplicationContext(), ResetPassword.class); //proceed to reset password ui
        intent.putExtra("email", email); //send this email input to the reset password activity
        startActivity(intent);

        //Intent intentHome = new Intent(getApplicationContext(), Home.class);
        //intentHome.putExtra("email", email); //send this email input to home, to display the logged in user
    }

    //generate a reset code
    protected int generateCode() {
        final int minCode = 100000; // 6-digit numbers start from 100000
        final int maxCode = 999999; // 6-digit numbers end at 999999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    //send reset code to user email
    protected void sendResetCode(String recipientEmail) {

        //email contents
        String subject = "CrediSync Account Password Reset";
        String body = "Hello there,\n" +
                "\n" +
                "Looks like a request was made to reset the password for your CrediSync Account. Your password reset code is: " + code + ". " +
                "If you did not ask to reset your password, you can ignore this email.\n" +
                "\n" +
                "Thanks,\n" +
                "\n" +
                "Your Beta App Developers team";
        try {
            //get it from values> emails.xml
            String senderEmail = getString(R.string.smtp_email);
            String senderPassword = getString(R.string.smtp_password);
            String receiverEmail = recipientEmail;

            String stringHost = "smtp.gmail.com";

            Properties properties = new Properties();
            properties.put("mail.smtp.host", stringHost); //host
            properties.put("mail.smtp.auth", "true"); //auth
            properties.put("mail.smtp.port", 465); //port
            properties.put("mail.smtp.ssl.enable", "true"); //ssl or tls enable

            javax.mail.Session session = Session.getInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(senderEmail, senderPassword);
                }
            });

            MimeMessage mimeMessage = new MimeMessage(session);
            mimeMessage.addRecipient(Message.RecipientType.TO, new InternetAddress(receiverEmail));
            mimeMessage.setSubject(subject);
            mimeMessage.setText(body);

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Transport.send(mimeMessage);
                    } catch (MessagingException e) {
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        } catch (AddressException e) {
            e.printStackTrace();
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    //method to replace email me button into enter code button
    protected void resetPassword() {
        // Hide email input and send code to email button
        findViewById(R.id.emailEditTxt).setVisibility(View.GONE);
        findViewById(R.id.emailLayout).setVisibility(View.GONE);
        findViewById(R.id.buttonReset).setVisibility(View.GONE);
        // Show code input and "Enter code" button
        title.setText("Enter Code");
        prompt.setText("Enter the code you have received from your email to proceed the next step");
        findViewById(R.id.codeLayout).setVisibility(View.VISIBLE);
        findViewById(R.id.codeEditTxt).setVisibility(View.VISIBLE);
        findViewById(R.id.buttonEnterCode).setVisibility(View.VISIBLE);

    }

    protected void emailInputListener(){
        emailInputLayout = findViewById(R.id.emailLayout);
        emailInputTxt = emailInputLayout.getEditText();
        assert emailInputTxt != null;
        emailInputTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedEmail = emailInputTxt.getText().toString().trim();
                if (!inputtedEmail.isEmpty()){
                    emailInputLayout.setError(null); //triggers when email edit text input is not empty
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        emailInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    protected void codeInputListener(){
        codeInputLayout = findViewById(R.id.codeLayout);
        codeInputTxt = codeInputLayout.getEditText();
        assert codeInputTxt != null;
        codeInputTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedCode = codeInputTxt.getText().toString().trim();
                if (!inputtedCode.isEmpty()){
                    codeInputLayout.setError(null); //triggers when email edit text input is not empty
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        codeInputLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
    }

    private static class EmailTextWatcher implements TextWatcher {
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

    private static class CodeTextWatcher implements TextWatcher {
        private static final int MAX_CODE_LENGTH = 6;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String code = editable.toString().trim();
            if (code.length() > MAX_CODE_LENGTH) {
                editable.replace(0, editable.length(), code.substring(0, MAX_CODE_LENGTH));
            }
        }
    }
}