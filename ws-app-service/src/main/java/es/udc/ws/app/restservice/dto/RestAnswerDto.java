package es.udc.ws.app.restservice.dto;

public class RestAnswerDto {

    private Long answerId;
    private Long eventId;
    private String employeeEmail;
    private Boolean attendance;

    public RestAnswerDto(){
    }

    public RestAnswerDto(Long answerId, Long eventId, String employeeEmail, Boolean attendance) {
        this.answerId = answerId;
        this.eventId = eventId;
        this.employeeEmail = employeeEmail;
        this.attendance = attendance;
    }

    public Long getAnswerId() {
        return answerId;
    }

    public void setAnswerId(Long answerId) {
        this.answerId = answerId;
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

    public Boolean getAttendance() {
        return attendance;
    }

    public void setAttendance(Boolean attendance) {
        this.attendance = attendance;
    }
}
