package com.surajvanshsv.fittrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            Log.d("BootReceiver", "Device rebooted, setting daily reset alarm...");

            // Reschedule the midnight alarm to reset steps
            StepResetScheduler.scheduleDailyReset(context);
        }
    }
}
