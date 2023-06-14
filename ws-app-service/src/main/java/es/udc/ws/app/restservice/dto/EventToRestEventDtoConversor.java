package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.event.Event;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EventToRestEventDtoConversor {

    public static List<RestEventDto> toRestEventDtos (List<Event> listEvent) {
        List<RestEventDto> listEventDto = new ArrayList<>(listEvent.size());
        for (Event event : listEvent) {
            listEventDto.add(toRestEventDto(event));
        }
        return listEventDto;
    }

    public static RestEventDto toRestEventDto (Event event){
        return new RestEventDto(event.getEventId(), event.getName(), event.getDescription(),
                event.getCelebrationDate().toString(), event.getDuration(), event.getNumberAttend(),
                event.getNumberAttend() + event.getNumberNotAttend(), event.getIsCancelled());
    }

    public static Event toEvent (RestEventDto event){
        return new Event(event.getEventId(), event.getName(), event.getDescription(),
                LocalDateTime.parse(event.getCelebrationDate()), event.getDuration(),
                event.getNumberAttend(), event.getTotalAnswers() - event.getNumberAttend(),
                event.getCancelled());
    }

}
