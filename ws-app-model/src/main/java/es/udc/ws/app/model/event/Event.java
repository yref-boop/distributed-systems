package es.udc.ws.app.model.event;

import java.time.LocalDateTime;
import java.util.Objects;

//Clase Evento
public class Event {

    private long eventId;                       //id del evento
    private String name;                        //nombre del evento
    private String description;                 //descripción del evento
    private LocalDateTime celebrationDate;      //fecha y hora de celebración del evento
    private Long duration;                      //duración del evento
    private LocalDateTime creationDate;         //fecha y hora a la que se dio de alta el evento
    private Long numberAttend;                  //número de personas que aceptaron acudir al evento
    private Long numberNotAttend;               //número de personas que rechazaron acudir al evento
    private Boolean isCancelled;                //boolean que indica si el evento está cancelado o no

    //Constructores de Evento
    public Event(String name, String description, LocalDateTime celebrationDate, Long duration) {
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
    }

    public Event(long eventId, String name, String description, LocalDateTime celebrationDate, Long duration) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
    }

    public Event(long eventId, String name, String description, LocalDateTime celebrationDate, Long duration, LocalDateTime creationDate, Long numberAttend, Long numberNotAttend, Boolean isCancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
        this.numberAttend = numberAttend;
        this.numberNotAttend = numberNotAttend;
        this.isCancelled = isCancelled;
    }

    public Event(String name, String description, LocalDateTime celebrationDate, Long duration, LocalDateTime creationDate, Long numberAttend, Long numberNotAttend, Boolean isCancelled) {
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
        this.numberAttend = numberAttend;
        this.numberNotAttend = numberNotAttend;
        this.isCancelled = isCancelled;
    }

    public Event(long eventId, String name, String description, LocalDateTime celebrationDate, Long duration, Long numberAttend, Long numberNotAttend, Boolean isCancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
        this.duration = duration;
        this.numberAttend = numberAttend;
        this.numberNotAttend = numberNotAttend;
        this.isCancelled = isCancelled;
    }

    //Getters y Setters de Evento
    public long getEventId() {
        return eventId;
    }

    public void setEventId(long eventId) {
        this.eventId = eventId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = (celebrationDate != null) ? celebrationDate.withNano(0) : null;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = (creationDate != null) ? creationDate.withNano(0) : null;
    }

    public Long getNumberAttend() {
        return numberAttend;
    }

    public void setNumberAttend(Long numberAttend) {
        this.numberAttend = numberAttend;
    }

    public Long getNumberNotAttend() {
        return numberNotAttend;
    }

    public void setNumberNotAttend(Long numberNotAttend) {
        this.numberNotAttend = numberNotAttend;
    }

    public Boolean getIsCancelled() {
        return isCancelled;
    }

    public void setIsCancelled(Boolean isCancelled) {
        this.isCancelled = isCancelled;
    }

    //Equals de Evento
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Event event = (Event) o;
        return eventId == event.eventId && name.equals(event.name) && description.equals(event.description) && celebrationDate.equals(event.celebrationDate) && duration.equals(event.duration) && creationDate.equals(event.creationDate) && numberAttend.equals(event.numberAttend) && numberNotAttend.equals(event.numberNotAttend) && isCancelled.equals(event.isCancelled);
    }

    //Hashcode de Evento
    @Override
    public int hashCode() {
        return Objects.hash(eventId, name, description, celebrationDate, duration, creationDate, numberAttend, numberNotAttend, isCancelled);
    }
}
