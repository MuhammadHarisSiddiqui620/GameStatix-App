package com.stat.tools.gadget.singleton;

import android.content.Context;
import android.graphics.Picture;
import android.graphics.drawable.PictureDrawable;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Singleton class for loading SVG images into an ImageView.
 */
public class SvgLoader {

    private static final String TAG = "SvgLoader";

    private final ExecutorService executorService; // Executor service for background tasks
    private final Handler mainHandler; // Handler for main thread tasks

    /**
     * Private constructor to prevent instantiation.
     * Initializes executor service and main thread handler.
     */
    private SvgLoader() {
        executorService = Executors.newFixedThreadPool(4);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    /**
     * Holder class for lazy initialization of the singleton instance.
     */
    private static class Holder {
        private static final SvgLoader INSTANCE = new SvgLoader();
    }

    /**
     * Provides the singleton instance of SvgLoader.
     *
     * @return The singleton instance of SvgLoader.
     */
    public static SvgLoader getInstance() {
        return Holder.INSTANCE;
    }

    /**
     * Loads an SVG image from assets into an ImageView.
     * Runs the loading operation on a background thread and updates the ImageView on the main thread.
     *
     * @param context   The application context.
     * @param imageView The ImageView where the SVG will be loaded.
     * @param fileName  The name of the SVG file in the assets folder.
     */
    public void loadSvgInBackground(Context context, ImageView imageView, String fileName) {
        executorService.execute(() -> {
            try {
                // Open the InputStream to the SVG file
                InputStream inputStream = context.getAssets().open(fileName);

                // Parse the SVG
                SVG svg = SVG.getFromInputStream(inputStream);

                // Render the SVG to a Picture
                Picture picture = svg.renderToPicture();

                // Convert the Picture to a PictureDrawable
                PictureDrawable pictureDrawable = new PictureDrawable(picture);

                // Post the drawable to the main thread to update the ImageView
                mainHandler.post(() -> {
                    imageView.setLayerType(View.LAYER_TYPE_SOFTWARE, null); // Disable hardware acceleration
                    imageView.setImageDrawable(pictureDrawable);
                });
            } catch (SVGParseException e) {
                Log.e(TAG, "SVG Parsing Error: ", e);
                mainHandler.post(() -> {
                    Toast.makeText(context, "Error parsing SVG: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } catch (IOException e) {
                Log.e(TAG, "IO Error: ", e);
                mainHandler.post(() -> {
                    Toast.makeText(context, "Error loading SVG: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            } catch (Exception e) {
                Log.e(TAG, "Unexpected Error: ", e);
                mainHandler.post(() -> {
                    Toast.makeText(context, "Unexpected error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
            }
        });
    }
}
