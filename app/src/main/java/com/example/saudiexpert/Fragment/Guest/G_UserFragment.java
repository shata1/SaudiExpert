package com.example.saudiexpert.Fragment.Guest;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.saudiexpert.Activity.RegisterActivity;
import com.example.saudiexpert.databinding.FragmentGUserBinding;

public class G_UserFragment extends Fragment {
    FragmentGUserBinding userBinding;

    public G_UserFragment() {
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
        userBinding = FragmentGUserBinding.inflate(inflater, container, false);
        setOnClickListener();
        return userBinding.getRoot();
    }

    private void setOnClickListener() {
        userBinding
                .buttonCreateAccount
                .setOnClickListener(v -> {
                    startActivity(new Intent(getActivity(), RegisterActivity.class));
                    ActivityCompat.finishAffinity(getActivity());
                });
    }
}