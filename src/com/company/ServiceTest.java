package com.company;

import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.junit.Assert.assertEquals;

/**
 * Created by Manol on 14/09/2015.
 */
public class ServiceTest {

    private Service testService;
    private ConcurrentMap<Integer,List<Observation>> testDataStore;
    private static final int TEST_IDENTIFIER = 1;
    private static final int NON_EXISTENT_IDENTIFIER = 2;

    @Before
    public void setUp(){
        List<Observation> historyOfObservationsTestList = new ArrayList<>();

        historyOfObservationsTestList.add(new Observation("TestData1",100000L));
        historyOfObservationsTestList.add(new Observation("TestData2",100001L));
        historyOfObservationsTestList.add(new Observation("TestData3",100002L));
        historyOfObservationsTestList.add(new Observation("TestData4",100003L));
        historyOfObservationsTestList.add(new Observation("TestData5",100004L));
        historyOfObservationsTestList.add(new Observation("TestData6",100005L));

        testDataStore = new ConcurrentHashMap<>();
        testDataStore.put(TEST_IDENTIFIER,historyOfObservationsTestList);

        testService = new Service(testDataStore);
    }

    @Test
    public void shouldCreateNewHistoryForGivenIdentifierAndReturnDataInserted(){
        String actual = testService.createNewHistory(NON_EXISTENT_IDENTIFIER,100000L,"Test Data1");
        assertEquals("OK TestData1",actual);
    }

    @Test
    public void shouldNotCreateNewHistoryForGivenIdentifierWhenHistoryAlreadyExists(){
        String actual = testService.createNewHistory(TEST_IDENTIFIER,999999L,"TestData");
        assertEquals("ERR A history already exists for identifier 1",actual);
    }

    @Test
    public void shouldUpdateHistoryForGivenIdentifier(){
        String actual = testService.updateHistory(TEST_IDENTIFIER,100006L,"TestData7");
        assertEquals("OK TestData6",actual);
    }

    @Test
    public void shouldReturnAnErrorWhenUpdatingANonExistentIdentifier(){
        String actual = testService.updateHistory(NON_EXISTENT_IDENTIFIER,100008L,"TestData8");
        assertEquals("ERR The specified identifier doesn't exist.",actual);
    }

    @Test
    public void shouldDeleteAllObservationsFromGivenTimestampForward(){
        String actual = testService.deleteObservation(TEST_IDENTIFIER,100005L);
        assertEquals("OK TestData6",actual);
    }

    @Test
     public void shouldReturnAnErrorWhenGivenIdentifierDoesntExist(){
        String actual = testService.deleteObservation(NON_EXISTENT_IDENTIFIER,100001L);
        assertEquals("ERR The specified identifier doesn't exist.",actual);
    }

    @Test
    public void shouldDeleteAllObservationsForGivenIdentifierWhenNoTmestampIsProvided(){
        String actual = testService.deleteObservation(TEST_IDENTIFIER);
        assertEquals("OK 100005",actual);
    }

    @Test
    public void shouldDeleteAllObservationsForGivenIdentifierWhenNoTimestampIsProvided(){
        testService.deleteObservation(TEST_IDENTIFIER);
        List<Observation> actual = testDataStore.get(TEST_IDENTIFIER);
        assertEquals(null,actual);
    }

    @Test
    public void shouldReturnSpecifiedObservation(){
        String actual = testService.getObservation(TEST_IDENTIFIER,100005L);
        assertEquals("OK TestData6",actual);
    }

    @Test
    public void shouldReturnAnErrorWhenAttemptingToGetHistoryForANonExistentIdentifier(){
        String actual = testService.getObservation(NON_EXISTENT_IDENTIFIER,100005L);
        assertEquals("ERR There is no available observation for the specified identifier or timestamp",actual);
    }

    @Test
    public void shouldReturnLatestObservationFromGivenHistory(){
        String actual = testService.getLatestObservation(TEST_IDENTIFIER);
        assertEquals("OK 100005 TestData6",actual);
    }

    @Test
    public void shouldReturnErrorWhenTimestampDoesntExistInHistory(){
        String actual = testService.getObservation(TEST_IDENTIFIER,123223L);
        assertEquals("ERR There is no available observation for the specified identifier or timestamp",actual);
    }

    @Test
    public void shouldReturnObservationWithGreatestTimestamp(){
        String actual = testService.getLatestObservation(TEST_IDENTIFIER);
        assertEquals("OK 100005 TestData6",actual);
    }
}
