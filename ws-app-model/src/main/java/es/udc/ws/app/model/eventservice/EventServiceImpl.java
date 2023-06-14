package es.udc.ws.app.model.eventservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.app.model.util.OtherPropertiesValidator;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.validation.PropertyValidator;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static es.udc.ws.app.model.util.ModelConstants.*;

public class EventServiceImpl implements EventService {

    private final DataSource dataSource;
    private SqlEventDao eventDao = null;
    private SqlAnswerDao answerDao = null;

    public EventServiceImpl(){
        dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);
        eventDao = SqlEventDaoFactory.getDao();
        answerDao = SqlAnswerDaoFactory.getDao();
    }

    private void validateEvent(Event event) throws InputValidationException {

        PropertyValidator.validateMandatoryString("name", event.getName());
        PropertyValidator.validateMandatoryString("description", event.getDescription());
        OtherPropertiesValidator.validateLocalDateTime("celebrationDate", event.getCelebrationDate());
        PropertyValidator.validateLong("duration", event.getDuration(), 0, MAX_DURATION);
    }

    /*
     * Nombre:       addEvent
     * Entrada:      un evento
     * Salida:       el evento que se ha dado de alta, con su nuevo identificador
     *               y la fecha y hora a la que se ha producido el alta;
     *               tras haber hecho uso de la funcion create del Dao de Event
     * Objetivo:     dar de alta un evento
     * Excepciones:  en caso de introducir erroneamente algun campo del evento,
     *               se lanzaría una InstanceNotFoundException.
     *               En caso de no poder establecer la conexion, se lanzaria una RuntimeException.
     *
     *               [FUNC-1]
     */
    @Override
    public Event addEvent(Event event) throws InputValidationException {

        validateEvent(event);
        event.setCreationDate(LocalDateTime.now());
        event.setNumberAttend(0L);
        event.setNumberNotAttend(0L);
        event.setIsCancelled(false);

        if((LocalDateTime.now()).isAfter(event.getCelebrationDate()))
            throw new InputValidationException("Past events can not be added");

        try(Connection connection = dataSource.getConnection()){

            try{

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                Event createdEvent = eventDao.create(connection, event);

                connection.commit();

                return createdEvent;
            } catch (SQLException e){
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException | Error e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /*
     * Nombre:       findEvents
     * Entrada:      una fecha inicial, una fecha final y (opcionalmente) una palabra clave
     * Salida:       la lista de eventos cuya fecha de celebración se encuentra entre las dos introducidas
     *               como parámetos y (opcionalemente) que contengan en su descripcion la palabra clave
     *               introducida; tras haber hecho uso de la funcion findEvents del Dao de Event
     * Objetivo:     buscar eventos entre dos fechas y (opcionalmente) que contengan en su descripción
     *               una palabra clave
     * Excepciones:  en caso de introducir erroneamente las fechas, se lanzaría una InstanceNotFoundException.
     *               En caso de no poder establecer la conexion, se lanzaria una RuntimeException.
     *
     *               [FUNC-2]
     */
    @Override
    public List<Event> findEvents(LocalDate init, LocalDate end, String keyword) throws InputValidationException {

        if(init.isAfter(end))
            throw new InputValidationException("Initial date should be an anterior date to End date");

        try (Connection connection = dataSource.getConnection()) {
            return eventDao.findEvents(connection, init, end, keyword);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Nombre:       findEvent
     * Entrada:      el parametro de busqueda (el identificador del evento a buscar)
     * Salida:       el evento que se ha encontrado a partir del parametro de busqueda tras haber hecho
     *               uso de la funcion find del Dao de Event
     * Objetivo:     buscar eventos por su identificador
     * Excepciones:  en caso de no encontrar dicho evento, se lanzaría una InstanceNotFoundException.
     *               En caso de no poder establecer la conexion, se lanzaria una RuntimeException.
     *
     *               [FUNC-3]
     */

    @Override
    public Event findEvent(Long eventId) throws InstanceNotFoundException {

        try (Connection connection = dataSource.getConnection()){
            return eventDao.find(connection, eventId);
        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }

    /*
     * Nombre:       createAnswer
     * Entrada:      el email del empleado, el id del evento correspondiente y si se pretende asistir o no
     * Objetivo:     crear una respuesta que guarde los datos correspondientes, cumpliendo las condiciones estipuladas
     * Excepciones:  en caso de no encontrar dicho evento, se lanzaría una InstanceNotFoundException.
     *               en caso de no poder establecer la conexion, se lanzaria una RuntimeException.
     *               en caso de que el evento haya sido cancelado con anterioridad, se lanzaria una
     *               EventCancelledException
     *               en caso de que el evento ya se haya celebrado, se lanzaria una OutOfDateException
     *               en caso de que este usuario ya haya respondido con anterioridad se lanzaria una AlreadyAnsweredException
     *
     *               [FUNC-4]
     */
    @Override
    public Answer createAnswer(String employeeEmail, Long eventId, Boolean attendance) throws InstanceNotFoundException,
             EventCancelledException, AlreadyAnsweredException, OutOfDateException, InputValidationException {

        OtherPropertiesValidator.validateEmail(employeeEmail);

        try (Connection connection = dataSource.getConnection()) {
            try {

                // preparar conexión
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // puede lanzar Instance not found
                Event event = eventDao.find(connection, eventId);

                // evento cancelado
                if (event.getIsCancelled()) throw new EventCancelledException(eventId);

                // no a tiempo
                if ((LocalDateTime.now().until(event.getCelebrationDate(), ChronoUnit.HOURS)) <= 24) {
                    throw new OutOfDateException(eventId);
                }

                // ya respondida
                if (answerDao.hasAnswered(connection, employeeEmail, eventId)) {
                    throw new AlreadyAnsweredException(eventId, employeeEmail);
                }

                Answer answer = answerDao.create(connection, new Answer(eventId, employeeEmail, attendance, LocalDateTime.now()));

                // commit
                connection.commit();

                // cambiar numero de asistentes en el evento correspondiente

                if (answer.getAttendance()){
                    event.setNumberAttend(event.getNumberAttend() + 1);
                } else {
                    event.setNumberNotAttend(event.getNumberNotAttend() + 1);
                }
                eventDao.update(connection, event);

                connection.commit();

                return answer;

            } catch (InstanceNotFoundException | EventCancelledException | AlreadyAnsweredException | OutOfDateException e){
                connection.commit();
                throw e;
            } catch (SQLException e) {
                connection.rollback();
                throw new RuntimeException(e);
            } catch (RuntimeException e) {
                connection.rollback();
                throw e;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /*
     * Nombre:       cancelEvent
     * Entrada:      el identificador del evento a cancelar
     * Objetivo:     marcar como cancelado un evento que ha sido creado con anterioridad, siempre que
     *               no se haya celebrado o que no haya sido cancelado con anterioridad
     * Excepciones:  en caso de no encontrar dicho evento, se lanzaría una InstanceNotFoundException.
     *               En caso de no poder establecer la conexion, se lanzaria una RuntimeException.
     *               En caso de que el evento haya sido cancelado con anterioridad, se lanzaria una
     *               EventCancelledException
     *               En caso de que el evento ya se haya celebrado, se lanzaria una OutOfDateException
     *
     *               [FUNC-5]
     */

    @Override
    public void cancelEvent(Long eventId) throws InstanceNotFoundException, EventCancelledException, OutOfDateException {
        Event event = findEvent(eventId);

        try (Connection connection = dataSource.getConnection()){
            try{

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                //comprobamos si ya ha sido cancelado. En ese caso se lanzará una EventCancelledException.
                if(event.getIsCancelled())
                    throw new EventCancelledException(eventId);

                //comprobamos si ya se ha celebrado. En ese caso se lanzará una OutOfDateException.
                if(event.getCelebrationDate().compareTo(LocalDateTime.now().withNano(0)) <= 0)
                    throw new OutOfDateException(eventId);

                //marcamos el evento como cancelado
                event.setIsCancelled(Boolean.TRUE);
                eventDao.update(connection, event);

                connection.commit();

            } catch (InstanceNotFoundException | EventCancelledException | OutOfDateException e){
                connection.commit();
                throw e;
            } catch (SQLException e){
                connection.rollback();
                throw  new RuntimeException(e);
            }catch (RuntimeException | Error e){
                connection.rollback();
                throw e;
            }

        } catch (SQLException e){
            throw new RuntimeException(e);
        }

    }


    /*
     * Nombre:       findAnswers
     * Entrada:      el email del empleado, y si se espera obtener todas las respuestas o solo las positivas
     * Objetivo:     listar todas las respuestas que un empleado ha realizado con anterioridad
     * Excepciones:  en caso de que el correo no sea válido, se lanzaría una InstanceNotFoundException.
     *
     *               [FUNC-6]
     */
    @Override
    public List<Answer> findAnswers(String employeeEmail, Boolean allAnswers) throws InputValidationException {

        OtherPropertiesValidator.validateEmail(employeeEmail);

        try (Connection connection = dataSource.getConnection()) {

            return answerDao.findByEmployee(connection, employeeEmail, allAnswers);

        }
        catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
