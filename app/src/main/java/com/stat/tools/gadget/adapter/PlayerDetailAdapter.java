package com.stat.tools.gadget.adapter;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.stat.tools.gadget.R;
import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.data.PlayerDto;

import java.util.ArrayList;
import java.util.List;


/**
 * Adapter for displaying and editing player details in a RecyclerView.
 */
public class PlayerDetailAdapter extends RecyclerView.Adapter<PlayerDetailAdapter.ViewHolder> {

    private Context context;
    private List<PlayerDto> playersIndicators;
    private Player item;
    private List<PlayerDto> players;
    private boolean isEditing;


    /**
     * Constructor for PlayerDetailAdapter.
     *
     * @param context           The context for accessing resources.
     * @param playersIndicators List of PlayerDto objects for displaying detailed indicators.
     * @param player            The Player object to get general indicators.
     */
    public PlayerDetailAdapter(Context context, List<PlayerDto> playersIndicators, Player item) {
        this.context = context;
        this.playersIndicators = playersIndicators != null ? playersIndicators : new ArrayList<>();
        this.item = item;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView playerDetailKey;
        public EditText playerDetailValue;
        public TextView playerDetailExistingValue;
        public ImageView icPlus;
        public ImageView underline;

        /**
         * Constructor for ViewHolder.
         *
         * @param itemView The view for an individual item.
         */
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            playerDetailKey = itemView.findViewById(R.id.playerDetailKey);
            playerDetailValue = itemView.findViewById(R.id.playerDetailValue);
            playerDetailExistingValue = itemView.findViewById(R.id.playerDetailExistingValue);
            icPlus = itemView.findViewById(R.id.ic_plus);
            underline = itemView.findViewById(R.id.underline);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.activity_player_detail_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < playersIndicators.size()) {
            bindPlayerIndicator(holder, position);
        }
    }


    /**
     * Binds player indicator data to the ViewHolder.
     *
     * @param holder   The ViewHolder.
     * @param position The position of the item.
     */
    private void bindPlayerIndicator(@NonNull ViewHolder holder, int position) {
        PlayerDto playerDto = playersIndicators.get(position);

        if (playerDto.getKey() != null && !playerDto.getKey().isEmpty()) {
            // Store the initial value
            final int initialValue = playerDto.getValue();

            holder.playerDetailKey.setText(playerDto.getKey());

            // Set the EditText to empty when editing starts
            if (isEditing) {
                holder.playerDetailValue.setText(""); // Set to empty
            } else {
                holder.playerDetailValue.setText(String.valueOf(initialValue));
            }

            configureEditText(holder);
            holder.playerDetailExistingValue.setText(String.valueOf(initialValue));

            // Remove any existing TextWatcher
            TextWatcher existingWatcher = (TextWatcher) holder.playerDetailValue.getTag(R.id.text_watcher_tag);
            if (existingWatcher != null) {
                holder.playerDetailValue.removeTextChangedListener(existingWatcher);
            }

            // Create and add a new TextWatcher
            TextWatcher newWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    // No need to handle value change here
                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (isEditing) {
                        try {
                            // If the field is empty, do not perform any updates
                            if (s.toString().isEmpty()) {
                                return;
                            }

                            int enteredValue = Integer.parseInt(s.toString());
                            int newValue = initialValue + enteredValue;
                            playerDto.setValue(newValue);
                            Log.d("Players000", "playerItem= " + playerDto.getKey());

                        } catch (NumberFormatException e) {
                            holder.playerDetailValue.setError("Invalid number");
                        }
                    }
                }
            };

            holder.playerDetailValue.addTextChangedListener(newWatcher);
            // Store the TextWatcher in the EditText
            holder.playerDetailValue.setTag(R.id.text_watcher_tag, newWatcher);
        }
    }

    /**
     * Configures the EditText and associated icons based on the editing mode.
     *
     * @param holder The ViewHolder containing the views to configure.
     */
    private void configureEditText(@NonNull ViewHolder holder) {
        holder.playerDetailValue.setFocusable(isEditing);
        holder.playerDetailValue.setFocusableInTouchMode(isEditing);
        holder.playerDetailValue.setClickable(isEditing);

        holder.icPlus.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        holder.underline.setVisibility(isEditing ? View.VISIBLE : View.GONE);
        holder.playerDetailExistingValue.setVisibility(isEditing ? View.VISIBLE : View.GONE);

    }

    @Override
    public int getItemCount() {

        return playersIndicators.isEmpty() ? 0 : playersIndicators.size();
    }

    /**
     * Sets the editing mode and updates the RecyclerView.
     *
     * @param isEditing Whether the RecyclerView is in editing mode.
     */
    public void setEditingMode(boolean isEditing) {
        this.isEditing = isEditing;
        notifyDataSetChanged();
    }


    /**
     * Gets the player's ID.
     *
     * @return The ID of the player.
     */
    public int getPlayer() {
        return item.getId();
    }
}
