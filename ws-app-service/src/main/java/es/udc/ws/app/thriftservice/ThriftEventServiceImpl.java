package es.udc.ws.app.thriftservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.app.restservice.dto.AnswerToRestAnswerDtoConversor;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.time.LocalDate;
import java.util.List;

public class ThriftEventServiceImpl implements ThriftEventService.Iface {
    @Override
    public ThriftEventDto addEvent(ThriftEventDto eventDto) throws ThriftInputValidationException {

        Event event = EventToThriftEventDtoConversor.toEvent(eventDto);

        try{
            Event addedEvent = EventServiceFactory.getService().addEvent(event);
            return EventToThriftEventDtoConversor.toThriftEventDto(addedEvent);
        } catch (InputValidationException e){
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public List<ThriftEventDto> findEvents(String endDate, String keyword) throws ThriftInputValidationException{

        try{
            List<Event> events = EventServiceFactory.getService().findEvents(LocalDate.now(), LocalDate.parse(endDate), keyword);
            return EventToThriftEventDtoConversor.toThriftEventDtos(events);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }
    }

    @Override
    public ThriftEventDto findEvent(long eventId) throws ThriftInstanceNotFoundException {
        try {
            Event event = EventServiceFactory.getService().findEvent(eventId);
            return EventToThriftEventDtoConversor.toThriftEventDto(event);
        }catch (InstanceNotFoundException e){
            throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                    e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
        }

    }

    @Override
    public ThriftAnswerDto createAnswer(String employeeEmail, long eventId, boolean attendance) throws ThriftInstanceNotFoundException,
            ThriftInputValidationException, ThriftEventCancelledException, ThriftAlreadyAnsweredException, ThriftOutOfDateException {

            try{

                Answer answer = EventServiceFactory.getService().createAnswer(employeeEmail, eventId, attendance);
                return AnswerToThriftAnswerDtoConversor.toThriftAnswerDto(answer);

            } catch (InstanceNotFoundException e){
                throw new ThriftInstanceNotFoundException(e.getInstanceId().toString(),
                        e.getInstanceType().substring(e.getInstanceType().lastIndexOf('.') + 1));
            } catch (InputValidationException e){
                throw  new ThriftInputValidationException(e.getMessage());
            } catch (EventCancelledException e) {
                throw new ThriftEventCancelledException(e.getEventId());
            } catch (AlreadyAnsweredException e) {
                throw new ThriftAlreadyAnsweredException(e.getEventId(), e.getEmployeeEmail());
            } catch (OutOfDateException e){
                throw new ThriftOutOfDateException(e.getEventId());
            }

    }

    @Override
    public void cancelEvent(long eventId) throws ThriftInstanceNotFoundException, ThriftInputValidationException, ThriftEventCancelledException, ThriftOutOfDateException {
        try {
            EventServiceFactory.getService().cancelEvent(eventId);

        } catch (OutOfDateException e) {
            throw new ThriftOutOfDateException (e.getEventId());
        } catch (InstanceNotFoundException e) {
            throw new ThriftInstanceNotFoundException();
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        } catch (EventCancelledException e) {
            throw new ThriftEventCancelledException();
        }
    }

    @Override
    public List<ThriftAnswerDto> findAnswers(String employeeEmail, boolean allAnswers) throws ThriftInputValidationException {

        try {
            List<Answer> answers = EventServiceFactory.getService().findAnswers(employeeEmail, !allAnswers);
            return AnswerToThriftAnswerDtoConversor.toThriftAnswerListDto(answers);
        } catch (InputValidationException e) {
            throw new ThriftInputValidationException(e.getMessage());
        }

    }
}
