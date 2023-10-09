package com.example.saudiexpert.Activity.Tourist;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.Model.TourBook;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ActivityTBookTourBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Objects;

import es.dmoral.toasty.Toasty;

public class T_BookTourActivity extends AppCompatActivity {

    ActivityTBookTourBinding binding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourBook = database.getReference("AllTourBook");

    Tour tour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTBookTourBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        tour = (Tour) getIntent().getSerializableExtra("TourObject");

        binding.inputStartTime.setText(tour.getTourStarTime());

        setOnClickListener();
    }

    void setOnClickListener() {
        binding.imageBack.setOnClickListener(v -> {
            onBackPressed();
        });

        binding.selectDate.setOnClickListener(v -> {
            selectDate();
        });

        binding.buttonBookTour.setOnClickListener(v -> {
            bookTour(tour);
        });

    }

    private void bookTour(Tour tour) {

        String inputSelectDate = Objects.requireNonNull(binding.inputSelectDate.getText()).toString().trim();
        if (inputSelectDate.isEmpty()) {
            binding.inputSelectDate.setError("");
            Toasty.info(this, "Please Select Date !", Toast.LENGTH_SHORT).show();
            return;
        } else {
            binding.inputSelectDate.setError(null);
        }

        TourBook tourBook = new TourBook();
        tourBook.setID(String.valueOf(System.currentTimeMillis()));
        tourBook.setTourGuideKey(tour.getTourGuideKey());
        tourBook.setTourID(tour.getTourID());
        tourBook.setTouristKey(user.getUid());
        tourBook.setDate(inputSelectDate);
        tourBook.setTime(tour.getTourStarTime());
        tourBook.setStatus("Pending");

        referenceAllTourBook
                .child(tourBook.getID())
                .setValue(tourBook)
                .addOnSuccessListener(unused -> {
                    showDialogMessageLayout("Your reservation has been sent,wait to be confirmed");
                });

    }

    void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                (view, year1, month1, dayOfMonth) -> {

                    String sDate = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
                    binding.inputSelectDate.setText(sDate);
                }, year, month, day
        );
        datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        long DURATION = 1 * 24 * 60 * 60 * 1000;
        datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() + DURATION);
        datePickerDialog.show();
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
            startActivity(new Intent(this, T_HomeActivity.class));
            ActivityCompat.finishAffinity(this);
            alertDialog.dismiss();
        });
    }


}