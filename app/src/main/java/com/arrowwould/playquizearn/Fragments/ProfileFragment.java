package com.arrowwould.playquizearn.Fragments;

import static android.content.ContentValues.TAG;

import static androidx.browser.customtabs.CustomTabsClient.getPackageName;
import android.content.pm.PackageManager;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import com.arrowwould.playquizearn.Actvitys.SignInActivity;
import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.FragmentProfileBinding;
import com.google.android.gms.ads.AdError;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;

import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;


public class ProfileFragment extends Fragment {

    FragmentProfileBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;
    private FirebaseUser user;
    FirebaseStorage storage;
    Uri uri;
    ProgressDialog progressDialog;
    InterstitialAd mInterstitialAd;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);

        storage = FirebaseStorage.getInstance();
        database = FirebaseDatabase.getInstance();
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();


        progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Profile");
        progressDialog.setMessage("We are uploading your profile");

        database.getReference().child("Users").child(user.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {

                    UserModel model = snapshot.getValue(UserModel.class);

                    binding.userNam.setText(model.getName());
                    binding.earnCoin.setText(model.getCoins() + "");

                    Picasso.get()
                            .load(model.getProfile())
                            .placeholder(R.drawable.userprofile)
                            .into(binding.userProfile);

                } else {

                    Toast.makeText(getContext(), "data not exist", Toast.LENGTH_SHORT).show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Toast.makeText(getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        binding.uplImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 8);
            }
        });

//        binding.

        binding.logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                auth.signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);//makesure user cant go back
                startActivity(intent);

            }
        });

        binding.termsAndCondtion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Replace "your_privacy_policy_url" with the actual URL of your privacy policy
                String privacyPolicyUrl = "https://arrow6699.blogspot.com/2024/05/playquizearn-app-terms-conditions.html";

                // Create an intent to open a web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));

                // Start the intent
                startActivity(browserIntent);

            }
        });

        binding.PrivacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Replace "your_privacy_policy_url" with the actual URL of your privacy policy
                String privacyPolicyUrl = "https://arrow6699.blogspot.com/2024/05/playquizearn-app-privacy-policy.html";

                // Create an intent to open a web browser
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));

                // Start the intent
                startActivity(browserIntent);

            }
        });

        binding.RateUsOnPlaystore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatingDialog();

            }
        });



        binding.ContactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String recipientEmail = "arrowwouldstudiofeedbackd@email.com";
                Intent intent = new Intent(Intent.ACTION_SENDTO);
                intent.setData(Uri.parse("mailto:" + recipientEmail));
                startActivity(intent);
            }
        });


        showAds();


        return binding.getRoot();
    }

    private void showRatingDialog() {
        // Handle rating submission, e.g., direct user to Play Store
        String uri = "market://details?id=" + getActivity().getPackageName();
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        startActivity(intent);
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


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == 8) {
            if (data != null) {

                uri = data.getData();
                binding.userProfile.setImageURI(uri);

                progressDialog.show();
                final StorageReference reference = storage.getReference().child("profile").child(user.getUid());

                reference.putFile(uri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                progressDialog.dismiss();
                                reference.getDownloadUrl()
                                        .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {


                                                HashMap<String, Object> map = new HashMap<>();
                                                map.put("profile", uri.toString());

                                                database.getReference().child("Users").child(user.getUid())
                                                        .updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {

                                                                if (task.isSuccessful()) {

                                                                    Toast.makeText(getContext(), "Profile uploaded", Toast.LENGTH_SHORT).show();
                                                                } else {

                                                                    Toast.makeText(getContext(), "error", Toast.LENGTH_SHORT).show();
                                                                }

                                                            }
                                                        });


                                            }
                                        });

                            }
                        });
            }
        }


    }


}