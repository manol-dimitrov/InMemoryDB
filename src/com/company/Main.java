package com.company;

import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Main {

    static ConcurrentMap<Integer, List<Observation>> dataStore = new ConcurrentHashMap<Integer, List<Observation>>();
    static Service service = new Service(dataStore);
    static Scanner scanner = new Scanner(System.in);
    static boolean exit = false;
    static String operation;
    static int identifier;
    static Long timestamp;
    static String observationData;

    public static void main(String[] args) {
        while (!exit) {
            System.out.println("Please enter a command: ");
            String command = scanner.nextLine();
            String[] parameters = command.split("\\s+");
            if (command.toUpperCase().equals("QUIT")) {
                System.exit(0);
            }

            /*
            Only get the parameter if index is valid
             */
            if(parameters.length > 0 && parameters[0] != null)
                operation = parameters[0].toUpperCase();
            if(parameters.length > 1 && parameters[1] != null)
                identifier = Integer.parseInt(parameters[1]);
            if(parameters.length > 2 && parameters[2] != null)
                timestamp = Long.parseLong(parameters[2]);
            else
                timestamp = null;
            if(parameters.length > 3 && parameters[3] != null)
                observationData = parameters[3];
            else
                observationData = null;

            switch (operation) {
                case "CREATE":
                    System.out.println(service.createNewHistory(identifier, timestamp, observationData));
                    break;
                case "UPDATE":
                    System.out.println(service.updateHistory(identifier, timestamp, observationData));
                    break;
                case "DELETE":
                    System.out.println(service.deleteObservation(identifier, timestamp));
                    break;
                case "GET":
                    System.out.println(service.getObservation(identifier, timestamp));
                    break;
                case "LATEST":
                    System.out.println(service.getLatestObservation(identifier));
                    break;
                default:
                    System.out.println("ERR Invalid Operation.");
                    break;
            }
        }
    }
}

