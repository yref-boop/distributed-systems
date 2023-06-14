package es.udc.ws.app.model.answer;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlAnswerDao implements SqlAnswerDao{

    protected AbstractSqlAnswerDao(){
    }

    @Override
    public List<Answer> findByEmployee(Connection connection, String employeeMail, Boolean allAnswers) {
        String queryString = "SELECT answerId, eventId, employeeEmail, answerDate, attendance FROM Answer ";

        queryString += "WHERE employeeEmail = ?";

        if (!allAnswers) queryString += " AND attendance = TRUE";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setString(i++, employeeMail);

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Answer> answers = new ArrayList<>();

            while(resultSet.next()){
                int j = 1;
                Long answerId = resultSet.getLong(j++);
                Long eventId = resultSet.getLong(j++);
                String employeeEmail = resultSet.getString(j++);
                Timestamp answerDateTimeStamp = resultSet.getTimestamp(j++);
                Boolean attendance = resultSet.getBoolean(j++);
                LocalDateTime answerDate =  answerDateTimeStamp.toLocalDateTime();

                answers.add(new Answer(answerId, eventId, employeeEmail, answerDate, attendance));
            }

            return answers;

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    @Override
    public Boolean hasAnswered(Connection connection, String employeeMail, Long eventId) throws InstanceNotFoundException {

        String queryString = "SELECT eventId, employeeEmail, answerDate, attendance FROM Answer";
        queryString += " WHERE employeeEmail = ?";
        queryString += " AND eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setString(i++, employeeMail);
            preparedStatement.setLong(i++, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();

            return resultSet.next();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void remove(Connection connection, Long answerId) throws InstanceNotFoundException {

        String queryString = "DELETE FROM Answer WHERE" + " answerId = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setLong(i++, answerId);

            int removedRows = preparedStatement.executeUpdate();

            if(removedRows == 0) throw new InstanceNotFoundException(answerId, Answer.class.getName());

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
