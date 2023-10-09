package com.example.saudiexpert.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saudiexpert.Activity.TourGuide.TG_TourInfoActivity;
import com.example.saudiexpert.Activity.TourGuide.TG_TouristInfoActivity;
import com.example.saudiexpert.Model.TourBook;
import com.example.saudiexpert.Model.Tourist;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.CartItemLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    Context context;
    ArrayList<TourBook> tourBooks;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourist = database.getReference("AllTourist");
    DatabaseReference referenceAllTourBook = database.getReference("AllTourBook");

    public CartAdapter(Context context, ArrayList<TourBook> tourBooks) {
        this.context = context;
        this.tourBooks = tourBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        CartItemLayoutBinding binding = CartItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @SuppressLint("UseCompatLoadingForColorStateLists")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        TourBook tourBook = tourBooks.get(position);

        SpannableString tourID = new SpannableString(tourBook.getTourID());
        tourID.setSpan(new UnderlineSpan(), 0, tourID.length(), 0);

        holder.binding.tourID.setText(tourID);

        getTouristDetails(holder, tourBook.getTouristKey());

        holder.binding.date.setText(tourBook.getDate());
        holder.binding.time.setText(tourBook.getTime());

        holder.binding.approve.setOnClickListener(v -> {
            if (tourBook.getStatus().equals("Pending"))
                showDialogApproveOrRejectMessageLayout(tourBook, "Approve", "Are You sure you want to (Approve / Reject) this appointment?");
        });

        holder.binding.reject.setOnClickListener(v -> {
            if (tourBook.getStatus().equals("Pending"))
                showDialogApproveOrRejectMessageLayout(tourBook, "Reject", "Are You sure you want to (Approve / Reject) this appointment?");
        });

        if (tourBook.getStatus().equals("Approve")) {
            holder.binding.approve.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageChecked));
            holder.binding.reject.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageFalse));
            holder.binding.getRoot().setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        } else if (tourBook.getStatus().equals("Reject")) {
            holder.binding.approve.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageTrue));
            holder.binding.reject.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageChecked));
            holder.binding.getRoot().setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        } else if (tourBook.getStatus().equals("Pending")) {
            holder.binding.approve.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageTrue));
            holder.binding.reject.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageFalse));
            holder.binding.getRoot().setBackgroundColor(context.getResources().getColor(R.color.colorWhite));
        } else {
            holder.binding.getRoot().setBackgroundColor(context.getResources().getColor(R.color.colorGray));
        }

        holder.binding.tourID.setOnClickListener(v -> {
            context.startActivity(new Intent(context, TG_TourInfoActivity.class).putExtra("TourID", tourBook.getTourID()));
        });

        holder.binding.touristID.setOnClickListener(v -> {
            context.startActivity(new Intent(context, TG_TouristInfoActivity.class).putExtra("TouristKey", tourBook.getTouristKey()));
        });
    }

    @Override
    public int getItemCount() {
        return tourBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CartItemLayoutBinding binding;

        public ViewHolder(CartItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    @SuppressLint("CheckResult")
    public void showDialogApproveOrRejectMessageLayout(TourBook tourBook, String status, String msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
        View dialogView = LayoutInflater.from(context).inflate(
                R.layout.approve_or_reject_message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //

        TextView inputMessage = dialogView.findViewById(R.id.inputMessage);
        inputMessage.setText(msg);

        Button buttonYes = dialogView.findViewById(R.id.buttonYes);
        Button buttonNo = dialogView.findViewById(R.id.buttonNo);

        buttonYes.setOnClickListener(v -> {
            setStatus(tourBook, status);
            alertDialog.dismiss();
        });

        buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    void setStatus(@NonNull TourBook tourBook, String status) {
        referenceAllTourBook
                .child(tourBook.getID())
                .child("status")
                .setValue(status);
    }

    void getTouristDetails(ViewHolder holder, String TouristKey) {

        referenceAllTourist
                .child(TouristKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tourist tourist = snapshot.getValue(Tourist.class);
                        if (tourist != null) {
                            SpannableString touristID = new SpannableString(String.valueOf(tourist.getIdNumber()));
                            touristID.setSpan(new UnderlineSpan(), 0, touristID.length(), 0);
                            holder.binding.touristID.setText(touristID);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
