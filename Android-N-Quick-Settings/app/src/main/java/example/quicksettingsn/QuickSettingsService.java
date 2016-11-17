package example.quicksettingsn;

import android.annotation.TargetApi;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.service.quicksettings.Tile;
import android.service.quicksettings.TileService;
import android.util.Log;

/**
 * Created by multidots on 6/30/2016.
 */
@TargetApi(Build.VERSION_CODES.N)
public class QuickSettingsService extends TileService {

    private static final String SERVICE_STATUS_FLAG = "serviceStatus";
    private static final String PREFERENCES_KEY = "com.google.android_quick_settings";

    @Override
    public void onTileAdded() {
        super.onTileAdded();
        Log.d(getClass().getSimpleName(), "Title added");
    }

    @Override
    public void onTileRemoved() {
        super.onTileRemoved();
        Log.d(getClass().getSimpleName(), "Title removed");
    }

    @Override
    public void onClick() {
        super.onClick();
        Log.d(getClass().getSimpleName(), "Title clicked");

        updateTile();
    }

    @Override
    public void onStartListening() {
        super.onStartListening();
        Log.d(getClass().getSimpleName(), "Title click listening started.");
    }

    @Override
    public void onStopListening() {
        super.onStopListening();
        Log.d(getClass().getSimpleName(), "Title click listening stopped.");
    }

    private void updateTile() {
        Tile tile = this.getQsTile();

        boolean isActive = this.getServiceStatus();

        String newLabel;
        Icon newIcon;
        int newState;
        if (isActive) {
            newLabel = "MyQs is active.";
            newIcon = Icon.createWithResource(getApplicationContext(),
                    android.R.drawable.ic_dialog_alert);
            newState = Tile.STATE_ACTIVE;

            //open result activity
            Intent intent = new Intent(getApplicationContext(), ResultActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else {
            newLabel = "MyQs is inactive.";
            newIcon = Icon.createWithResource(getApplicationContext(),
                    R.mipmap.ic_tile_inactive);
            newState = Tile.STATE_INACTIVE;
        }

        tile.setIcon(newIcon);
        tile.setLabel(newLabel);
        tile.setState(newState);

        tile.updateTile();
    }

    // Access storage to see how many times the tile
    // has been tapped.
    private boolean getServiceStatus() {
        SharedPreferences prefs = getApplicationContext().getSharedPreferences(PREFERENCES_KEY, MODE_PRIVATE);

        //read from prefrance, check if tile is activite.
        boolean isActive = prefs.getBoolean(SERVICE_STATUS_FLAG, false);
        isActive = !isActive;

        //update preference
        prefs.edit().putBoolean(SERVICE_STATUS_FLAG, isActive).apply();
        return isActive;
    }
}
