package com.company;

/**
 * Created by Manol on 12/09/2015.
 */
public class Observation {
    private String data;
    private Long timestamp;

    public Observation(String data, Long timestamp) {
        this.data = data;
        this.timestamp = timestamp;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public String getData() {
        return data;
    }

}
