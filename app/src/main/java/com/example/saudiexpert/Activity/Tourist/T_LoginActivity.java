package com.example.saudiexpert.Activity.Tourist;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.saudiexpert.databinding.ActivityTLoginBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class T_LoginActivity extends AppCompatActivity {

    ActivityTLoginBinding binding;

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setOnClickListener();
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.buttonLogin.setOnClickListener(v -> {
            reviewInputData();
        });
    }

    void reviewInputData() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        loading(true);

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
            binding.inputPassword.setError("Password must not contain a space");
            binding.inputPassword.setFocusable(true);
            binding.inputPassword.requestFocus();
            loading(false);
            return;
        }

        TouristLogin(inputEmail, inputPassword);
    }

    void TouristLogin(String Email, String Password) {
        firebaseAuth.signInWithEmailAndPassword(Email, Password)
                .addOnCompleteListener(task -> {
                    loading(false);
                    if (task.isSuccessful()) {
                        setTourGuide(false);
                        Toasty.success(this, "You are logged in successfully", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, T_HomeActivity.class));
                        ActivityCompat.finishAffinity(this);
                    } else if (task.getException() instanceof FirebaseNetworkException) {
                        Toasty.warning(this, "There is no internet connection…", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseAuthInvalidUserException) {
                        Toasty.warning(this, "This user was not found…", Toast.LENGTH_SHORT).show();
                    } else if ((task.getException() instanceof FirebaseAuthInvalidCredentialsException)) {
                        Toasty.warning(this, "The password is incorrect…", Toast.LENGTH_SHORT).show();
                    } else if (task.getException() instanceof FirebaseTooManyRequestsException) {
                        Toasty.warning(this, "I tried entering the password several times.\nTry after 1 minute", Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.warning(this, "Error+->" + task.getException(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonLogin.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonLogin.setVisibility(View.VISIBLE);
        }
    }

    public void setTourGuide(boolean tourGuide) {
        SharedPreferences sp = getSharedPreferences("UserType", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("TourGuide", tourGuide);
        editor.apply();
    }


}