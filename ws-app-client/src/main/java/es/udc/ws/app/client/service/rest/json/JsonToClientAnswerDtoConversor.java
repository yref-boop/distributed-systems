package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.InputStream;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientAnswerDtoConversor {

    public static ClientAnswerDto toClientAnswerDto(InputStream answerJson) throws ParsingException {

        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode node = objectMapper.readTree(answerJson);

            if(node.getNodeType() != JsonNodeType.OBJECT)
                throw new ParsingException("JSON not recognized (an object was expected)");
            else{
              ObjectNode answerObj = (ObjectNode) node;
              JsonNode answerIdNode = answerObj.get("answerId");
              Long answerId = (answerIdNode != null) ? answerIdNode.longValue() : null;

              String employeeEmail = answerObj.get("employeeEmail").textValue().trim();
              Long eventId = answerObj.get("eventId").longValue();
              Boolean attendance = answerObj.get("attendance").booleanValue();

              return new ClientAnswerDto(answerId, eventId, employeeEmail, attendance);
            }

        }catch (ParsingException e){
            throw e;
        }catch(Exception e){
            throw new ParsingException(e);
        }
    }

    public static List<ClientAnswerDto> toClientAnswerDtoList(InputStream answerJson) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(answerJson);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode answersArray = (ArrayNode) rootNode;
                List<ClientAnswerDto> answerDtos = new ArrayList<>(answersArray.size());
                for(JsonNode answerNode : answersArray){
                    answerDtos.add(toClientAnswerDto(answerNode));
                }
                return answerDtos;
            }
        } catch (ParsingException exception) {
            throw exception;
        } catch (Exception ex) {
            throw new ParsingException(ex);
        }
    }


    private static ClientAnswerDto toClientAnswerDto(JsonNode answerNode) throws ParsingException {
        if (answerNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode answerObject = (ObjectNode) answerNode;

            JsonNode answerIdNode = answerObject.get("answerId");
            Long answerId = (answerIdNode != null) ? answerIdNode.longValue() : null;
            Long eventId = answerObject.get("eventId").longValue();
            String employeeEmail = answerObject.get("employeeEmail").textValue().trim();
            boolean attendance = answerObject.get("attendance").booleanValue();

            return new ClientAnswerDto(answerId,eventId, employeeEmail, attendance);

        }
    }

}
