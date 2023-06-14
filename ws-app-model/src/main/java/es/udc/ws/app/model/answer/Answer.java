package es.udc.ws.app.model.answer;

import java.time.LocalDateTime;
import java.util.Objects;

//Clase Respuesta
public class Answer {

    private Long answerId;              //id de la respuesta
    private Long eventId;               //id del evento
    private String employeeEmail;       //mail del empleado
    private LocalDateTime answerDate;   //fecha de respuesta
    private Boolean attendance;         //boolean que indica si el empleado asistirá o no


    //Constructores
    public Answer(Long eventId, String employeeEmail, Boolean attendance, LocalDateTime answerDate) {
        this.eventId = eventId;
        this.employeeEmail = employeeEmail;
        this.attendance = attendance;
        this.answerDate = (answerDate != null) ? answerDate.withNano(0) : null;
    }

    public Answer(Long answerId, Long eventId, String employeeEmail, LocalDateTime answerDate, Boolean attendance) {
        this.answerId = answerId;
        this.eventId = eventId;
        this.employeeEmail = employeeEmail;
        this.answerDate = (answerDate != null) ? answerDate.withNano(0) : null;
        this.attendance = attendance;
    }

    //Getters y Setters
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

    public LocalDateTime getAnswerDate() {
        return answerDate;
    }

    public void setAnswerDate(LocalDateTime answerDate) {
        this.answerDate = (answerDate != null) ? answerDate.withNano(0) : null;
    }

    public Boolean getAttendance() {
        return attendance;
    }

    public void setAttendance(Boolean attendance) {
        this.attendance = attendance;
    }

    //Equals de Answer
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Answer answer = (Answer) o;
        return answerId.equals(answer.answerId) && eventId.equals(answer.eventId) &&
                employeeEmail.equals(answer.employeeEmail) && answerDate.equals(answer.answerDate)
                && attendance.equals(answer.attendance);
    }

    //HashCode de Answer
    @Override
    public int hashCode() {
        return Objects.hash(answerId, eventId, employeeEmail, answerDate, attendance);
    }
}
