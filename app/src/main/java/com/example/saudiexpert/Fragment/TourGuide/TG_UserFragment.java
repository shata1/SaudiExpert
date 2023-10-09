package com.example.saudiexpert.Fragment.TourGuide;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import com.example.saudiexpert.Activity.RegisterActivity;
import com.example.saudiexpert.Activity.TourGuide.TG_EditPersonalInfoActivity;
import com.example.saudiexpert.Activity.TourGuide.TG_EditProfileActivity;
import com.example.saudiexpert.R;
import com.example.saudiexpert.databinding.FragmentTgUserBinding;
import com.google.firebase.auth.FirebaseAuth;

public class TG_UserFragment extends Fragment {

    FragmentTgUserBinding userBinding;

    FirebaseAuth auth = FirebaseAuth.getInstance();

    public TG_UserFragment() {
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
        userBinding = FragmentTgUserBinding.inflate(inflater, container, false);
        setListener();
        return userBinding.getRoot();
    }

    void setListener() {
        userBinding.logout.setOnClickListener(v -> {
            showDialogLogoutMessageLayout("Are you sure you want to Logout ?");
        });

        userBinding.buttonEditPersonalInfo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TG_EditPersonalInfoActivity.class));
        });

        userBinding.buttonEditProfile.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), TG_EditProfileActivity.class));
        });
    }

    @SuppressLint("CheckResult")
    public void showDialogLogoutMessageLayout(String Msg) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
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
            auth.signOut();
            setTourGuide(false);
            startActivity(new Intent(getContext(), RegisterActivity.class));
            ActivityCompat.finishAffinity(getActivity());
            alertDialog.dismiss();
        });
        buttonNo.setOnClickListener(v -> {
            alertDialog.dismiss();
        });
    }

    public void setTourGuide(boolean tourGuide) {
        SharedPreferences sp = getActivity().getSharedPreferences("UserType", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean("TourGuide", tourGuide);
        editor.apply();
    }


}