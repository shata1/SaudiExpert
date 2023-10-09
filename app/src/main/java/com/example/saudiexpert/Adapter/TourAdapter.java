package com.example.saudiexpert.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.TourItemLayoutBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TourAdapter extends RecyclerView.Adapter<TourAdapter.ViewHolder> {

    Context context;
    ArrayList<Tour> tours;
    private OnItemClickListener mListener;

    public TourAdapter(Context context, ArrayList<Tour> tours, OnItemClickListener mListener) {
        this.context = context;
        this.tours = tours;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TourItemLayoutBinding binding = TourItemLayoutBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Tour tour = tours.get(position);

        holder.binding.tourTitle.setText(tour.getTourTitle());
        holder.binding.tourDescription.setText(tour.getTourDescription());
        holder.binding.tourLocation.setText("location:" + tour.getTourLocation());

        try {
            Picasso
                    .get()
                    .load(tour.getTourImageUrl() + "")
                    .fit()
                    .placeholder(R.drawable.loading)
                    .into(holder.binding.tourImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return tours.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public interface OnItemClickListener {
        void onItem_Click(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        TourItemLayoutBinding binding;

        public ViewHolder(TourItemLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mListener != null) {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    mListener.onItem_Click(position);
                }
            }
        }
    }
}
