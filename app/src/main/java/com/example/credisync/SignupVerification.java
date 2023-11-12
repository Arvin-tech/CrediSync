package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
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

public class SignupVerification extends AppCompatActivity {

    protected final String TAG = this.getClass().getName();
    protected EditText otpTxt1, otpTxt2, otpTxt3, otpTxt4;
    protected TextView emailTxt, resendTxt;
    protected AppCompatButton verifyButton;
    protected boolean resendEnabled = false; //true after every 60 seconds
    protected int resendTime = 60; //in seconds
    protected int selectedETPosition = 0;
    protected int code = generateCode(); //get the generated code

    protected Map<String, Object> user; //used to save user details
    protected int accountNumber = generateAccountNumber();
    protected String getEmail, getUserPassword, getFirstname, getLastName, getContact, getBirthdate; //variable to receive data from applicant signup & applicant signup step 2
    protected Calendar calendar = Calendar.getInstance();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //year month date -- hour minute seconds
    protected String currentDate = simpleDateFormat.format(calendar.getTime()); //or currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    protected FirebaseFirestore db; //cloud fire store

    protected final TextWatcher textWatcher = new TextWatcher() {
        private static final int MAX_EDIT_TEXT_INPUT_LENGTH = 1; //input limit per edit text
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String otpInput = editable.toString().trim();
            if (otpInput.length() > MAX_EDIT_TEXT_INPUT_LENGTH) {
                editable.replace(0, editable.length(), otpInput.substring(0, MAX_EDIT_TEXT_INPUT_LENGTH));
            }

            if(editable.length() > 0){
                if(selectedETPosition == 0){
                    selectedETPosition = 1;
                    showKeyboard(otpTxt2);
                }
                else if(selectedETPosition == 1) {
                    selectedETPosition = 2;
                    showKeyboard(otpTxt3);
                }
                else if(selectedETPosition == 2){
                    selectedETPosition = 3;
                    showKeyboard(otpTxt4);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_verification);

        db = FirebaseFirestore.getInstance();
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color resendTextview

        findViewById(); //reference to ui elements
        resendTxt.setPaintFlags(resendTxt.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        getSignupStep2Data();

        sendVerificationCode(getEmail); //pass the email from applicant signup as parameter

        otpTextWatcher();
        showKeyboard(otpTxt1);
        startCountDownTimer();

        //click verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String generateOtp = otpTxt1.getText().toString()+otpTxt2.getText().toString()+otpTxt3.getText().toString()+otpTxt4.getText().toString();
                if(generateOtp.length()==4){

                    int enteredCode;
                    try{
                        enteredCode = Integer.parseInt(generateOtp);
                    }
                    catch(NumberFormatException e){
                        return;
                    }
                    //validate entered code
                    if(enteredCode == code){
                        saveToFirebase();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Incorrect verification code",Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //click resend text view
        resendTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(resendEnabled){
                    //resend code here
                    sendVerificationCode(getEmail);
                    startCountDownTimer(); //start new resend count down timer
                }
            }
        });
    }

    protected void getSignupStep2Data(){
        getEmail = getIntent().getStringExtra("emailData"); //receive email from applicant signup activity
        emailTxt.setText(getEmail); //set the text view in xml into the email from applicant signup\
        getUserPassword = getIntent().getStringExtra("passwordData");

        //receive data from applicant signup step 2 : observe the keys
        getFirstname = getIntent().getStringExtra("firstnameData");
        getLastName = getIntent().getStringExtra("lastnameData");
        getContact = getIntent().getStringExtra("contactData");
        getBirthdate = getIntent().getStringExtra("birthdateDate");

    }

    protected void redirectLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
    }

    protected void showKeyboard(EditText otpET){
        otpET.requestFocus();
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(otpET, InputMethodManager.SHOW_IMPLICIT);
    }

