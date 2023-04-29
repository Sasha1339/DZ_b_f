package ru.mpei.rza.backend.repository;

import ru.mpei.rza.backend.dto.Measurement;

import java.util.List;

public interface Repository {

    void sendFault(List<Measurement> measurements);

    List<Measurement> getAll();

}
