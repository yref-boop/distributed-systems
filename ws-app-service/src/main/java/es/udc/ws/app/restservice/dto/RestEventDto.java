package es.udc.ws.app.restservice.dto;

public class RestEventDto {

    private Long eventId;
    private String name;
    private String description;
    private String celebrationDate;
    private Long duration;
    private Long numberAttend;
    private Long totalAnswers;
    private Boolean isCancelled;

    public RestEventDto(){
    }

    public RestEventDto(Long eventId, String name, String description, String celebrationDate, Long duration, Long numberAttend, Long totalAnswers, Boolean isCancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.duration = duration;
        this.numberAttend = numberAttend;
        this.totalAnswers = totalAnswers;
        this.isCancelled = isCancelled;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
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

    public String getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(String celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public Long getDuration() {
        return duration;
    }

    public void setDuration(Long duration) {
        this.duration = duration;
    }

    public Long getNumberAttend() {
        return numberAttend;
    }

    public void setNumberAttend(Long numberAttend) {
        this.numberAttend = numberAttend;
    }

    public Long getTotalAnswers() {
        return totalAnswers;
    }

    public void setTotalAnswers(Long totalAnswers) {
        this.totalAnswers = totalAnswers;
    }

    public Boolean getCancelled() {
        return isCancelled;
    }

    public void setCancelled(Boolean cancelled) {
        isCancelled = cancelled;
    }
}
