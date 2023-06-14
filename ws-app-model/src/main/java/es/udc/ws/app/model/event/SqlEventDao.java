package es.udc.ws.app.model.event;

import es.udc.ws.util.exceptions.InstanceNotFoundException;

import java.sql.Connection;
import java.time.LocalDate;
import java.util.List;

public interface SqlEventDao {

    Event create(Connection connection, Event event);

    List<Event> findEvents(Connection connection, LocalDate init, LocalDate end, String keyword);

    Event find(Connection connection, Long eventId) throws InstanceNotFoundException;

    void update(Connection connection, Event event) throws InstanceNotFoundException;

    void remove(Connection connection, Long eventId) throws InstanceNotFoundException;

}
