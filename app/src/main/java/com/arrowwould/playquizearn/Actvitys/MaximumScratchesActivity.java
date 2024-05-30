package com.arrowwould.playquizearn.Actvitys;



import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.arrowwould.playquizearn.MainActivity;
import com.arrowwould.playquizearn.R;

public class MaximumScratchesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maximum_scratches);

        Button tryAgainTomorrowButton = findViewById(R.id.tryAgainTomorrowButton);
        tryAgainTomorrowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Navigate back to the main activity or close the app
                openMainActivity();

            }
        });
    }

    private void openMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
