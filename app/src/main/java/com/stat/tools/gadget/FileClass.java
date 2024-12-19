package com.stat.tools.gadget;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.stat.tools.gadget.adapter.ItemAdapter;
import com.stat.tools.gadget.data.AppDatabase;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.data.PlayerDao;
import com.stat.tools.gadget.singleton.DatabaseSingleton;
import com.google.android.material.button.MaterialButton;

import java.util.List;


/**
 * Activity class for managing and displaying a list of players.
 * Allows deletion of players and updates the UI accordingly.
 */
public class FileClass extends AppCompatActivity implements ItemAdapter.OnItemDeleteListener, DeleteDialogFragment.OnDialogDismissListener,
        DeleteDialogFragment.OnDeleteConfirmedListener {

    private static PlayerDao playerDao;
    private static RecyclerView recyclerView;
    private static TextView emptyList;
    private static AppDatabase db;
    private ItemAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_class);

        emptyList = findViewById(R.id.noRecord);
        recyclerView = findViewById(R.id.recyclerView);

        // Use the singleton instance of the database
        db = DatabaseSingleton.getInstance(this);
        playerDao = db.playerDao();

        setUpRecyclerView();

        MaterialButton menuButton = findViewById(R.id.materialButton2);
        menuButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, MenuClass.class);
            startActivity(intent);
        });
    }

    /**
     * Set up the RecyclerView with the list of players.
     * If the list is empty, display the "no records" message.
     */
    private void setUpRecyclerView() {
        if (getArrayList().size() > 0) {
            emptyList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);

            // Initialize the RecyclerView
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            adapter = new ItemAdapter(this, playerDao.getAllPlayers(), this);
            recyclerView.setAdapter(adapter);

        } else {
            emptyList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        }
    }

    /**
     * Retrieve the list of players from the database.
     *
     * @return List of Player objects.
     */
    private List<Player> getArrayList() {
        return playerDao.getAllPlayers();
    }

    /**
     * Handle the deletion of an item in the RecyclerView.
     *
     * @param player   The player to delete.
     * @param position The position of the player in the RecyclerView.
     */
    @Override
    public void onItemDelete(Player player, int position) {
        showDeleteDialog(player, position);
    }

    /**
     * Show a dialog to confirm the deletion of a player.
     *
     * @param player   The player to delete.
     * @param position The position of the player in the RecyclerView.
     */
    private void showDeleteDialog(Player player, int position) {
        DeleteDialogFragment dialogFragment = new DeleteDialogFragment(player, this);
        dialogFragment.setOnDialogDismissListener(this);
        dialogFragment.setOnDeleteConfirmedListener(playerName -> {
            // Remove the player from the database and update the RecyclerView
            playerDao.delete(player);
            adapter.removePlayer(position);

            List<Player> updatedList = getArrayList();
            if (updatedList.isEmpty()) {
                emptyList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
            }
        });
        dialogFragment.show(getSupportFragmentManager(), DeleteDialogFragment.TAG);
    }

    /**
     * Handle additional actions needed after deletion is confirmed.
     *
     * @param playerName The name of the deleted player.
     */
    @Override
    public void onDeleteConfirmed(String playerName) {
        // You can handle any additional actions needed after deletion is confirmed
        Toast.makeText(this, playerName + " has been deleted.", Toast.LENGTH_SHORT).show();
    }

    /**
     * Handle actions when the delete dialog is dismissed.
     */
    @Override
    public void onDialogDismiss() {

    }
}
