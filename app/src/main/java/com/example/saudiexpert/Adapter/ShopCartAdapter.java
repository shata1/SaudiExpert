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
import com.example.saudiexpert.Activity.Tourist.T_TourGuideInfoActivity;
import com.example.saudiexpert.Model.TourBook;
import com.example.saudiexpert.Model.TourGuide;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.ShopCartItemLayoutBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ShopCartAdapter extends RecyclerView.Adapter<ShopCartAdapter.ViewHolder> {

    Context context;
    ArrayList<TourBook> tourBooks;

    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourGuide = database.getReference("AllTourGuide");
    DatabaseReference referenceAllTourBook = database.getReference("AllTourBook");

    public ShopCartAdapter(Context context, ArrayList<TourBook> tourBooks) {
        this.context = context;
        this.tourBooks = tourBooks;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ShopCartItemLayoutBinding binding = ShopCartItemLayoutBinding.inflate(
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

        getTourGuideDetails(holder, tourBook.getTourGuideKey());

        holder.binding.date.setText(tourBook.getDate());
        holder.binding.time.setText(tourBook.getTime());
        holder.binding.status.setText(tourBook.getStatus());

        if (tourBook.getStatus().equals("Approve")) {
            holder.binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageTrue));
            holder.binding.cancel.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageChecked));
        } else if (tourBook.getStatus().equals("Reject")) {
            holder.binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageFalse));
            holder.binding.cancel.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageChecked));
        } else if (tourBook.getStatus().equals("Pending")) {
            holder.binding.status.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageChecked));
        } else {
            holder.binding.getRoot().setBackgroundColor(context.getResources().getColor(R.color.colorGray));
            holder.binding.cancel.setBackgroundTintList(context.getResources().getColorStateList(R.color.backgroundImageTrue));
            holder.binding.cancel.setImageResource(R.drawable.ic_true);
        }

        holder.binding.cancel.setOnClickListener(v -> {
            if (tourBook.getStatus().equals("Pending")) {
                showDialogApproveOrRejectMessageLayout(tourBook, "Cancel", "Are you sure you want to cancel your appointment ?");
            }
        });

        holder.binding.tourID.setOnClickListener(v -> {
            context.startActivity(new Intent(context, TG_TourInfoActivity.class).putExtra("TourID", tourBook.getTourID()));
        });

        holder.binding.tourGuideName.setOnClickListener(v -> {
            context.startActivity(new Intent(context, T_TourGuideInfoActivity.class).putExtra("TourGuideKey", tourBook.getTourGuideKey()));
        });
    }

    @Override
    public int getItemCount() {
        return tourBooks.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ShopCartItemLayoutBinding binding;

        public ViewHolder(ShopCartItemLayoutBinding binding) {
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

    void getTourGuideDetails(ViewHolder holder, String TourGuideKey) {
        referenceAllTourGuide
                .child(TourGuideKey)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        TourGuide tourGuide = snapshot.getValue(TourGuide.class);
                        if (tourGuide != null) {
                            SpannableString tourGuideName = new SpannableString(String.valueOf(tourGuide.getFirstName()));
                            tourGuideName.setSpan(new UnderlineSpan(), 0, tourGuideName.length(), 0);
                            holder.binding.tourGuideName.setText(tourGuideName);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }
}
