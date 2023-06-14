package es.udc.ws.app.client.service;

import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientOutOfDateException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface ClientEventService {

    Long addEvent(ClientEventDto event) throws InputValidationException;
    List<ClientEventDto> findEvents(LocalDate end, String keyword) throws InputValidationException;
    ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException;
    Long createAnswer(String employeeEmail, Long eventId, Boolean attendance) throws InstanceNotFoundException,
            InputValidationException, ClientEventCancelledException, ClientAlreadyAnsweredException, ClientOutOfDateException;
    void cancelEvent(Long eventId) throws InstanceNotFoundException, InputValidationException, ClientEventCancelledException,
            ClientOutOfDateException;
    List<ClientAnswerDto> findAnswers (String employeeEmail, Boolean allAnswers) throws InputValidationException;

}
