package shag.com.shag.Other;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import shag.com.shag.Models.Event;
import shag.com.shag.Models.Memory;

/**
 * Created by hanapearlman on 7/18/17.
 */

public class MyAlarm extends BroadcastReceiver {
    private final String REMINDER_BUNDLE = "MyReminderBundle";

    // this constructor is called by the alarm manager.
    public MyAlarm(){ }

    // you can use this constructor to create the alarm.
    //  Just pass in the main activity as the context,
    //  any extras you'd like to get later when triggered
    //  and the timeout
    public MyAlarm(Context context, Bundle extras, long longStartTime){
        AlarmManager alarmMgr = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, MyAlarm.class);
        intent.putExtra(REMINDER_BUNDLE, extras);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        Calendar time = Calendar.getInstance();
        time.setTimeInMillis(longStartTime);
        time.add(Calendar.SECOND, 20); //TODO hard code reminder for 24 hours later
        alarmMgr.set(AlarmManager.RTC_WAKEUP, time.getTimeInMillis(),
                pendingIntent);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        final Bundle extras = intent.getBundleExtra(REMINDER_BUNDLE);

        //send push notification to all users who are a part of the event
        HashMap<String, String> payload = new HashMap<>();
        payload.put("customData", "Checkout your new shared album!");
        payload.put("eventId", extras.getString("eventId"));
        ParseCloud.callFunctionInBackground("pushMemoryNotification", payload);

        //query to find the users in the event, create a memory with these users as participants
        //eventName will likely be null until the call to new MyAlarm is moved
        ParseQuery<Event> parseQuery = ParseQuery.getQuery("Event");
        parseQuery.getInBackground(extras.getString("eventId"), new GetCallback<Event>() {
            @Override
            public void done(Event event, ParseException e) {
                if (e == null) {
                    // TODO only save if more than two participants (owner and Shaggy)
                    ArrayList<String> participantIds = event.getParticipantsIds();
                    new Memory(extras.getString("eventDescription"), participantIds, extras.getString("eventId"));
                } else {
                    Log.e("MyAlarm error", "Error Loading Messages" + e);
                }
            }
        });

        //TODO: delete event from database?
    }
}