package es.udc.ws.app.client.service.rest;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;
import es.udc.ws.app.client.service.exceptions.ClientAlreadyAnsweredException;
import es.udc.ws.app.client.service.exceptions.ClientEventCancelledException;
import es.udc.ws.app.client.service.exceptions.ClientOutOfDateException;
import es.udc.ws.app.client.service.rest.json.JsonToClientAnswerDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientEventDtoConversor;
import es.udc.ws.app.client.service.rest.json.JsonToClientExceptionConversor;
import es.udc.ws.util.configuration.ConfigurationParametersManager;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.json.ObjectMapperFactory;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.fluent.Form;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.List;

public class RestClientEventService implements ClientEventService {

    private final static String ENDPOINT_ADDRESS_PARAMETER = "RestClientEventService.endpointAddress";
    private String endpointAddress;


    @Override
    public Long addEvent(ClientEventDto event) throws InputValidationException {

        try{
            HttpResponse response = Request.Post(getEndpointAddress() + "events").
                    bodyStream(toInputStream(event), ContentType.create("application/json")).
                    execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientEventDtoConversor.toClientEventDto(response.getEntity().getContent()).getEventId();

        } catch (InputValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientEventDto> findEvents(LocalDate end, String keyword) {

        try {
            HttpResponse response;
            if(keyword != null){
                response = Request.Get(getEndpointAddress() + "events?end=" + end.toString()
                                + "&keyword=" + URLEncoder.encode(keyword, StandardCharsets.UTF_8)).execute().returnResponse();
            }else{
                response = Request.Get(getEndpointAddress() + "events?end="
                        + end.toString()).execute().returnResponse();
            }

            validateStatusCode(HttpStatus.SC_OK, response);

            return JsonToClientEventDtoConversor.toClientEventDtoList(response.getEntity().getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ClientEventDto findEvent(Long eventId) throws InstanceNotFoundException {
        try{
            HttpResponse response = Request.Get(getEndpointAddress() + "events?eventId=" + eventId).execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);
            List<ClientEventDto> listEventDto = JsonToClientEventDtoConversor.toClientEventDtoList(response.getEntity().getContent());
            return listEventDto.get(0);

        }catch(InstanceNotFoundException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long createAnswer(String employeeEmail, Long eventId, Boolean attendance) throws InstanceNotFoundException,
            InputValidationException, ClientEventCancelledException, ClientAlreadyAnsweredException, ClientOutOfDateException {
        try {

            HttpResponse response = Request.Post(getEndpointAddress() + "answers").bodyForm(

                    Form.form().add("employeeEmail", employeeEmail).add("eventId", Long.toString(eventId)).
                            add("attendance", Boolean.toString(attendance)).build()).execute().returnResponse();

            validateStatusCode(HttpStatus.SC_CREATED, response);

            return JsonToClientAnswerDtoConversor.toClientAnswerDto(response.getEntity().getContent()).getAnswerId();

        }catch (InstanceNotFoundException | InputValidationException | ClientEventCancelledException
                | ClientAlreadyAnsweredException | ClientOutOfDateException e){
            throw e;
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, InputValidationException, ClientEventCancelledException, ClientOutOfDateException {
        try {
            HttpResponse response = Request.Post(getEndpointAddress() + "events?eventId=" + eventId ).
                    execute().returnResponse();
            validateStatusCode(HttpStatus.SC_OK, response);

        } catch( InstanceNotFoundException | InputValidationException | ClientEventCancelledException | ClientOutOfDateException e){
            throw e;
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<ClientAnswerDto> findAnswers(String employeeEmail, Boolean onlyAffirmative) {

        try {
            HttpResponse response;
            if (onlyAffirmative) {
                response = Request.Get(getEndpointAddress() + "answers?employeeEmail=" +
                    URLEncoder.encode(employeeEmail, StandardCharsets.UTF_8) + "&attendance=" + "true").
                    execute().returnResponse();
            } else {
                response = Request.Get(getEndpointAddress() + "answers?employeeEmail=" +
                    URLEncoder.encode(employeeEmail, StandardCharsets.UTF_8)).execute().returnResponse();
            }
            validateStatusCode(HttpStatus.SC_OK, response);
            return JsonToClientAnswerDtoConversor.toClientAnswerDtoList(response.getEntity()
                    .getContent());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private synchronized String getEndpointAddress() {
        if(endpointAddress == null) {
            endpointAddress = ConfigurationParametersManager.getParameter(ENDPOINT_ADDRESS_PARAMETER);
        }
        return endpointAddress;
    }

    private InputStream toInputStream(ClientEventDto event) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectMapper objectMapper = ObjectMapperFactory.instance();
            objectMapper.writer(new DefaultPrettyPrinter()).writeValue(outputStream,
                    JsonToClientEventDtoConversor.toObjectNode(event));

            return new ByteArrayInputStream(outputStream.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void validateStatusCode(int successCode, HttpResponse response) throws Exception {

        try{
            int statusCode = response.getStatusLine().getStatusCode();
            if(statusCode == successCode)
                return;

            switch (statusCode) {
                case HttpStatus.SC_NOT_FOUND -> throw JsonToClientExceptionConversor.fromNotFoundErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_BAD_REQUEST -> throw JsonToClientExceptionConversor.fromBadRequestErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_FORBIDDEN -> throw JsonToClientExceptionConversor.fromForbiddenErrorCode(
                        response.getEntity().getContent());
                case HttpStatus.SC_GONE -> throw JsonToClientExceptionConversor.fromGoneErrorCode(
                        response.getEntity().getContent());
                default -> throw new RuntimeException("HTTP error; status code = " + statusCode);
            }
        } catch (IOException e){
            throw new RuntimeException(e);
        }
    }
}
