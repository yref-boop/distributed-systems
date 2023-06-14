package es.udc.ws.app.restservice.servlets;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.app.restservice.dto.AnswerToRestAnswerDtoConversor;
import es.udc.ws.app.restservice.dto.RestAnswerDto;
import es.udc.ws.app.restservice.json.AppExceptionToJsonConversor;
import es.udc.ws.app.restservice.json.JSonToRestAnswerDtoConversor;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.servlet.RestHttpServletTemplate;
import es.udc.ws.util.servlet.ServletUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AnswerServlet extends RestHttpServletTemplate {

    @Override
    protected void processPost(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException, InstanceNotFoundException {
        ServletUtils.checkEmptyPath(request);
        Long eventId = ServletUtils.getMandatoryParameterAsLong(request, "eventId");
        String employeeEmail = ServletUtils.getMandatoryParameter(request, "employeeEmail");
        boolean attendance = ServletUtils.getMandatoryParameter(request, "attendance").equals("true");


        Answer answer;
        try {
            answer = EventServiceFactory.getService().createAnswer(employeeEmail, eventId, attendance);
            RestAnswerDto answerDto = AnswerToRestAnswerDtoConversor.toRestAnswerDto(answer);
            String answerURL = ServletUtils.normalizePath(request.getRequestURL().toString()) + "/" + answer.getAnswerId().toString();
            Map<String, String> headers = new HashMap<>(1);
            headers.put("Location", answerURL);
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_CREATED,
                    JSonToRestAnswerDtoConversor.toObjectNode(answerDto), headers);
        } catch (EventCancelledException e) {
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toEventCancelledException(e), null);
        } catch (AlreadyAnsweredException e) {
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_FORBIDDEN, AppExceptionToJsonConversor.toAlreadyAnsweredException(e), null);
        } catch (OutOfDateException e) {
            ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_GONE, AppExceptionToJsonConversor.toOutOfDateException(e), null);
        }
    }

    @Override
    protected void processGet(HttpServletRequest request, HttpServletResponse response) throws IOException,
            InputValidationException {

        String employeeEmail = ServletUtils.getMandatoryParameter(request, "employeeEmail");

        String onlyAffirmative = request.getParameter("attendance");

        List<Answer> answers = EventServiceFactory.getService().findAnswers(employeeEmail, (onlyAffirmative == null));

        List<RestAnswerDto> answerDto = AnswerToRestAnswerDtoConversor.toRestAnswerListDto(answers);
        ServletUtils.writeServiceResponse(response, HttpServletResponse.SC_OK,
                JSonToRestAnswerDtoConversor.toArrayNode(answerDto), null);

    }

}
