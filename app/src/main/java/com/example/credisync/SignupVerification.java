package com.example.credisync;

import static java.lang.ref.Cleaner.create;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.telephony.SmsManager;
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
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

//import com.twilio.Twilio;
//import com.twilio.rest.api.v2010.account.Message;
//import com.twilio.type.PhoneNumber;

public class SignupVerification extends AppCompatActivity {

    protected final String TAG = this.getClass().getName();
    protected EditText otpTxt1, otpTxt2, otpTxt3, otpTxt4, otpTxt5, otpTxt6;
    protected TextView phoneTxt, resendTxt;
    protected AppCompatButton verifyButton;
    protected boolean resendEnabled = false; //true after every 60 seconds
    protected int resendTime = 60; //in seconds
    protected int selectedETPosition = 0;
    protected int accountNumber = generateAccountNumber();
    protected String getEmail, getUserPassword, getFirstname, getLastName, getCooperative, getMembershipID, getAddress, getContact, getBirthdate; //variable to receive data from applicant signup & applicant signup step 2
    protected Calendar calendar = Calendar.getInstance();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //year month date -- hour minute seconds
    protected String currentDate = simpleDateFormat.format(calendar.getTime()); //or currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    protected FirebaseFirestore db; //cloud fire store
    protected int code = generateCode(); //get the generated code
    //protected final String ACCOUNT_SID = getString(R.string.ACCOUNT_SD);
    //protected final String AUTH_TOKEN = getString(R.string.AUTH_TOKEN);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_verification);
        //Twilio.init(ACCOUNT_SID, AUTH_TOKEN); //initialize twilio

        db = FirebaseFirestore.getInstance(); //initialize fire store
        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color resendTextview

        findViewById(); //reference to ui elements
        resendTxt.setPaintFlags(resendTxt.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG); //underline resend code text
        getSignupStep2Data(); //grab data from step signup step 2
        phoneTxt.setText(getContact); //phone number of user

        //sendTwilioSms();
        checkSelfPermission(); //check permissions and send verification code

        otpTextWatcher();
        showKeyboard(otpTxt1);
        startCountDownTimer();

        //click verify button
        verifyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String generateOtp = otpTxt1.getText().toString()
                                           +otpTxt2.getText().toString()
                                           +otpTxt3.getText().toString()
                                           +otpTxt4.getText().toString()
                                           +otpTxt5.getText().toString()
                                           +otpTxt6.getText().toString();

