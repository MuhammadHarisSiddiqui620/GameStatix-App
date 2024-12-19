package com.stat.tools.gadget;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.stat.tools.gadget.adapter.PlayerDetailAdapter;
import com.stat.tools.gadget.data.AppDatabase;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.data.PlayerDao;
import com.stat.tools.gadget.singleton.DatabaseSingleton;
import com.stat.tools.gadget.singleton.SvgLoader;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * Activity to display and edit details of a specific player.
 */
public class PlayerDetails extends AppCompatActivity {

    private int playerId;           // Player ID
    private TextView playerName;    // TextView for displaying player's name
    private ImageView playerLogo;   // ImageView for displaying player's logo
    private MaterialButton editButton;
    private MaterialButton analyzeButton;
    private boolean isEditing = false; // Flag to check if editing is enabled

    private Player player;          // Player object for storing player details

    private PlayerDao playerDao;    // DAO for accessing player data

    private PlayerDetailAdapter generalAdapter;
    private PlayerDetailAdapter victoriesAdapter;  // Adapter for victories
    private PlayerDetailAdapter defeatsAdapter;    // Adapter for defeats
    private PlayerDetailAdapter otherMetricsAdapter; // Adapter for other metrics
    private PlayerDetailAdapter adapter;

    private ArrayList<HashMap<String, Integer>> generalIndicators = new ArrayList<HashMap<String, Integer>>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_details);

        initializeUIComponents();    // Initialize UI components
        initializeDatabase();        // Initialize database and DAO

        retrievePlayerIdFromIntent(); // Retrieve Player ID from Intent

        if (playerId != -1) {
            loadPlayerDetails();     // Fetch and display player details
            setupRecyclerViews();    // Initialize RecyclerViews and Adapters
        } else {
            showPlayerNotFoundError(); // Show error if player not found
        }

        MaterialButton menuButton = findViewById(R.id.materialButton3);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuClass.class);
            startActivity(intent);
        });
    }

    /**
     * Initializes UI components.
     */
    private void initializeUIComponents() {
        playerName = findViewById(R.id.selected_item_name);
        playerLogo = findViewById(R.id.selected_item_logo);

        editButton = findViewById(R.id.btn_edit);
        analyzeButton = findViewById(R.id.btn_analyze);

        // Set up edit button click listener
        editButton.setOnClickListener(v -> {
            if (isEditing) {
                // Save changes when exiting edit mode
                toggleEditMode(true); // Save changes
            } else {
                // Enter edit mode
                toggleEditMode(false); // Don't save changes yet

            }
        });
        // Set up analyze button click listener
        analyzeButton.setOnClickListener(v -> {
            if (isEditing) {
                toggleEditMode(false); // Do not save changes

                // Cancel editing and revert changes
               loadPlayerDetails();
               setupRecyclerViews();
                // Exit edit mode
            } else {
                int currentPlayerId = generalAdapter.getPlayer();
                Intent intent = new Intent(PlayerDetails.this, AnalyzeClass.class);
                intent.putExtra("analyze_player_id", currentPlayerId);  // Pass player ID as extra
                startActivity(intent);
            }
        });

    }

    /**
     * Initializes the database and DAO.
     */
    private void initializeDatabase() {
        // Singleton database instance
        AppDatabase db = DatabaseSingleton.getInstance(this);
        playerDao = db.playerDao();
    }

    /**
     * Retrieves the Player ID from the Intent.
     */
    private void retrievePlayerIdFromIntent() {
        if (getIntent() != null && getIntent().hasExtra("player_id")) {
            playerId = getIntent().getIntExtra("player_id", -1); // Default value -1 if not found
        }
    }

    /**
     * Loads player details from the database and updates the UI.
     */
    private void loadPlayerDetails() {
        // Fetch player details from the database
        player = playerDao.getPlayerById(playerId);
        if (player != null) {

            playerName.setText(player.getName());
            // Use SvgLoader singleton to load the SVG into the ImageView
            SvgLoader.getInstance().loadSvgInBackground(this, playerLogo, player.getLogo());
        } else {
            showPlayerNotFoundError(); // Show error if player not found in the database
        }
    }

    /**
     * Shows a toast message if the player is not found.
     */
    private void showPlayerNotFoundError() {
        Toast.makeText(this, "Player not found!", Toast.LENGTH_SHORT).show();
    }


    /**
     * Toggles between edit mode and view mode.
     *
     * @param saveChanges Whether to save changes when exiting edit mode.
     */
    private void toggleEditMode(boolean saveChanges) {
        isEditing = !isEditing; // Toggle editing mode

        // Update the button text based on the editing state
        editButton.setText(isEditing ? "Save" : "Edit");
        analyzeButton.setText(isEditing ? "Cancel" : "Analyze");

        // Notify adapters to switch between editable and non-editable mode
        victoriesAdapter.setEditingMode(isEditing);
        defeatsAdapter.setEditingMode(isEditing);
        otherMetricsAdapter.setEditingMode(isEditing);
        generalAdapter.setEditingMode(isEditing);


        if (!isEditing && saveChanges) {
            saveChangesToDatabase(player); // Save changes when exiting edit mode
        }
    }


    /**
     * Saves changes to the database.
     *
     * @param player The Player object with updated details.
     */
    private void saveChangesToDatabase(Player player) {
        if (player != null) {
            playerDao.update(player);
            Toast.makeText(this, "Changes saved successfully!", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Sets up RecyclerViews and their adapters.
     */
    private void setupRecyclerViews() {
        // Cache the views
        ConstraintLayout victoriesLayout = findViewById(R.id.cl_victories);
        RecyclerView victoriesRecyclerView = findViewById(R.id.rl_victories);
        victoriesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        victoriesAdapter = new PlayerDetailAdapter(this, player.getVictories(), player);
        victoriesRecyclerView.setAdapter(victoriesAdapter);

        ConstraintLayout defeatLayout = findViewById(R.id.cl_defeat);
        RecyclerView defeatsRecyclerView = findViewById(R.id.rl_defeat);
        defeatsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        defeatsAdapter = new PlayerDetailAdapter(this, player.getDefeats(), player);
        defeatsRecyclerView.setAdapter(defeatsAdapter);

        ConstraintLayout otherMetricsLayout = findViewById(R.id.cl_indicators);
        RecyclerView otherMetricsRecyclerView = findViewById(R.id.rl_otherIndicators);
        otherMetricsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        otherMetricsAdapter = new PlayerDetailAdapter(this, player.getOtherMetrics(), player);
        otherMetricsRecyclerView.setAdapter(otherMetricsAdapter);


        ConstraintLayout generalLayout = findViewById(R.id.cl_general);
        RecyclerView generalsRecyclerView = findViewById(R.id.rl_general);
        generalsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        generalAdapter = new PlayerDetailAdapter(this, player.getIndicator(), player);
        generalsRecyclerView.setAdapter(generalAdapter);

        // Setup victories RecyclerView
        if (player.getVictories() != null && !player.getVictories().isEmpty()) {
            victoriesLayout.setVisibility(View.VISIBLE);
        } else {
            victoriesLayout.setVisibility(View.GONE);
        }

        // Setup defeats RecyclerView
        if (player.getDefeats() != null && !player.getDefeats().isEmpty()) {
            defeatLayout.setVisibility(View.VISIBLE);
        } else {
            defeatLayout.setVisibility(View.GONE);
        }

        // Setup other metrics RecyclerView
        if (player.getOtherMetrics() != null && !player.getOtherMetrics().isEmpty()) {
            otherMetricsLayout.setVisibility(View.VISIBLE);
        } else {
            otherMetricsLayout.setVisibility(View.GONE);
        }

        // Setup general indicators RecyclerView
        if (player.getIndicator() != null && !player.getIndicator().isEmpty()) {
            generalLayout.setVisibility(View.VISIBLE);
        } else {
            generalLayout.setVisibility(View.GONE);
        }
    }
}
