package ru.mpei.rza.backend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.mpei.rza.backend.dto.Measurement;
import ru.mpei.rza.backend.dto.ValueCFG;
import ru.mpei.rza.backend.service.BackService;

import java.util.List;

@RestController
public class BackController implements Controller{

    @Autowired
    private BackService service;

    @Override
    @CrossOrigin(origins = "http://localhost:4200")
    @PostMapping("data/sendFiles")
    public void getFiles(@RequestParam MultipartFile cfg, @RequestParam MultipartFile dat) {
        service.startCalculate(cfg, dat);
    }

    @Override
    @GetMapping("data/getname")
    public String getName() {
        return "Выполнено";
    }

    @Override
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("data/get")
    public List<Measurement> getAllInDataBase() {
        return service.getAllInDataBase();
    }
    @CrossOrigin(origins = "http://localhost:4200")
    @GetMapping("data/getgraph")
    public List<List<Double>> getGraph() {
        return service.getGraph();
    }
}
