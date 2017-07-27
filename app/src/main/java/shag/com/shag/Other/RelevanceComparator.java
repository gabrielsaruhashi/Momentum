package shag.com.shag.Other;

import java.util.Comparator;

import shag.com.shag.Models.Event;

/**
 * Created by gabesaruhashi on 7/25/17.
 */

public class RelevanceComparator implements Comparator<Event> {

    @Override
    public int compare(Event e1, Event e2) {
        return e1.getRelevance().compareTo(e2.getRelevance());
    }
}
