package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.thrift.ThriftEventDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ClientEventDtoToThriftEventDtoConversor {

    public static ThriftEventDto toThriftEventDto(ClientEventDto clientEventDto) {

        Long eventId = clientEventDto.getEventId();

        return new ThriftEventDto(
                eventId == null ? -1 : eventId,
                clientEventDto.getName(), clientEventDto.getDescription(), clientEventDto.getCelebrationDate().toString(),
                Duration.between(clientEventDto.getCelebrationDate(), clientEventDto.getFinishTime()).toHoursPart(),
                clientEventDto.getNumberAttend(), clientEventDto.getTotalAnswers(), clientEventDto.getCancelled());
    }

    public static List<ClientEventDto> toClientEventDtos(List<ThriftEventDto> events) {

        List<ClientEventDto> clientEventDtos = new ArrayList<>(events.size());

        for (ThriftEventDto event : events) {
            clientEventDtos.add(toClientEventDto(event));
        }
        return clientEventDtos;
    }

    public static ClientEventDto toClientEventDto(ThriftEventDto event) {

        LocalDateTime celebrationDate = LocalDateTime.parse(event.getCelebrationDate());

        return new ClientEventDto(
                event.getEventId(),
                event.getName(),
                event.getDescription(),
                celebrationDate,
                celebrationDate.plus(event.getDuration(), ChronoUnit.HOURS),
                event.getNumberAttend(),
                event.getTotalAnswers(),
                event.isIsCancelled()
        );
    }

}
