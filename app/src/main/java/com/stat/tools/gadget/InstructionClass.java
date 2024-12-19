package com.stat.tools.gadget;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;


/**
 * Activity class for displaying instructions and navigating to the MenuClass activity.
 */
public class InstructionClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instruction_class);

        // Find the button with ID materialButton3 and assign it to menuButton
        MaterialButton menuButton = findViewById(R.id.materialButton3);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuClass.class);

            // Start the MenuClass activity
            startActivity(intent);
        });
    }
}