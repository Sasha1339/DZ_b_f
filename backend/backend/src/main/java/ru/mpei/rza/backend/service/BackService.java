package ru.mpei.rza.backend.service;

import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.multipart.MultipartFile;
import ru.mpei.rza.backend.dto.Measurement;
import ru.mpei.rza.backend.dto.ValueCFG;
import ru.mpei.rza.backend.repository.BackRepository;
import ru.mpei.rza.backend.repository.Repository;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;


@org.springframework.stereotype.Service
public class BackService implements Service{

    @Autowired
    private BackRepository repository;

    private List<List<Double>> data;
    private int encod;

    @Override
    public List<Measurement> getAllInDataBase() {
        return repository.getAll();
    }

    @Override
    public void startCalculate(MultipartFile cfg, MultipartFile dat) {
        List<ValueCFG> values = parsingCFG(cfg);
        String encoding = fingEncod(cfg);
        List<Measurement> newMeasurements = new ArrayList<>();
        byte[] b = new byte[0];
        try{
            b = encoding.getBytes(StandardCharsets.US_ASCII);
            List<List<Integer>> newFile = getFile(dat);
            List<List<Double>> RMSs = parsingDataBin(newFile, values);
            List<Measurement> measurements = findFault(RMSs, values, 0);
            newMeasurements = calculateTimeFaultBin(measurements);

        }catch (Exception e){
            List<List<Double>> RMSs = parsingData(dat, values);
            List<Measurement> measurements = findFault(RMSs, values, 1);
            newMeasurements = calculateTimeFault(measurements, dat);
        }
        sendFaultToDB(newMeasurements);
    }

    @Override
    public void sendFaultToDB(List<Measurement> measurements) {
        repository.sendFault(measurements);
    }

    @SneakyThrows
    public String fingEncod(MultipartFile cfg){
        InputStream inputStream = cfg.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = br.readLine();
        String encoding = "";
        for(int i = 1; i <= 170; i++){

            if (i == 169){
                encoding = line;
            }
            line = br.readLine();
        }
        return encoding;
    }


    @Override
    @SneakyThrows
    public List<ValueCFG> parsingCFG(MultipartFile cfg) {

        List<ValueCFG> values = new ArrayList<>();
        InputStream inputStream = cfg.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = br.readLine();
        String[] string = br.readLine().split(",");

        String countAnalog= string[1].substring(0, string[1].length() - 1);

        for(int i = 0; i < Integer.parseInt(countAnalog); i++){
            ValueCFG valueCFG = new ValueCFG();
            string = br.readLine().split(",");
            valueCFG.setNumber_channel(Integer.parseInt(string[0]));
            valueCFG.setId(string[1]);
            valueCFG.setPhase(string[2]);
            valueCFG.setEquipment(string[3]);
            valueCFG.setMeasurement(string[4]);
            valueCFG.setA(Double.parseDouble(string[5]));
            valueCFG.setB(Double.parseDouble(string[6]));
            valueCFG.setSkew(Double.parseDouble(string[7]));
            valueCFG.setMin(Integer.parseInt(string[8]));
            valueCFG.setMax(Integer.parseInt(string[9]));
            values.add(valueCFG);
        }
        //System.out.println(values.get(0).toString());
        return values;
    }

//    @Override
//    @SneakyThrows
//    public int countChoice(MultipartFile dat, List<ValueCFG> values) {
//        return null;
//    }

//        InputStream inputStream = dat.getInputStream();
//        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
//        boolean flag = true;
//        String[] line = br.readLine().split(",");
//        String measurements = line[];
//        int count = 1;
//        while (flag){
//            String[] measurements = br.readLine().split(",");}
//
//        return 0;
//    }
    @SneakyThrows
    public List<List<Integer>> getFile(MultipartFile dat) {
        List<List<Integer>> newFile = new ArrayList<>();
        // create a reader
        BufferedInputStream reader = new BufferedInputStream(dat.getInputStream());

        // read one byte at a time
        int ch;
        try {
            int k = 0;
            for (int j = 0; j < 6000; j++) {
                newFile.add(new ArrayList<>());
                boolean flag = true;
                int i = 0;
                while (flag) {
                    if (i == 0 || i == 4) {
                        int c1 = reader.read();
                        i += 1;
                        int c2 = reader.read();
                        i += 1;
                        int c3 = reader.read();
                        i += 1;
                        int c4 = reader.read();
                        i += 1;
                        //System.out.print((c1 & 0xFF) | (c2 & 0xFF) << 8 | (c3 & 0xFF) << 16 | (c4 & 0xFF) << 24);
                        newFile.get(j).add((c1 & 0xFF) | (c2 & 0xFF) << 8 | (c3 & 0xFF) << 16 | (c4 & 0xFF) << 24);
                    } else if (i > 4 && i < 74) {
                        byte c1 = (byte) reader.read();
                        i += 1;
                        byte c2 = (byte) reader.read();
                        i += 1;
                        if ((c1 & 0xFF | (c2 & 0xFF) << 8) > 32767){
                            //System.out.print((~c1 & 0xFF | (~c2 & 0xFF) << 8)*-1);
                            newFile.get(j).add((~c1 & 0xFF | (~c2 & 0xFF) << 8)*-1);
                        }else{
                           // System.out.print((c1 & 0xFF | (c2 & 0xFF) << 8));
                            newFile.get(j).add((c1 & 0xFF | (c2 & 0xFF) << 8));
                        }
                       // System.out.print(" ");
                    }else {
                        int c1 = reader.read();
                        i += 1;
                    }if (i == 90) {
                        //System.out.print("\n");
                        flag = false;
                        continue;
                    }

                }

                    }

            // close the reader
            //reader.close();
        }catch (Exception e){
            reader.close();
        }
        return newFile;
    }

