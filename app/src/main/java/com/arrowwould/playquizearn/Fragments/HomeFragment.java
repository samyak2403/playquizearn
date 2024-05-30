package com.arrowwould.playquizearn.Fragments;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.arrowwould.playquizearn.Actvitys.ReferActivity;
import com.arrowwould.playquizearn.Actvitys.ScratchCardActivity;
import com.arrowwould.playquizearn.Actvitys.WatchVideoActivity;
import com.arrowwould.playquizearn.AdManage.AdsInt.Helper;
import com.arrowwould.playquizearn.Models.CategoryModel;
import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.FragmentHomeBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


import static android.content.ContentValues.TAG;


import androidx.annotation.NonNull;


public class HomeFragment extends Fragment {

    FragmentHomeBinding binding;
    ArrayList<CategoryModel> list = new ArrayList<>();
    ;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private FirebaseUser user;

    AdView adView;
    InterstitialAd mInterstitialAd;
    private Dialog dialog;

    private Helper helper;

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
        binding = FragmentHomeBinding.inflate(inflater, container, false);

        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        MobileAds.initialize(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();
        binding.adView.loadAd(adRequest);


        loadInterstialAd();

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.loading_dialog);
        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }


        binding.inviteFd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

//                helper.showInterstitialAd();
//                showIntertialAds(1);
                Intent intent = new Intent(getContext(), ReferActivity.class);
                startActivity(intent);

            }
        });


//        binding.spinWheel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                // showIntertialAds(2);
//                Intent intent = new Intent(getContext(), SpinnerActivity.class);
//                startActivity(intent);
//
//            }
//        });

        binding.watchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), WatchVideoActivity.class);
                startActivity(intent);

            }
        });

        binding.scratchCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), ScratchCardActivity.class);
                startActivity(intent);

            }
        });


        dialog.show();

        database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel model = snapshot.getValue(UserModel.class);

                if (snapshot.exists()) {

                    binding.userName.setText(model.getName());
                    binding.mobileNumber.setText(model.getNumber() + "");
                    binding.coins.setText(model.getCoins() + "");

                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.userprofile)
                            .into(binding.profileImage);

                } else {

                    Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
                }


                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

                dialog.dismiss();
            }
        });


//        binding.dailyReward.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                final SweetAlertDialog pDialog = new SweetAlertDialog(getContext(), SweetAlertDialog.PROGRESS_TYPE);
//                pDialog.getProgressHelper().setBarColor(Color.parseColor("#A5DC86"));
//                pDialog.setTitleText("Loading");
//                pDialog.setCancelable(false);
//                pDialog.show();
//
//                final Date currentDate = Calendar.getInstance().getTime();
//                final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
//
//                // final String date = dateFormat.format(currentDate);
//
//                database.getReference().child("Daily Check").child(user.getUid())
//                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                            @Override
//                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                if (snapshot.exists()){
//
//                                    String dbDateString = snapshot.child("date").getValue(String.class);
//
//                                    try {
//
//                                        assert dbDateString !=null;
//                                        Date dbDate = dateFormat.parse(dbDateString);
//
//                                        String xDate = dateFormat.format(currentDate);
//                                        Date date = dateFormat.parse(xDate);
//
//                                        if (date.after(dbDate) && date.compareTo(dbDate) !=0){
//
//                                            database.getReference().child("Users").child(user.getUid())
//                                                    .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                        @Override
//                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
//
//                                                            UserModel model = snapshot.getValue(UserModel.class);
//
//                                                            int currentCoins = (int) model.getCoins();
//                                                            int update = currentCoins + 10;
//
//                                                            int spinC = model.getSpins();
//                                                            int updatedSpins = spinC + 2;
//
//                                                            HashMap<String,Object> map = new HashMap<>();
//                                                            map.put("coins",update);
//                                                            map.put("spins",updatedSpins);
//
//                                                            database.getReference().child("Users").child(user.getUid())
//                                                                    .updateChildren(map);
//
//                                                            Date newDate = Calendar.getInstance().getTime();
//                                                            String newDateString = dateFormat.format(newDate);
//
//                                                            HashMap<String,Object> dateMap = new HashMap<>();
//                                                            dateMap.put("date",newDateString);
//
//                                                            database.getReference().child("Daily Check")
//                                                                    .child(FirebaseAuth.getInstance().getUid()).setValue(dateMap)
//                                                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                                        @Override
//                                                                        public void onComplete(@NonNull Task<Void> task) {
//
//                                                                            pDialog.changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
//                                                                            pDialog.setTitleText("Success");
//                                                                            pDialog.setContentText("coins added");
//                                                                            pDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
//                                                                                @Override
//                                                                                public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                                                                                    pDialog.dismissWithAnimation();
//                                                                                }
//                                                                            }).show();
//
//                                                                        }
//                                                                    });
//
//                                                        }
//
//                                                        @Override
//                                                        public void onCancelled(@NonNull DatabaseError error) {
//
//                                                            Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//                                                        }
//                                                    });
//
//                                        }
//                                        else {
//
//                                            pDialog.changeAlertType(SweetAlertDialog.ERROR_TYPE);
//                                            pDialog.setTitleText("Failed");
//                                            pDialog.setContentText("You have already rewarded, Come back tomorrow ");
//                                            pDialog.setConfirmButton("Dismiss",null);
//                                            pDialog.show();
//
//                                        }
//
//
//                                    }
//                                    catch (Exception e){
//
//                                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                        pDialog.dismissWithAnimation();
//                                    }
//
//
//                                }
//                                else {
//
//                                    Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
//
//                                    pDialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
//                                    pDialog.setTitleText("System Busy");
//                                    pDialog.setContentText("System is busy, please try later");
//                                    pDialog.setConfirmButton("Dismiss", new SweetAlertDialog.OnSweetClickListener() {
//                                        @Override
//                                        public void onClick(SweetAlertDialog sweetAlertDialog) {
//
//                                            pDialog.dismissWithAnimation();
//                                        }
//                                    });
//
//                                    pDialog.dismiss();
//
//                                }
//
//                            }
//
//                            @Override
//                            public void onCancelled(@NonNull DatabaseError error) {
//
//                                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
//
//                                pDialog.dismissWithAnimation();
//                            }
//                        });
//
//
//            }
//
//
//        });


        return binding.getRoot();
    }




    private void loadInterstialAd() {

        MobileAds.initialize(getContext(), new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        Toast.makeText(getContext(), "ads loaded", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;

                        Toast.makeText(getContext(), loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });


    }


}