package com.stat.tools.gadget;

import android.content.Intent;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.collection.LruCache;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;
import com.stat.tools.gadget.data.AppDatabase;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.data.PlayerDao;
import com.stat.tools.gadget.data.PlayerDto;
import com.stat.tools.gadget.singleton.DatabaseSingleton;
import com.stat.tools.gadget.singleton.SvgLoader;
import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity for managing player statistics, including victories, defeats, and metrics.
 * Handles logo selection and saving player details to the database.
 */
public class NewFile extends AppCompatActivity {

    private static final int LOGO_COUNT = 20; // Number of logos
    private static final String ASSET_PREFIX = "shadow_logo_";
    private static final String REPLACEMENT_PREFIX = "logo_";
    private static final String SVG_EXTENSION = ".svg";
    private ImageView lastClickedImageView = null; // Track the last clicked ImageView
    // Track the file name of the last clicked ImageView

    private String selectedLogoName = ""; // Track the file name of the last clicked ImageView

    private List<View> outputVictoryViews = new ArrayList<>();
    private List<View> outputDefeatViews = new ArrayList<>();
    private List<View> outputMetricsViews = new ArrayList<>();

    private ExecutorService executorService;
    private Handler mainHandler;
    private LruCache<String, PictureDrawable> svgCache;

    private static AppDatabase db;

    private static PlayerDao playerDao;

    private EditText nameEditText; // Define the EditText for player's name

