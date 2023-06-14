namespace java es.udc.ws.app.thrift

struct ThriftEventDto {
    1: i64 eventId
    2: string name
    3: string description
    4: string celebrationDate
    5: i64 duration
    6: i64 numberAttend
    7: i64 totalAnswers
    8: bool isCancelled
}

struct ThriftAnswerDto {
    1: i64 answerId
    2: i64 eventId
    3: string employeeEmail
    4: bool attendance
}

exception ThriftInputValidationException {
    1: string message
}

exception ThriftInstanceNotFoundException {
    1: string instanceId
    2: string instanceType
}

exception ThriftEventCancelledException{
    1: i64 eventId
}

exception ThriftAlreadyAnsweredException{
    1: i64 eventId
    2: string employeeEmail
}

exception ThriftOutOfDateException{
    1: i64 eventId
}

service ThriftEventService {

    ThriftEventDto addEvent(1: ThriftEventDto eventDto) throws (1: ThriftInputValidationException e)

    list<ThriftEventDto> findEvents(1: string endDate, 2: string keyword) throws (1: ThriftInputValidationException e)

    ThriftEventDto findEvent(1: i64 eventId) throws (1: ThriftInstanceNotFoundException e)

    ThriftAnswerDto createAnswer(1: string employeeEmail, 2: i64 eventId, 3: bool attendance) throws
    (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee, 3: ThriftEventCancelledException eee,
    4: ThriftAlreadyAnsweredException eeee, 5: ThriftOutOfDateException eeeee)

    void cancelEvent(1: i64 eventId) throws (1: ThriftInstanceNotFoundException e, 2: ThriftInputValidationException ee,
        3: ThriftEventCancelledException eee, 4: ThriftOutOfDateException eeee)

    list<ThriftAnswerDto> findAnswers (1: string employeeEmail, 2: bool allAnswers) throws (1: ThriftInputValidationException e)

}