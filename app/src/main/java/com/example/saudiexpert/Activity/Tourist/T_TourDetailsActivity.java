package com.example.saudiexpert.Activity.Tourist;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTTourDetailsBinding;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class T_TourDetailsActivity extends AppCompatActivity {

    ActivityTTourDetailsBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");

    Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTTourDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tour = (Tour) getIntent().getSerializableExtra("TourObject");
        if (tour != null) {
            loadingData(tour);
            loadingTourGuideInfo(tour.getTourGuideKey());
        }
        setOnClickListener();

    }

    void loadingData(Tour tour) {
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

        binding.tourTitle.setText(tour.getTourTitle());
        binding.tourDescription.setText(tour.getTourDescription());
        binding.tourPrice.setText("Price : " + tour.getTourPrice() + " SAR");
        binding.tourDuration.setText("Duration : " + tour.getTourDuration());
        binding.tourNumberOfPeople.setText("Number Of People : " + tour.getNumberOfPeople());
        binding.tourMeetingPoint.setText("MeetingPoint : " + tour.getTourMeetingPoint());
    }

    void loadingTourGuideInfo(String tourGuideKey) {
        referenceAllTourGuide
                .child(tourGuideKey)
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TourGuide tourGuide = snapshot.getValue(TourGuide.class);
                        if (tourGuide != null) {
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

                            binding.tourGuideName.setText(tourGuide.getFirstName() + tourGuide.getLastName());
                            binding.tourGuideDescription.setText(tourGuide.getBriefDescription());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.tourImage.setOnClickListener(v -> {
            showImageLayout();
        });

        binding.buttonBookTour.setOnClickListener(v -> {
            startActivity(new Intent(this, T_BookTourActivity.class)
                    .putExtra("TourObject", tour));
        });

        binding.seeMoreLayout.setOnClickListener(v -> {
            startActivity(new Intent(this, T_TourGuideInfoActivity.class).putExtra("TourGuideKey", tour.getTourGuideKey()));
        });

    }

    @SuppressLint("CheckResult")
    public void showImageLayout() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.image_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //

        ImageButton imageClose = dialogView.findViewById(R.id.imageClose);
        PhotoView tourImage = dialogView.findViewById(R.id.tourImage);

        try {
            Picasso
                    .get()
                    .load(tour.getTourImageUrl() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(tourImage);
        } catch (Exception e) {
            e.printStackTrace();
        }

        imageClose.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

}