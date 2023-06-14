package es.udc.ws.app.model.event;

import java.sql.*;

public class Jdbc3CcSqlEventDao extends AbstractSqlEventDao{

    /*
     * Nombre:       create
     * Entrada:      la conexion y un evento
     * Salida:       el evento que se ha dado de alta, con su nuevo identificador
     *               y la fecha y hora a la que se ha producido el alta
     * Objetivo:     dar de alta un evento
     * Excepciones:  en caso de no poder realizar el alta, se lanzaria una RuntimeException
     *
     *               [FUNC-1]
     */
    @Override
    public Event create(Connection connection, Event event){

        String queryString = "INSERT INTO Event"
                + "(name, description, celebrationDate, duration, creationDate, numberAttend, numberNotAttend, isCancelled)"
                + "VALUES (?, ?, ?, ?, ?, ?, ? ,?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(
                queryString, Statement.RETURN_GENERATED_KEYS)) {

            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setLong(i++, event.getDuration());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCreationDate()));
            preparedStatement.setLong(i++, event.getNumberAttend());
            preparedStatement.setLong(i++, event.getNumberNotAttend());
            preparedStatement.setBoolean(i++, event.getIsCancelled());

            preparedStatement.executeUpdate();

            ResultSet resultSet = preparedStatement.getGeneratedKeys();

            if(!resultSet.next()){
                throw new SQLException("JDBC driver did not return generated key");
            }

            Long eventId = resultSet.getLong(1);

            return new Event(eventId, event.getName(), event.getDescription(), event.getCelebrationDate(),
                    event.getDuration(), event.getCreationDate(), event.getNumberAttend(),
                    event.getNumberNotAttend(), event.getIsCancelled());


        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
