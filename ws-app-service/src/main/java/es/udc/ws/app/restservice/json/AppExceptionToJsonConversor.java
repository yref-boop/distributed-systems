package es.udc.ws.app.restservice.json;

import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;

public class AppExceptionToJsonConversor {

    public static ObjectNode toAlreadyAnsweredException(AlreadyAnsweredException except){

        ObjectNode exceptObject = JsonNodeFactory.instance.objectNode();
        exceptObject.put("errorType", "AlreadyAnswered");
        exceptObject.put("eventId", (except.getEventId() != null) ? except.getEventId(): null);
        exceptObject.put("employeeEmail", (except.getEmployeeEmail() != null) ? except.getEmployeeEmail(): null);

        return exceptObject;
    }

    public static ObjectNode toEventCancelledException(EventCancelledException except){

        ObjectNode exceptObject = JsonNodeFactory.instance.objectNode();
        exceptObject.put("errorType", "EventCancelled");
        exceptObject.put("eventId", (except.getEventId() != null) ? except.getEventId(): null);

        return exceptObject;
    }

    public static ObjectNode toOutOfDateException(OutOfDateException except){

        ObjectNode exceptObject = JsonNodeFactory.instance.objectNode();
        exceptObject.put("errorType", "OutOfDate");
        exceptObject.put("eventId", (except.getEventId() != null) ? except.getEventId(): null);

        return exceptObject;
    }


}
