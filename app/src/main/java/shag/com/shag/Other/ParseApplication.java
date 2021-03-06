package shag.com.shag.Other;

import android.app.Application;
import android.content.Context;
import android.content.Intent;

import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowLog;
import com.raizlabs.android.dbflow.config.FlowManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import shag.com.shag.Activities.LoginActivity;
import shag.com.shag.Clients.FacebookClient;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.Memory;
import shag.com.shag.Models.Message;
import shag.com.shag.Models.Poll;
import shag.com.shag.Models.User;

/**
 * Created by gabesaruhashi on 7/12/17.
 */

/*
 * This is the Android application itself and is used to configure various settings
 * including the image cache in memory and on disk. This also adds a singleton
 * for accessing the relevant rest client.
 *
 *     RestClient client = RestApplication.getRestClient();
 *     // use client to send requests to API
 *
 */
public class ParseApplication extends Application {
    // application context
    private static Context context;
    private static FacebookClient facebookClient;
    private static ArrayList<Long> facebookFriendsIds;
    private static HashMap recentFriendsMap;
    private static boolean mFirstLoad;
    public static boolean mOnboardingLoad;
    private static TreeSet facebookPermissionsSet;
    private static ParseUser currentUser;
    public static ArrayList<Event> usersEventsForChat;

    @Override
    public void onCreate() {
        super.onCreate();

        // instantiate boolean
        mOnboardingLoad = true;

        // Use for troubleshooting -- remove this line for production
        Parse.setLogLevel(Parse.LOG_LEVEL_DEBUG);

        // Use for monitoring Parse OkHttp trafic
        // Can be Level.BASIC, Level.HEADERS, or Level.BODY
        // See http://square.github.io/okhttp/3.x/logging-interceptor/ to see the options.
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        builder.networkInterceptors().add(httpLoggingInterceptor);

        //  register parse classes
        ParseObject.registerSubclass(Event.class);
        ParseObject.registerSubclass(Message.class);
        ParseObject.registerSubclass(Memory.class);
        ParseObject.registerSubclass(Poll.class);

        // set applicationId, and server server based on the values in the Heroku settings.
        // clientKey is not needed unless explicitly configured
        // any network interceptors must be added with the Configuration Builder given this syntax
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("myAppId") // should correspond to APP_ID env variable
                .clientKey(null)  // set explicitly unless clientKey is explicitly configured on Parse server
                .clientBuilder(builder)
                .server("https://shagshag.herokuapp.com/parse/").build());

        // initizlie parse-facebook
        ParseFacebookUtils.initialize(this);

