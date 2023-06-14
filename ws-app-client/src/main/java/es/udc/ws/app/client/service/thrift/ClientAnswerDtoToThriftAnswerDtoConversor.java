package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.thrift.ThriftAnswerDto;

import java.util.ArrayList;
import java.util.List;

public class ClientAnswerDtoToThriftAnswerDtoConversor {

    public static ThriftAnswerDto toThriftAnswerDto (ClientAnswerDto clientAnswerDto) {

        return new ThriftAnswerDto(
            clientAnswerDto.getAnswerId(),
            clientAnswerDto.getEventId(),
            clientAnswerDto.getEmployeeEmail(),
            clientAnswerDto.getAttendance());

    }

    public static List<ClientAnswerDto> toClientAnswerDtoList (List<ThriftAnswerDto> answers) {

        List<ClientAnswerDto> clientAnswerDtos = new ArrayList<>(answers.size());

        for (ThriftAnswerDto answer : answers) {
            clientAnswerDtos.add(toClientAnswerDtoList(answer));
        }
        return clientAnswerDtos;
    }

    private static ClientAnswerDto toClientAnswerDtoList(ThriftAnswerDto answer) {

        return new ClientAnswerDto(
                answer.getAnswerId(),
                answer.getEventId(),
                answer.getEmployeeEmail(),
                answer.isAttendance());

    }
}
