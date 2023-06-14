package es.udc.ws.app.model.eventservice.exceptions;

public class OutOfDateException extends Exception {

    private Long eventId;

    public OutOfDateException(Long eventId) {
        super("Event with id= " + eventId + " does not accept answers anymore");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
