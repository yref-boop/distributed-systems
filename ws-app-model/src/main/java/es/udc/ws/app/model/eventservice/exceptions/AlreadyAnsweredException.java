package es.udc.ws.app.model.eventservice.exceptions;

public class AlreadyAnsweredException extends Exception {

    private Long eventId;
    private String employeeEmail;

    public AlreadyAnsweredException(Long eventId, String employeeEmail) {
        super("Event with id=" + eventId + " has been already answered by " + employeeEmail);
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
