package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.app.restservice.dto.EventToRestEventDtoConversor;
import es.udc.ws.app.restservice.dto.RestEventDto;
import es.udc.ws.app.restservice.json.JsonToRestEventDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException {

        ServletUtils.checkEmptyPath(request);

        String eventId = request.getParameter("eventId");

        if (eventId == null) {

            RestEventDto eventDto = JsonToRestEventDtoConversor.toRestEventDto(request.getInputStream());
        
            Event event = EventToRestEventDtoConversor.toEvent(eventDto);

            event = EventServiceFactory.getService().addEvent(event);

            eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
        
            String eventURL = ServletUtils.normalizePath(request.getRequestURL().toString()) + "/" + event.getEventId();

            Map<String, String> headers = new HashMap<>(1);
        
            headers.put("Location", eventURL);
        
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_CREATED,
                JsonToRestEventDtoConversor.toObjectNode(eventDto), headers);
        }

        else {
           
            try {
                EventServiceFactory.getService().cancelEvent(Long.valueOf(eventId));
            }
            catch (OutOfDateException | EventCancelledException | InstanceNotFoundException exception) {
                throw new InputValidationException("Event cannot be cancelled");
            }
        }
    }

    @Override
    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(request);
        String eventId = request.getParameter("eventId");
        String lastDate = request.getParameter("end");
        String keyWord = request.getParameter("keyword");

        List<RestEventDto> listEventDto = new ArrayList<>();

        if (eventId != null && lastDate == null && keyWord == null){
            Event event = EventServiceFactory.getService().findEvent(Long.parseLong(eventId));
            RestEventDto eventDto = EventToRestEventDtoConversor.toRestEventDto(event);
            listEventDto.add(eventDto);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toArrayNode (listEventDto), null);
        }else if (eventId == null && (lastDate != null)){
            List<Event> listEvent = EventServiceFactory.getService().findEvents(LocalDate.now(), LocalDate.parse(lastDate), keyWord);
            listEventDto = EventToRestEventDtoConversor.toRestEventDtos(listEvent);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                    JsonToRestEventDtoConversor.toArrayNode (listEventDto), null);
        }
    }

}
