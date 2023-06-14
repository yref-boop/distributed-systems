package es.udc.ws.app.client.service.rest.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;

import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class JsonToClientEventDtoConversor {
    public static ObjectNode toObjectNode(ClientEventDto event) throws IOException {
        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();


        eventObject.put("eventId", event.getEventId());
        eventObject.put("name", event.getName()).
        put("description", event.getDescription()).
        put("celebrationDate", event.getCelebrationDate().toString()).
        put("numberAttend", event.getNumberAttend()).
        put("totalAnswers", event.getTotalAnswers()).
        put("isCancelled", event.getCancelled()).
        put("duration", (Duration.between(event.getCelebrationDate(), event.getFinishTime()).toHoursPart()));

        return eventObject;
    }

    public static ClientEventDto toClientEventDto(InputStream jsonEvent) throws ParsingException {
        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);
            if (rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                return toClientEventDto(rootNode);
            }
        } catch (ParsingException exception) {
            throw exception;
        } catch (Exception ex) {
            throw new ParsingException(ex);
        }
    }

    public static List<ClientEventDto> toClientEventDtoList(InputStream jsonEvents) throws ParsingException {
        try {
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvents);
            if (rootNode.getNodeType() != JsonNodeType.ARRAY) {
                throw new ParsingException("Unrecognized JSON (array expected)");
            } else {
                ArrayNode eventsArray = (ArrayNode) rootNode;
                List<ClientEventDto> eventDtos = new ArrayList<>(eventsArray.size());
                for(JsonNode eventNode : eventsArray){
                    eventDtos.add(toClientEventDto(eventNode));
                }
                return eventDtos;
            }
        } catch (ParsingException exception) {
            throw exception;
        } catch (Exception ex) {
            throw new ParsingException(ex);
        }
    }


    private static ClientEventDto toClientEventDto(JsonNode eventNode) throws ParsingException {
        if (eventNode.getNodeType() != JsonNodeType.OBJECT) {
            throw new ParsingException("Unrecognized JSON (object expected)");
        } else {
            ObjectNode eventObject = (ObjectNode) eventNode;

            JsonNode eventIdNode = eventObject.get("eventId");
            Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;
            String name = eventObject.get("name").textValue().trim();
            String description = eventObject.get("description").textValue().trim();
            LocalDateTime celebrationDate = LocalDateTime.parse(eventObject.get("celebrationDate").textValue());
            LocalDateTime finishTime = celebrationDate.plus((eventObject.get("duration").intValue()), ChronoUnit.HOURS);
            long numberAttend = eventObject.get("numberAttend").longValue();
            long totalAnswers = eventObject.get("totalAnswers").longValue();
            boolean isCancelled = eventObject.get("isCancelled").booleanValue();

            return new ClientEventDto(eventId, name, description, celebrationDate, finishTime, numberAttend, totalAnswers, isCancelled);
        }
    }
}