    public List<List<Double>> parsingDataBin(List<List<Integer>> newFile, List<ValueCFG> values){
        List<List<Double>> data = new ArrayList<>();
        List<List<Double>> RMSs = new ArrayList<>();
        for (int i = 0; i< values.size(); i++){
            if (i == 0){
                data.add(new ArrayList<>());
                data.add(new ArrayList<>());
            }else{
                data.add(new ArrayList<>());
            }
            RMSs.add(new ArrayList<>());
            double a = 0;
            double b = 0;
            double RMS = 0;
            int count = 0;
            for(List<Integer> line: newFile) {

                if (count < 20){
                    if (i == 0){
                        data.get(i).add((double)line.get(1));
                        data.get(i+1).add((double)line.get(i+2)*values.get(i).getA()+values.get(i).getB());
                    }else{
                        data.get(i+1).add((double)line.get(i+2)*values.get(i).getA()+values.get(i).getB());
                    }
                    double choice = (double)line.get(i+2);
                    a += 0.1 * (choice * values.get(i).getA() + values.get(i).getB()) * Math.cos((double) 1 * (double) 2 * Math.PI * ((double) count / (double) 20));
                    b += 0.1 * (choice * values.get(i).getA() + values.get(i).getB()) * Math.sin((double) 1 * (double) 2 * Math.PI * ((double) count / (double) 20));
                }else{
                    RMS = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)) / 2);
                    RMSs.get(i).add(RMS);
                    a = 0;
                    b = 0;
                    RMS = 0;
                    count = 0;
                }
                count += 1;

            }
        }
        this.data = data;
        this.encod = 2;
        return RMSs;
    }


    @Override
    @SneakyThrows
    public List<List<Double>> parsingData(MultipartFile dat, List<ValueCFG> values) {
        List<List<Double>> data = new ArrayList<>();
        List<List<Double>> RMSs = new ArrayList<>();

        for (int i = 0; i< values.size(); i++){
            if (i == 0){
                data.add(new ArrayList<>());
                data.add(new ArrayList<>());
            }else{
                data.add(new ArrayList<>());
            }
            RMSs.add(new ArrayList<>());
            InputStream inputStream = dat.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line = br.readLine();
            String[] measurements = line.split(",");
        while (line!=null) {

            double a = 0;
            double b = 0;
            double RMS = 0;
            for (int j = 0; j < 400; j++) {


                //первый канал измерений
                if (line != null) {
                    if (i == 0){
                        data.get(i).add(Double.parseDouble(measurements[1]));
                        data.get(i+1).add(Double.parseDouble(measurements[i+2])*values.get(i).getA()+values.get(i).getB());
                    }else{
                        data.get(i+1).add(Double.parseDouble(measurements[i+2])*values.get(i).getA()+values.get(i).getB());
                    }
                    double choice = Double.parseDouble(measurements[i + 2]);
                    a += 0.005 * (choice * values.get(i).getA() + values.get(i).getB()) * Math.cos((double) 1 * (double) 2 * Math.PI * ((double) j / (double) 400));
                    b += 0.005 * (choice * values.get(i).getA() + values.get(i).getB()) * Math.sin((double) 1 * (double) 2 * Math.PI * ((double) j / (double) 400));
                    line = br.readLine();
                    if (line != null) {
                        measurements = line.split(",");
                    }
                }
            }
            RMS = Math.sqrt((Math.pow(a, 2) + Math.pow(b, 2)) / 2);
            RMSs.get(i).add(RMS);
        }

        }
        this.data = data;
        this.encod = 1;
        return RMSs;
    }

    public List<List<Double>> getGraph(){
        List<Measurement> measurements = repository.getAll();
        List<List<Double>> data = this.data;
        List<List<Double>> newData = new ArrayList<>();
        double minTime = 10E10;
        double maxTime = 0;
        for (Measurement measurement: measurements){
            if ((double)measurement.getTime() < minTime){
                minTime = (double)measurement.getTime();
            }
        }
        if (this.encod == 1){
            minTime = minTime - 100000;
        }else if(this.encod == 2){
            minTime = minTime - 100000;
        }

        if (this.encod == 1){
            maxTime = 350000;
        }else if(this.encod == 2){
            maxTime = 1200000;
        }
        int count = -1;
        for(int i = 0; i < data.size(); i++){
            if (i == 0){
                newData.add(new ArrayList<>());
                count += 1;
                for(int j = 0; j < data.get(0).size();j++){
                    if (data.get(0).get(j) >= minTime && data.get(0).get(j) < maxTime){
                        newData.get(count).add(data.get(count).get(j));
                    }
                }
            }else{
                for(Measurement measurement: measurements){
                    if (i == measurement.getNumberChannel()){
                        newData.add(new ArrayList<>());
                        count += 1;
                        for(int j = 0; j < data.get(0).size();j++){
                            if (data.get(0).get(j) >= minTime && data.get(0).get(j) < maxTime){
                                newData.get(count).add(data.get(measurement.getNumberChannel()).get(j));
                            }
                        }
                    }
                }
            }
        }
        return newData;
    }


    @Override
    public List<Measurement> findFault(List<List<Double>> RMSs, List<ValueCFG> values, int idEncoding) {
        List<Measurement> measurements = new ArrayList<>();
        for(int i =0; i < values.size(); i++) {
            if (values.get(i).getMeasurement().indexOf("A") != -1 || values.get(i).getMeasurement().indexOf("А") != -1 || values.get(i).getId().indexOf("I") != -1) {
               if (values.get(i).getId().indexOf("a") != -1
                       || values.get(i).getId().indexOf("b") != -1
                       || values.get(i).getId().indexOf("c") != -1
                       || values.get(i).getId().indexOf("A") != -1
                       || values.get(i).getId().indexOf("B") != -1
                       || values.get(i).getId().indexOf("C") != -1){
                boolean flag = true;

                double differ = RMSs.get(i).get(0);
                if (differ < 0.01) {
                    differ = 0.075;
                }
                double valueCurrent = 0;
                int numberID = 0;
                for (int j = 1; j < RMSs.get(i).size(); j++) {
                    if (differ * 1.05 < RMSs.get(i).get(j)) {
                        if (flag) {
                            if (idEncoding == 0){
                                numberID = (int) ((j + 0.5) * (double) 20);
                            }else{
                                numberID = (int) ((j + 0.5) * (double) 400);
                            }

                            flag = false;
                        }
                        differ = RMSs.get(i).get(j);
                        valueCurrent = RMSs.get(i).get(j);
                    }
                }
                if (numberID != 0) {
                    Measurement measurement = new Measurement();
                    measurement.setId(numberID);
                    measurement.setNumberChannel(values.get(i).getNumber_channel());
                    measurement.setChannel(values.get(i).getId());
                    measurement.setValueCurrent(valueCurrent);
                    measurements.add(measurement);
                }
            }
            }

        }

        return measurements;
    }

    @Override
    @SneakyThrows
    public List<Measurement> calculateTimeFault(List<Measurement> measurements, MultipartFile dat) {
        InputStream inputStream = dat.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        String line = br.readLine();
        while (line != null){
            String[] string = line.split(",");
            for(Measurement measurement: measurements){
                if (Integer.parseInt(string[0]) == measurement.getId()){
                    measurement.setTime(Integer.parseInt(string[1]));
                }
            }
            line = br.readLine();
        }
        //System.out.println(measurements);
        return  measurements;
    }

    public List<Measurement> calculateTimeFaultBin(List<Measurement> measurements) {
            for(Measurement measurement: measurements){
                    measurement.setTime(measurement.getId()*1000-1000);
                }
        //System.out.println(measurements);
        return  measurements;
    }

}
