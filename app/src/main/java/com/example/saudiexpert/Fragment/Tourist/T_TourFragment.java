package com.example.saudiexpert.Fragment.Tourist;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.saudiexpert.Adapter.ShopCartAdapter;
import com.example.saudiexpert.Model.TourBook;
import com.example.saudiexpert.databinding.FragmentTTourBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class T_TourFragment extends Fragment {

    FragmentTTourBinding tourBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    FirebaseUser user = auth.getCurrentUser();
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference referenceAllTourBook = database.getReference("AllTourBook");

    ArrayList<TourBook> tourBooks;
    ShopCartAdapter adapter;

    public T_TourFragment() {
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
        tourBinding = FragmentTTourBinding.inflate(inflater, container, false);

        init();
        loadingAllTourBooks();
        return tourBinding.getRoot();
    }

    private void init() {
        tourBooks = new ArrayList<>();
        adapter = new ShopCartAdapter(getContext(), tourBooks);
        tourBinding.tourBooksRecyclerView.setAdapter(adapter);
    }

    private void loadingAllTourBooks() {
        tourBinding.progressBar.setVisibility(View.VISIBLE);
        referenceAllTourBook
                .addValueEventListener(new ValueEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        tourBooks.clear();
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            TourBook tourBook = snapshot.getValue(TourBook.class);
                            if (tourBook != null) {
                                if (tourBook.getTouristKey().equals(user.getUid())) {
                                    tourBooks.add(tourBook);
                                }
                            }
                        }

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            Collections.sort(tourBooks, Comparator.comparing(TourBook::getID));
                            Collections.reverse(tourBooks);
                        }

                        adapter.notifyDataSetChanged();
                        tourBinding.progressBar.setVisibility(View.GONE);
                        if (tourBooks.isEmpty()) {
                            tourBinding.textEmptyBooks.setVisibility(View.VISIBLE);
                            tourBinding.tourBookLayout.setVisibility(View.GONE);
                        } else {
                            tourBinding.textEmptyBooks.setVisibility(View.GONE);
                            tourBinding.tourBookLayout.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        tourBinding.progressBar.setVisibility(View.GONE);
                    }
                });
    }

}