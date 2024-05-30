package com.arrowwould.playquizearn.Actvitys;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.arrowwould.playquizearn.AdManage.banner.Control;
import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.ActivityReferBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class ReferActivity extends AppCompatActivity {

    ActivityReferBinding binding;
    FirebaseAuth auth;
    FirebaseUser user;
    private String oppositeUID;
    DatabaseReference reference;
    InterstitialAd mInterstitialAd;

    private Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReferBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        loadInterstialAd();

        loadData();

        redeemAvailability();

        //---- banner ads start ----//
        control = new Control(this);
        control.loadBannerAd(R.id.bannerLayout);
        //---- banner ads end ---- //


        binding.copyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                ClipboardManager clipboardManager = (ClipboardManager) getSystemService(ReferActivity.this.CLIPBOARD_SERVICE);
                ClipData clipData = ClipData.newPlainText("Data",binding.copyCode.getText().toString());
                clipboardManager.setPrimaryClip(clipData);

                Toast.makeText(ReferActivity.this, "Referral Code copied", Toast.LENGTH_SHORT).show();

            }
        });

        binding.shareBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String referCode = binding.referCode.getText().toString();

                String shareBody = "Hey, I am using best earning app. Join using my invite code to instantly get 100"
                        +"coins. My invite code is "+referCode;

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT,shareBody);
                startActivity(intent);

            }
        });

        binding.redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText editText = new EditText(ReferActivity.this);
                editText.setHint("abc123");

                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);

                editText.setLayoutParams(layoutParams);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(ReferActivity.this);
                alertDialog.setTitle("Redeem Code");
                alertDialog.setView(editText);

                alertDialog.setPositiveButton("Redeem", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {

                        String inputCode = editText.getText().toString();

                        if (TextUtils.isEmpty(inputCode)){

                            Toast.makeText(ReferActivity.this, "Input valid code", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (inputCode.equals(binding.referCode.getText().toString())){

                            Toast.makeText(ReferActivity.this, "You can not input your own code", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        redeemQuery(inputCode,dialog);

                    }
                }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        Toast.makeText(ReferActivity.this, "No", Toast.LENGTH_SHORT).show();

                        dialogInterface.dismiss();
                    }
                });
                alertDialog.show();

            }
        });

    }

    private void redeemAvailability() {

        reference.child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        if (snapshot.exists() && snapshot.hasChild("redeemed")){

                            boolean isAvaliable = snapshot.child("redeemed").getValue(Boolean.class);

                            if (isAvaliable){

                                binding.redeemBtn.setVisibility(View.GONE);
                                binding.redeemBtn.setEnabled(false);
                            }
                            else {

                                binding.redeemBtn.setEnabled(true);
                                binding.redeemBtn.setVisibility(View.VISIBLE);
                            }

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ReferActivity.this, "", Toast.LENGTH_SHORT).show();

                    }
                });
    }
    private void redeemQuery(String inputCode, final DialogInterface dialog) {

        Query query = reference.orderByChild("referCode").equalTo(inputCode);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){

                    oppositeUID = dataSnapshot.getKey();

                    reference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            UserModel model = snapshot.child(oppositeUID).getValue(UserModel.class);
                            UserModel mymodel = snapshot.child(user.getUid()).getValue(UserModel.class);

                            int coins = model.getCoins();
                            int updatedCoins = coins + 100;

                            int myCoins = mymodel.getCoins();
                            int myUpdatedCoin = myCoins + 100;

                            HashMap<String,Object> map = new HashMap<>();
                            map.put("coins",updatedCoins);

                            HashMap<String,Object> myMap = new HashMap<>();
                            myMap.put("coins",myUpdatedCoin);
                            myMap.put("redeemed",true);

                            reference.child(oppositeUID).updateChildren(map);
                            reference.child(user.getUid()).updateChildren(myMap)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            dialog.dismiss();

                                            Toast.makeText(ReferActivity.this, "congrates", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                            Toast.makeText(ReferActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(ReferActivity.this, "error in query", Toast.LENGTH_SHORT).show();

            }
        });



    }

    private void loadData() {

        reference.child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        String referCode = snapshot.child("referCode").getValue(String.class);
                        binding.referCode.setText(referCode);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                        Toast.makeText(ReferActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();

                        finish();

                    }
                });

    }

    private void loadInterstialAd() {

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });

        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this,getString(R.string.admob_interstitial_id), adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        Toast.makeText(ReferActivity.this, "ads loaded", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d(TAG, loadAdError.toString());
                        mInterstitialAd = null;

                        Toast.makeText(ReferActivity.this,loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);

            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    mInterstitialAd = null;

                    finish();
                }

            });
            return;


        } else {

            Toast.makeText(ReferActivity.this, "The interstitial ad wasn't ready yet", Toast.LENGTH_SHORT).show();
        }

    }



}