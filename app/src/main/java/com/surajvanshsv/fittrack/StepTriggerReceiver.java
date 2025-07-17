package com.surajvanshsv.fittrack;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StepTriggerReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String today = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

        StepRepository repo = new StepRepository((Application) context);
        LiveData<StepEntry> stepLiveData = repo.getStep(today);

        stepLiveData.observeForever(new Observer<StepEntry>() {
            @Override
            public void onChanged(StepEntry stepEntry) {
                stepLiveData.removeObserver(this);

                int steps = (stepEntry != null) ? stepEntry.getSteps() : 0;

                Intent notifyIntent = new Intent(context, StepNotificationReceiver.class);
                notifyIntent.putExtra("steps", steps);
                notifyIntent.putExtra("date", today);
                context.sendBroadcast(notifyIntent);
            }
        });
    }
}
