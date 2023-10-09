package com.example.saudiexpert.Activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Activity.Guest.G_HomeActivity;
import com.example.saudiexpert.Activity.TourGuide.TG_LoginActivity;
import com.example.saudiexpert.Activity.TourGuide.TG_SignUpActivity;
import com.example.saudiexpert.Activity.Tourist.T_LoginActivity;
import com.example.saudiexpert.Activity.Tourist.T_SignUpActivity;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityRegisterBinding;


public class RegisterActivity extends AppCompatActivity {

    ActivityRegisterBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.btnSignUp.setOnClickListener(v -> {
            showDialogUserTypeLayout("SignUp");
        });

        binding.btnLogin.setOnClickListener(v -> {
            showDialogUserTypeLayout("Login");
        });

        binding.btnExplore.setOnClickListener(v -> {
            startActivity(new Intent(this, G_HomeActivity.class));
            finish();
        });

    }

    @SuppressLint("CheckResult")
    public void showDialogUserTypeLayout(String registerType) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.user_type_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));

        TextView tourGuide = dialogView.findViewById(R.id.tourGuide);
        TextView tourist = dialogView.findViewById(R.id.tourist);

        tourGuide.setOnClickListener(v -> {
            if (registerType.equals("SignUp")) {
                startActivity(new Intent(RegisterActivity.this, TG_SignUpActivity.class));
            } else if (registerType.equals("Login")) {
                startActivity(new Intent(RegisterActivity.this, TG_LoginActivity.class));
            }
            alertDialog.dismiss();
        });

        tourist.setOnClickListener(v -> {
            if (registerType.equals("SignUp")) {
                startActivity(new Intent(RegisterActivity.this, T_SignUpActivity.class));
            } else if (registerType.equals("Login")) {
                startActivity(new Intent(RegisterActivity.this, T_LoginActivity.class));
            }
            alertDialog.dismiss();
        });
    }

}