package es.udc.ws.app.restservice.dto;

import es.udc.ws.app.model.answer.Answer;

import java.util.ArrayList;
import java.util.List;

public class AnswerToRestAnswerDtoConversor {

    public static RestAnswerDto toRestAnswerDto (Answer answer){
        return new RestAnswerDto(answer.getAnswerId(), answer.getEventId(), answer.getEmployeeEmail(), answer.getAttendance());
    }

    public static List<RestAnswerDto> toRestAnswerListDto (List<Answer> answers){
        List<RestAnswerDto> answerDtos = new ArrayList<>(answers.size());
        for (Answer answer : answers) {
            answerDtos.add(toRestAnswerDto(answer));
        }
        return answerDtos;
    }

}
