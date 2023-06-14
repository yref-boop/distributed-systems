package es.udc.ws.app.client.service.thrift;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientOutOfDateException;
import es.udc.ws.app.thrift.*;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.THttpClient;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import java.time.LocalDate;
import java.util.List;

public class ThriftClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "ThriftClientEventService.endpointAddress";

    private final static String endpointAddress =
            ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try{
            transport.open();
            return client.addEvent(ClientEventDtoToThriftEventDtoConversor.toThriftEventDto(event)).getEventId();

        } catch (ThriftInputValidationException e){
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDate end, String keyword) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try{
            transport.open();
            return ClientEventDtoToThriftEventDtoConversor.toClientEventDtos(client.findEvents(end.toString(), keyword));
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try{
            transport.open();

            return ClientEventDtoToThriftEventDtoConversor.toClientEventDto(client.findEvent(eventId));

        } catch (ThriftInstanceNotFoundException e) {
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }
    }

    @Override
    public Long createAnswer(String employeeEmail, Long eventId, Boolean attendance) throws InstanceNotFoundException,
            InputValidationException, ClientEventCancelledException, ClientAlreadyAnsweredException, ClientOutOfDateException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try{
            transport.open();
            return client.createAnswer(employeeEmail, eventId, attendance).getAnswerId();
        } catch (ThriftInstanceNotFoundException e){
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftInputValidationException e){
            throw new InputValidationException(e.getMessage());
        } catch (ThriftEventCancelledException e ){
            throw new ClientEventCancelledException(e.getEventId());
        } catch (ThriftAlreadyAnsweredException e){
            throw new ClientAlreadyAnsweredException(eventId, employeeEmail);
        } catch (ThriftOutOfDateException e){
            throw new ClientOutOfDateException(eventId);
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally{
            transport.close();
        }

    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, InputValidationException, ClientEventCancelledException, ClientOutOfDateException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {
            transport.open();
            client.cancelEvent(eventId);
        } catch (ThriftInstanceNotFoundException e){
            throw new InstanceNotFoundException(e.getInstanceId(), e.getInstanceType());
        } catch (ThriftInputValidationException e){
            throw new InputValidationException(e.getMessage());
        } catch (ThriftEventCancelledException e ){
            throw new ClientEventCancelledException(e.getEventId());
        } catch (ThriftOutOfDateException e){
            throw new ClientOutOfDateException(eventId);
        } catch (Exception e){
            throw new RuntimeException(e);
        } finally{
            transport.close();
        }
    }

    @Override
    public List<ClientAnswerDto> findAnswers(String employeeEmail, Boolean allAnswers) throws InputValidationException {

        ThriftEventService.Client client = getClient();
        TTransport transport = client.getInputProtocol().getTransport();

        try {
            transport.open();
            return ClientAnswerDtoToThriftAnswerDtoConversor.toClientAnswerDtoList(client.findAnswers(employeeEmail, allAnswers));
        } catch (ThriftInputValidationException e) {
            throw new InputValidationException(e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            transport.close();
        }

    }

    private ThriftEventService.Client getClient() {

        try {

            TTransport transport = new THttpClient(endpointAddress);
            TProtocol protocol = new TBinaryProtocol(transport);
            return new ThriftEventService.Client(protocol);

        } catch (TTransportException e) {
            throw new RuntimeException(e);
        }
    }

}
