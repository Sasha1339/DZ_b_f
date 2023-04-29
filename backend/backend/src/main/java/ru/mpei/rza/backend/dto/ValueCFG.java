package ru.mpei.rza.backend.dto;

import lombok.Data;

@Data
public class ValueCFG {

    private int number_channel;

    private String id;

    private String phase;

    private String equipment;

    private String measurement;

    private double a;

    private double b;

    private double skew;

    private int min;

    private int max;

}
