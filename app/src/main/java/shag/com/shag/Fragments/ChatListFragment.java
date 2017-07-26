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
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import shag.com.shag.Adapters.ChatListAdapter;
import shag.com.shag.Models.Chat;
import shag.com.shag.Models.Event;
import shag.com.shag.Models.User;
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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

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
        //rvChats.addItemDecoration(itemDecoration);
        // instantiate current user id
        currentUserId = ParseUser.getCurrentUser().getObjectId();

        populateChatList(currentUserId);

        return v;
    }

    public void populateChatList(String userId) {

        ParseQuery<ParseObject> query = new ParseQuery<ParseObject>("Event");
        List list = new ArrayList();
        list.add(userId);
        query.whereContainedIn("participants_id", list);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> itemList, ParseException e) {
                if (e == null) {
                    for (ParseObject item : itemList) {
                        // convert each item found to an event
                        final Event event = Event.fromParseObject(item);

                        try {
                            // fetch event owner
                            ParseObject object = item.getParseObject("User_event_owner").fetch();

                            // user found! Convert it to a user model
                            User eventOwner = User.fromParseObject(object);
                            event.setEventOwner(eventOwner); // setting event owner

                            // get chat's info to later populate view
                            Chat eventChat = getChatInfoFromEvent(event);

                            //add event to list to be displayed
                            chats.add(eventChat);
                            adapter.notifyItemInserted(chats.size() - 1);
                            rvChats.smoothScrollToPosition(0);

                        } catch (ParseException e1) {
                            e1.printStackTrace();
                        }

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
        chat.setEvent(event);
        chat.setDescription(event.getDescription());
        chat.setEventId(event.getEventId());
        chat.setChatParticipantsIds(event.getParticipantsIds());
        chat.addChatParticipantsIds("InuSHuTqkn");
        // get icon url
        if (event.getEventOwner() != null) {
            User eventOwner = event.getEventOwner();
            // set chat icon to be the owner's image
            chat.setChatIconUrl(eventOwner.getImageUrl());
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


}
