package es.udc.ws.app.client.service.exceptions;

public class ClientEventCancelledException extends Exception{

    private Long eventId;

    public ClientEventCancelledException (Long eventId) {
        super("Event with id=\"" + eventId + "\n is cancelled");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }
}
