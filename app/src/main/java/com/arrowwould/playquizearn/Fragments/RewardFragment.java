package com.arrowwould.playquizearn.Fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arrowwould.playquizearn.Actvitys.TransactionHistoryActivity;
import com.arrowwould.playquizearn.AdManage.AdsInt.Helper;
import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.arrowwould.playquizearn.databinding.FragmentRewardBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class RewardFragment extends Fragment {

    FragmentRewardBinding binding;
    private Dialog dialog;
    AppCompatButton cancelBtn, redeemBtn;
    ImageView trLogo;
    TextView traMethods;
    EditText edtAmount, edtNumber;
    DatabaseReference reference;
    FirebaseAuth auth;
    FirebaseUser user;
    int currentCoin;

    private Helper helper;
    InterstitialAd mInterstitialAd;

    public RewardFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentRewardBinding.inflate(inflater, container, false);

        dialog = new Dialog(getContext());
        dialog.setContentView(R.layout.esewa_payment_dialog);

        if (dialog.getWindow() != null) {

            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.setCancelable(false);
        }

        cancelBtn = dialog.findViewById(R.id.cancelBtn);
        redeemBtn = dialog.findViewById(R.id.redeemAmountBtn);
        edtAmount = dialog.findViewById(R.id.edtAmount);
        edtNumber = dialog.findViewById(R.id.tranNumber);
        trLogo = dialog.findViewById(R.id.trLogo);
        traMethods = dialog.findViewById(R.id.payMethods);

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference();

        showAds();


        binding.btnHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), TransactionHistoryActivity.class);
                startActivity(intent);

            }
        });

        binding.esewaRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trLogo.setImageResource(R.drawable.payt);
                traMethods.setText("Paytm");
                edtNumber.setHint("Enter your paytm number");

                dialog.show();

            }
        });

        binding.ncellRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trLogo.setImageResource(R.drawable.paypal);
                traMethods.setText("Paypal");
                edtNumber.setHint("Enter your email");
                dialog.show();

            }
        });


        binding.kkhaltiRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trLogo.setImageResource(R.drawable.amazon);
                traMethods.setText("Amazon Gift");
                edtNumber.setHint("Enter your Amazon Gift number");

                dialog.show();

            }
        });

        binding.ntcRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                trLogo.setImageResource(R.drawable.googleplay);
                traMethods.setText("Google pay");
                edtNumber.setHint("Enter your Google pay number");

                dialog.show();

            }
        });


        reference.child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                UserModel model = snapshot.getValue(UserModel.class);

                binding.totalCoinmx.setText(String.valueOf(model.getCoins()));
                binding.coinMinum.setText(String.valueOf(model.getCoins()));
                binding.progressBarUp.setProgress(model.getCoins());

                currentCoin = Integer.parseInt(String.valueOf(model.getCoins()));
                int requiredCoin = 5000 - currentCoin;

                binding.esewaProg.setProgress(model.getCoins());
                binding.khaltiProg.setProgress(model.getCoins());
                binding.ncellProg.setProgress(model.getCoins());
                binding.ntcProg.setProgress(model.getCoins());

                if (currentCoin >= 5000) {

                    //binding.lock.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.unlock));
                    binding.reqEsewa.setText("Completed!");
                    binding.reqKhalti.setText("Completed!");
                    binding.reqNcell.setText("Completed!");
                    binding.reqNtc.setVisibility(View.GONE);
                    binding.needEsewa.setVisibility(View.GONE);
                    binding.needKhalti.setVisibility(View.GONE);
                    binding.needNcell.setVisibility(View.GONE);
                    binding.needNtc.setVisibility(View.GONE);

                } else {

                    // binding.lock.setBackgroundDrawable(ContextCompat.getDrawable(getContext(),R.drawable.lock));
                    binding.reqEsewa.setText(requiredCoin + "");
                    binding.reqKhalti.setText(requiredCoin + "");
                    binding.reqNcell.setText(requiredCoin + "");
                    binding.reqNtc.setText(requiredCoin + "");
                    binding.reqNtc.setVisibility(View.VISIBLE);
                    binding.esewaRedeem.setEnabled(false);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


        binding.esewaRedeem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.show();
            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });

        redeemBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String paymentMethods = traMethods.getText().toString();

                updateRedeem(paymentMethods);


            }
        });


        return binding.getRoot();
    }

    private void updateRedeem(String paymentMethods) {

        String withdCoin = edtAmount.getText().toString();
        String mobNumber = edtNumber.getText().toString();

        Calendar calForDate = Calendar.getInstance();
        SimpleDateFormat currentDate = new SimpleDateFormat("dd-MM-yy");
        String date = currentDate.format(calForDate.getTime());

        HashMap<String, Object> map = new HashMap<>();
        map.put("number", mobNumber);
        map.put("coin", withdCoin);
        map.put("paymentMethode", paymentMethods);
        map.put("status", "false");
        map.put("date", date);

        reference.child("Redeem").child(user.getUid())
                .push()
                .setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        upDateCoin();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void upDateCoin() {

        int withdrawalCoin = Integer.parseInt(edtAmount.getText().toString());
        int updatedCoin = currentCoin - withdrawalCoin;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoin);

        reference.child("Users").child(user.getUid())
                .updateChildren(map)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {

                            dialog.dismiss();
                            Toast.makeText(getContext(), "Congratulation", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                        }

                    }
                });

    }


    private void showAds() {

        MobileAds.initialize(getContext());
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(getContext(), getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);

                mInterstitialAd = interstitialAd;

                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdClicked() {
                        super.onAdClicked();
                    }

                    @Override
                    public void onAdDismissedFullScreenContent() {
                        super.onAdDismissedFullScreenContent();
                    }

                    @Override
                    public void onAdFailedToShowFullScreenContent(@NonNull AdError adError) {
                        super.onAdFailedToShowFullScreenContent(adError);
                    }

                    @Override
                    public void onAdImpression() {
                        super.onAdImpression();
                    }

                    @Override
                    public void onAdShowedFullScreenContent() {
                        super.onAdShowedFullScreenContent();

                        mInterstitialAd = null;
                    }
                });


            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                Toast.makeText(getContext(), loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }

        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (mInterstitialAd != null)

                    mInterstitialAd.show(getActivity());

            }
        }, 2000);

    }

}