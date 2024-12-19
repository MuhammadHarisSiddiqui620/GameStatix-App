package com.stat.tools.gadget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.PictureDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.collection.LruCache;

import com.stat.tools.gadget.data.Player;
import com.stat.tools.gadget.singleton.SvgLoader;
import com.google.android.material.button.MaterialButton;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dialog Fragment class for displaying a confirmation dialog before deleting a player.
 */

public class DeleteDialogFragment extends DialogFragment {

    public static final String TAG = "DeleteDialogFragment";
    private final Context context;
    private final String playerName;
    private final String playerImageIcon;

    private ExecutorService executorService;
    private Handler mainHandler;
    private LruCache<String, PictureDrawable> svgCache;
    private OnDialogDismissListener onDialogDismissListener;

    private OnDeleteConfirmedListener onDeleteConfirmedListener;

    /**
     * Constructor for creating a new instance of DeleteDialogFragment.
     *
     * @param player The player to be deleted.
     * @param context The context of the activity.
     */

    public DeleteDialogFragment(Player player, Context context) {
        this.playerName = player.getName();
        this.playerImageIcon = player.getLogo();
        this.context = context;
    }

    /**
     * Set the listener to handle dialog dismiss actions.
     *
     * @param listener The listener to be notified on dialog dismiss.
     */
    public void setOnDialogDismissListener(OnDialogDismissListener listener) {
        this.onDialogDismissListener = listener;
    }

    /**
     * Set the listener to handle delete confirmation actions.
     *
     * @param listener The listener to be notified on delete confirmation.
     */
    public void setOnDeleteConfirmedListener(OnDeleteConfirmedListener listener) {
        this.onDeleteConfirmedListener = listener;
    }

    @Override public void onStart() {
        super.onStart();

        Window window = getDialog().getWindow();
        WindowManager.LayoutParams windowParams = window.getAttributes();
        windowParams.dimAmount = 0.75f; // Set the dim amount to be more intense
        windowParams.flags |= WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(windowParams);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.delete_dialog, null);

        builder.setView(view);

        AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        final int cacheSize = 4 * 1024 * 1024; // 4MiB
        svgCache = new LruCache<>(cacheSize);
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler();

        // Find the views
        ImageView delImgLogo = view.findViewById(R.id.del_img_logo);
        TextView delItemText = view.findViewById(R.id.del_item_text);
        MaterialButton btnCancel = view.findViewById(R.id.item_cancel);
        MaterialButton btnDelete = view.findViewById(R.id.item_del);

        btnCancel.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.base_color));
        btnDelete.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.base_color));


        if (delImgLogo != null) {
            SvgLoader.getInstance().loadSvgInBackground(context, delImgLogo, playerImageIcon);

        }

        if (delItemText != null) {
            delItemText.setText(playerName);
        }

        // Set up button listeners
        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnDelete.setOnClickListener(v -> {
            // Notify the listener that the delete action was confirmed
            if (onDeleteConfirmedListener != null) {
                onDeleteConfirmedListener.onDeleteConfirmed(playerName);
            }
            dialog.dismiss();
        });
        return dialog;
    }

    /**
     * Interface for handling dialog dismiss events.
     */
    public interface OnDialogDismissListener {
        void onDialogDismiss();
    }
    /**
     * Interface for handling delete confirmation events.
     */
    public interface OnDeleteConfirmedListener {
        void onDeleteConfirmed(String playerName);
    }
}
