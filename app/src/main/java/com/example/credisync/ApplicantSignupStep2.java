package com.example.credisync;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class ApplicantSignupStep2 extends AppCompatActivity {

    protected final String TAG = this.getClass().getName(); //tag argument is the tag of the message,  msg argument is the message to display on the Logcat
    protected static final Pattern PHONE_PATTERN = Pattern.compile("09[0-9]{9}"); //philippine format? 09 prefix yarn
    protected EditText firstNameTxt, lastNameTxt, coopTxt, memberIdTxt, addressTxt, phoneTxt, birthTxt;
    protected TextInputLayout firstNameLayout, lastNameLayout, cooperativeLayout, memberIDLayout, addressLayout, phoneLayout, birthdateLayout;
    protected AppCompatButton signUp;
    protected String inputtedFirstName, inputtedLastName, inputtedCoop, inputtedMemberId, inputtedAddress, inputtedPhone, inputtedBirthdate; //input variables for this activity/page
    protected FirebaseFirestore db; //cloud fire store
    protected Map<String, Object> user; //used to save user details
    protected Calendar calendar = Calendar.getInstance();
    protected SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //year month date -- hour minute seconds
    protected String currentDate = simpleDateFormat.format(calendar.getTime()); //or currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(calendar.getTime());
    protected int redColor = Color.parseColor("#FF0000");
    protected DatePickerDialog.OnDateSetListener setListener;
    protected String getEmail, getUserPassword; //variables used to get data from applicant signup activity (step 1)
    protected ImageView validID_Image;
    protected TextView fileNameTxt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_applicant_signup_step2);

        setStatusBarColor(getResources().getColor(R.color.peacher)); // Set the status bar color

        db = FirebaseFirestore.getInstance();
        findViewById(); //method that creates reference to ui elements
        textWatchers(); //for input lengths
        inputListeners(); //when inputs are not empty, set to null the error messages
        getSignupPageData(); //grab the data from step 1 as this activity starts

        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);

        /*
        birthTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        getApplicationContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        month = month+1;
                        String date = month+"/"+day+"/"+year;
                        birthTxt.setText(date);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        setListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month = month+1;
                String date = day+"/"+month+"/"+year;
                birthTxt.setText(date);
            }
        };
         */

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputtedFirstName = firstNameTxt.getText().toString();
                inputtedLastName = lastNameTxt.getText().toString();
                inputtedCoop = coopTxt.getText().toString();
                inputtedMemberId = memberIdTxt.getText().toString();
                inputtedAddress = addressTxt.getText().toString();
                inputtedPhone = phoneTxt.getText().toString();
                //inputtedBirthdate = birthTxt.getText().toString();

                if(validateFirstName(inputtedFirstName) && validateLastName(inputtedLastName)
                                                        && validateCooperativeName(inputtedCoop)
                                                        && validateMemberId(inputtedMemberId)
                                                        && validateAddress(inputtedAddress)
                                                        && validatePhone(inputtedPhone)){

                    //send email, password, firstname, lastname, member id, coop, phone and birthdate to signup verification
                    Intent intent = new Intent(getApplicationContext(), SignupVerification.class);
                    intent.putExtra("emailData", getEmail);
                    intent.putExtra("passwordData", getUserPassword);
                    intent.putExtra("firstnameData", inputtedFirstName);
                    intent.putExtra("lastnameData", inputtedLastName);
                    intent.putExtra("coopData", inputtedCoop);
                    intent.putExtra("memberIdData", inputtedMemberId);
                    intent.putExtra("addressData", inputtedAddress);
                    intent.putExtra("contactData", inputtedPhone);
                    intent.putExtra("birthdateData", inputtedBirthdate);

                    // Start the SignupVerification activity
                    startActivity(intent);
                }else{

                }

            }
        });
    }

    private void textWatchers() {
        firstNameTxt.addTextChangedListener(new FirstNameTextWatcher());
        lastNameTxt.addTextChangedListener(new LastNameTextWatcher());
        coopTxt.addTextChangedListener(new CooperativeTextWatcher());
        memberIdTxt.addTextChangedListener(new MembershipIdTextWatcher());
        addressTxt.addTextChangedListener(new AddressTextWatcher());
        phoneTxt.addTextChangedListener(new PhoneTextWatcher());
    }

    private void setStatusBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        }
    }

    protected  void findViewById(){
        //id of all ui elements in this activity
        firstNameTxt = (EditText) findViewById(R.id.firstNameEditTxt);
        lastNameTxt = (EditText) findViewById(R.id.lastNameEditTxt);
        coopTxt = (EditText) findViewById(R.id.memberCoopEditTxt);
        memberIdTxt = (EditText) findViewById(R.id.memberIdEditTxt);
        addressTxt = (EditText) findViewById(R.id.addressEditTxt);
        phoneTxt = (EditText) findViewById(R.id.phoneEditTxt);
        birthTxt = (EditText) findViewById(R.id.birthdateTxt);
        validID_Image = (ImageView) findViewById(R.id.validIdImage); //click image view
        fileNameTxt = (TextView) findViewById(R.id.fileNameTextview);
        signUp = (AppCompatButton) findViewById(R.id.buttonSignup); //click button
    }

    protected void inputListeners(){
        firstNameInputListener();
        lastNameInputListener();
        cooperativeInputListener();
        membershipIdInputListener();
        addressInputListener();
        phoneInputListener();
    }

    protected void getSignupPageData(){
        getEmail = getIntent().getStringExtra("email"); //receive email from applicant signup activity
        getUserPassword = getIntent().getStringExtra("password");
    }

    protected void redirectLoginActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish(); //close signup two (step 2) activity
    }

    protected void redirectSignupVerification(String emailTemp, String passwordTemp, String firstNameTemp, String lastNameTemp){
        Intent intent = new Intent(getApplicationContext(), SignupVerification.class); //redirect to SignupVerification activity
        intent.putExtra("firstname", firstNameTemp); //send to SignupVerification activity
        startActivity(intent);
        //finish(); to be decided
    }

    //boolean validations : firstname, lastname, contact number
    protected Boolean validateFirstName(String name){
        firstNameLayout = findViewById(R.id.firstNameLayout);
        firstNameTxt = firstNameLayout.getEditText();

        // Check if the name is empty
        if (name.isEmpty()) {
            firstNameLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            firstNameLayout.setError("First Name Required!");
            return false;
        } else {
            // Check if the first name contains special characters or numeric values
            Pattern pattern = Pattern.compile("^[a-zA-Z]*$"); // Only allow alphabets
            if (!pattern.matcher(name).matches()) {
                firstNameLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
                firstNameLayout.setError("Invalid characters in First Name!");
                return false;
            } else {
                firstNameLayout.setError(null);
                return true;
            }
        }
    }

    protected Boolean validateLastName(String name){

        lastNameLayout = findViewById(R.id.lastNameLayout);
        lastNameTxt = lastNameLayout.getEditText();

        if (name.isEmpty()){
            lastNameLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            lastNameLayout.setError("Last Name Required!"); //red
            return false;
        } else{
            // Check if the last name contains special characters or numeric values
            Pattern pattern = Pattern.compile("^[a-zA-Z]*$"); // Only allow alphabets
            if (!pattern.matcher(name).matches()) {
                lastNameLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
                lastNameLayout.setError("Invalid characters in Last Name!");
                return false;
            } else {
                lastNameLayout.setError(null); //no more red
                return true;
            }
        }
    }

    //cooperative name
    protected Boolean validateCooperativeName(String coopName){

        cooperativeLayout = findViewById(R.id.memberCoopLayout);
        coopTxt = cooperativeLayout.getEditText();

        if(coopName.isEmpty()){
            cooperativeLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            cooperativeLayout.setError("Cooperative Required!");
            return false;
        }else{
            // Check if the first name contains special characters or numeric values
            Pattern pattern = Pattern.compile("^[a-zA-Z]*$"); // Only allow alphabets
            if (!pattern.matcher(coopName).matches()) {
                cooperativeLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
                cooperativeLayout.setError("Invalid Cooperative Name!");
                return false;
            } else {
                cooperativeLayout.setError(null);
                return true;
            }
        }
    }

    //cooperative member id
    protected Boolean validateMemberId(String memberID){
        memberIDLayout = findViewById(R.id.memberCoopLayout);
        memberIdTxt = memberIDLayout.getEditText();
        if(memberID.isEmpty()){
            memberIDLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            memberIDLayout.setError("Member ID required!");
            return false;
        }else{
            memberIDLayout.setError(null);
            return true;
        }
    }

    //home address
    protected Boolean validateAddress(String address){
        //can contain dash or numbers
        addressLayout = findViewById(R.id.addressLayout);
        addressTxt = addressLayout.getEditText();
        if(address.isEmpty()){
            addressLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            addressLayout.setError("Address is required!");
            return false;
        } else{
            addressLayout.setError(null);
            return true;
        }
    }

    //contact number
    protected Boolean validatePhone(String phone){
        phoneLayout = findViewById(R.id.phoneNumberLayout);
        phoneTxt = phoneLayout.getEditText();
        if(phone.isEmpty()){
            phoneLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            phoneLayout.setError("Phone number required!"); //red
            return false;
        }
        else if (phone.length() != 11){
            phoneLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            phoneLayout.setError("Phone number must be 11 digits"); //red gihapon
            return false;
        }
        else if (!PHONE_PATTERN.matcher(phone).matches()){
            phoneLayout.setErrorTextColor(ColorStateList.valueOf(redColor));
            phoneLayout.setError("Invalid Phone number"); //red gihapon
            return false;
        }
        else{
            phoneLayout.setError(null); //no more red
            return true;
        }
    }

    //Input listeners
    protected void firstNameInputListener(){
        firstNameLayout = findViewById(R.id.firstNameLayout);
        firstNameTxt = firstNameLayout.getEditText();
        assert firstNameTxt != null;
        firstNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedFirstName = firstNameTxt.getText().toString().trim();
                if (!inputtedFirstName.isEmpty()){
                    firstNameLayout.setError(null); //triggers when first name not empty
                }
            }
            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    private void lastNameInputListener() {
        lastNameLayout = findViewById(R.id.lastNameLayout);
        lastNameTxt = lastNameLayout.getEditText();
        assert lastNameTxt != null;

        lastNameTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputtedLastName = lastNameTxt.getText().toString().trim();
                if (!inputtedLastName.isEmpty()){
                    lastNameLayout.setError(null); //triggers when last name not empty
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void cooperativeInputListener(){
        cooperativeLayout = findViewById(R.id.memberCoopLayout);
        coopTxt = cooperativeLayout.getEditText();
        assert coopTxt != null;
        coopTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputtedCoop = coopTxt.getText().toString().trim();
                if (!inputtedCoop.isEmpty()){
                    cooperativeLayout.setError(null); //triggers when cooperative not empty
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void membershipIdInputListener(){
        memberIDLayout = findViewById(R.id.memberIdLayout);
        memberIdTxt = memberIDLayout.getEditText();
        assert memberIdTxt != null;
        memberIdTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputtedMemberId = memberIdTxt.getText().toString().trim();
                if (!inputtedMemberId.isEmpty()){
                    memberIDLayout.setError(null); //triggers when membership id not empty
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void addressInputListener(){
        addressLayout = findViewById(R.id.addressLayout);
        addressTxt = addressLayout.getEditText();
        assert addressTxt != null;
        addressTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                inputtedAddress = addressTxt.getText().toString().trim();
                if (!inputtedAddress.isEmpty()){
                    addressLayout.setError(null); //triggers when address id not empty
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    protected void phoneInputListener(){
        phoneLayout = findViewById(R.id.phoneNumberLayout);
        phoneTxt = phoneLayout.getEditText();
        assert phoneTxt != null;
        phoneTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputtedPhone = phoneTxt.getText().toString().trim();
                if (!inputtedPhone.isEmpty()){
                    phoneLayout.setError(null); //triggers when contact no not empty
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    //inner classes: text watchers used especially to limit input
    private static class FirstNameTextWatcher implements TextWatcher {
        private static final int MAX_FIRSTNAME_LENGTH = 30;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String firstname = editable.toString().trim();
            if (firstname.length() > MAX_FIRSTNAME_LENGTH) {
                editable.replace(0, editable.length(), firstname.substring(0, MAX_FIRSTNAME_LENGTH));
            }
        }
    }

    private static class LastNameTextWatcher implements TextWatcher {
        private static final int MAX_LASTNAME_LENGTH = 30;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String lastname = editable.toString().trim();
            if (lastname.length() > MAX_LASTNAME_LENGTH) {
                editable.replace(0, editable.length(), lastname.substring(0, MAX_LASTNAME_LENGTH));
            }
        }
    }

    private static class CooperativeTextWatcher implements TextWatcher{
        private static final int MAX_COOPERATIVE_NAME_LENGTH = 30;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String coop = editable.toString().trim();
            if (coop.length() > MAX_COOPERATIVE_NAME_LENGTH) {
                editable.replace(0, editable.length(), coop.substring(0, MAX_COOPERATIVE_NAME_LENGTH));
            }
        }
    }

    private static class MembershipIdTextWatcher implements TextWatcher{
        private static final int MAX_MEMBERSHIP_NO_LENGTH = 8;
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String memberID = editable.toString().trim();
            if (memberID.length() > MAX_MEMBERSHIP_NO_LENGTH) {
                editable.replace(0, editable.length(), memberID.substring(0, MAX_MEMBERSHIP_NO_LENGTH));
            }
        }
    }

    private static class AddressTextWatcher implements TextWatcher{
        private static final int MAX_ADDRESS_LENGTH = 50;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String address = editable.toString().trim();
            if (address.length() > MAX_ADDRESS_LENGTH) {
                editable.replace(0, editable.length(), address.substring(0, MAX_ADDRESS_LENGTH));
            }
        }
    }

    private static class PhoneTextWatcher implements TextWatcher {
        private static final int MAX_PHONE_LENGTH = 11;

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void afterTextChanged(Editable editable) {
            String phone = editable.toString().trim();
            if (phone.length() > MAX_PHONE_LENGTH) {
                editable.replace(0, editable.length(), phone.substring(0, MAX_PHONE_LENGTH));
            }
        }
    }
}