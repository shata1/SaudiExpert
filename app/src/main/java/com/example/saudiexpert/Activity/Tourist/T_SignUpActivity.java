package com.example.saudiexpert.Activity.Tourist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tourist;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTSignUpBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class T_SignUpActivity extends AppCompatActivity {

    ActivityTSignUpBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourist = database.getReference("AllTourist");

    // BirthDate
    DatePickerDialog.OnDateSetListener setListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setOnClickListener();
        getBirthDate();
    }

    void init() {
        setIDNumber();
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.buttonSignUp.setOnClickListener(v -> {
            reviewInputData();
        });
    }

    public void reviewInputData() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        loading(true);

        String IDNumber = Objects.requireNonNull(binding.inputIDNumber.getText()).toString().trim();
        String inputFirstName = Objects.requireNonNull(binding.inputFirstName.getText()).toString().trim();
        if (inputFirstName.isEmpty()) {
            binding.inputFirstName.setError("First name is required");
            binding.inputFirstName.setFocusable(true);
            binding.inputFirstName.requestFocus();
            loading(false);
            return;
        }

        String inputLastName = Objects.requireNonNull(binding.inputLastName.getText()).toString().trim();
        if (inputLastName.isEmpty()) {
            binding.inputLastName.setError("Last name is required");
            binding.inputLastName.setFocusable(true);
            binding.inputLastName.requestFocus();
            loading(false);
            return;
        }

        String inputPhoneNumber = Objects.requireNonNull(binding.inputPhoneNumber.getText()).toString();
        if (inputPhoneNumber.isEmpty()) {
            binding.inputPhoneNumber.setError("Phone Number is required");
            binding.inputPhoneNumber.setFocusable(true);
            binding.inputPhoneNumber.requestFocus();
            loading(false);
            return;
        }

        String inputEmail = Objects.requireNonNull(binding.inputEmail.getText()).toString().trim();
        if (inputEmail.isEmpty()) {
            binding.inputEmail.setError("Email name is required");
            binding.inputEmail.setFocusable(true);
            binding.inputEmail.requestFocus();
            loading(false);
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(inputEmail).matches()) {
            binding.inputEmail.setError("Please enter valid email");
            binding.inputEmail.setFocusable(true);
            binding.inputEmail.requestFocus();
            loading(false);
            return;
        }

        String inputPassword = Objects.requireNonNull(binding.inputPassword.getText()).toString().trim();
        if (inputPassword.isEmpty()) {
            binding.inputPassword.setError("Password is required");
            binding.inputPassword.setFocusable(true);
            binding.inputPassword.requestFocus();
            loading(false);
            return;
        }
        if (inputPassword.length() < 8) {
            binding.inputPassword.setError("The minimum password length must be 8");
            binding.inputPassword.setFocusable(true);
            binding.inputPassword.requestFocus();
            loading(false);
            return;
        }

        if (inputPassword.contains(" ")) {
            binding.inputPassword.setError("Password must not contain a white space");
            binding.inputPassword.setFocusable(true);
            binding.inputPassword.requestFocus();
            loading(false);
            return;
        }

        String confirmPassword = Objects.requireNonNull(binding.inputConfirmationPassword.getText()).toString().trim();
        if (!confirmPassword.equals(inputPassword)) {
            binding.inputConfirmationPassword.setError("Password does not match");
            binding.inputConfirmationPassword.setFocusable(true);
            binding.inputConfirmationPassword.requestFocus();
            loading(false);
            return;
        }

        String inputGender = "";
        if (binding.radioGroupGender.getCheckedRadioButtonId() == -1) {
            Toasty.info(this, "Please select gender", Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        } else {
            RadioButton selectedType = findViewById(binding.radioGroupGender.getCheckedRadioButtonId());
            if (selectedType.getId() == R.id.male) {
                inputGender = "Male";
            } else {
                inputGender = "Female";
            }
        }

        String inputBirthDate = Objects.requireNonNull(binding.inputBirthDate.getText()).toString().trim();

        Tourist tourist = new Tourist();
        tourist.setIdNumber(Integer.parseInt(IDNumber));
        tourist.setFirstName(inputFirstName);
        tourist.setLastName(inputLastName);
        tourist.setPhone(inputPhoneNumber);
        tourist.setEmail(inputEmail);
        tourist.setPassword(inputPassword);
        tourist.setBirthDate(inputBirthDate);
        tourist.setGender(inputGender);

        // Recording user data into the database
        TouristRegistration(tourist);
    }

    public void TouristRegistration(Tourist tourist) {
        loading(true);
        auth
                .createUserWithEmailAndPassword(tourist.getEmail(), tourist.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        if (user != null) {
                            tourist.setKey(user.getUid());
                            referenceAllTourist
                                    .child(user.getUid())
                                    .setValue(tourist);
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(tourist.getFirstName().trim() + tourist.getLastName().trim())
                                    .build();
                            user.updateProfile(profile).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    setTourGuide(false);
                                    loading(false);
                                    Toasty.success(this, "Account has been successfully registered", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(this, T_LoginActivity.class));
                                    finish();
                                }
                            });
                        }
                    } else {
                        loading(false);
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toasty.warning(this, "You are already registered", Toast.LENGTH_SHORT).show();
                        } else if (task.getException() instanceof FirebaseNetworkException) {
                            Toasty.warning(this, "There is no internet connection…", Toast.LENGTH_SHORT).show();
                        } else {
                            Toasty.error(this, "Exception -> " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(e -> {
                    loading(false);
                    if (e instanceof FirebaseAuthUserCollisionException) {
                        Toasty.warning(this, "You are already registered", Toast.LENGTH_SHORT).show();
                    } else if (e instanceof FirebaseNetworkException) {
                        Toasty.warning(this, "There is no internet connection…", Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(this, "Exception -> " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void getBirthDate() {
        Calendar calendar = Calendar.getInstance();
        final int Year = calendar.get(Calendar.YEAR);
        final int Month = calendar.get(Calendar.MONTH);
        final int Day = calendar.get(Calendar.DAY_OF_MONTH);

        binding.selectBirthDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    setListener, Year, Month, Day
            );
            datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            datePickerDialog.show();
        });

        setListener = (view, year, month, dayOfMonth) -> {
            month = month + 1;
            String date = dayOfMonth + "/" + month + "/" + year;
            binding.inputBirthDate.setText(date);
        };
    }

    @SuppressLint("SetTextI18n")
    void setIDNumber() {
        String longID = String.valueOf(System.currentTimeMillis());
        binding.inputIDNumber.setText(longID.substring(longID.length() - 7));
    }

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    public void setTourGuide(boolean tourGuide) {
        SharedPreferences sp = getSharedPreferences("UserType", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("TourGuide", tourGuide);
        editor.apply();
    }

}