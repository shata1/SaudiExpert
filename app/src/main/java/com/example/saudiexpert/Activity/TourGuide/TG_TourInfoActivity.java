package com.example.saudiexpert.Activity.TourGuide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgTourInfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class TG_TourInfoActivity extends AppCompatActivity {

    ActivityTgTourInfoBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTours = database.getReference("AllTours");

    String tourID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgTourInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tourID = getIntent().getStringExtra("TourID");
        if (!tourID.isEmpty())
            loadingTourInfo(tourID);

        setListener();
    }

    private void loadingTourInfo(String tourID) {
        referenceAllTours
                .child(tourID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tour tour = snapshot.getValue(Tour.class);
                        if(tour!=null)
                        {
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
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    void setListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });
    }
}