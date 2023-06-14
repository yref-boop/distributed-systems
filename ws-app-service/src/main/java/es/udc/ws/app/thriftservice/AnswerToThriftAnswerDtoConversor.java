package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.thrift.ThriftAnswerDto;

import java.util.ArrayList;
import java.util.List;

public class AnswerToThriftAnswerDtoConversor {

    public static ThriftAnswerDto toThriftAnswerDto (Answer answer) {

        return new ThriftAnswerDto(answer.getAnswerId(), answer.getEventId(), answer.getEmployeeEmail(), answer.getAttendance());

    }

    public static List<ThriftAnswerDto> toThriftAnswerListDto (List<Answer> answers) {

        List<ThriftAnswerDto> dtos = new ArrayList<>(answers.size());

        for (Answer answer: answers) {
            dtos.add(toThriftAnswerDto(answer));
        }
        return dtos;
    }

}
