package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestAnswerDto;

import java.util.List;

public class JSonToRestAnswerDtoConversor {

    public static ObjectNode toObjectNode (RestAnswerDto answerDto){

        ObjectNode answerNode = JsonNodeFactory.instance.objectNode();

        if(answerDto.getAnswerId() != null){
            answerNode.put("answerId", answerDto.getAnswerId());
        }

        answerNode.put("eventId", answerDto.getEventId()).put("employeeEmail", answerDto.getEmployeeEmail()).
                put("attendance", answerDto.getAttendance());

        return  answerNode;
    }

    public static ArrayNode toArrayNode(List<RestAnswerDto> answers) {
        ArrayNode answersNode = JsonNodeFactory.instance.arrayNode();
        for (RestAnswerDto movieDto : answers) {
            ObjectNode movieObject = toObjectNode(movieDto);
            answersNode.add(movieObject);
        }
        return answersNode;
    }
}