    private CheckBox checkBoxWin, checkBoxDraw, checkBoxLoose, checkBoxScore, checkBoxPoints, checkBoxPlayedGames;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_file);

        // Initialize Checkboxes
        checkBoxWin = findViewById(R.id.checkbox_win);
        checkBoxDraw = findViewById(R.id.checkbox_draw);
        checkBoxLoose = findViewById(R.id.checkbox_loose);
        checkBoxScore = findViewById(R.id.checkbox_score);
        checkBoxPoints = findViewById(R.id.checkbox_points);
        checkBoxPlayedGames = findViewById(R.id.checkbox_played_games);

        executorService = Executors.newFixedThreadPool(4); // Adjust the number of threads as needed
        mainHandler = new Handler(Looper.getMainLooper());
        final int cacheSize = 4 * 1024 * 1024; // 4MiB
        svgCache = new LruCache<String, PictureDrawable>(cacheSize);


        // Use the singleton instance of the database
        db = DatabaseSingleton.getInstance(this);
        playerDao = db.playerDao();

        nameEditText = findViewById(R.id.name_editext); // Initialize the EditText

        addVictories();
        addDefeat();
        addMetrics();

        savePlayer();

        // Initialize and set click listeners for ImageViews
        for (int i = 1; i <= LOGO_COUNT; i++) {
            int imageViewId = getResources().getIdentifier("img_logo_" + i, "id", getPackageName());
            ImageView imageView = findViewById(imageViewId);
            imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Handle hardware acceleration issues

            // Load shadow logos
            String shadowSvgFileName = ASSET_PREFIX + i + SVG_EXTENSION;

            SvgLoader.getInstance().loadSvgInBackground(this, imageView, shadowSvgFileName);

            final int index = i;
            imageView.setOnClickListener(v -> {
                if (lastClickedImageView != null && lastClickedImageView != imageView) {
                    // Revert the previously clicked ImageView to shadow_logo
                    String revertFileName = ASSET_PREFIX + getIndexFromImageView(lastClickedImageView) + SVG_EXTENSION;
                    replaceSvgOnClick(lastClickedImageView, revertFileName);
                }

                if (imageView == lastClickedImageView) {
                    // If the same image is clicked again, do nothing
                    return;
                }

                // Update the new ImageView with the logo SVG
                String replacementFileName = REPLACEMENT_PREFIX + index + SVG_EXTENSION;
                replaceSvgOnClick(imageView, replacementFileName);

                // Update the last clicked ImageView references
                lastClickedImageView = imageView;

                // Update the selectedLogoName to reflect the new selection
                selectedLogoName = replacementFileName;
            });
        }

        MaterialButton menuButton = findViewById(R.id.materialButton);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuClass.class);
            startActivity(intent);
        });
    }

    /**
     * Get the index of the clicked ImageView based on its ID.
     *
     * @param imageView The ImageView whose index is to be found.
     * @return The index of the ImageView, or -1 if not found.
     */
    private int getIndexFromImageView(ImageView imageView) {
        for (int i = 1; i <= LOGO_COUNT; i++) {
            int imageViewId = getResources().getIdentifier("img_logo_" + i, "id", getPackageName());
            if (imageView.getId() == imageViewId) {
                return i;
            }
        }
        return -1; // Return -1 if not found
    }


    /**
     * Replace the SVG image in the given ImageView with a new SVG file.
     *
     * @param imageView The ImageView to update.
     * @param fileName  The SVG file to load and display.
     */
    private void replaceSvgOnClick(ImageView imageView, String fileName) {
        // Load the replacement SVG in background
        executorService.execute(() -> {
            try {
                InputStream inputStream = getAssets().open(fileName);
                SVG svg = SVG.getFromInputStream(inputStream);
                Picture picture = svg.renderToPicture();
                PictureDrawable pictureDrawable = new PictureDrawable(picture);

                // Cache the replacement SVG
                svgCache.put(fileName, pictureDrawable);

                // Post to the main thread to update the ImageView
                mainHandler.post(() -> imageView.setImageDrawable(pictureDrawable));
            } catch (IOException e) {
                mainHandler.post(() -> Toast.makeText(this, "Error loading " + fileName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            } catch (SVGParseException e) {
                mainHandler.post(() -> Toast.makeText(this, "Error parsing " + fileName + ": " + e.getMessage(), Toast.LENGTH_SHORT).show());
                e.printStackTrace();
            }
        });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (executorService != null) {
            executorService.shutdown(); // Clean up the executor service
        }
    }

    /**
     * Add an EditText and a plus button to the victories section.
     * When the plus button is clicked, the text in the EditText is added to the list of victories.
     */
    private void addVictories() {
        // Find the parent LinearLayout where new views will be added
        LinearLayout layoutVictory = findViewById(R.id.ll_victories_entry);

        // Inflate the input layout containing the EditText
        View childVictory = getLayoutInflater().inflate(R.layout.input_edittext, null);

        // Add the child view (EditText) to the layout
        layoutVictory.addView(childVictory);

        // Find the EditText within the child layout
        EditText inputVictory = childVictory.findViewById(R.id.victories_edittext);
        ImageView plusVictoryButton = childVictory.findViewById(R.id.ic_plus);

        // Set an OnClickListener to the plus icon
        plusVictoryButton.setOnClickListener(v -> {
            // Retrieve the text from the EditText after user input
            String victories = inputVictory.getText().toString().trim();

            // Check if the victories string is not empty
            if (!victories.isEmpty()) {
                // Inflate a new view from the output layout
                View output = getLayoutInflater().inflate(R.layout.show_output_textview, null);

                // Find the TextView and minus button inside the newly inflated output layout
                TextView outputView = output.findViewById(R.id.victories_textview);
                ImageView minusVictoryButton = output.findViewById(R.id.ic_minus);

                // Set the text in the TextView
                outputView.setText(victories);

                // Add the output view (containing the TextView) to the layout
                layoutVictory.addView(output);

                // Add the output view to the list
                outputVictoryViews.add(output);

                // Clear the EditText after adding the text
                inputVictory.setText("");

                // Set a click listener for the minus button to remove the current output view
                minusVictoryButton.setOnClickListener(t -> {
                    removeView(layoutVictory, output, outputVictoryViews);
                });
            } else {
                // Handle the case when input is empty
                Toast.makeText(this, "Please enter a value before adding.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Add an EditText and a plus button to the Defeat section.
     * When the plus button is clicked, the text in the EditText is added to the list of Defeats.
     */
    private void addDefeat() {
        // Find the parent LinearLayout where new views will be added
        LinearLayout layoutDefeat = findViewById(R.id.ll_defeat_entry);

        // Inflate the input layout containing the EditText
        View childDefeat = getLayoutInflater().inflate(R.layout.input_edittext, null);

        // Add the child view (EditText) to the layout
        layoutDefeat.addView(childDefeat);

        // Find the EditText within the child layout
        EditText inputDefeat = childDefeat.findViewById(R.id.victories_edittext);
        ImageView plusDefeatButton = childDefeat.findViewById(R.id.ic_plus);

        // Set an OnClickListener to the plus icon
        plusDefeatButton.setOnClickListener(v -> {
            // Retrieve the text from the EditText after user input
            String defeats = inputDefeat.getText().toString().trim();

            // Check if the defeats string is not empty
            if (!defeats.isEmpty()) {
                // Inflate a new view from the output layout
                View output = getLayoutInflater().inflate(R.layout.show_output_textview, null);

                // Find the TextView and minus button inside the newly inflated output layout
                TextView outputView = output.findViewById(R.id.victories_textview);
                ImageView minusDefeatButton = output.findViewById(R.id.ic_minus);

                // Set the text in the TextView
                outputView.setText(defeats);

                // Add the output view (containing the TextView) to the layout
                layoutDefeat.addView(output);

                // Add the output view to the list
                outputDefeatViews.add(output);

                // Clear the EditText after adding the text
                inputDefeat.setText("");

                // Set a click listener for the minus button to remove the current output view
                minusDefeatButton.setOnClickListener(t -> {
                    removeView(layoutDefeat, output, outputDefeatViews);
                });
            } else {
                // Handle the case when input is empty
                Toast.makeText(this, "Please enter a value before adding.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    /**
     * Add an EditText and a plus button to the Metrics section.
     * When the plus button is clicked, the text in the EditText is added to the list of Metrics.
     */
    private void addMetrics() {
        // Find the parent LinearLayout where new views will be added
        LinearLayout layoutMetrics = findViewById(R.id.ll_metrics_entry);

        // Inflate the input layout containing the EditText
        View childMetrics = getLayoutInflater().inflate(R.layout.input_edittext, null);

        // Add the child view (EditText) to the layout
        layoutMetrics.addView(childMetrics);

        // Find the EditText within the child layout
        EditText inputMetrics = childMetrics.findViewById(R.id.victories_edittext);
        ImageView plusMetricButton = childMetrics.findViewById(R.id.ic_plus);

        // Set an OnClickListener to the plus icon
        plusMetricButton.setOnClickListener(v -> {
            // Retrieve the text from the EditText after user input
            String metrics = inputMetrics.getText().toString().trim();

            // Check if the metrics string is not empty
            if (!metrics.isEmpty()) {
                // Inflate a new view from the output layout
                View output = getLayoutInflater().inflate(R.layout.show_output_textview, null);

                // Find the TextView and minus button inside the newly inflated output layout
                TextView outputView = output.findViewById(R.id.victories_textview);
                ImageView minusButtonMetrics = output.findViewById(R.id.ic_minus);

                // Set the text in the TextView
                outputView.setText(metrics);

                // Add the output view (containing the TextView) to the layout
                layoutMetrics.addView(output);

                // Add the output view to the list
                outputMetricsViews.add(output);

                // Clear the EditText after adding the text
                inputMetrics.setText("");

                // Set a click listener for the minus button to remove the current output view
                minusButtonMetrics.setOnClickListener(t -> {
                    removeView(layoutMetrics, output, outputMetricsViews);
                });
            } else {
                // Handle the case when input is empty
                Toast.makeText(this, "Please enter a value before adding.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /**
     * Remove the specified view from the given layout and the associated list.
     *
     * @param layout   The layout from which the view should be removed.
     * @param view     The view to remove.
     * @param viewList The list that holds the views.
     */
    private void removeView(LinearLayout layout, View output, List<View> outputViews) {
        // Remove the view from the parent layout
        layout.removeView(output);

        // Remove the view from the list
        outputViews.remove(output);
    }


    /**
     * Save the player's details including name, selected logo, and various metrics.
     * Checks for input validation and inserts the player data into the database.
     */
    private void savePlayer() {
        ImageButton saveButton = findViewById(R.id.ic_save);

        saveButton.setOnClickListener(v -> {
            // Collecting input data
            String name = nameEditText.getText().toString().trim(); // Retrieve player's name
            List<PlayerDto> victories = collectInputDataAsPlayerDto(outputVictoryViews); // Convert to List<PlayerDto>
            List<PlayerDto> defeats = collectInputDataAsPlayerDto(outputDefeatViews);    // Convert to List<PlayerDto>
            List<PlayerDto> metrics = collectInputDataAsPlayerDto(outputMetricsViews);   // Convert to List<PlayerDto]
            List<PlayerDto> indicator = new ArrayList<>();

            // Check checkbox states
            boolean winSelected = checkBoxWin.isChecked();
            boolean drawSelected = checkBoxDraw.isChecked();
            boolean looseSelected = checkBoxLoose.isChecked();
            boolean scoreSelected = checkBoxScore.isChecked();
            boolean pointsSelected = checkBoxPoints.isChecked();
            boolean playedGames = checkBoxPlayedGames.isChecked();

            // Add selected indicators to the list
            if (winSelected) {
                indicator.add(new PlayerDto("Wins", 0));
            }
            if (drawSelected) {
                indicator.add(new PlayerDto("Draws", 0));
            }
            if (looseSelected) {
                indicator.add(new PlayerDto("Loses", 0));
            }
            if (scoreSelected) {
                indicator.add(new PlayerDto("Scores", 0));
            }
            if (pointsSelected) {
                indicator.add(new PlayerDto("Points", 0));
            }
            if (playedGames) {
                indicator.add(new PlayerDto("PlayedGames", 0));
            }
            // Validation of input data
            if (name.isEmpty() || selectedLogoName.isEmpty()) {
                Toast.makeText(this, "Please enter all fields before saving.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Creating a Player object with checkbox states
            Player player = new Player(
                    name, // Pass the player's name as the first argument
                    selectedLogoName,
                    victories,  // Pass List<PlayerDto> for victories
                    defeats,    // Pass List<PlayerDto> for defeats
                    metrics,    // Pass List<PlayerDto> for metrics
                    indicator
            );

            // Inserting the Player object into the database
            playerDao.insert(player);

            // Set all checkboxes to false
            for (CheckBox checkBox : Arrays.asList(checkBoxWin, checkBoxDraw, checkBoxLoose, checkBoxScore, checkBoxPoints)) {
                checkBox.setChecked(false);
            }

            // Notify the user of successful save
            Toast.makeText(this, "Player saved successfully!", Toast.LENGTH_SHORT).show();
            Log.d("Player", "Player= " + player);


            // Clear the EditText fields
            nameEditText.setText("");

            // Revert shadow logos to their original state
            revertShadowLogos();

            // Remove all views from the layouts and clear the lists
            clearLayoutAndList(R.id.ll_victories_entry, outputVictoryViews);
            clearLayoutAndList(R.id.ll_defeat_entry, outputDefeatViews);
            clearLayoutAndList(R.id.ll_metrics_entry, outputMetricsViews);


            Intent intent = new Intent(this, MenuClass.class);
            startActivity(intent);
        });
    }

    /**
     * Helper method to convert List<View> to List<PlayerDto>.
     *
     * @param outputViews The list of views to convert.
     * @return A list of PlayerDto objects.
     */
    private List<PlayerDto> collectInputDataAsPlayerDto(List<View> views) {
        List<PlayerDto> dataList = new ArrayList<>();
        for (View view : views) {
            TextView textView = view.findViewById(R.id.victories_textview);
            if (textView != null) {
                String key = textView.getText().toString().trim();
                // Use a default value of 0 for `value`
                dataList.add(new PlayerDto(key, 0));
            }
        }
        return dataList;
    }

    // Method to clear layout and list
    private void clearLayoutAndList(int layoutId, List<View> viewList) {
        LinearLayout layout = findViewById(layoutId);
        for (View view : viewList) {
            layout.removeView(view);
        }
        viewList.clear();
    }

    // Method to revert shadow logos
    private void revertShadowLogos() {
        for (int i = 1; i <= LOGO_COUNT; i++) {
            int imageViewId = getResources().getIdentifier("img_logo_" + i, "id", getPackageName());
            ImageView imageView = findViewById(imageViewId);

            if (imageView != null) {
                String shadowSvgFileName = ASSET_PREFIX + i + SVG_EXTENSION;
                replaceSvgOnClick(imageView, shadowSvgFileName);
            }
        }
    }

}