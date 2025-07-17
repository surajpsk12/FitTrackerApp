package com.surajvanshsv.fittrack;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StepResetReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        // Restart the service to reset steps for new day
        context.startService(new Intent(context, StepSensorService.class));
    }
}
