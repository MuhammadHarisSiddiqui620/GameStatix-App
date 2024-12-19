package com.stat.tools.gadget;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.stat.tools.gadget.data.AppDatabase;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.data.PlayerDao;
import com.stat.tools.gadget.data.PlayerDto;
import com.stat.tools.gadget.singleton.DatabaseSingleton;
import com.stat.tools.gadget.singleton.SvgLoader;
import com.google.android.material.button.MaterialButton;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;

import java.util.List;

/**
 * Activity class for displaying a list of players in charts diagram.
 */
public class AnalyzeClass extends AppCompatActivity {

    private int playerId;           // Player ID
    private Player player;          // Player object for storing player details
    private AppDatabase db;
    private PlayerDao playerDao;

    PieChart pieChart1, pieChart2, pieChart3, pieChart4;
    LinearLayout ll_piechart1, ll_piechart2, ll_piechart3, ll_piechart4, ll_piechart5, ll_piechart6, ll_piechart7, ll_piechart8;
    int wins, draws, loses, playedGames, scores, points;
    TextView scoreText, pointsText, scorePerpointText, metricPerGameText;

    // Define colors for pieCharts
    int[] colors = new int[]{Color.parseColor("#FF46B5"), Color.parseColor("#0013C0"), Color.parseColor("#46FFD3"), Color.parseColor("#B9FF46"), Color.parseColor("#B9FF46"), Color.parseColor("#8B299B")};
    int leftoverColor = Color.parseColor("#504D4D"); // Dark grey color for leftover

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_anaylze_class);

        if (getIntent() != null && getIntent().hasExtra("analyze_player_id")) {
            playerId = getIntent().getIntExtra("analyze_player_id", -1); // Default value -1 if not found
        }

        db = DatabaseSingleton.getInstance(this);
        // DAO for accessing player data
        playerDao = db.playerDao();

        initializeUIComponents();

        if (player != null) {
            ImageView playerLogo = findViewById(R.id.selected_analyze_item_logo);
            TextView playerName = findViewById(R.id.selected_item_name);

            SvgLoader.getInstance().loadSvgInBackground(this, playerLogo, player.getLogo());
            playerName.setText(player.getName());

            getIndicatorsValues(player.getIndicator());
            setFirstData();
            setSecondData();
            setThirdData();
            setFourthData();
            setSixthData();
            setFifthData();
            setSeventhData();
            setEightData();
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

        player = playerDao.getPlayerById(playerId);
        pieChart1 = findViewById(R.id.piechart01);
        pieChart2 = findViewById(R.id.piechart02);
        pieChart3 = findViewById(R.id.piechart03);
        pieChart4 = findViewById(R.id.piechart04);

        ll_piechart1 = findViewById(R.id.ll_piechart1);
        ll_piechart2 = findViewById(R.id.ll_piechart2);
        ll_piechart3 = findViewById(R.id.ll_piechart3);
        ll_piechart4 = findViewById(R.id.ll_piechart4);
        ll_piechart5 = findViewById(R.id.ll_piechart5);
        ll_piechart6 = findViewById(R.id.ll_piechart6);
        ll_piechart7 = findViewById(R.id.ll_piechart7);
        ll_piechart8 = findViewById(R.id.ll_piechart8);

        scoreText = findViewById(R.id.scoreText);
        pointsText = findViewById(R.id.pointsText);
        scorePerpointText = findViewById(R.id.scorePerpointText);
        metricPerGameText = findViewById(R.id.metricPerGameText);
    }

    /**
     * Set data and configure PieChart 1.
     */
    private void setFirstData() {
        int playedGamesColor = Color.parseColor("#FF4646");
        int winsColor = Color.parseColor("#FF4646");
        int drawsColor = Color.parseColor("#18A75D");
        int losesColor = Color.parseColor("#3CADFF");

        float winPercentage = (float) calculatePercentage(wins, playedGames);
        float drawPercentage = (float) calculatePercentage(draws, playedGames);
        float losePercentage = (float) calculatePercentage(loses, playedGames);
        float totalPercentage = winPercentage + drawPercentage + losePercentage;

        // Check if there is no data to display
        if (wins == 0 && draws == 0 && loses == 0) {
            ll_piechart1.setVisibility(View.GONE); // Hide layout if no data
            return;
        }

        ll_piechart1.setVisibility(View.VISIBLE); // Hide layout if no data

        // Set the data and color to the pie chart
        pieChart1.addPieSlice(new PieModel("", winPercentage, winsColor));
        pieChart1.addPieSlice(new PieModel("", drawPercentage, drawsColor));
        pieChart1.addPieSlice(new PieModel("", losePercentage, losesColor));

        if (totalPercentage < 100) {
            pieChart1.addPieSlice(new PieModel("", 100 - totalPercentage, leftoverColor));
        }

        pieChart1.setInnerPaddingColor(ContextCompat.getColor(this, R.color.base_color));
        pieChart1.startAnimation();
        pieChart1.setDrawValueInPie(false);


        // Add indicators below the pie chart with calculated percentages
        addIndicatorBelowPieChart(ll_piechart1, "Wins", winPercentage, winsColor);
        addIndicatorBelowPieChart(ll_piechart1, "Draws", drawPercentage, drawsColor);
        addIndicatorBelowPieChart(ll_piechart1, "Losses", losePercentage, losesColor);

    }

    /**
     * Set data and configure PieChart 2.
     */
    private void setSecondData() {

        List<PlayerDto> victories = player.getVictories();

        // Calculate total victories and set up pie chart slices
        int totalVictories = 0;
        for (PlayerDto victory : victories) {
            totalVictories += victory.getValue();
        }

        // Check if there are no victories
        if (totalVictories == 0) {
            ll_piechart2.setVisibility(View.GONE); // Hide layout if no victories
            return;
        }


        float totalPercentage = 0;
        for (int i = 0; i < victories.size(); i++) {
            PlayerDto victory = victories.get(i);
            float victoryPercentage = (float) calculatePercentage(victory.getValue(), totalVictories);

            // Use different colors for each victory slice
            int color = colors[i % colors.length];
            pieChart2.addPieSlice(new PieModel("", victoryPercentage, color));

            totalPercentage += victoryPercentage;
        }

        // Add leftover portion if necessary
        if (totalPercentage < 100) {
            pieChart2.addPieSlice(new PieModel("", 100 - totalPercentage, leftoverColor));
        }

        pieChart2.setInnerPaddingColor(ContextCompat.getColor(this, R.color.base_color));
        pieChart2.startAnimation();
        pieChart2.setDrawValueInPie(false);

        // Add indicators below the pie chart with victories data
        for (int i = 0; i < victories.size(); i++) {
            PlayerDto victory = victories.get(i);
            float victoryPercentage = (float) calculatePercentage(victory.getValue(), totalVictories);

            // Use the same color as the pie chart slice for indicators
            int color = colors[i % colors.length];
            addIndicatorBelowPieChart(ll_piechart2, victory.getKey(), victoryPercentage, color);
        }

        /*// Optionally add a summary indicator for the total correlation
        if (playedGames > 0) {
            float correlation = (float) calculatePercentage(totalVictories, playedGames);
            addIndicatorBelowPieChart(ll_piechart2, "Total Correlation", correlation, victoryColors[0]);
        }*/
    }


    /**
     * Set data and configure PieChart 3.
     */
    private void setThirdData() {

        List<PlayerDto> defeats = player.getDefeats();

        // Calculate total defeats and set up pie chart slices
        int totalDefeats = 0;
        for (PlayerDto defeat : defeats) {
            totalDefeats += defeat.getValue();
        }

        // Check if there are no defeats
        if (totalDefeats == 0) {
            ll_piechart3.setVisibility(View.GONE); // Hide layout if no defeats
            return;
        }

        float totalPercentage = 0;
        for (int i = 0; i < defeats.size(); i++) {
            PlayerDto defeat = defeats.get(i);
            float defeatPercentage = (float) calculatePercentage(defeat.getValue(), totalDefeats);

            // Use different colors for each defeat slice
            int color = colors[i % colors.length];
            pieChart3.addPieSlice(new PieModel("", defeat.getValue(), color));

            totalPercentage += defeatPercentage;
        }

        // Add leftover portion if necessary
        if (totalPercentage < 100) {
            pieChart3.addPieSlice(new PieModel("", 100 - totalPercentage, leftoverColor));
        }

        pieChart3.setInnerPaddingColor(ContextCompat.getColor(this, R.color.base_color));
        pieChart3.startAnimation();
        pieChart3.setDrawValueInPie(false);

        // Add indicators below the pie chart with defeats data
        for (int i = 0; i < defeats.size(); i++) {
            PlayerDto defeat = defeats.get(i);
            float defeatPercentage = (float) calculatePercentage(defeat.getValue(), totalDefeats);

            // Use the same color as the pie chart slice for indicators
            int color = colors[i % colors.length];
            addIndicatorBelowPieChart(ll_piechart3, defeat.getKey(), defeatPercentage, color);
        }

/*        // Optionally add a summary indicator for the total correlation
        if (playedGames > 0) {
            float correlation = (float) calculatePercentage(totalDefeats, playedGames);
            addIndicatorBelowPieChart(ll_piechart3, "Total Correlation", correlation, defeatColors[0]);
        }*/
    }

    /**
     * Set data and configure PieChart 4.
     */
    private void setFourthData() {

        List<PlayerDto> otherIndicators = player.getOtherMetrics();

        // Calculate total indicators and set up pie chart slices
        int totalIndicators = 0;
        for (PlayerDto indicators : otherIndicators) {
            totalIndicators += indicators.getValue();
        }

        // Check if there are no defeats
        if (totalIndicators == 0) {
            ll_piechart4.setVisibility(View.GONE); // Hide layout if no defeats
            return;
        }

        float totalPercentage = 0;
        for (int i = 0; i < otherIndicators.size(); i++) {
            PlayerDto indicator = otherIndicators.get(i);
            float defeatPercentage = (float) calculatePercentage(indicator.getValue(), totalIndicators);

            // Use different colors for each defeat slice
            int color = colors[i % colors.length];
            pieChart4.addPieSlice(new PieModel("", indicator.getValue(), color));

            totalPercentage += defeatPercentage;
        }

        // Add leftover portion if necessary
        if (totalPercentage < 100) {
            pieChart4.addPieSlice(new PieModel("", 100 - totalPercentage, leftoverColor));
        }

        pieChart4.setInnerPaddingColor(ContextCompat.getColor(this, R.color.base_color));
        pieChart4.startAnimation();
        pieChart4.setDrawValueInPie(false);

        // Add indicators below the pie chart with defeats data
        for (int i = 0; i < otherIndicators.size(); i++) {
            PlayerDto indicator = otherIndicators.get(i);
            float defeatPercentage = (float) calculatePercentage(indicator.getValue(), totalIndicators);

            // Use the same color as the pie chart slice for indicators
            int color = colors[i % colors.length];
            addIndicatorBelowPieChart(ll_piechart4, indicator.getKey(), defeatPercentage, color);
        }
    }

    /**
     * Set data and configure fourth Card.
     */
    private void setSixthData() {
        if (playedGames > 0 && scores > 0) {
            double averageScoresPerGame = (double) scores / playedGames;
            scoreText.setText(String.format("%.2f", averageScoresPerGame));
            ll_piechart6.setVisibility(View.VISIBLE);
        } else {
            ll_piechart6.setVisibility(View.GONE);
        }
    }


    /**
     * Set data and configure Fifth Card.
     */
    private void setFifthData() {
        if (playedGames > 0 && points > 0) {
            double averagePointsPerGame = (double) points / playedGames;
            pointsText.setText(String.format("%.2f", averagePointsPerGame));
            ll_piechart5.setVisibility(View.VISIBLE);
        } else {
            ll_piechart5.setVisibility(View.GONE);
        }
    }

    /**
     * Set data and configure Seventh Card.
     */
    private void setSeventhData() {

        List<PlayerDto> otherIndicators = player.getOtherMetrics();

        // Calculate total indicators and set up pie chart slices
        int totalIndicators = 0;
        for (PlayerDto indicators : otherIndicators) {
            totalIndicators += indicators.getValue();
        }

        // Check if there are no defeats
        if (totalIndicators == 0) {
            ll_piechart7.setVisibility(View.GONE); // Hide layout if no defeats
            return;
        }

        float totalPercentage = 0;
        for (int i = 0; i < otherIndicators.size(); i++) {
            PlayerDto indicator = otherIndicators.get(i);
            float defeatPercentage = (float) indicator.getValue() /playedGames;
            // Use the same color as the pie chart slice for indicators
            int color = colors[i % colors.length];
            addIndicatorBelowCard(ll_piechart7, indicator.getKey(), defeatPercentage, color);
            totalPercentage += defeatPercentage;
        }

    }

    /**
     * Set data and configure Eight Card.
     */
    private void setEightData() {
        if (points > 0 && scores > 0) {
            double scoresPerPoint = (double) scores / points;
            scorePerpointText.setText(String.format("%.2f", scoresPerPoint));
            ll_piechart8.setVisibility(View.VISIBLE);
        } else {
            ll_piechart8.setVisibility(View.GONE);
        }
    }


    /**
     * Add indicator views below a given PieChart.
     *
     * @param layout         The LinearLayout to add the indicators to.
     * @param indicatorText  The text to display for the indicator.
     * @param indicatorValue The value of the indicator.
     * @param color          The color to set for the indicator item.
     */
    private void addIndicatorBelowPieChart(LinearLayout layout, String indicatorText, float indicatorValue, int color) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View indicatorView = inflater.inflate(R.layout.analyze_player_details, layout, false);

        TextView indicatorTextView = indicatorView.findViewById(R.id.indicator_textview);
        TextView indicatorValueTextView = indicatorView.findViewById(R.id.indicator_value_textview);
        View itemColorView = indicatorView.findViewById(R.id.item_color);

        indicatorTextView.setText(indicatorText);

        if (((int) indicatorValue) == 0)
        {
            indicatorValueTextView.setText(String.format("%.1f%%", indicatorValue));
        }
        else
        {
            indicatorValueTextView.setText(String.format("%d%%", (int) indicatorValue)); // Format as integer percentage
        }
        itemColorView.setBackgroundColor(color); // Set the background color for the indicator item

        layout.addView(indicatorView);
    }


    private void addIndicatorBelowCard(LinearLayout layout, String indicatorText, float indicatorValue, int color) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View indicatorView = inflater.inflate(R.layout.analyze_player_details, layout, false);

        TextView indicatorTextView = indicatorView.findViewById(R.id.indicator_textview);
        TextView indicatorValueTextView = indicatorView.findViewById(R.id.indicator_value_textview);
        View itemColorView = indicatorView.findViewById(R.id.item_color);

        indicatorTextView.setText(indicatorText);
        indicatorValueTextView.setText(String.format("%.2f", indicatorValue)); // Format as integer percentage
        itemColorView.setBackgroundColor(color); // Set the background color for the indicator item

        layout.addView(indicatorView);
    }


    /**
     * Calculate percentage of a part out of total.
     *
     * @param part  The part value.
     * @param total The total value.
     * @return The percentage.
     */
    private double calculatePercentage(int part, int total) {
        return total > 0 ? (part * 100.0 / total) : 0;
    }


    private void getIndicatorsValues(List<PlayerDto> indicator) {
        // Check if there are no defeats
        if (indicator == null || indicator.isEmpty()) {
            return;
        }

        for (PlayerDto indicators : indicator) {

            if (indicators.getKey().contains("Wins")) {
                wins = indicators.getValue();
            }

            if (indicators.getKey().contains("Draws")) {
                draws = indicators.getValue();
            }

            if (indicators.getKey().contains("Loses")) {
                loses = indicators.getValue();
            }

            if (indicators.getKey().contains("Scores")) {
                scores = indicators.getValue();
            }

            if (indicators.getKey().contains("Points")) {
                points = indicators.getValue();
            }

            if (indicators.getKey().contains("PlayedGames")) {
                playedGames = indicators.getValue();
            }
        }

    }
}
