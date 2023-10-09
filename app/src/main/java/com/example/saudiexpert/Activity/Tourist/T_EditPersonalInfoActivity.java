package com.example.saudiexpert.Activity.Tourist;

import android.app.DatePickerDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tourist;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTEditPersonalInfoBinding;
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

public class T_EditPersonalInfoActivity extends AppCompatActivity {

    ActivityTEditPersonalInfoBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourist = database.getReference("AllTourist");

    Tourist currentTourist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTEditPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingTouristInfo();
        setOnClickListener();
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.selectBirthDate.setOnClickListener(v -> {
            getBirthDate();
        });

        binding.buttonSave.setOnClickListener(v -> {
            reviewData();
        });
    }

    public void reviewData() {

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

        Tourist tourist = currentTourist;
        tourist.setFirstName(inputFirstName);
        tourist.setLastName(inputLastName);
        tourist.setPhone(inputPhoneNumber);
        tourist.setBirthDate(inputBirthDate);
        tourist.setGender(inputGender);

        // Recording user data into the database
        referenceAllTourist
                .child(user.getUid())
                .setValue(tourist);

        if (!currentTourist.getPassword().equals(inputPassword)) {
            updatePassword(currentTourist, inputPassword);
        } else {
            loading(false);
            Toasty.success(this, "Updated successfully", Toast.LENGTH_SHORT).show();
        }


    }

    void getBirthDate() {
        Calendar calendar = Calendar.getInstance();
        final int Year = calendar.get(Calendar.YEAR);
        final int Month = calendar.get(Calendar.MONTH);
        final int Day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, year1, month1, dayOfMonth) -> {

                    String sDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    binding.inputBirthDate.setText(sDate);
                }, Year, Month, Day
        );
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        datePickerDialog.show();
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

    void loadingTouristInfo() {
        referenceAllTourist
                .child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        Tourist tourist = snapshot.getValue(Tourist.class);
                        if (tourist != null) {
                            currentTourist = tourist;
                            binding.inputIDNumber.setText(String.valueOf(tourist.getIdNumber()));
                            binding.inputFirstName.setText(tourist.getFirstName());
                            binding.inputLastName.setText(tourist.getLastName());
                            binding.inputPhoneNumber.setText(tourist.getPhone());
                            binding.inputEmail.setText(tourist.getEmail());
                            binding.inputPassword.setText(tourist.getPassword());
                            binding.inputConfirmationPassword.setText(tourist.getPassword());
                            binding.inputBirthDate.setText(tourist.getBirthDate());
                            String gender = tourist.getGender();
                            if (gender.equals("Male")) {
                                binding.male.setChecked(true);
                            } else if (gender.equals("Female")) {
                                binding.female.setChecked(true);
                            } else {
                                binding.male.setChecked(false);
                                binding.female.setChecked(false);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void updatePassword(Tourist tourist, String newPassword) {
        loading(true);
        AuthCredential credential = EmailAuthProvider.getCredential(tourist.getEmail(), tourist.getPassword());
        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    user.updatePassword(newPassword)
                            .addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    referenceAllTourist
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