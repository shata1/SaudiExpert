package com.example.saudiexpert.Activity.TourGuide;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.databinding.ActivityTgEditProfileBinding;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class TG_EditProfileActivity extends AppCompatActivity {

    ActivityTgEditProfileBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");

    // BirthDate
    DatePickerDialog.OnDateSetListener setListener;

    TourGuide currentTourGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingPersonalInfo();
        setOnClickListener();
        getBirthDate();
    }

    void setOnClickListener() {

        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.buttonSave.setOnClickListener(v -> {
            updateTourGuide();
        });
    }

    public void updateTourGuide() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        loading(true);

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

        String inputBirthDate = Objects.requireNonNull(binding.inputBirthDate.getText()).toString().trim();

        referenceAllTourGuide
                .child(user.getUid())
                .child("firstName")
                .setValue(inputFirstName);

        referenceAllTourGuide
                .child(user.getUid())
                .child("lastName")
                .setValue(inputLastName);

        referenceAllTourGuide
                .child(user.getUid())
                .child("birthDate")
                .setValue(inputBirthDate);


        if (!currentTourGuide.getPassword().equals(inputPassword)) {
            updatePassword(currentTourGuide, inputPassword);
        } else {
            loading(false);
            Toasty.success(this, "Updated successfully", Toast.LENGTH_SHORT).show();
        }
    }

    void loadingPersonalInfo() {
        loading(true);
        referenceAllTourGuide
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TourGuide tourGuide = snapshot.getValue(TourGuide.class);
                        if (tourGuide != null) {
                            loading(false);
                            binding.inputIDNumber.setText(String.valueOf(tourGuide.getIdNumber()));
                            binding.inputFirstName.setText(tourGuide.getFirstName());
                            binding.inputLastName.setText(tourGuide.getLastName());
                            binding.inputEmail.setText(tourGuide.getEmail());
                            binding.inputPassword.setText(tourGuide.getPassword());
                            binding.inputConfirmationPassword.setText(tourGuide.getPassword());
                            binding.inputBirthDate.setText(tourGuide.getBirthDate());
                            currentTourGuide = tourGuide;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loading(false);
                    }
                });
    }

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSave.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSave.setVisibility(View.VISIBLE);
        }
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

    void updatePassword(TourGuide tourGuide, String newPassword) {
        loading(true);
        AuthCredential credential = EmailAuthProvider.getCredential(tourGuide.getEmail(), tourGuide.getPassword());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    referenceAllTourGuide
                                            .child(user.getUid())
                                            .child("password")
                                            .setValue(newPassword)
                                            .addOnCompleteListener(task2 -> {
                                                if (task2.isSuccessful()) {
                                                    loading(false);
                                                    Toasty.success(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                } else {
                                    try {
                                        throw task1.getException();
                                    } catch (Exception e) {
                                        Toast.makeText(this, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        loading(false);
                                    }
                                }
                            });
                });
    }

}