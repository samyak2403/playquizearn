package com.arrowwould.playquizearn.Actvitys;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.ActivityWatchVideoBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.OnUserEarnedRewardListener;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.ads.rewarded.RewardItem;
import com.google.android.gms.ads.rewarded.RewardedAd;
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kaopiz.kprogresshud.KProgressHUD;

import java.util.HashMap;

public class WatchVideoActivity extends AppCompatActivity {

    private static final String TAG = "WatchVideoActivity";
    private ActivityWatchVideoBinding binding;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private RewardedAd mRewardedAd;
    private InterstitialAd mInterstitialAd;
    private KProgressHUD progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWatchVideoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        if (user != null) {
            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid());
        } else {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setStatusBarColorAndBrightness();
        initializeProgressDialog();
        loadInterstitialAd();
        loadData();

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(@NonNull InitializationStatus initializationStatus) {
                Toast.makeText(WatchVideoActivity.this, "Initialization completed", Toast.LENGTH_SHORT).show();
            }
        });

        binding.watchVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAds();
            }
        });
    }

    private void setStatusBarColorAndBrightness() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.your_status_bar_color));

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
        }
    }

    private void initializeProgressDialog() {
        progress = KProgressHUD.create(this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setLabel("Loading")
                .setCancellable(false)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);
    }

    private void loadData() {
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = snapshot.getValue(UserModel.class);
                if (model != null) {
                    binding.coinTv.setText(String.valueOf(model.getCoins()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(WatchVideoActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showAds() {
        if (mRewardedAd == null) {
            progress.show();
            AdRequest adRequest = new AdRequest.Builder().build();
            RewardedAd.load(this, "ca-app-pub-9845749769832734/4605007451", adRequest, new RewardedAdLoadCallback() {
                @Override
                public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                    progress.dismiss();
                    Log.d(TAG, loadAdError.toString());
                    Toast.makeText(WatchVideoActivity.this, "Failed to load ad", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onAdLoaded(@NonNull RewardedAd rewardedAd) {
                    progress.dismiss();
                    mRewardedAd = rewardedAd;
                    binding.watchVideo.setEnabled(true);
                    showRewardedAd();
                }
            });
        } else {
            showRewardedAd();
        }
    }

    private void showRewardedAd() {
        if (mRewardedAd != null) {
            mRewardedAd.show(this, new OnUserEarnedRewardListener() {
                @Override
                public void onUserEarnedReward(@NonNull RewardItem rewardItem) {
                    updateDataFirebase();
                }
            });
        } else {
            Log.d(TAG, "The rewarded ad wasn't ready yet.");
            Toast.makeText(this, "Ad wasn't ready", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateDataFirebase() {
        int currentCoins = Integer.parseInt(binding.coinTv.getText().toString());
        int updatedCoins = currentCoins + 5;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", updatedCoins);

        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(WatchVideoActivity.this, "Coins added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(WatchVideoActivity.this, "Coins not added", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void loadInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Toast.makeText(WatchVideoActivity.this, "Interstitial ad loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                Log.d(TAG, loadAdError.toString());
                Toast.makeText(WatchVideoActivity.this, "Failed to load interstitial ad", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (mInterstitialAd != null) {
            mInterstitialAd.show(this);
            mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                @Override
                public void onAdDismissedFullScreenContent() {
                    finish();
                }
            });
        } else {
            Toast.makeText(this, "The interstitial ad wasn't ready yet", Toast.LENGTH_SHORT).show();
            super.onBackPressed();
        }
    }
}
