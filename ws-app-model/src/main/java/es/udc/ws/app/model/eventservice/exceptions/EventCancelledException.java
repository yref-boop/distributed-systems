package es.udc.ws.app.model.eventservice.exceptions;

public class EventCancelledException extends Exception {

    private Long eventId;

    public EventCancelledException (Long eventId) {
        super("Event with id=" + eventId + " is cancelled");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