        // idk
        FlowManager.init(new FlowConfig.Builder(this).build());
        FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);

        // set context
        ParseApplication.context = this;

        // registering device for push notifications
        ParseInstallation.getCurrentInstallation().saveInBackground();

        // initialize the array and treeset
        mFirstLoad = true;
        facebookFriendsIds = new ArrayList<Long>();
        facebookPermissionsSet = new TreeSet();
        usersEventsForChat = new ArrayList<>();

    }

    // facebook client singleton
    public static FacebookClient getFacebookRestClient() {
        if (facebookClient == null) {
            facebookClient = new FacebookClient(context);
        }
        return facebookClient;
    }

    public static ArrayList<Long> getFacebookFriends() {

        if (mFirstLoad) {
            // start a new thread to execute the runnable codeblock
            Thread thread = new Thread( ) {
                @Override
                public void run() {

                    // the code to execute when the runnable is processed by a thread
                    FacebookClient client;
                    client = ParseApplication.getFacebookRestClient();

                    client.getFriendsUsingApp(new GraphRequest.Callback() {
                        public void onCompleted(GraphResponse response) {
                            // gets friends ids

                            try {
                                JSONObject obj = response.getJSONObject();
                                //obj should never be null but occassionally is-- need to log in again
                                if (obj == null) {
                                    Intent intent = new Intent(context, LoginActivity.class); //sometimes this doesn't work

                                    context.startActivity(intent);
                                } else {
                                    JSONArray friends = response.getJSONObject().getJSONArray("data");
                                    for (int i = 0; i < friends.length(); i++) {
                                        User friend = User.fromJson(friends.getJSONObject(i));
                                        facebookFriendsIds.add(friend.fbUserID);
                                    }
                                }
                            } catch(JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            };

            // start thread
            thread.start();
            // set first load to false for future getFacebookFriends() call
            mFirstLoad = false;
            // wait for the thread to return the facebook API request
            try {
                thread.join(0);
            } catch (InterruptedException i) {
                i.getMessage();
            }

        }
        // return your fb friends' ids
        return facebookFriendsIds;
    }

    public static HashMap getRecentFriends() {
        // ensure user is authenticated
        if (ParseApplication.getCurrentUser().isAuthenticated()) {
            final ParseUser currentUser = ParseApplication.getCurrentUser();
            // instantiate recent friends with last version of recent friends from the database
            //TODO add ternary operator in case user just returned
            recentFriendsMap = (currentUser.getMap("recent_friends_map") != null) ? (HashMap) currentUser.getMap("recent_friends_map") : new HashMap() ;

            // upon first load, do all the logic to the  updated recent_friends
            if (mFirstLoad) {

                // Find all posts by the current user
                ParseQuery<Memory> query = ParseQuery.getQuery("Memory");

                // create auxiliary list with current user id
                List list = new ArrayList();
                list.add(ParseApplication.getCurrentUser().getObjectId());

                query.whereContainedIn("participants_ids", list);
                query.findInBackground(new FindCallback<Memory>() {
                    @Override
                    public void done(List<Memory> memories, ParseException e) {
                        // check if there are new memories to be added to the map
                        if (memories != null) {
                            /* for each memory, get all the participants ids but that of the current user's
                            and add them to the respective keys in the recent friends hashmap
                             */
                            HashMap updatedRecentFriendsMap = new HashMap<>();

                            for (Memory memory : memories) {
                                ArrayList<String> participantsIds = (ArrayList) memory.getParticipantsIds();

                                for (String participantId : participantsIds) {
                                    if (!participantId.equals(currentUser.getObjectId())) {
                                        // if key already exists, just add to the key counter
                                        if (updatedRecentFriendsMap.containsKey(participantId)) {
                                            int counter = (int) updatedRecentFriendsMap.get(participantId);
                                            updatedRecentFriendsMap.put(participantId, counter + 1);
                                        } else { // else create key
                                            updatedRecentFriendsMap.put(participantId, 1);
                                        }
                                    }
                                }
                            }
                            // update user's recent friends in the database
                            ParseApplication.getCurrentUser().put("recent_friends_map", updatedRecentFriendsMap);
                            ParseApplication.getCurrentUser().saveInBackground();
                            // update local recent friends for future reference
                            recentFriendsMap = updatedRecentFriendsMap;
                        }
                    }

                });
            }
        }

        return recentFriendsMap;
    }

    public static TreeSet getFacebookPermissionsSet() {
        // upon the first load, get all permissions user gave us
        if (mFirstLoad) {
            // get my permissions from facebook
            facebookClient.getMyPermissions(new GraphRequest.Callback() {
                // create a set with the names of all permissions
                @Override
                public void onCompleted(GraphResponse response) {
                    try {
                        JSONArray permissions = response.getJSONObject().getJSONArray("data");
                        for (int i = 0; i < permissions.length(); i ++) {
                            String permissionName = permissions.getJSONObject(i).getString("permission");
                            String status = permissions.getJSONObject(i).getString("status");
                            // add all permissions to global set of permissions
                            if (status.equals("granted")) {
                                facebookPermissionsSet.add(permissionName);
                            }
                        }
                    } catch (JSONException e) {
                        e.getMessage();
                    }
                }
            });
        }

        return facebookPermissionsSet;
    }

    public static ArrayList<Event> getUsersEventsForChat() {
        if (mFirstLoad) {
            // start a new thread to execute the runnable codeblock
            Thread thread = new Thread( ) {
                @Override
                public void run() {
                    // the code to execute when the runnable is processed by a thread
                    ParseQuery<Event> query = new ParseQuery("Event");
                    List list = new ArrayList();
                    list.add(currentUser.getObjectId());
                    query.whereContainedIn("participants_id", list);
                    //query.include("User_event_owner");
                    query.include("last_message_sent");
                    query.findInBackground(new FindCallback<Event>() {
                        @Override
                        public void done(List<Event> eventsList, ParseException e) {
                            if (e == null) {
                                Collections.sort(eventsList, new Comparator<Event>() {
                                    @Override
                                    public int compare(Event event, Event t1) {
                                        if (event.getLastMessageSent() == null && t1.getLastMessageSent() != null) {
                                            return 1;
                                        }

                                        if (event.getLastMessageSent() != null && t1.getLastMessageSent() == null) {
                                            return -1;
                                        }

                                        if (event.getLastMessageSent() == null && t1.getLastMessageSent() == null) {
                                            return 0;
                                        }

                                        ParseObject first = event.getLastMessageSent();
                                        ParseObject second = t1.getLastMessageSent();
                                        return -1 *first.getCreatedAt().compareTo(second.getCreatedAt());
                                    }
                                });

                                usersEventsForChat = (ArrayList) eventsList;

                            } else { // if there is an error
                                e.printStackTrace();
                            }
                        }
                    });

                }
            };

            // start thread
            thread.start();
            /* wait for the thread to return the request
            try {
                thread.join(0);
            } catch (InterruptedException i) {
                i.getMessage();
            } */
        }
        // return your fb friends' ids
        return usersEventsForChat;
    }

    public static ParseUser getCurrentUser() {
        if (currentUser == null) {
            currentUser = ParseUser.getCurrentUser();
        }

        return currentUser;
    }

    public static void setUsersEventsForChat(ArrayList<Event> events) {
        usersEventsForChat = events;
    }
}
