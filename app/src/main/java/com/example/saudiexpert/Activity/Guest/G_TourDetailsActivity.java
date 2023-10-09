package com.example.saudiexpert.Activity.Guest;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Activity.Tourist.T_TourGuideInfoActivity;
import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityGTourDetailsBinding;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class G_TourDetailsActivity extends AppCompatActivity {

    ActivityGTourDetailsBinding binding;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");

    Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityGTourDetailsBinding.inflate(getLayoutInflater());
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
            showDialogMessageLayout("You need to have an account to book an appointment.");
        });

        binding.seeMoreLayout.setOnClickListener(v -> {
            startActivity(new Intent(this, T_TourGuideInfoActivity.class)
                    .putExtra("TourGuideKey", tour.getTourGuideKey()));
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
            alertDialog.dismiss();
        });
    }
}