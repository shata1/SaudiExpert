package com.example.saudiexpert.Activity.TourGuide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.databinding.ActivityTgSignUpBinding;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TG_SignUpActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ActivityTgSignUpBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("TourGuideImages");

    //
    private static final int GALLERY_REQUEST = 1;

    // BirthDate
    DatePickerDialog.OnDateSetListener setListener;

    // SpeakingLanguages
    boolean[] selectedLanguages;
    ArrayList<Integer> languageList = new ArrayList<>();
    String[] languageArray = {"Arabic", "English", "Francais", "German"};

    Uri imageUri_Gallery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgSignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setOnClickListener();
        getBirthDate();
        getSpeakingLanguages();
    }

    void init() {
        setIDNumber();
    }

    void setOnClickListener() {
        binding.layoutImage.setOnClickListener(v -> {
            //  Define storage permissions
            String[] strings = {Manifest.permission.READ_EXTERNAL_STORAGE};
            //  Check condition
            if (EasyPermissions.hasPermissions(this, strings)) {
                openGallery();
            } else {
                EasyPermissions.requestPermissions(
                        this,
                        "App needs access to your camera & storage",
                        100,
                        strings
                );
            }
        });

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

        String inputBirthDate = Objects.requireNonNull(binding.inputBirthDate.getText()).toString().trim();
        String inputSpeakingLanguages = Objects.requireNonNull(binding.inputSpeakingLanguages.getText()).toString().trim();
        String inputNationality = Objects.requireNonNull(binding.inputNationality.getText()).toString().trim();
        String inputBriefDescription = Objects.requireNonNull(binding.inputBriefDescription.getText()).toString().trim();


        TourGuide tourGuide = new TourGuide();
        tourGuide.setIdNumber(Integer.parseInt(IDNumber));
        tourGuide.setFirstName(inputFirstName);
        tourGuide.setLastName(inputLastName);
        tourGuide.setEmail(inputEmail);
        tourGuide.setPassword(inputPassword);
        tourGuide.setBirthDate(inputBirthDate);
        tourGuide.setSpeakingLanguages(inputSpeakingLanguages);
        tourGuide.setNationality(inputNationality);
        tourGuide.setBriefDescription(inputBriefDescription);
        tourGuide.setImageUrl("");

        // Recording user data into the database
        TourGuideRegistration(tourGuide);
    }

    public void TourGuideRegistration(TourGuide tourGuide) {
        loading(true);
        auth
                .createUserWithEmailAndPassword(tourGuide.getEmail(), tourGuide.getPassword())
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user = auth.getCurrentUser();
                        if (user != null) {
                            tourGuide.setKey(user.getUid());
                            referenceAllTourGuide
                                    .child(user.getUid())
                                    .setValue(tourGuide);
                            UserProfileChangeRequest profile = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(tourGuide.getFirstName().trim() + tourGuide.getLastName().trim())
                                    .build();
                            user.updateProfile(profile).addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    setTourGuide(true);
                                    if (imageUri_Gallery != null) {
                                        uploadImage(imageUri_Gallery);
                                    } else {
                                        loading(false);
                                        Toasty.success(this, "Account has been successfully registered", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(TG_SignUpActivity.this, TG_LoginActivity.class)
                                                .putExtra("User_Type", "Tour_Guide"));
                                        finish();
                                    }
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

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignUp.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSignUp.setVisibility(View.VISIBLE);
        }
    }

    @SuppressLint("SetTextI18n")
    void setIDNumber() {
        String longID = String.valueOf(System.currentTimeMillis());
        binding.inputIDNumber.setText(longID.substring(longID.length() - 7));
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

    void getSpeakingLanguages() {
        selectedLanguages = new boolean[languageArray.length];

        binding.selectSpeakingLanguages.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Select Speaking Language");
            builder.setCancelable(false);
            builder.setMultiChoiceItems(languageArray, selectedLanguages, (dialog, which, isChecked) -> {
                if (isChecked) {
                    languageList.add(which);
                    Collections.sort(languageList);
                } else {
                    languageList.remove(which);
                }
            });
            builder.setPositiveButton("OK", (dialog, which) -> {
                StringBuilder stringBuilder = new StringBuilder();
                for (int i = 0; i < languageList.size(); i++) {
                    stringBuilder.append(languageArray[languageList.get(i)]);
                    if (i != languageList.size() - 1) {
                        stringBuilder.append(",");
                    }
                }
                binding.inputSpeakingLanguages.setText(stringBuilder.toString());
            });

            builder.setNegativeButton("Cancel", (dialog, which) -> {
                dialog.dismiss();
            });

            builder.setNeutralButton("Clear All", (dialog, which) -> {
                for (int i = 0; i < selectedLanguages.length; i++) {
                    selectedLanguages[i] = false;
                    languageList.clear();
                    binding.inputSpeakingLanguages.setText("");
                }
            });
            builder.show();
        });
    }

    public void openGallery() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("image/*");
        startActivityForResult(galleryIntent, GALLERY_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            try {
                imageUri_Gallery = data.getData();
                binding.imageProfile.setImageURI(imageUri_Gallery);
                binding.textAddImage.setVisibility(View.GONE);
            } catch (Exception e) {
                finish();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (requestCode == 100 && perms.size() == 1) {
            openGallery();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        } else {
            Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            loading(true);
            StorageReference ref = storageReference
                    .child(user.getUid())
                    .child("TourGuide_Image.jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    referenceAllTourGuide
                                            .child(user.getUid())
                                            .child("imageUrl")
                                            .setValue(uri.toString())
                                            .addOnSuccessListener(unused -> {
                                                loading(false);
                                                Toasty.success(TG_SignUpActivity.this, "Account has been successfully registered", Toast.LENGTH_SHORT).show();
                                                startActivity(new Intent(TG_SignUpActivity.this, TG_LoginActivity.class));
                                                finish();
                                            });
                                });
                    }).addOnFailureListener(e -> {
                        loading(false);
                    });
        }
    }

    public void setTourGuide(boolean tourGuide) {
        SharedPreferences sp = getSharedPreferences("UserType", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("TourGuide", tourGuide);
        editor.apply();
    }
}