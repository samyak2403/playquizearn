package com.arrowwould.playquizearn.Actvitys;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.anupkumarpanwar.scratchview.ScratchView;
import com.arrowwould.playquizearn.AdManage.banner.Control;
import com.arrowwould.playquizearn.Models.UserModel;
import com.arrowwould.playquizearn.R;
import com.arrowwould.playquizearn.databinding.ActivityScratchCardBinding;
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
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class ScratchCardActivity extends AppCompatActivity {

    private static final int MAX_SCRATCHES_PER_DAY = 10;
    private static final String PREFS_NAME = "ScratchCardPrefs";
    private static final String PREF_SCRATCH_COUNT = "scratchCount";
    private static final String PREF_LAST_SCRATCH_DATE = "lastScratchDate";

    private ActivityScratchCardBinding binding;
    private boolean doubleTab = false;
    private final int duration = 2000;
    private View view;
    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth auth;
    private InterstitialAd mInterstitialAd;
    private int scratchCount;
    private String lastScratchDate;

    private Control control;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityScratchCardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize ads and load the interstitial ad
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {}
        });
        loadInterstitialAd();

        // Load banner ads
        control = new Control(this);
        control.loadBannerAd(R.id.bannerLayout);

        // Initialize Firebase references
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference().child("Users");

        // Load user data
        loadData();

        view = findViewById(android.R.id.content);

        // Load scratch data
        loadScratchData();

        // Set up scratch view listener
        binding.scratchView.setRevealListener(new ScratchView.IRevealListener() {
            @Override
            public void onRevealed(ScratchView scratchView) {
                if (canScratch()) {
                    handleScratchReveal();
                } else {
                    Toast.makeText(ScratchCardActivity.this, "You've reached the maximum scratches for today. Try again tomorrow.", Toast.LENGTH_LONG).show();
                    binding.scratchView.setVisibility(View.GONE);
                    openMaximumScratchesActivity();
                }
            }

            @Override
            public void onRevealPercentChangedListener(ScratchView scratchView, float percent) {
                if (percent >= 0.5) {
                    Log.d("Reveal Percentage", "onRevealPercentChangedListener: " + percent);
                }
            }
        });
    }

    private void loadData() {
        reference.child(Objects.requireNonNull(user).getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserModel model = snapshot.getValue(UserModel.class);
                if (model != null) {
                    binding.totalCoi.setText(String.valueOf(model.getCoins()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ScratchCardActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadInterstitialAd() {
        if (mInterstitialAd != null) {
            return;
        }

        AdRequest adRequest = new AdRequest.Builder().build();
        InterstitialAd.load(this, getString(R.string.admob_interstitial_id), adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                mInterstitialAd = interstitialAd;
                Toast.makeText(ScratchCardActivity.this, "Ads loaded", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                mInterstitialAd = null;
                Toast.makeText(ScratchCardActivity.this, loadAdError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadScratchData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        scratchCount = preferences.getInt(PREF_SCRATCH_COUNT, 0);
        lastScratchDate = preferences.getString(PREF_LAST_SCRATCH_DATE, "");

        String currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        if (!currentDate.equals(lastScratchDate)) {
            scratchCount = 0;
            lastScratchDate = currentDate;
            saveScratchData();
        }

        if (scratchCount >= MAX_SCRATCHES_PER_DAY) {
            binding.scratchView.setVisibility(View.GONE);
            openMaximumScratchesActivity();
        } else {
            binding.scratchView.setVisibility(View.VISIBLE);
        }
    }

    private void saveScratchData() {
        SharedPreferences preferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(PREF_SCRATCH_COUNT, scratchCount);
        editor.putString(PREF_LAST_SCRATCH_DATE, lastScratchDate);
        editor.apply();
    }

    private boolean canScratch() {
        return scratchCount < MAX_SCRATCHES_PER_DAY;
    }

    private void handleScratchReveal() {
        scratchCount++;
        saveScratchData();

        Toast.makeText(getApplicationContext(), "Revealed", Toast.LENGTH_LONG).show();
        int currentCoins = Integer.parseInt(binding.totalCoi.getText().toString());
        int won = Integer.parseInt(binding.textWin.getText().toString());
        int totalCoin = currentCoins + won;

        HashMap<String, Object> map = new HashMap<>();
        map.put("coins", totalCoin);

        reference.child(user.getUid()).updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(ScratchCardActivity.this, "Coins added", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ScratchCardActivity.this, "Coins not added", Toast.LENGTH_SHORT).show();
                }
            }
        });

        if (scratchCount >= MAX_SCRATCHES_PER_DAY) {
            binding.scratchView.setVisibility(View.GONE);
            openMaximumScratchesActivity();
        }
    }

    private void openMaximumScratchesActivity() {
        Intent intent = new Intent(this, MaximumScratchesActivity.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        if (doubleTab) {
            if (mInterstitialAd != null) {
                mInterstitialAd.show(this);
                mInterstitialAd.setFullScreenContentCallback(new FullScreenContentCallback() {
                    @Override
                    public void onAdDismissedFullScreenContent() {
                        mInterstitialAd = null;
                        finish();
                    }
                });
            } else {
                Toast.makeText(ScratchCardActivity.this, "The interstitial ad wasn't ready yet", Toast.LENGTH_SHORT).show();
            }
        } else {
            doubleTab = true;
            Snackbar.make(view, "Press again to exit...", duration).show();

            if (binding.scratchView != null) {
                binding.scratchView.mask(); // Ensure scratchView is initialized before masking
            }

            Random random = new Random();
            int val = random.nextInt(5);
            binding.textWin.setText(String.valueOf(val));

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    doubleTab = false;
                }
            }, duration);
        }
    }
}
