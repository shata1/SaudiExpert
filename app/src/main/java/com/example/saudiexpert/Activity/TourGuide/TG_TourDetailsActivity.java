package com.example.saudiexpert.Activity.TourGuide;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.Model.TourBook;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTgTourDetailsBinding;
import com.github.chrisbanes.photoview.PhotoView;
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

import es.dmoral.toasty.Toasty;

public class TG_TourDetailsActivity extends AppCompatActivity {

    ActivityTgTourDetailsBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTours = database.getReference("AllTours");
    DatabaseReference referenceAllTourBook = database.getReference("AllTourBook");

    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference storageTourImages = storage.getReference("TourImages");

    Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTgTourDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tour = (Tour) getIntent().getSerializableExtra("TourObject");
        loadingData(tour);

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

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.tourImage.setOnClickListener(v -> {
            showImageLayout();
        });

        binding.updateTour.setOnClickListener(v -> {
            startActivity(new Intent(this, TG_AddTourActivity.class).putExtra("TourObject", tour));
        });

        binding.deleteTour.setOnClickListener(v -> {
            isTourBooked();
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
    public void showDialogDeleteMessageLayout(String Msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.delete_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //

        TextView message = dialogView.findViewById(R.id.inputMessage);
        Button buttonYes = dialogView.findViewById(R.id.buttonYes);
        Button buttonNo = dialogView.findViewById(R.id.buttonNo);

        message.setText(Msg);
        buttonYes.setOnClickListener(v -> {

            storageTourImages
                    .child(tour.getTourID())
                    .child("TourImage.jpg")
                    .delete();
            referenceAllTours
                    .child(tour.getTourID())
                    .removeValue();
            onBackPressed();
            Toasty.success(this, "Successfully Deleted", Toast.LENGTH_SHORT).show();
            alertDialog.dismiss();
        });
        buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    void isTourBooked() {
        referenceAllTourBook
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        boolean isBooked = false;
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TourBook tourBook = snapshot.getValue(TourBook.class);
                            if (tourBook != null) {
                                if (tourBook.getTourID().contains(tour.getTourID()) && !tourBook.getStatus().equals("Pending")) {
                                    isBooked = true;
                                    break;
                                }
                            }
                        }

                        if (isBooked) {
                            Toasty.info(TG_TourDetailsActivity.this, "It cannot be deleted because it is booked", Toast.LENGTH_SHORT).show();
                        } else {
                            showDialogDeleteMessageLayout("Are you sure you want to delete this tour");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }


}