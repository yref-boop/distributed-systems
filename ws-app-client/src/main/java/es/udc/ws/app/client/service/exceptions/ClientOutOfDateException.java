package es.udc.ws.app.client.service.exceptions;

public class ClientOutOfDateException extends Exception{
    private Long eventId;

    public ClientOutOfDateException(Long eventId) {
        super("Event with id=\"" + eventId + "\n does not accept answers anymore");
        this.eventId = eventId;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

}
