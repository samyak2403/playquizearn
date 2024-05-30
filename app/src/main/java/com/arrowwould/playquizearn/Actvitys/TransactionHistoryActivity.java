package com.arrowwould.playquizearn.Actvitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;


import com.arrowwould.playquizearn.AdManage.banner.Control;
import com.arrowwould.playquizearn.Adapters.TrHistoryAdapter;
import com.arrowwould.playquizearn.Adapters.TrHistoryModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.ActivityTransactionHistoryBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TransactionHistoryActivity extends AppCompatActivity {

    TrHistoryAdapter adapter;
    ArrayList<TrHistoryModel>list;
    ActivityTransactionHistoryBinding binding;
    FirebaseDatabase database;
    FirebaseUser user;
    FirebaseAuth auth;


    private Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTransactionHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

//        getSupportActionBar().hide();

        //---- banner ads start ----//
        control = new Control(this);
        control.loadBannerAd(R.id.bannerLayout);
        //---- banner ads end ---- //

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        binding.historyRecy.setLayoutManager(layoutManager);



        database.getReference().child("Redeem").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                list = new ArrayList<>();

                if (snapshot.exists()){

                    for (DataSnapshot dataSnapshot:snapshot.getChildren()){
                        TrHistoryModel model= dataSnapshot.getValue(TrHistoryModel.class);
                        list.add(model);
                    }

                    adapter = new TrHistoryAdapter(TransactionHistoryActivity.this,list);
                    binding.historyRecy.setAdapter(adapter);

                }
                else {

                    Toast.makeText(TransactionHistoryActivity.this, "data not exist", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(TransactionHistoryActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });





    }
}