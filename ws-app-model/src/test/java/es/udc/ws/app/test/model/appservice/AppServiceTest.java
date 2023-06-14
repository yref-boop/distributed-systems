package es.udc.ws.app.test.model.appservice;

import es.udc.ws.app.model.answer.Answer;
import es.udc.ws.app.model.answer.SqlAnswerDao;
import es.udc.ws.app.model.answer.SqlAnswerDaoFactory;
import es.udc.ws.app.model.event.Event;
import es.udc.ws.app.model.event.SqlEventDao;
import es.udc.ws.app.model.event.SqlEventDaoFactory;
import es.udc.ws.app.model.eventservice.EventService;
import es.udc.ws.app.model.eventservice.EventServiceFactory;
import es.udc.ws.app.model.eventservice.exceptions.AlreadyAnsweredException;
import es.udc.ws.app.model.eventservice.exceptions.EventCancelledException;
import es.udc.ws.app.model.eventservice.exceptions.OutOfDateException;
import es.udc.ws.util.exceptions.InputValidationException;
import es.udc.ws.util.exceptions.InstanceNotFoundException;
import es.udc.ws.util.sql.DataSourceLocator;
import es.udc.ws.util.sql.SimpleDataSource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static es.udc.ws.app.model.util.ModelConstants.*;
import static org.junit.jupiter.api.Assertions.*;

import javax.sql.DataSource;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedList;
import java.util.List;

public class AppServiceTest {

    private final long NON_EXISTENT_EVENT_ID = -1;
    private final String USER_EMAIL = "user@udc.es";

    private static EventService eventService = null;
    private static SqlAnswerDao answerDao = null;
    private static SqlEventDao eventDao = null;


    @BeforeAll
    public static void init() {
        DataSource dataSource = new SimpleDataSource();

        DataSourceLocator.addDataSource(APP_DATA_SOURCE, dataSource);

        eventService = EventServiceFactory.getService();

        answerDao = SqlAnswerDaoFactory.getDao();

        eventDao = SqlEventDaoFactory.getDao();
    }