                if(generateOtp.length()==6){

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
                    //sendTwilioSms();
                    checkSelfPermission();
                    startCountDownTimer(); //start new resend count down timer
                }
            }
        });
    }

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
                else if(selectedETPosition == 3){
                    selectedETPosition = 4;
                    showKeyboard(otpTxt5);
                }
                else if(selectedETPosition == 4){
                    selectedETPosition = 5;
                    showKeyboard(otpTxt6);
                }
            }
        }
    };

    //generated otp code
    private int generateCode() {
        final int minCode = 100000; // 6-digit numbers start from 100000
        final int maxCode = 999999; // 6-digit numbers end at 999999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    //send verification code sms
    protected void sendVerificationCode(String phoneNumber) {
        SmsManager smsManager = SmsManager.getDefault();
        ArrayList<String> parts = smsManager.divideMessage("Your CrediSync account verification code is: " + code);
        smsManager.sendMultipartTextMessage(phoneNumber, null, parts, null, null);
    }

    //check manifest permissions
    protected void checkSelfPermission(){
        String permissionStr = "android.permission.SEND_SMS";

        if(ContextCompat.checkSelfPermission(SignupVerification.this, permissionStr) == PackageManager.PERMISSION_GRANTED){
            sendVerificationCode(getContact);
        }else{
            ActivityCompat.requestPermissions(SignupVerification.this, new String[]{permissionStr}, 100);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == 100){
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                sendVerificationCode(getContact);
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
    //send verification code to user - this is an outbound sms
    protected void sendTwilioSms(){
        Message message = Message.creator(
                new PhoneNumber(getString(R.string.TWILIO_PHONE)), new PhoneNumber(getContact),
                "Your CrediSync account verification code is: "+code).create();

        // Log the SID of the sent message
        Log.d("TwilioSMS", "Message SID: " + message.getSid());
    }**/

    //grab data from step 2 when this activity start
    protected void getSignupStep2Data(){
        getEmail = getIntent().getStringExtra("emailData"); //receive email from applicant signup activity
        getUserPassword = getIntent().getStringExtra("passwordData");

        //receive data from applicant signup step 2 : observe the keys
        getFirstname = getIntent().getStringExtra("firstnameData");
        getLastName = getIntent().getStringExtra("lastnameData");
        getCooperative = getIntent().getStringExtra("coopData");
        getMembershipID = getIntent().getStringExtra("memberIdData");
        getAddress = getIntent().getStringExtra("addressData");
        getContact = getIntent().getStringExtra("contactData");
        phoneTxt.setText(getContact); //set the text view in xml into the phone number from applicant signup\
        getBirthdate = getIntent().getStringExtra("birthdateDate");

    }

    //redirect to login if verified
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
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);
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
        otpTxt5.addTextChangedListener(textWatcher);
        otpTxt6.addTextChangedListener(textWatcher);
    }

    protected void findViewById(){
        otpTxt1 = (EditText) findViewById(R.id.otpET1);
        otpTxt2 = (EditText) findViewById(R.id.otpET2);
        otpTxt3 = (EditText) findViewById(R.id.otpET3);
        otpTxt4 = (EditText) findViewById(R.id.otpET4);
        otpTxt5 = (EditText) findViewById(R.id.otpET5);
        otpTxt6 = (EditText) findViewById(R.id.otpET6);
        verifyButton = (AppCompatButton) findViewById(R.id.verifyButton);
        phoneTxt = (TextView) findViewById(R.id.phoneTextView); //email txt that will be replaced with inputted email from signup step 1
        resendTxt = (TextView) findViewById(R.id.resendTextview); //resend code
    }

    //generated account number
    protected int generateAccountNumber() {
        final int minCode = 10000000; // 8-digit numbers start from 100000
        final int maxCode = 99999999; // 8-digit numbers end at 999999
        Random random = new Random();
        return random.nextInt(maxCode - minCode + 1) + minCode;
    }

    //this method creates a user account and save details to firebase
    protected void saveToFirebase(){
        //refer to the ERD : hash map for users collection
        Map<String, Object> users = new HashMap<>();
        users.put("User_ID", accountNumber);
        users.put("User_EmailAddress", getEmail);
        users.put("User_Password", getUserPassword);
        users.put("User_DateRegistered", currentDate); //sign up date
        users.put("User_Type", "Loan Applicant"); //user type

        //hashmap for loan applicants collection
        Map<String, Object> loanApplicants = new HashMap<>();
        loanApplicants.put("LA_ID", accountNumber); //generated account number
        //data from Step1
        loanApplicants.put("LA_EmailAddress", getEmail);
        loanApplicants.put("LA_Password", getUserPassword);
        //data from step 2
        loanApplicants.put("LA_FirstName", getFirstname);
        loanApplicants.put("LA_LastName", getLastName);
        loanApplicants.put("LA_Cooperative", getCooperative);
        loanApplicants.put("LA_MembershipID", getMembershipID);
        loanApplicants.put("LA_Address", getAddress);
        loanApplicants.put("LA_ContactNo", getContact);
        loanApplicants.put("LA_Birthdate", getBirthdate);

        db.collection("Users")
                .document(String.valueOf(accountNumber))
                .set(users)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

        // Set the document ID as the inputted email (Loan Applicants collection)
        db.collection("Loan Applicants")
                .document(getEmail)
                .set(loanApplicants)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot added with ID: " + getEmail);
                        redirectLoginActivity();
                        sendConfirmationEmail(getEmail); //send confirmation email to user
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

    protected void sendConfirmationEmail(String recipientEmail) {
        //email contents
        String subject = "CrediSync Account Confirmation!";
        String body = "Dear " + getFirstname + ",\n\n" +
                "Welcome to CrediSync! We're thrilled to have you on board. " +
                "Your account has been successfully created, and we can't wait for you to explore all the exciting features.\n\n" +
                "If you didn't sign up for our app or received this email in error, please disregard it.\n\n" +
                "Should you encounter any issues or have questions, feel free to reach out to our support team at support @ credisync@gmail.com\n\n" +
                "We're excited to have you as part of our community!\n\n" +
                "Best regards,\n\n" +
                "The CrediSync Team";

        try {
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