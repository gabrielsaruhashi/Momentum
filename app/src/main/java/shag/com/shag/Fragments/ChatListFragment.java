package shag.com.shag.Fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseLiveQueryClient;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SubscriptionHandling;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import shag.com.shag.Adapters.ChatListAdapter;
import shag.com.shag.Models.Chat;
import shag.com.shag.Models.Event;
import shag.com.shag.Other.DividerItemDecorator;
import shag.com.shag.R;

/**
 * Created by gabesaruhashi on 7/14/17.
 */

public class ChatListFragment extends Fragment {

    // list of tweets
    ArrayList<Chat> chats;
    // recycler view
    RecyclerView rvChats;
    // the adapter wired to the new view
    ChatListAdapter adapter;
    // user id
    String currentUserId;
    ArrayList<String> eventIds;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);
        eventIds = new ArrayList<>();

        // initialize the list of chats
        chats = new ArrayList<>();
        // construct the adater from the data source
        adapter = new ChatListAdapter(chats);
        // initialize recycler view
        rvChats = (RecyclerView) v.findViewById(R.id.rvChats);

        // attach the adapter to the RecyclerView
        rvChats.setAdapter(adapter);
        // Set layout manager to position the items
        rvChats.setLayoutManager(new LinearLayoutManager(getContext()));

        // add line divider decorator
        RecyclerView.ItemDecoration itemDecoration = new
                DividerItemDecorator(rvChats.getContext(), DividerItemDecorator.VERTICAL_LIST);
        rvChats.addItemDecoration(itemDecoration);
        // instantiate current user id
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        populateChatList(currentUserId);
        startLiveQueries();
        return v;
    }

    public void populateChatList(String userId) {

        ParseQuery<Event> query = new ParseQuery("Event");
        List list = new ArrayList();
        list.add(userId);
        query.whereContainedIn("participants_id", list);
        query.include("User_event_owner");
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

                    for (Event event : eventsList) {

                        // get chat's info to later populate view
                        Chat eventChat = getChatInfoFromEvent(event);

                        //add event to list to be displayed
                        chats.add(eventChat);
                        eventIds.add(event.getEventId());
                        adapter.notifyItemInserted(chats.size() - 1);
                        rvChats.smoothScrollToPosition(0);

                    }
                } else { // if there is an error
                    e.printStackTrace();
                }
            }
        });
    }
    // for each event, return the chat information
    public Chat getChatInfoFromEvent(Event event) {

        // create new local chat
        Chat chat = new Chat();
        // set chat info
        chat.setDescription(event.getDescription());
        chat.setEventId(event.getEventId());
        chat.setEvent(event);
        // get participants
        chat.setChatParticipantsIds(event.getParticipantsIds());

        // add shaggy bot
        chat.addChatParticipantsIds((String) getText(R.string.shaggy_bot_id));

        // get icon url
        if (event.getEventOwner() != null) {
            ParseUser eventOwner = event.getEventOwner();
            // set chat icon to be the owner's image
            chat.setChatIconUrl(eventOwner.getString("profile_image_url"));
        }

        // get event info
        int participantsNumber = event.getParticipantsIds().size();
        String eventOwnerName = event.getEventOwnerName();
        String currentUserName = (String) ParseUser.getCurrentUser().get("name");

        // create chat title
        if (Objects.equals(eventOwnerName, currentUserName)) {
            if (participantsNumber > 2) {
                chat.setChatTitle("Me" + " + " + (participantsNumber - 2));
            } else {
                chat.setChatTitle("Me");
            }
        } else { // apply the same title logic for other chat owners
            if (participantsNumber > 2) {
                chat.setChatTitle(eventOwnerName + " + " + (participantsNumber - 2));
            } else {
                chat.setChatTitle(eventOwnerName);
            }
        }

        return chat;
    }

    public void startLiveQueries() {
        ParseLiveQueryClient parseLiveQueryClient = ParseLiveQueryClient.Factory.getClient();
        ParseQuery<Event> parseQuery = ParseQuery.getQuery(Event.class);
        SubscriptionHandling<Event> subscriptionHandling = parseLiveQueryClient.subscribe(parseQuery);
        // Listen for CREATE events
        subscriptionHandling.handleEvent(SubscriptionHandling.Event.UPDATE, new
                SubscriptionHandling.HandleEventCallback<Event>() {
                    @Override
                    public void onEvent(ParseQuery<Event> query, Event object) {
                        String newEventId = object.getEventId();
                        if (eventIds.contains(newEventId)) {
                            ParseQuery<Event> eventQuery = ParseQuery.getQuery(Event.class);
                            eventQuery.include("User_event_owner");
                            eventQuery.include("last_message_sent");
                            try {
                                Event event = eventQuery.get(newEventId);
                                Chat chat = getChatInfoFromEvent(event);
                                int index = eventIds.indexOf(newEventId);
                                chats.remove(index);
                                eventIds.remove(index);
                                chats.add(0, chat);
                                eventIds.add(0, newEventId);
                                //chats.set(index, chat);
                                itemChanged(0);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                        }

                    }

                });

    }

    //execute adapter refresh on ui thread
    public void itemChanged(final int index) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }


}
