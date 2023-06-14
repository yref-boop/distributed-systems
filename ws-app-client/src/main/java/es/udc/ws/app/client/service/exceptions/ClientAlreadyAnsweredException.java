package es.udc.ws.app.client.service.exceptions;

public class ClientAlreadyAnsweredException extends Exception {
    private Long eventId;
    private String employeeEmail;

    public ClientAlreadyAnsweredException(Long eventId, String employeeEmail) {
        super("Event with id=\"" + eventId + "\n has been already answered by" + employeeEmail);
        this.eventId = eventId;
        this.employeeEmail = employeeEmail;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public void setEmployeeEmail(String employeeEmail) {
        this.employeeEmail = employeeEmail;
    }
}
