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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // inflate the layout
        View v = inflater.inflate(R.layout.fragment_chat_list, container, false);

        // initialize the list of tweets
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
                        Event event = Event.fromParseObject(item);
                        Chat eventChat = event.getEventChat();
                        // TODO delete this after we clear the database. rn some events dont have chats
                        if (eventChat == null) {
                            eventChat = new Chat();
                        }

                        // get chat's info to later populate view
                        getChatInfoFromEvent(event, eventChat);

                        //add event to list to be displayed
                        chats.add(eventChat);
                        adapter.notifyItemInserted(chats.size() - 1);
                        rvChats.smoothScrollToPosition(0);

                    }
                } else {
                    e.printStackTrace();
                }
            }
        });
    }
    // for each event, return the chat information
    public Chat getChatInfoFromEvent(Event event, Chat chat) {
        chat.setDescription(event.getDescription());
        chat.setChatTitle(event.getEventOwnerName() + (event.getParticipantsIds().size()));

        return chat;
    }
}
