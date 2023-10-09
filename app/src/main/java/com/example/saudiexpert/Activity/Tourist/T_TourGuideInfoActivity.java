package com.example.saudiexpert.Activity.Tourist;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTTourGuideInfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class T_TourGuideInfoActivity extends AppCompatActivity {

    ActivityTTourGuideInfoBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");
    DatabaseReference referenceAllTours = database.getReference("AllTours");

    String spokenLanguages = "";
    String tours = "";
    String tourGuidKey = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTTourGuideInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tourGuidKey = getIntent().getStringExtra("TourGuideKey");
        loadingTourGuideInfo(tourGuidKey);
        getTourGuideTours(tourGuidKey);
        setOnClickListener();
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
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
                                        .into(binding.tourGuideImageProfile);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            binding.tourGuideName.setText(tourGuide.getFirstName() + tourGuide.getLastName());
                            binding.tourGuideNationality.setText(tourGuide.getNationality());
                            binding.tourGuideDescription.setText(tourGuide.getBriefDescription());

                            spokenLanguages = tourGuide.getSpeakingLanguages();
                            spokenLanguages = "* " + spokenLanguages.replaceAll(",", "\n* ");

                            binding.tourGuideSpokenLanguages.setText(spokenLanguages);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void getTourGuideTours(String TourGuideKey) {
        referenceAllTours
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Tour tour = snapshot.getValue(Tour.class);
                            if (tour != null) {
                                if (tour.getTourGuideKey().equals(TourGuideKey)) {
                                    String tourTitle= tour.getTourTitle();
                                    if (!tours.contains(tourTitle)) {
                                        tours = tours + "* " + tourTitle + "\n";
                                    }
                                }
                            }
                        }
                        binding.tourGuideTours.setText(tours);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

    }
}