package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.util.json.ObjectMapperFactory;
import es.udc.ws.util.json.exceptions.ParsingException;


import java.io.InputStream;
import java.util.List;

public class JsonToRestEventDtoConversor {

    public static ObjectNode toObjectNode (RestEventDto eventDto){

        ObjectNode eventObject = JsonNodeFactory.instance.objectNode();

        if(eventDto.getEventId() != null){
            eventObject.put("eventId", eventDto.getEventId());
        }

        eventObject.put("name", eventDto.getName()).put("description", eventDto.getDescription()).
                put("celebrationDate", eventDto.getCelebrationDate()).put("duration", eventDto.getDuration()).
                put("numberAttend", eventDto.getNumberAttend()).put("totalAnswers", eventDto.getTotalAnswers()).
                put("isCancelled", eventDto.getCancelled());

        return  eventObject;

    }

    public static ArrayNode toArrayNode (List<RestEventDto> listEventDto){
        ArrayNode listEventNode = JsonNodeFactory.instance.arrayNode();
        for (RestEventDto eventDto : listEventDto) {
            ObjectNode eventObject = toObjectNode(eventDto);
            listEventNode.add(eventObject);
        }
        return listEventNode;
    }

    public static RestEventDto toRestEventDto(InputStream jsonEvent) throws ParsingException {
        try{
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            JsonNode rootNode = objectMapper.readTree(jsonEvent);

            if(rootNode.getNodeType() != JsonNodeType.OBJECT) {
                throw new ParsingException("Unrecognized JSON (object expected)");
            } else {
                ObjectNode eventObject = (ObjectNode) rootNode;

                JsonNode eventIdNode = eventObject.get("eventId");
                Long eventId = (eventIdNode != null) ? eventIdNode.longValue() : null;

                String name = eventObject.get("name").textValue().trim();
                String description = eventObject.get("description").textValue().trim();
                String celebrationDate = eventObject.get("celebrationDate").textValue().trim();
                Long duration = eventObject.get("duration").longValue();
                Long numberAttend = eventObject.get("numberAttend").longValue();
                Long totalAnswers = eventObject.get("totalAnswers").longValue();
                boolean isCancelled = eventObject.get("isCancelled").booleanValue();

                return new RestEventDto(eventId, name, description, celebrationDate, duration, numberAttend, totalAnswers, isCancelled);
            }

        } catch (ParsingException ex) {
            throw ex;
        } catch (Exception e) {
            throw new ParsingException(e);
        }
    }

}
