package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public interface EventService {

    Event addEvent(Event event) throws InputValidationException;

    List<Event> findEvents(LocalDate init, LocalDate end, String keyword) throws InputValidationException;

    Event findEvent(Long eventId) throws InstanceNotFoundException;

    Answer createAnswer(String employeeEmail, Long eventId, Boolean attendance) throws InstanceNotFoundException,
            InputValidationException, EventCancelledException, AlreadyAnsweredException, OutOfDateException;

    void cancelEvent(Long eventId) throws InstanceNotFoundException, InputValidationException,
            EventCancelledException, OutOfDateException;

    List<Answer> findAnswers(String employeeEmail, Boolean allAnswers) throws InputValidationException;

}
