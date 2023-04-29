package ru.mpei.rza.backend.controller;

import org.springframework.web.multipart.MultipartFile;
import ru.mpei.rza.backend.dto.Measurement;
import ru.mpei.rza.backend.dto.ValueCFG;

import java.util.List;

public interface Controller {

    void getFiles(MultipartFile cfg, MultipartFile dat);

    String getName();

    List<Measurement> getAllInDataBase();

}