    protected void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected void startCountDownTimer(){
        resendEnabled = false;
        resendTxt.setTextColor(Color.parseColor("#D48809"));

        new CountDownTimer(resendTime * 1000, 1000){

            @Override
            public void onTick(long l) {
                resendTxt.setText("Resend Code("+(l / 1000)+")");
            }

            @Override
            public void onFinish() {
                resendEnabled = true;
                resendTxt.setText("Resend Code");
                resendTxt.setTextColor(getResources().getColor(R.color.black));
            }
        }.start();

    }

    protected void otpTextWatcher(){
        otpTxt1.addTextChangedListener(textWatcher);
        otpTxt2.addTextChangedListener(textWatcher);
        otpTxt3.addTextChangedListener(textWatcher);
        otpTxt4.addTextChangedListener(textWatcher);
    }

    protected void findViewById(){
        otpTxt1 = (EditText) findViewById(R.id.otpET1);
        otpTxt2 = (EditText) findViewById(R.id.otpET2);
        otpTxt3 = (EditText) findViewById(R.id.otpET3);
        otpTxt4 = (EditText) findViewById(R.id.otpET4);
        verifyButton = (AppCompatButton) findViewById(R.id.verifyButton);
        emailTxt = (TextView) findViewById(R.id.emailTextview); //email txt that will be replaced with inputted email from signup step 1
        resendTxt = (TextView) findViewById(R.id.resendTextview); //resend code
    }

    private int generateCode() {
        final int minCode = 1000; // 4-digit numbers start from 1000
        final int maxCode = 9999; // 4-digit numbers end at 9999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    protected int generateAccountNumber() {
        final int minCode = 10000000; // 8-digit numbers start from 100000
        final int maxCode = 99999999; // 8-digit numbers end at 999999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    //this method creates a user account and save details to firebase
    protected void saveToFirebase(){
        user = new HashMap<>();
        user.put("Applicant ID", accountNumber); //generated account number

        //data from Step1
        user.put("LA_EmailAddress", getEmail);
        user.put("LA_Password", getUserPassword);

        //data from step 2
        user.put("LA_FirstName", getFirstname);
        user.put("LA_LastName", getLastName);
        user.put("LA_ContactNo", getContact);
        user.put("LA_Birthdate", getBirthdate);
        user.put("LA_dateRegistered", currentDate);

        // Set the document ID as the inputted email
        db.collection("Loan Applicants")
                .document(getEmail)
                .set(user)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + getEmail);
                        redirectLoginActivity();
                        Toast.makeText(getApplicationContext(), "Account Successfully Created ", Toast.LENGTH_SHORT).show();
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

    protected void sendVerificationCode(String recipientEmail) {
        //email contents
        String subject = "Verify Your Account - Welcome to CrediSync!";
        String body = "Dear "+getFirstname+",\n\n" +
                "Thank you for signing up with CrediSync  To complete the registration process, " +
                "please use the following verification code:\n\n" +
                "Verification Code: " +code+ "\n\n" +
                "Please enter this code on the verification page to activate your account. " +
                "This step ensures that you have provided a valid email address and helps us maintain the security of your account.\n\n" +
                "If you didn't sign up for CrediSync or you think you received this email by mistake, please ignore this email.\n\n" +
                "If you have any questions or needs, please don't hesitate to contact our support team at credisync@gmail.com " +
                "Best regards,\n\n" +
                "The CrediSync Team";

        try {
            //get it from values> emails.xml
            String senderEmail = getString(R.string.smtp_email); //emails xml
            String senderPassword = getString(R.string.smtp_password); //emails xml
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

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_DEL){
            if(selectedETPosition == 3){
                selectedETPosition = 2;
                showKeyboard(otpTxt3);
            }
            else if(selectedETPosition == 2){
                selectedETPosition = 1;
                showKeyboard(otpTxt2);
            }
            else if(selectedETPosition == 1){
                selectedETPosition = 0;
                showKeyboard(otpTxt1);
            }
            return true;
        }
        else{
            return super.onKeyUp(keyCode, event);
        }
    }
}