package com.memoria.kiosk;

import android.app.ActivityManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.memoria.kiosk.monitor.SensorMonitor;
import com.memoria.kiosk.offline.OfflineTherapyRepository;

public class KioskActivity extends AppCompatActivity {

    private SensorMonitor sensorMonitor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kiosk);

        TextView status = findViewById(R.id.statusText);
        TextView offline = findViewById(R.id.offlineText);

        sensorMonitor = new SensorMonitor(this, summary ->
                status.setText("Monitoring quietly: " + summary));

        OfflineTherapyRepository repository = new OfflineTherapyRepository(this);
        String fallback = repository.getFirstTherapy();
        offline.setText(fallback == null ? "Offline therapy unavailable" : "Offline: " + fallback);

        enterLockTaskIfAllowed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        sensorMonitor.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorMonitor.stop();
    }

    private void enterLockTaskIfAllowed() {
        ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        if (am != null && am.getLockTaskModeState() == ActivityManager.LOCK_TASK_MODE_NONE) {
            try {
                startLockTask();
            } catch (IllegalArgumentException ignored) {
                // Device owner provisioning is required for strict kiosk lock in production.
            }
        }
    }
}