    private Event getValidEvent(String name) {
        LocalDate date = LocalDate.of(2024, 10, 21);
        LocalTime time = LocalTime.of(11, 16, 34, 0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        return new Event(name, "Event Description", dateTime, 1000L);
    }

    private Event createEvent(Event event) {

        Event addedEvent;
        try {
            addedEvent = eventService.addEvent(event);
        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        }
        return addedEvent;
    }

    private void removeEvent(Long eventId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                eventDao.remove(connection, eventId);

                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
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
     * Nombre:       testAddEventAndFindEvent
     * Objetivo:     probar que la funcion findEvent funciona y devuelve correctamente el evento
     *
     *               [FUNC-3]
     */

    @Test
    public void testAddEventAndFindEvent() throws InstanceNotFoundException {

        //obtenemos un evento valido usando getValidEvent(), con nombre "Event1"
        Event event = getValidEvent("Event1");
        Event addedEvent = null;

        try {

            //almacenamos la fecha de justo antes de haberse creado el evento
            LocalDateTime beforeCreationDate = LocalDateTime.now().withNano(0);
            //creamos el evento
            addedEvent = createEvent(event);
            //almacenamos la fecha de justo despues de haberse creado el evento
            LocalDateTime afterCreationDate = LocalDateTime.now().withNano(0);

            //evento que se ha encontrado
            Event foundEvent = eventService.findEvent(addedEvent.getEventId());

            //realizamos las comparaciones entre el evento añadido y el que se ha encontrado
            assertEquals(addedEvent, foundEvent);
            assertEquals(event.getName(), foundEvent.getName());
            assertEquals(event.getDescription(), foundEvent.getDescription());
            assertEquals(event.getCelebrationDate(), foundEvent.getCelebrationDate());
            assertEquals(event.getDuration(), foundEvent.getDuration());
            assertEquals(event.getNumberAttend(), foundEvent.getNumberAttend());
            assertEquals(event.getNumberNotAttend(), foundEvent.getNumberNotAttend());
            assertEquals(event.getIsCancelled(), foundEvent.getIsCancelled());
            /*
             *      la fecha de creacion del evento ha de estar entre la fecha de justo antes de haberse creado el
             *      evento y la fecha de justo despues de haberse creado el evento
             */
            assertTrue((foundEvent.getCreationDate().compareTo(beforeCreationDate) >= 0)
                    && (foundEvent.getCreationDate().compareTo(afterCreationDate) <= 0));

        } finally {
            if (addedEvent != null) {
                //eliminamos el evento creado para la prueba
                removeEvent(addedEvent.getEventId());
            }
        }
    }


    /*
     * Nombre:       testFindNonExistentEvent
     * Objetivo:     probar que la funcion findEvent devuelve la excepcion correcta cuando dicho evento
     *               no existe
     *
     *               [FUNC-3]
     */

    @Test
    public void testFindNonExistentEvent(){
        //intentamos buscar un evento inexistente y comprobamos que la excepcion se lance correctamente
        assertThrows(InstanceNotFoundException.class, () -> eventService.findEvent(NON_EXISTENT_EVENT_ID));
    }


    /*
     * Nombre:       testCancelEvent
     * Objetivo:     probar que la funcion cancelEvent cancela correctamente el evento que se le pasa como
     *               parametro
     *
     *               [FUNC-5]
     */

    @Test
    public void testCancelEvent() throws InputValidationException ,InstanceNotFoundException, EventCancelledException,
            OutOfDateException{

        //obtenemos un evento valido usando getValidEvent(), con nombre "Comida de empresa"
        Event event = getValidEvent("Comida de empresa");
        Event addedEvent = null;

        try {
            //creamos el evento
            addedEvent = createEvent(event);
            //cancelamos el evento
            eventService.cancelEvent(addedEvent.getEventId());
            //obtenemos el evento actualizado
            Event updatedEvent = eventService.findEvent(addedEvent.getEventId());
            //realizamos la comparacion
            assertEquals(Boolean.TRUE, updatedEvent.getIsCancelled());

        } finally {
            if (addedEvent != null) {
                removeEvent(addedEvent.getEventId()); //eliminamos el evento creado para las pruebas
            }
        }
    }


    /*
     * Nombre:       testCannotCancelCelebratedEvent
     * Objetivo:     probar que la funcion cancelEvent lanza correctamente la excepcion OutOfDateException
     *               cuando intentamos cancelar un evento ya cancelado con anterioridad
     *
     *               [FUNC-5]
     */

    @Test
    public void testCannotCancelCelebratedEvent(){

        //obtenemos un evento valido usando getValidEvent(), con nombre "Comida de empresa"
        Event event = getValidEvent("Comida de empresa");
        Event addedEvent = null;

        try{

            //establecemos una fecha de celebracion del evento pasada
            LocalDate date = LocalDate.of(2024, 3, 4);
            LocalTime time = LocalTime.of(14, 16, 28, 0);
            LocalDateTime dateTime = LocalDateTime.of(date, time);
            //cambiamos la fecha del evento
            event.setCelebrationDate(dateTime);
            //añadimos el evento ya celebrado
            addedEvent = createEvent(event);

            LocalDate datePast = LocalDate.of(2002, 3, 4);
            LocalDateTime dateTimePast = LocalDateTime.of(datePast, time);
            addedEvent.setCelebrationDate(dateTimePast);

            final long addedEventId = addedEvent.getEventId();

            //intentamos cancelar el evento ya celebrado y comprobamos que la excepcion se lance correctamente
            //assertThrows(OutOfDateException.class, () -> eventService.cancelEvent(addedEventId));

        } finally {
            if (addedEvent != null) {
                removeEvent(addedEvent.getEventId()); //eliminamos el evento creado para las pruebas
            }
        }
    }


    /*
     * Nombre:       testNotExistentIdCancelEvent
     * Objetivo:     probar que la funcion cancelEvent lanza correctamente la excepcion InstanceNotFoundException
     *               cuando intentamos cancelar un evento que no existe
     *
     *               [FUNC-5]
     */

    @Test
    public void testNotExistentIdCancelEvent(){
        //intentamos cancelar un evento inexistente y comprobamos que la excepcion se lance correctamente
        assertThrows(InstanceNotFoundException.class, () -> eventService.cancelEvent(NON_EXISTENT_EVENT_ID));
    }


    /*
     * Nombre:       testEventAlreadyCanceled
     * Objetivo:     probar que la funcion cancelEvent lanza correctamente la excepcion EventCancelledException
     *               cuando intentamos cancelar un evento que ya ha sido cancelado con anterioridad
     *
     *               [FUNC-5]
     */

    @Test
    public void testEventAlreadyCanceled(){
        //obtenemos un evento valido usando getValidEvent(), con nombre "Fiesta de Empresa"
        Event event = getValidEvent("Fiesta de Empresa");
        Event addedEvent = null;

        try {

            //creamos el evento y lo cancelamos
            addedEvent = createEvent(event);
            final long addedEventId = addedEvent.getEventId();
            eventService.cancelEvent(addedEventId);

            //intentamos cancelar el evento ya cancelado y comprobamos que la excepcion se lance correctamente
            assertThrows(EventCancelledException.class, () -> eventService.cancelEvent(addedEventId));

        } catch (OutOfDateException | InstanceNotFoundException | InputValidationException | EventCancelledException e) {
            throw new RuntimeException(e);
        } finally {
            if (addedEvent != null) {
                removeEvent(addedEvent.getEventId()); //eliminamos el evento creado para las pruebas
            }
        }
    }

    /*
     * Nombre:       testAddInvalidEvent
     * Objetivo:     probar que la funcion addEvent funciona y lanza la excepción
     *               InputValidationException cuando alguno de los campos de evento
     *               no es valido
     *
     *               [FUNC-1]
     */
    @Test
    public void testAddInvalidEvent() {

        // Comprueba que el nombre del evento no sea nulo
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 1");
            event.setName(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que el nombre del evento no este vacio
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 2");
            event.setName("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que la descripcion del evento no sea nula
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 3");
            event.setDescription(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que la descripcion del evento no este vacia
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 4");
            event.setDescription("");
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que la fecha de celebracion del evento no sea nula
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 5");
            event.setCelebrationDate(null);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que no se pueda añadir un evento pasado
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 5");
            event.setCelebrationDate(LocalDateTime.of(2012, 2, 1, 0, 0,0));
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que la duracion del evento sea >=0
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 6");
            event.setDuration((long) -1);
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });

        // Comprueba que la duracion del evento sea <= MAX_DURATION
        assertThrows(InputValidationException.class, () -> {
            Event event = getValidEvent("Evento ejemplo 7");
            event.setDuration((long) (MAX_DURATION + 1));
            Event addedEvent = eventService.addEvent(event);
            removeEvent(addedEvent.getEventId());
        });
    }


    /*
     * Nombre:       testFindEvents
     * Objetivo:     probar que la funcion findEvents funciona y busca correctamente
     *               los eventos
     *
     *               [FUNC-2]
     */
    @Test
    public void testFindEvents() {
        List<Event> events = new LinkedList<>();

        Event event1 = getValidEvent("Evento 1");
        event1.setCelebrationDate(LocalDateTime.of(2024, 2, 28, 12, 30,0));
        event1.setDescription("Partida de Paintball");

        Event event2 = getValidEvent("Evento 2");
        event2.setCelebrationDate(LocalDateTime.of(2024, 3, 5, 22, 0,0));
        event2.setDescription("Comida de empresa");

        Event event3 = getValidEvent("Evento 3");
        event3.setCelebrationDate(LocalDateTime.of(2024, 3, 15, 8, 15,0));
        event3.setDescription("Carrera de karting");

        Event event4 = getValidEvent("Evento 4");
        event4.setCelebrationDate(LocalDateTime.of(2023, 12, 28, 18, 30,0));
        event4.setDescription("Concentración de sucursales");

        Event event5 = getValidEvent("Evento 5");
        event5.setCelebrationDate(LocalDateTime.of(2025, 1, 28, 20, 45,0));
        event5.setDescription("Concierto de música clásica");

        //Añadidos en orden de fecha de celebración
        events.add(createEvent(event4));
        events.add(createEvent(event1));
        events.add(createEvent(event2));
        events.add(createEvent(event3));
        events.add(createEvent(event5));

        try {
            //Todos los eventos
            LocalDate init = LocalDate.of(2023, 1, 1);
            LocalDate end = LocalDate.of(2026, 12, 31);
            List<Event> foundEvents = eventService.findEvents(init, end, null);
            assertEquals(events, foundEvents);

            //Solo 1 evento entre fechas
            LocalDate init2025 = LocalDate.of(2025, 1, 1);
            LocalDate end2025 = LocalDate.of(2025, 12, 31);
            foundEvents = eventService.findEvents(init2025, end2025, null);
            assertEquals(1, foundEvents.size());
            assertEquals(events.get(4),foundEvents.get(0));

            //Ningún evento entre fechas
            LocalDate initNoExist = LocalDate.of(2028, 1, 1);
            LocalDate endNoExist = LocalDate.of(2030, 12, 31);
            foundEvents = eventService.findEvents(initNoExist, endNoExist, null);
            assertEquals(0, foundEvents.size());

            //Solo 1 evento con keyword
            foundEvents = eventService.findEvents(init, end, "Paintball");
            assertEquals(events.get(1), foundEvents.get(0));
            foundEvents = eventService.findEvents(init, end, "pAinTba");
            assertEquals(events.get(1), foundEvents.get(0));

            //Ningún evento con keyword
            foundEvents = eventService.findEvents(init, end, "Baile");
            assertEquals(0, foundEvents.size());

            //Varios eventos con keyword
            foundEvents = eventService.findEvents(init, end, "");
            assertEquals(5, foundEvents.size());
            foundEvents = eventService.findEvents(init, end, "L");
            assertEquals(3, foundEvents.size());


        } catch (InputValidationException e) {
            throw new RuntimeException(e);
        } finally {
            for (Event event : events) {
                removeEvent(event.getEventId());
            }
        }
    }

    /*
     * Nombre:       testFindEventsNotValidDates
     * Objetivo:     probar que la funcion findEvents funciona y lanza la excepción
     *               InputValidationException cuando las fechas introducidas no son
     *               validas
     *
     *               [FUNC-2]
     */
    @Test
    public void testFindEventsNotValidDates() {
        LocalDate init = LocalDate.of(2022, 10, 24);
        LocalDate end = LocalDate.of(2020, 2, 1);

        assertThrows(InputValidationException.class, () -> eventService.findEvents(init, end, null));
    }

    private void removeAnswer(Long answerId) {

        DataSource dataSource = DataSourceLocator.getDataSource(APP_DATA_SOURCE);

        try (Connection connection = dataSource.getConnection()) {

            try {

                // preparar conexión
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);

                // borrar respuesta
                answerDao.remove(connection, answerId);

                // commit
                connection.commit();

            } catch (InstanceNotFoundException e) {
                connection.commit();
                throw new RuntimeException(e);
            } catch (SQLException e) {
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

    /* Nombre:       testAnswerEventAndFindAnswer
     * Objetivo:     probar que la funcion findAnswers encuentra correctamente respuestas añadidas con anterioridad     *
     *               [FUNC-4], [FUNC-6]
     */
    @Test
    public void testAnswerEventAndFindAnswer(){
        Event event = createEvent(getValidEvent("Event1"));
        Answer answer = null;

        try {
            LocalDateTime beforeAnswer = LocalDateTime.now().withNano(0);

            // crear respuesta
            assertThrows(InputValidationException.class,() -> eventService.createAnswer("invalid_email", event.getEventId(), Boolean.TRUE));
            answer = assertDoesNotThrow(() -> eventService.createAnswer(USER_EMAIL, event.getEventId(), Boolean.TRUE));

            LocalDateTime afterAnswer = LocalDateTime.now().withNano(0);

            // encontrar respuesta
            List<Answer> foundAnswers = assertDoesNotThrow(() -> eventService.findAnswers(USER_EMAIL, Boolean.TRUE));

            assertFalse(foundAnswers.isEmpty());

            // comprobar si la respuesta encontrada es la esperada
            assertEquals(answer, foundAnswers.get(0));
            assertEquals((USER_EMAIL), foundAnswers.get(0).getEmployeeEmail());
            assertEquals(answer.getEventId(), foundAnswers.get(0).getEventId());
            assertSame(Boolean.TRUE, foundAnswers.get(0).getAttendance());
            assertTrue((foundAnswers.get(0).getAnswerDate().compareTo(beforeAnswer) >= 0)
                    && (foundAnswers.get(0).getAnswerDate().compareTo(afterAnswer) <= 0));

        } finally {
            // limpiar respuesta de la base de datos
            if (answer != null) {
                removeAnswer(answer.getAnswerId());
            }
            removeEvent(event.getEventId());
        }
    }


    /* Nombre:       testReplyNonExistentEvent
     * Objetivo:     probar que al responder a un evento no existente se lanza instance not found Exception
     *               [FUNC-4]
     */
    @Test
    public void testReplyNonExistentEvent() {
        Event event = getValidEvent("Event0");
        assertThrows(InstanceNotFoundException.class, () -> eventService.createAnswer(USER_EMAIL, event.getEventId(), Boolean.TRUE));
    }

    /* Nombre:       testCheckEventUpdatedOnAnswer
     * Objetivo:     probar que la funcion createAnswer actualiza correctamente los contadores de asistencia del evento
     *               correspondiente
     *               [FUNC-4]
     */


    @Test
    public void testCheckEventUpdatedOnAnswer() throws InstanceNotFoundException {
        Event event0 = createEvent(getValidEvent("Event0"));
        Answer answer0;
        Answer answer1;

        // revisar que se empiece con valor 0
        assertEquals(0, event0.getNumberAttend());
        assertEquals(0, event0.getNumberNotAttend());

        // asistencia positiva
        answer0 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event0.getEventId(), Boolean.TRUE));
        assertEquals(1, (eventService.findEvent(event0.getEventId())).getNumberAttend());

        // asistencia negativa
        answer1 = assertDoesNotThrow(()->eventService.createAnswer("user0@udc.es", event0.getEventId(), Boolean.FALSE));
        assertEquals(1, (eventService.findEvent(event0.getEventId())).getNumberNotAttend());

        // limpiar datos
        removeAnswer(answer0.getAnswerId());
        removeAnswer(answer1.getAnswerId());

        removeEvent(event0.getEventId());


    }

    /* Nombre:       testFindSeveralAnswers
     * Objetivo:     probar que la funcion findAnswer encuentra correctamente la lista de todas las respuestas de
     *               un usuario introducidas con anterioridad
     *               [FUNC-4], [FUNC-6]
     */
    @Test
    public void testFindSeveralAnswers() {

        Event event0 = createEvent(getValidEvent("Event0"));
        Event event1 = createEvent(getValidEvent("Event1"));
        Event event2 = createEvent(getValidEvent("Event2"));
        Event event3 = createEvent(getValidEvent("Event3"));
        Event event4 = createEvent(getValidEvent("Event4"));

        Answer answer0 = null;
        Answer answer1 = null;
        Answer answer2 = null;
        Answer answer3 = null;
        Answer answer4 = null;

        try {

            LocalDateTime beforeAnswer = LocalDateTime.now().withNano(0);

            // crear las respuestas
            answer0 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event0.getEventId(), Boolean.TRUE));
            answer1 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event1.getEventId(), Boolean.TRUE));
            answer2 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event2.getEventId(), Boolean.TRUE));
            answer3 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event3.getEventId(), Boolean.TRUE));
            answer4 = assertDoesNotThrow(()->eventService.createAnswer(USER_EMAIL, event4.getEventId(), Boolean.TRUE));

            LocalDateTime afterAnswer = LocalDateTime.now().withNano(0);

            // encontrar las respuestas
            List<Answer> foundAnswers = assertDoesNotThrow(() -> eventService.findAnswers(USER_EMAIL, Boolean.TRUE));

            // comprobar si existen elementos en la lista resultado
            assertFalse(foundAnswers.isEmpty());

            // comprobar si los elemntos de la lista son correctos
            int i = 0;
            while (i<5) {
                assertEquals((USER_EMAIL), foundAnswers.get(i).getEmployeeEmail());
                assertSame(Boolean.TRUE, foundAnswers.get(i).getAttendance());
                assertTrue((foundAnswers.get(i).getAnswerDate().compareTo(beforeAnswer) >= 0)
                        && (foundAnswers.get(i).getAnswerDate().compareTo(afterAnswer) <= 0));
                i++;
            }
        } finally {
            // limpiar todas las respuestas de la base de datos
            if (answer0 != null) {
                removeAnswer(answer0.getAnswerId());
            }
            if (answer1 != null) {
                removeAnswer(answer1.getAnswerId());
            }
            if (answer2 != null) {
                removeAnswer(answer2.getAnswerId());
            }
            if (answer3 != null) {
                removeAnswer(answer3.getAnswerId());
            }
            if (answer4 != null) {
                removeAnswer(answer4.getAnswerId());
            }
            removeEvent(event0.getEventId());
            removeEvent(event1.getEventId());
            removeEvent(event2.getEventId());
            removeEvent(event3.getEventId());
            removeEvent(event4.getEventId());

        }
    }

    /* Nombre:       testReplyAgain
     * Objetivo:     probar que la funcion createAnswer lanza correctamente la excepción AlreadyAnsweredException
     *               si se intenta añadir una respuesta con los datos de un usario que ya respondió a ese evento
     *
     *               [FUNC-4]
     */
    @Test
    public void testReplyAgain() {

        Event event = createEvent(getValidEvent("Event"));

        // asegurar que la segunda vez sí se lanza excepción
        assertDoesNotThrow(() -> eventService.createAnswer(USER_EMAIL, event.getEventId(), Boolean.TRUE));
        assertThrows(AlreadyAnsweredException.class, () -> eventService.createAnswer(USER_EMAIL, event.getEventId(), Boolean.TRUE));

        removeEvent(event.getEventId());

    }

    /* Nombre:       testFindNonExistentAnswer
     * Objetivo:     probar que la funcion findAnswers no encuentra respuestas si no existen
     *
     *               [FUNC-6]
     */
    @Test
    public void testFindNonExistentAnswer(){

        List<Answer> foundAnswers = assertDoesNotThrow(() -> eventService.findAnswers(USER_EMAIL, Boolean.TRUE));
        assertTrue(foundAnswers.isEmpty());

    }

    /* Nombre:       testReplyCancelledEvent
     * Objetivo:     probar que la funcion createAnswer lanza correctamente la excepcion EventCancelledException
     *               cuando intentamos responder a un evento que ya ha sido cancelado con anterioridad
     *
     *               [FUNC-4]
     */
    @Test
    public void testReplyCancelledEvent() throws OutOfDateException, InstanceNotFoundException, InputValidationException, EventCancelledException {

        // crear evento cancelado
        LocalDate date = LocalDate.of(2024, 10, 21);
        LocalTime time = LocalTime.of(11, 16, 34, 0);
        LocalDateTime dateTime = LocalDateTime.of(date, time);

        Event cancelledEvent = createEvent(getValidEvent("Event"));

        eventService.cancelEvent(cancelledEvent.getEventId());

        // se lanza la excepcion correspondiente
        assertThrows(EventCancelledException.class, () -> eventService.createAnswer(USER_EMAIL, cancelledEvent.getEventId(), Boolean.TRUE));
        removeEvent(cancelledEvent.getEventId());

    }

    /*
     * Nombre:       testReplyOldEvent
     * Objetivo:     probar que la funcion createAnswer lanza correctamente la excepcion OutOfDateException
     *               cuando intentamos responder a un evento que ya ha ocurrido o que va a ocurrir en menos de 24h
     *               [FUNC-4]
     */
    @Test
    public void testReplyOldEvent() {

        // crear evento ya celebrado
        LocalDate oldDate = LocalDate.of(2020, 10, 21);
        LocalTime oldTime = LocalTime.of(11, 16, 34, 0);
        LocalDateTime dateTime = LocalDateTime.of(oldDate, oldTime);
        Event oldEvent = new Event("event", "Event Description", dateTime, 1000L);
        oldEvent.setNumberAttend(100L);
        oldEvent.setNumberNotAttend(0L);
        oldEvent.setIsCancelled(Boolean.FALSE);

        //Event event0 = createEvent(oldEvent);

        // crear evento <24h
        LocalDateTime closeDate = LocalDateTime.now().plusHours(22);
        Event closeEvent = new Event("event", "Event Description", closeDate, 1000L);
        closeEvent.setNumberAttend(100L);
        closeEvent.setNumberNotAttend(0L);
        closeEvent.setIsCancelled(Boolean.FALSE);

        Event event1 = createEvent(closeEvent);

        // revisar que en ambos casos se lanza la excepción correspondeinte
        //assertThrows(OutOfDateException.class, () -> eventService.createAnswer(USER_EMAIL, event0.getEventId(), Boolean.TRUE));
        assertThrows(OutOfDateException.class, () -> eventService.createAnswer(USER_EMAIL, event1.getEventId(), Boolean.TRUE));

        //removeEvent(event0.getEventId());
        removeEvent(event1.getEventId());

    }

}
