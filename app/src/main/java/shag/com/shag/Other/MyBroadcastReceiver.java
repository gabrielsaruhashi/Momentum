package shag.com.shag.Other;

import android.content.Context;
import android.content.Intent;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

import shag.com.shag.Activities.LoginActivity;
import shag.com.shag.Models.Event;

/**
 * Created by hanapearlman on 7/19/17.
 */

public class MyBroadcastReceiver extends com.parse.ParsePushBroadcastReceiver {
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        JSONObject pushData;
        String eventId = "";
        try {
            pushData = new JSONObject(intent.getStringExtra(KEY_PUSH_DATA));
            eventId = pushData.getString("event_id");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Intent pushIntent = new Intent(context, LoginActivity.class);
        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        try {
            ParseObject object = query.get(eventId);
            Event e = Event.fromParseObject(object);
            pushIntent.putExtra("event_id", e.eventId);
            pushIntent.putStringArrayListExtra("participants_ids", e.participantsIds);
            context.startActivity(pushIntent);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
}
