package es.udc.ws.app.client.service.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ClientEventDto {

    private Long eventId;
    private String name;
    private String description;
    private LocalDateTime celebrationDate;
    private LocalDateTime finishTime;
    private Long numberAttend;
    private Long totalAnswers;
    private Boolean isCancelled;

    public ClientEventDto() {
    }

    public ClientEventDto(Long eventId, String name, String description, LocalDateTime celebrationDate, LocalDateTime finishTime, Long numberAttend, Long totalAnswers, Boolean isCancelled) {
        this.eventId = eventId;
        this.name = name;
        this.description = description;
        this.celebrationDate = celebrationDate;
        this.finishTime = finishTime;
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

    public LocalDateTime getCelebrationDate() {
        return celebrationDate;
    }

    public void setCelebrationDate(LocalDateTime celebrationDate) {
        this.celebrationDate = celebrationDate;
    }

    public LocalDateTime getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(LocalDateTime finishTime) {
        this.finishTime = finishTime;
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
