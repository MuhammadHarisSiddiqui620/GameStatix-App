package com.stat.tools.gadget.adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.stat.tools.gadget.PlayerDetails;
import com.stat.tools.gadget.R;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.singleton.SvgLoader;

import java.util.List;

/**
 * Adapter for displaying a list of Player objects in a RecyclerView.
 */
public class ItemAdapter extends RecyclerView.Adapter<ItemAdapter.ViewHolder> {
    private final Context context;     // Context to access resources
    private final List<Player> dataSet;  // List of Player objects
    private View view;

    public interface OnItemDeleteListener {
        void onItemDelete(Player player, int position);
    }

    private final OnItemDeleteListener deleteListener;

    /**
     * Constructor for ItemAdapter.
     *
     * @param context        The context for accessing resources and starting activities.
     * @param dataSet        The list of Player objects to display.
     * @param deleteListener The listener for item delete events.
     */
    public ItemAdapter(Context context, List<Player> dataSet, OnItemDeleteListener deleteListener) {
        this.context = context;
        this.dataSet = dataSet;
        this.deleteListener = deleteListener;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the item layout
        view = LayoutInflater.from(context).inflate(R.layout.player_item, parent, false);

        return new ViewHolder(view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView playerName;     // TextView for displaying player name
        public ImageView playerImageIcon;   // ImageView for displaying player icon
        public ImageView delPlayer;   // ImageView for displaying player icon
        public ConstraintLayout clickItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Initialize view components
            playerName = itemView.findViewById(R.id.item_text);
            playerImageIcon = itemView.findViewById(R.id.img_logo);
            delPlayer = itemView.findViewById(R.id.item_del);
            clickItem = itemView.findViewById(R.id.cl_item);
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // Get element from your dataset at this position
        Player player = dataSet.get(position);
        Log.d("PLayer","PLayer ="+player);

        // Replace the contents of the view with that element
        holder.playerName.setText(player.getName()); // Set user name

        // Get the logo file name from the player object
        String logoFileName = player.getLogo(); // Example: "logo_20.svg"

        // Use SvgLoader singleton to load the SVG into the ImageView
        SvgLoader.getInstance().loadSvgInBackground(context, holder.playerImageIcon, logoFileName);

        // Set up the delete button
        holder.delPlayer.setOnClickListener(v -> {
            if (deleteListener != null) {
                deleteListener.onItemDelete(player, position);
            }
        });


        holder.clickItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, PlayerDetails.class);
            intent.putExtra("player_id", player.getId());  // Pass player ID as extra
            context.startActivity(intent);
        });



    }

    @Override
    public int getItemCount() {
        return dataSet.size(); // Ensure this returns the correct size of the dataset
    }

    /**
     * Remove a player from the data set and notify RecyclerView of the change.
     *
     * @param position The position of the item to be removed.
     */
    public void removePlayer(int position) {
        dataSet.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, dataSet.size());
    }

    /**
     * Remove a player from the data set and notify RecyclerView of the change.
     *
     * @param position The position of the item to be removed.
     */
    public Player getPlayerAtPosition(int position) {
        return dataSet.get(position);
    }

}
