package com.example.habithelper.fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.habithelper.LoginActivity;
import com.example.habithelper.MainActivity;
import com.example.habithelper.R;
import com.parse.ParseUser;

public class HomeFragment extends Fragment {

    Button btnLogout;
    TextView tvHello;

    public HomeFragment() {
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
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);
        btnLogout=view.findViewById(R.id.btnLogout);
        tvHello=view.findViewById(R.id.tvHello);

        ParseUser currentUser = ParseUser.getCurrentUser();
        String name = currentUser.getString("name");
        tvHello.setText("Nice to see you again, "+name);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseUser.logOut();
                ParseUser currentUser = ParseUser.getCurrentUser();
                Intent i = new Intent (getContext(), LoginActivity.class);
                startActivity(i);
            }
        });
    }
}