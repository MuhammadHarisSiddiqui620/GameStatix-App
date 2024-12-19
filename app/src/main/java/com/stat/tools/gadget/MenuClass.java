package com.stat.tools.gadget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Owner: Muhammad Haris Siddiqui
 * Linkedin: https://www.linkedin.com/in/muhammad-haris-siddiqui-549b55186/
 * Upwork: https://www.upwork.com/freelancers/~0112a3b9bbcff6e434?mp_source=share
 */

/**
 * MenuClass is the main activity for the menu screen.
 * It sets up click listeners for various buttons to navigate to different activities.
 */
public class MenuClass extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);


        // Set a click listener for the instruction button
        findViewById(R.id.fl_instruction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Create an intent to start the InstructionClass activity
                Intent myIntent = new Intent(MenuClass.this, InstructionClass.class);
                // Start the InstructionClass activity
                MenuClass.this.startActivity(myIntent);
            }
        });

        // Set a click listener for the new file button
        findViewById(R.id.fl_new_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create an intent to start the NewFile activity
                Intent myIntent = new Intent(MenuClass.this, NewFile.class);
                // Start the NewFile activity
                MenuClass.this.startActivity(myIntent);
            }
        });

        findViewById(R.id.fl_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myIntent = new Intent(MenuClass.this, FileClass.class);
                MenuClass.this.startActivity(myIntent);
            }
        });

    }
}