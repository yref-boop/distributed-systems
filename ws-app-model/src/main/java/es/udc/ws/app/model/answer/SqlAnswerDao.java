package es.udc.ws.app.model.answer;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.util.List;

public interface SqlAnswerDao {

    Answer create(Connection connection, Answer answer);

    List<Answer> findByEmployee(Connection connection, String employeeMail, Boolean allAnswers);

    Boolean hasAnswered(Connection connection, String employeeMail, Long eventId) throws InstanceNotFoundException;

    void remove(Connection connection, Long eventId) throws InstanceNotFoundException;

}
