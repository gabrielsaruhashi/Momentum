package shag.com.shag.Models;

import android.database.Cursor;

import java.util.Date;

/**
 * Created by gabesaruhashi on 7/27/17.
 */

public class CalendarEvent {
    private String calendarId;
    private String title;
    private String description;
    private Date dStart;
    private Date dEnd;
    private boolean allDay;
    private String eventLocation;


    // Constructor

    public static CalendarEvent fromCalendarCursor(Cursor cursor) {
        CalendarEvent calendarEvent = new CalendarEvent();
        calendarEvent.setCalendarId(cursor.getString(0));
        calendarEvent.setTitle(cursor.getString(1));
        calendarEvent.setDescription(cursor.getString(2));
        calendarEvent.setdStart(new Date(cursor.getLong(3)));
        calendarEvent.setdEnd(new Date(cursor.getLong(4)));
        calendarEvent.setAllDay(cursor.getInt(5) > 0);
        calendarEvent.setEventLocation(cursor.getString(6));
        return calendarEvent;

    }

    // GETTERS


    public String getCalendarId() {
        return calendarId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Date getdStart() {
        return dStart;
    }

    public Date getdEnd() {
        return dEnd;
    }


    public String getEventLocation() {
        return eventLocation;
    }

    // SETTERS

    public void setCalendarId(String calendarId) {
        this.calendarId = calendarId;
    }

    public void setAllDay(boolean allDay) {
        this.allDay = allDay;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setdStart(Date dStart) {
        this.dStart = dStart;
    }

    public void setdEnd(Date dEnd) {
        this.dEnd = dEnd;
    }


    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }
}
