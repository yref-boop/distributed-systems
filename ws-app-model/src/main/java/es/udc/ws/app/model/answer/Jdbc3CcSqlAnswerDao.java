package es.udc.ws.app.model.answer;

import java.sql.*;

public class Jdbc3CcSqlAnswerDao extends AbstractSqlAnswerDao{


    /*
     * Nombre:       create
     * Entrada:      la conexion y una respuestaa
     * Salida:       la respuesta que se ha dado de alta, con su nuevo identificador, el email del usuario,
     *               la fecha y hora a la que se ha producido el alta y la asistencia
     * Objetivo:     dar de alta un evento
     * Excepciones:  en caso de no poder realizar el alta, se lanzaria una RuntimeException
     *
     *               [FUNC-4]
     */
    @Override
    public Answer create(Connection connection, Answer answer) {
        // crear "queryString"
        String queryString = "INSERT INTO Answer"
                + " (eventId, employeeEmail, answerDate, attendance) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setLong(i++, answer.getEventId());
            preparedStatement.setString(i++, answer.getEmployeeEmail());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(answer.getAnswerDate()));
            preparedStatement.setBoolean(i++, answer.getAttendance());

            // ejecutar la query
            preparedStatement.executeUpdate();

            // identificador
            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if (!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key");
            }
            Long answerId = resultSet.getLong(1);

            // devolver respuesta
            return new Answer(answerId, answer.getEventId(), answer.getEmployeeEmail(),
                    answer.getAnswerDate(), answer.getAttendance());

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}