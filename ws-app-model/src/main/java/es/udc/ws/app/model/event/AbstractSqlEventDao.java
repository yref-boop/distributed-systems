package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractSqlEventDao implements SqlEventDao{

    protected AbstractSqlEventDao(){
    }

    /*
     * Nombre:       findEvents
     * Entrada:      la conexion, una fecha de inicio, una fecha de fin y (opcionalmente) una palabra clave
     * Salida:       la lista de eventos cuya fecha de celebración se encuentra entre las dos introducidas
     *               como parámetos y (opcionalemente) que contengan en su descripcion la palabra clave
     *               introducida
     * Objetivo:     buscar eventos entre dos fechas y (opcionalmente) que contengan en su descripción
     *               una palabra clave
     * Excepciones:  en caso de no poder realizar la busqueda, se lanzaria una RuntimeException
     *
     *               [FUNC-2]
     */
    @Override
    public List<Event> findEvents(Connection connection, LocalDate init, LocalDate end, String keyword) {

        LocalTime timeZero = LocalTime.of(0,0,0);

        Timestamp timeInit = init != null ? Timestamp.valueOf(LocalDateTime.of(init,timeZero)) : null;
        Timestamp timeEnd = end != null ? Timestamp.valueOf(LocalDateTime.of(end,timeZero)) : null;

        String queryString = "SELECT eventId, name, description, celebrationDate, duration, creationDate, numberAttend,"
                + " numberNotAttend, isCancelled FROM Event WHERE celebrationDate BETWEEN '" + timeInit + "' AND '"
                + timeEnd + "'";

        if(keyword != null && !keyword.equals(""))
            queryString += " AND LOWER(description) LIKE LOWER(?)";

        queryString += " ORDER BY celebrationDate ";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            if(keyword != null && !keyword.equals(""))
                preparedStatement.setString(1, "%" + keyword + "%");

            ResultSet resultSet = preparedStatement.executeQuery();

            List<Event> events = new ArrayList<>();

            while(resultSet.next()){
                int i = 1;
                Long eventId = resultSet.getLong(i++);
                String name = resultSet.getString(i++);
                String description = resultSet.getString(i++);
                Timestamp celebrationDateTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime celebrationDate = celebrationDateTimestamp.toLocalDateTime();
                Long duration = resultSet.getLong(i++);
                Timestamp creationDateTimestamp = resultSet.getTimestamp(i++);
                LocalDateTime creationDate = creationDateTimestamp.toLocalDateTime();
                Long numberAttend = resultSet.getLong(i++);
                Long numberNotAttend = resultSet.getLong(i++);
                Boolean isCancelled = resultSet.getBoolean(i++);

                events.add(new Event(eventId, name, description, celebrationDate, duration, creationDate,
                        numberAttend, numberNotAttend, isCancelled));
            }

            return events;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
    * Nombre:       find
    * Entrada:      la conexion y el parametro de busqueda (el ID del evento a buscar)
    * Salida:       el evento que se ha encontrado a partir del parametro de busqueda, con la informacion
    *               proporcionada al darlo de alta, la fecha y la hora a la que se ha dado de alta,
    *               numero de empleados que han respondido que asistiran al evento, numero de empleados
    *               que han respondido que no asistiran al evento y si el evento esta cancelado o no
    * Objetivo:     buscar eventos por su identificador
    * Excepciones:  en caso de no encontrar dicho evento, se lanzaria una InstanceNotFoundException. En
    *               caso de no poder realizar la busqueda, se lanzaria una RuntimeException
    *
    *               [FUNC-3]
    */

    @Override
    public Event find(Connection connection, Long eventId) throws InstanceNotFoundException {

        String queryString = "SELECT name, description, celebrationDate, duration, creationDate, numberAttend,"
                + " numberNotAttend, isCancelled FROM Event WHERE eventId = ?";

        try (PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setLong(i, eventId);

            ResultSet resultSet = preparedStatement.executeQuery();

            //no se ha encontrado un Evento con ese identificador
            if(!resultSet.next())
                throw new InstanceNotFoundException(eventId, Event.class.getName());

            //recogemos la informacion resultante de la busqueda
            String name = resultSet.getString(i++);
            String description = resultSet.getString(i++);
            Timestamp celebrationDateTimestamp = resultSet.getTimestamp(i++);
            Long duration = resultSet.getLong(i++);
            Timestamp creationDateTimestamp = resultSet.getTimestamp(i++);
            Long numberAttend = resultSet.getLong(i++);
            Long numberNotAttend = resultSet.getLong(i++);
            Boolean isCancelled = resultSet.getBoolean(i);
            LocalDateTime celebrationDate = celebrationDateTimestamp.toLocalDateTime();
            LocalDateTime creationDate = creationDateTimestamp.toLocalDateTime();

            //devolvemos un nuevo Event que representa al Event que se ha encontrado
            return new Event(eventId, name, description, celebrationDate, duration, creationDate,
                    numberAttend, numberNotAttend, isCancelled);

        }catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Nombre:       update
     * Entrada:      la conexion y el evento a actualizar
     * Objetivo:     modificar los parametros de un evento ya creado para, por ejemplo, cancelar
     *               dicho evento
     * Excepciones:  en caso de no encontrar dicho evento, se lanzaria una InstanceNotFoundException. En
     *               caso de no poder realizar la busqueda, se lanzaria una RuntimeException
     *
     *               [FUNC-5]
     */

    @Override
    public void update(Connection connection, Event event) throws InstanceNotFoundException {

        String queryString = "UPDATE Event " +
                "SET name = ?, description = ?, celebrationDate = ?, duration = ?," +
                " numberAttend = ?, numberNotAttend = ?, isCancelled = ? WHERE eventId = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setString(i++, event.getName());
            preparedStatement.setString(i++, event.getDescription());
            preparedStatement.setTimestamp(i++, Timestamp.valueOf(event.getCelebrationDate()));
            preparedStatement.setLong(i++, event.getDuration());
            preparedStatement.setLong(i++, event.getNumberAttend());
            preparedStatement.setLong(i++, event.getNumberNotAttend());
            preparedStatement.setBoolean(i++, event.getIsCancelled());
            preparedStatement.setLong(i, event.getEventId());

            int updatedRows = preparedStatement.executeUpdate();

            if(updatedRows == 0)
                throw new InstanceNotFoundException(event.getEventId(), Event.class.getName());

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /*
     * Nombre:       remove
     * Entrada:      la conexion y el identificador del evento a eliminar
     * Objetivo:     eliminar una evento de la base de datos. Necesario para los test
     * Excepciones:  en caso de no poder realizar la busqueda, se lanzaria una RuntimeException
     *
     */
    @Override
    public void remove(Connection connection, Long eventId) throws InstanceNotFoundException {

        String queryString = "DELETE FROM Event WHERE" + " eventId = ?";

        try(PreparedStatement preparedStatement = connection.prepareStatement(queryString)){

            int i = 1;
            preparedStatement.setLong(i++, eventId);

            int removedRows = preparedStatement.executeUpdate();

            if (removedRows == 0){
                throw new InstanceNotFoundException(eventId, Event.class.getName());
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
