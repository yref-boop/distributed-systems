package es.udc.ws.app.client.ui;

import es.udc.ws.app.client.service.ClientEventService;
import es.udc.ws.app.client.service.ClientEventServiceFactory;
import es.udc.ws.app.client.service.dto.ClientAnswerDto;
import es.udc.ws.app.client.service.dto.ClientEventDto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class AppServiceClient {
    public static void main(String[] args) {

        if(args.length == 0)
            printUsageAndExit();
        ClientEventService clientEventService = ClientEventServiceFactory.getService();

        if("-a".equalsIgnoreCase(args[0])){
            validateArgs(args, 5, new int[] {});

            // [addEvent] AppServiceClient -a <name> <description> <start_date> <end_date>

            try{
                Long eventId = clientEventService.addEvent(new ClientEventDto(null, args[1], args[2],
                        LocalDateTime.parse(args[3]), LocalDateTime.parse(args[4]), 0L, 0L, false));

                System.out.println("Event " + eventId + " created sucessfully");

            } catch (Exception ex){
                ex.printStackTrace(System.err);
            }
        }else if ("-fes".equalsIgnoreCase(args[0])){
            if(args.length == 2)
                validateArgs(args, 2, new int[] {});
            else
                validateArgs(args, 3, new int[] {});

            // [findEvents] AppServiceClient -fes <untilDate> [<keyword>]

            try {
                List<ClientEventDto> events;
                if(args.length == 2){
                    events = clientEventService.findEvents(LocalDate.parse(args[1]), null);
                    System.out.println("Found " + events.size() + " event(s) celebrated before " + args[1]);
                }else{
                    events = clientEventService.findEvents(LocalDate.parse(args[1]), args[2]);
                    System.out.println("Found " + events.size() + " event(s) celebrated before " + args[1]
                            + " with keyword '" + args[2] + "'");
                }

                for (ClientEventDto eventDto : events) {
                    System.out.println("Id: " + eventDto.getEventId() +
                            ", Name: " + eventDto.getName() +
                            ", Description: " + eventDto.getDescription() +
                            ", Celebration Date: " + eventDto.getCelebrationDate() +
                            ", Finishing Date: " + eventDto.getFinishTime() +
                            ", Positive Answers: " + eventDto.getNumberAttend() +
                            ", Total Answers: " + eventDto.getTotalAnswers() +
                            ", Is Cancelled?: " + eventDto.getCancelled());
                }
            } catch (Exception ex) {
                ex.printStackTrace(System.err);
            }
        }else if("-fe".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[]{1});

            // [findEvent] AppServiceClient -fe <eventId>

            try{
                ClientEventDto event = clientEventService.findEvent(Long.parseLong(args[1]));
                System.out.println("Found event with id " + args[1] + ":\nName: " + event.getName() + "\nDescription: "
                        + event.getDescription() + "\nCelebration date: " + event.getCelebrationDate().toString() +
                        "\nFinish time: " + event.getFinishTime() + "\nNumber of attendants: " + event.getNumberAttend()
                        + "\nTotal answers: " + event.getTotalAnswers() + "\nIs cancelled? : " + event.getCancelled());
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }else if("-r".equalsIgnoreCase(args[0])){
            validateArgs(args, 4, new int[] {2});

            // [respond] AppServiceClient -r <userEmail> <eventId> <response>

            Long answerId;
            try{
                answerId = clientEventService.createAnswer(args[1], Long.parseLong(args[2]), Boolean.parseBoolean(args[3]));

                System.out.println("Employee with email " + args[1] + " answered correctly to the event " + args[2] +
                        " with answerId " + answerId + ". Assistance? " + args[3]);

            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }else if("-c".equalsIgnoreCase(args[0])){
            validateArgs(args, 2, new int[] {1});

            //[cancel] AppServiceClient -c <eventId>

            try {
                Long eventId = Long.parseLong(args[1]);
                clientEventService.cancelEvent(eventId);
                System.out.println("Event " + args[1] + " cancelled sucessfully");
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }else if("-fr".equalsIgnoreCase(args[0])){
            validateArgs(args, 3, new int[] {});

            //[findResponses] AppServiceClient -fr <userEmail> <onlyAffirmative>

            try{
                List<ClientAnswerDto> answers = clientEventService.findAnswers(args[1], Boolean.valueOf(args[2]));
                for (ClientAnswerDto answerDto : answers){
                    System.out.println("Id: " + answerDto.getAnswerId() +
                            ", Event id: " + answerDto.getEventId() +
                            ", Employee email: " + answerDto.getEmployeeEmail() +
                            ", Attendance: " + answerDto.getAttendance());
                }
            } catch (Exception e) {
                e.printStackTrace(System.err);
            }
        }
    }

    public static void validateArgs(String[] args, int expectedArgs, int[] numericArguments){
        if(expectedArgs != args.length)
            printUsageAndExit();
        for (int position : numericArguments) {
            try {
                Double.parseDouble(args[position]);
            } catch (NumberFormatException n) {
                printUsageAndExit();
            }
        }
    }

    public static void printUsageAndExit() {
        printUsage();
        System.exit(-1);
    }

    public static void printUsage(){
        System.err.println("""
                Usage:
                    [addEvent]      AppServiceClient -a <name> <description> <start_date> <end_date>
                    [respond]       AppServiceClient -r <userEmail> <eventId> <response>
                    [cancel]        AppServiceClient -c <eventId>
                    [findEvents]    AppServiceClient -fes <untilDate> [<keyword>]
                    [findEvent]     AppServiceClient -fe <eventId>
                    [findResponses] AppServiceClient -fr <userEmail> <onlyAffirmative>
                """);
    }
}