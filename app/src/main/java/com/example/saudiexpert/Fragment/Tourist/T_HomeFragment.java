package com.example.saudiexpert.Fragment.Tourist;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.saudiexpert.Activity.Tourist.T_TourDetailsActivity;
import com.example.saudiexpert.Adapter.TourAdapter;
import com.example.saudiexpert.Model.Tour;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.FragmentTHomeBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class T_HomeFragment extends Fragment implements TourAdapter.OnItemClickListener {

    FragmentTHomeBinding homeBinding;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTours = database.getReference("AllTours");

    ArrayList<Tour> tours;
    TourAdapter adapter;

    TextWatcher searchTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(final CharSequence s, int start, int before, int count) {
            loadingAllTour(s.toString().toLowerCase());
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    };


    public T_HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        homeBinding = FragmentTHomeBinding.inflate(inflater, container, false);

        init();
        setOnClickListener();
        loadingAllTour(null);

        return homeBinding.getRoot();
    }


    private void init() {
        tours = new ArrayList<>();
        adapter = new TourAdapter(getContext(), tours, this);
        homeBinding.tourRecyclerView.setAdapter(adapter);

        homeBinding.tourSearch.addTextChangedListener(searchTextWatcher);
    }

    private void setOnClickListener() {
        homeBinding.getRoot().setOnClickListener(v -> {
            // Hide the android keyboard
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(homeBinding.getRoot().getWindowToken(), 0);
        });

    }

    private void loadingAllTour(CharSequence city) {
        homeBinding.progressBar.setVisibility(View.VISIBLE);
        referenceAllTours
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tours.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Tour tour = snapshot.getValue(Tour.class);
                            if (tour != null) {
                                if (city == null) {
                                    tours.add(tour);
                                } else {
                                    if (tour.getTourLocation().toLowerCase().contains(city)) {
                                        tours.add(tour);
                                    }
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                        homeBinding.textEmptyTour.setVisibility(tours.isEmpty() ? View.VISIBLE : View.GONE);
                        homeBinding.tourRecyclerView.setVisibility(tours.isEmpty() ? View.GONE : View.VISIBLE);
                        homeBinding.progressBar.setVisibility(View.GONE);

                        if (city != null && tours.isEmpty()) {
                            // Hide the android keyboard
                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(homeBinding.getRoot().getWindowToken(), 0);
                            showDialogMessageLayout("Sorry this region is not available");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    @SuppressLint("CheckResult")
    public void showDialogMessageLayout(String Msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.message_layout, null);
        dialogBuilder.setView(dialogView);
        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();

        //
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(0));
        //

        TextView message = dialogView.findViewById(R.id.inputMessage);
        Button done = dialogView.findViewById(R.id.buttonDone);

        message.setText(Msg);
        done.setOnClickListener(v -> {
            if (!homeBinding.tourSearch.getText().toString().isEmpty())
                homeBinding.tourSearch.setText("");
            alertDialog.dismiss();
        });
    }


    @Override
    public void onItem_Click(int position) {
        Tour tour = tours.get(position);
         startActivity(new Intent(getActivity(), T_TourDetailsActivity.class).putExtra("TourObject", tour));
    }
}