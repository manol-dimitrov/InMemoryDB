package com.company;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * A service class providing CRUD type operations on the datastore.
 * The underslying data structure is a concurrent hashmap in order to allow multiple clients to
 * update and query the datastore.
 */
public class Service {

    private ConcurrentMap<Integer, List<Observation>> dataStore;
    private List<Observation> listOfCurrentObservations;

    public Service(ConcurrentMap<Integer, List<Observation>> dataStore) {
        this.dataStore = dataStore;
    }

    /**
     * Creates a new history for a given identifier.
     * @param identifier
     * @param timestamp
     * @param data
     * @return
     */
    public String createNewHistory(Integer identifier, Long timestamp, String data) {
        if (!dataStore.containsKey(identifier)) {
            listOfCurrentObservations = new ArrayList<>();

            listOfCurrentObservations.add(new Observation(data, timestamp));

            dataStore.put(identifier, listOfCurrentObservations);
            return "OK " + sanitiseData(data);
        } else {
            return "ERR A history already exists for identifier " + identifier;
        }
    }

    public String updateHistory(Integer identifier, Long timestamp, String data) {
        if (dataStore.containsKey(identifier)) {
            List<Observation> historyForSpecifiedIdentifier = getHistoryForSpecifiedIdentifier(identifier);

            historyForSpecifiedIdentifier.add(new Observation(data, timestamp));

            String previousObservationData = getPreviousDataForObservation(historyForSpecifiedIdentifier, timestamp);

            dataStore.put(identifier, historyForSpecifiedIdentifier);

            return "OK " + previousObservationData;
        } else {
            return "ERR The specified identifier doesn't exist.";
        }
    }

    public String deleteObservation(Integer identifier, Long... timestamp) {
        List<Observation> historyForSpecifiedIdentifier = getHistoryForSpecifiedIdentifier(identifier);

        Observation observationWithGreatestTimestamp = getObservationWithGreatestTimestamp(historyForSpecifiedIdentifier);

        if(observationWithGreatestTimestamp == null) {
            return "ERR The specified identifier doesn't exist.";
        }

        Long greatestTimeStamp = observationWithGreatestTimestamp.getTimestamp();

        if (timestamp.length >= 1 && timestamp[0] != null && historyForSpecifiedIdentifier.size() > 1) {
            Long optionalTimestamp = timestamp[0];

            String latestObservationData = getSpecifiedObservation(historyForSpecifiedIdentifier, optionalTimestamp).getData();

            historyForSpecifiedIdentifier.removeIf(observation -> observation.getTimestamp() < optionalTimestamp);

            return "OK " + latestObservationData;
        } else {
            dataStore.remove(identifier);
        }
        return "OK " + greatestTimeStamp;
    }

    public String getObservation(Integer identifier, Long timestamp) {
        List<Observation> historyForSpecifiedIdentifier = getHistoryForSpecifiedIdentifier(identifier);
        if (historyForSpecifiedIdentifier != null) {
            Observation observation = getSpecifiedObservation(historyForSpecifiedIdentifier, timestamp);
            if (observation != null) {
                return "OK " + observation.getData();
            }
        }
        return "ERR There is no available observation for the specified identifier or timestamp";
    }

    /**
     * Gets the latest observation based on a give identifier.
     * @param identifier
     * @return
     */
    public String getLatestObservation(Integer identifier) {
        List<Observation> historyForSpecifiedIdentifier = getHistoryForSpecifiedIdentifier(identifier);
        if (historyForSpecifiedIdentifier != null) {
            Observation latestObservation = getObservationWithGreatestTimestamp(historyForSpecifiedIdentifier);
            return "OK " + latestObservation.getTimestamp() + " " + latestObservation.getData();
        }
        return "ERR No history exists for identifier " + identifier;
    }

    private String getPreviousDataForObservation(List<Observation> observationList, Long timestamp) {
        Observation currentObservation = getSpecifiedObservation(observationList, timestamp);
        String previousObservationData;
        if (observationList.size() > 1) {
            previousObservationData = observationList.get(observationList.indexOf(currentObservation) - 1).getData();
        } else {
            previousObservationData = observationList.get(observationList.indexOf(currentObservation)).getData();
        }
        return previousObservationData;
    }

    private Observation getSpecifiedObservation(List<Observation> observationList, Long timestamp) {
        Observation observation = observationList
                .stream()
                .filter(observation1 -> observation1.getTimestamp() >= timestamp)
                .findAny()
                .orElse(null);
        return observation;
    }

    private Observation getObservationWithGreatestTimestamp(List<Observation> observationList) {
        if(observationList != null && observationList.size() > 1) {
            Observation observation = observationList
                    .stream()
                    .sorted((o1, o2) -> Long.compare(o2.getTimestamp(), o1.getTimestamp()))
                    .limit(1)
                    .collect(Collectors.toList())
                    .get(0);
            return observation;
        }
        return null;
    }

    private List<Observation> getHistoryForSpecifiedIdentifier(Integer identifier) {
        if (dataStore.containsKey(identifier)) {
            return dataStore.get(identifier);
        }
        return null;
    }

    private String sanitiseData(String data) {
        return data.replaceAll("\\s+", "");
    }
}
