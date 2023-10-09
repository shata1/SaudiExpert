package com.example.saudiexpert.Activity.TourGuide;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgAddTourBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import es.dmoral.toasty.Toasty;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class TG_AddTourActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    ActivityTgAddTourBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTours = database.getReference("AllTours");
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageTourImages = storage.getReference("TourImages");

    //
    private static final int GALLERY_REQUEST = 1;
    Uri imageUri_Gallery = null;

    ArrayList<String> locations;
    String location;

    ArrayList<String> durations;
    String duration;

    ArrayList<String> manyPeoples;
    String manyPeople;

    int currentHour, currentMinute;

    Tour objectTour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgAddTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
        setOnClickListener();

        objectTour = (Tour) getIntent().getSerializableExtra("TourObject");
        if (objectTour != null)
            loadingData(objectTour);
        else
            setIDNumber();

    }

    void init() {

        locations = new ArrayList<>();
        locations.add("Is Riyadh");
        locations.add("Tabuk");
        locations.add("Asir");
        locations.add("Al-Ahsa");
        locations.add("Jeddah");

        ArrayAdapter locationAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, locations);
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inputLocation.setAdapter(locationAdapter);

        durations = new ArrayList<>();
        durations.add("1 Hour");
        durations.add("2 Hour");
        durations.add("3 Hour");
        durations.add("4 Hour");
        durations.add("5 Hour");
        durations.add("6 Hour");
        durations.add("7 Hour");
        durations.add("8 Hour");

        ArrayAdapter durationAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, durations);
        durationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inputDuration.setAdapter(durationAdapter);

        manyPeoples = new ArrayList<>();
        manyPeoples.add("2 People");
        manyPeoples.add("3 People");
        manyPeoples.add("4 People");
        manyPeoples.add("5 People");
        manyPeoples.add("6 People");
        manyPeoples.add("7 People");

        ArrayAdapter manyPeopleAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, manyPeoples);
        manyPeopleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.inputManyPeople.setAdapter(manyPeopleAdapter);

    }

    @SuppressLint("SimpleDateFormat")
    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.AddTourImageLayout.setOnClickListener(v -> {
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

        binding.inputLocation.setOnItemClickListener((parent, view, position, id) -> {
            location = locations.get(position);
        });

        binding.selectTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(
                    TG_AddTourActivity.this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                    (view, hourOfDay, minute) -> {
                        currentHour = hourOfDay;
                        currentMinute = minute;

                        String Time = currentHour + ":" + currentMinute;
                        SimpleDateFormat f24Hours = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
                        try {
                            Date date = f24Hours.parse(Time);
                            SimpleDateFormat f12Hours = new SimpleDateFormat("hh:mm aa", Locale.ENGLISH);
                            binding.inputStartTime.setText(f12Hours.format(date));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }, 12, 0, false
            );

            timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            timePickerDialog.updateTime(currentHour, currentMinute);
            timePickerDialog.show();
        });

        binding.inputDuration.setOnItemClickListener((parent, view, position, id) -> {
            duration = durations.get(position);
        });

        binding.inputManyPeople.setOnItemClickListener((parent, view, position, id) -> {
            manyPeople = manyPeoples.get(position);
        });

        binding.buttonSaveTour.setOnClickListener(v -> {
            reviewInputTour();
        });

    }

    private void loadingData(Tour tour) {
        binding.titlePage.setText("Update Tour");
        try {
            Picasso
                    .get()
                    .load(tour.getTourImageUrl() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(binding.tourImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        binding.tourImage.setVisibility(View.VISIBLE);
        binding.hintImage.setVisibility(View.GONE);

        binding.inputIDNumber.setText(String.valueOf(tour.getTourID()));
        binding.inputTitle.setText(tour.getTourTitle());
        binding.inputDescription.setText(tour.getTourDescription());
        binding.inputLocation.setText(tour.getTourLocation(), false);
        binding.inputStartTime.setText(tour.getTourStarTime());
        binding.inputDuration.setText(tour.getTourDuration(), false);
        binding.inputManyPeople.setText(tour.getNumberOfPeople() + " People", false);
        binding.inputPrice.setText(String.valueOf(tour.getTourPrice()));
        binding.inputMeetingPoint.setText(tour.getTourMeetingPoint());
    }

    private void reviewInputTour() {

        // Hide the android keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(binding.getRoot().getWindowToken(), 0);

        loading(true);

        String IDNumber = Objects.requireNonNull(binding.inputIDNumber.getText()).toString().trim();

        if (imageUri_Gallery == null && objectTour == null) {
            Toasty.warning(this, "Please Add Tour Image ", Toast.LENGTH_SHORT).show();
            loading(false);
            return;
        }
        String inputTitle = Objects.requireNonNull(binding.inputTitle.getText()).toString().trim();
        if (inputTitle.isEmpty()) {
            binding.inputTitle.setError("Title is required");
            binding.inputTitle.setFocusable(true);
            binding.inputTitle.requestFocus();
            loading(false);
            return;
        }

        String inputDescription = Objects.requireNonNull(binding.inputDescription.getText()).toString().trim();
        if (inputDescription.isEmpty()) {
            binding.inputDescription.setError("Description is required");
            binding.inputDescription.setFocusable(true);
            binding.inputDescription.requestFocus();
            loading(false);
            return;
        }

        String inputLocation = Objects.requireNonNull(binding.inputLocation.getText()).toString().trim();
        if (inputLocation.isEmpty()) {
            Toasty.warning(this, "Location is required", Toast.LENGTH_SHORT).show();
            binding.inputLocation.setError("");
            binding.inputLocation.setFocusable(true);
            binding.inputLocation.requestFocus();
            loading(false);
            return;
        } else {
            binding.inputLocation.setError(null);
        }

        String inputStartTime = Objects.requireNonNull(binding.inputStartTime.getText()).toString().trim();
        if (inputStartTime.isEmpty()) {
            Toasty.warning(this, "StartTime is required", Toast.LENGTH_SHORT).show();
            binding.inputStartTime.setError("");
            binding.inputStartTime.setFocusable(true);
            binding.inputStartTime.requestFocus();
            loading(false);
            return;
        } else {
            binding.inputStartTime.setError(null);
        }

        String inputDuration = Objects.requireNonNull(binding.inputDuration.getText()).toString().trim();
        if (inputDuration.isEmpty()) {
            Toasty.warning(this, "Duration is required", Toast.LENGTH_SHORT).show();
            binding.inputDuration.setError("");
            binding.inputDuration.setFocusable(true);
            binding.inputDuration.requestFocus();
            loading(false);
            return;
        } else {
            binding.inputDuration.setError(null);
        }

        String inputManyPeople = Objects.requireNonNull(binding.inputManyPeople.getText()).toString().trim();
        if (inputManyPeople.isEmpty()) {
            Toasty.warning(this, "ManyPeople is required", Toast.LENGTH_SHORT).show();
            binding.inputManyPeople.setError("");
            binding.inputManyPeople.setFocusable(true);
            binding.inputManyPeople.requestFocus();
            loading(false);
            return;
        } else {
            binding.inputManyPeople.setError(null);
        }

        String inputPrice = Objects.requireNonNull(binding.inputPrice.getText()).toString().trim();
        if (inputPrice.isEmpty()) {
            binding.inputPrice.setError("Price is required");
            binding.inputPrice.setFocusable(true);
            binding.inputPrice.requestFocus();
            loading(false);
            return;
        }

        String inputMeetingPoint = Objects.requireNonNull(binding.inputMeetingPoint.getText()).toString().trim();
        if (inputMeetingPoint.isEmpty()) {
            binding.inputMeetingPoint.setError("Meeting Point is required");
            binding.inputMeetingPoint.setFocusable(true);
            binding.inputMeetingPoint.requestFocus();
            loading(false);
            return;
        }

        Tour tour = new Tour();
        tour.setTourID(IDNumber);
        tour.setTourGuideKey(user.getUid());
        tour.setTourTitle(inputTitle);
        tour.setTourDescription(inputDescription);
        tour.setTourLocation(inputLocation);
        tour.setTourStarTime(inputStartTime);
        tour.setTourDuration(inputDuration);
        tour.setNumberOfPeople(manyPeoples.indexOf(inputManyPeople) + 2);
        tour.setTourPrice(Integer.parseInt(inputPrice));
        tour.setTourMeetingPoint(inputMeetingPoint);

        if (objectTour == null)
            tour.setTourImageUrl("");
        else
            tour.setTourImageUrl(objectTour.getTourImageUrl() + "");

        // Upload Tour into Database
        if (imageUri_Gallery == null)
            SaveTour(tour);
        else
            SaveTourTour(imageUri_Gallery, tour);

    }

    private void SaveTour(Tour tour) {
        loading(true);
        referenceAllTours
                .child(tour.getTourID())
                .setValue(tour)
                .addOnSuccessListener(unused -> {
                    loading(false);
                    showDialogMessageLayout("Your tour has been saved Successfully");
                }).addOnFailureListener(e -> loading(false));
    }

    private void SaveTourTour(Uri filePath, Tour tour) {
        if (filePath != null) {
            loading(true);
            StorageReference ref = storageTourImages
                    .child(tour.getTourID())
                    .child("TourImage.jpg");
            ref.putFile(filePath)
                    .addOnSuccessListener(taskSnapshot -> {
                        ref.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    tour.setTourImageUrl(uri.toString());
                                    loading(true);
                                    referenceAllTours
                                            .child(tour.getTourID())
                                            .setValue(tour)
                                            .addOnSuccessListener(unused -> {
                                                loading(false);
                                                showDialogMessageLayout("Your tour has been saved Successfully");
                                            });
                                });
                    }).addOnFailureListener(e -> {
                        loading(false);
                    });
        }
    }

    @SuppressLint("SetTextI18n")
    void setIDNumber() {
        String longID = String.valueOf(System.currentTimeMillis());
        binding.inputIDNumber.setText(longID.substring(longID.length() - 7));
    }

    void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSaveTour.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
            binding.buttonSaveTour.setVisibility(View.VISIBLE);
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
                binding.tourImage.setImageURI(imageUri_Gallery);
                binding.tourImage.setVisibility(View.VISIBLE);
                binding.hintImage.setVisibility(View.GONE);
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

    @SuppressLint("CheckResult")
    public void showDialogMessageLayout(String Msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //

        TextView message = dialogView.findViewById(R.id.inputMessage);
        Button done = dialogView.findViewById(R.id.buttonDone);

        message.setText(Msg);
        done.setOnClickListener(v -> {
            startActivity(new Intent(TG_AddTourActivity.this, TG_HomeActivity.class));
            finishAffinity();
            alertDialog.dismiss();
        });
    }
}