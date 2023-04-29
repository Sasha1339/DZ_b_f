package ru.mpei.rza.backend.service;

import org.springframework.web.multipart.MultipartFile;
import ru.mpei.rza.backend.dto.Measurement;
import ru.mpei.rza.backend.dto.ValueCFG;

import java.util.List;


public interface Service {

    void startCalculate(MultipartFile cfg, MultipartFile dat);

    void sendFaultToDB(List<Measurement> measurements);

    List<ValueCFG> parsingCFG(MultipartFile cfg);

    List<List<Double>> parsingData(MultipartFile dat, List<ValueCFG> values);

    List<Measurement> findFault(List<List<Double>> RMSs, List<ValueCFG> values, int idEncoding);

    List<Measurement> calculateTimeFault(List<Measurement> measurements, MultipartFile dat);

    List<Measurement> getAllInDataBase();

}
