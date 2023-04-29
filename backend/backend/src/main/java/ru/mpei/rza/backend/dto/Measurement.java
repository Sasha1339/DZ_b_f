package ru.mpei.rza.backend.dto;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "measurement")
@AllArgsConstructor
@NoArgsConstructor
public class Measurement {



    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int numberChannel;
    @Id
    private String channel;

    private double valueCurrent;

    private int time;

}
