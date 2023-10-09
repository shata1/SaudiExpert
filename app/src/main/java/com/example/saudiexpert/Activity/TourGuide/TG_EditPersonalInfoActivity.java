package com.example.saudiexpert.Activity.TourGuide;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgEditPersonalInfoBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TG_EditPersonalInfoActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ActivityTgEditPersonalInfoBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageReference = storage.getReference("TourGuideImages");

    //
    private static final int GALLERY_REQUEST = 1;

    // SpeakingLanguages
    boolean[] selectedLanguages;
    ArrayList<Integer> languageList = new ArrayList<>();
    String[] languageArray = {"Arabic", "English", "Francais", "German"};

    Uri imageUri_Gallery = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgEditPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadingPersonalInfo();
        setOnClickListener();
        getSpeakingLanguages();
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

        binding.buttonSave.setOnClickListener(v -> {
            updateTourGuide();
        });
    }

    public void updateTourGuide() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        String inputBriefDescription = Objects.requireNonNull(binding.inputBriefDescription.getText()).toString().trim();
        String inputNationality = Objects.requireNonNull(binding.inputNationality.getText()).toString().trim();
        String inputSpeakingLanguages = Objects.requireNonNull(binding.inputSpeakingLanguages.getText()).toString().trim();

        referenceAllTourGuide
                .child(user.getUid())
                .child("briefDescription")
                .setValue(inputBriefDescription);

        referenceAllTourGuide
                .child(user.getUid())
                .child("nationality")
                .setValue(inputNationality);

        referenceAllTourGuide
                .child(user.getUid())
                .child("speakingLanguages")
                .setValue(inputSpeakingLanguages);

        if (imageUri_Gallery != null) {
            uploadImage(imageUri_Gallery);
        } else {
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
                            if (!tourGuide.getImageUrl().isEmpty()) {
                                try {
                                    Picasso
                                            .get()
                                            .load(tourGuide.getImageUrl() + "")
                                            .fit()
                                            .placeholder(R.drawable.loading)
                                            .into(binding.imageProfile);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                binding.textAddImage.setVisibility(View.GONE);
                            }

                            binding.inputBriefDescription.setText(tourGuide.getBriefDescription());
                            binding.inputNationality.setText(tourGuide.getNationality());
                            binding.inputSpeakingLanguages.setText(tourGuide.getSpeakingLanguages());

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        loading(false);
                    }
                });
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

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSave.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSave.setVisibility(View.VISIBLE);
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
                                                Toasty.success(this, "Updated successfully", Toast.LENGTH_SHORT).show();
                                                imageUri_Gallery = null;
                                            });
                                });
                    }).addOnFailureListener(e -> {
                        loading(false);
                    });
        }
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

}