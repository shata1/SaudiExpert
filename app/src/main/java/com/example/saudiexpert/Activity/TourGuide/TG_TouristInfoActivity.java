package com.example.saudiexpert.Activity.TourGuide;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tourist;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgTouristInfoBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TG_TouristInfoActivity extends AppCompatActivity {

    ActivityTgTouristInfoBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourist = database.getReference("AllTourist");

    String touristKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgTouristInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        touristKey = getIntent().getStringExtra("TouristKey");
        if (!touristKey.isEmpty())
            loadingTouristInfo(touristKey);

        setListener();
    }

    private void loadingTouristInfo(String touristKey) {
        referenceAllTourist
                .child(touristKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tourist tourist = snapshot.getValue(Tourist.class);
                        if (tourist != null) {
                            binding.touristFirstName.setText(tourist.getFirstName());
                            binding.touristLastName.setText(tourist.getLastName());
                            binding.touristGender.setText(tourist.getGender());
                            binding.touristEmail.setText(tourist.getEmail());
                            binding.touristPhone.setText(tourist.getPhone());

                            int gender = tourist.getGender().equals("Male") ? R.drawable.ic_male : R.drawable.ic_female;
                            binding.touristGender.setCompoundDrawablesWithIntrinsicBounds(gender, 0, 0, 0);

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