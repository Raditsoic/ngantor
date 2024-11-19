package com.example.ngantor;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Ngantor extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Apply window insets for padding
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set bold part of the text in TextView
        TextView timeUntilBedTextView = findViewById(R.id.time_until_bed);
        String text = "Time until bedtime: 16 Hours";

        // Create a SpannableString
        SpannableString spannableString = new SpannableString(text);

        // Apply bold style to "16 Hours" - adjust indexes to match actual text
        spannableString.setSpan(
                new StyleSpan(Typeface.BOLD),
                text.indexOf("16"),
                text.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        );

        // Set the styled text to the TextView
        timeUntilBedTextView.setText(spannableString);
    }
}
