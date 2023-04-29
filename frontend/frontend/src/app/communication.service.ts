import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";
import {Measurement} from "./data/data/data.component";

@Injectable({
  providedIn: 'root'
})
export class CommunicationService {

  measurements: Measurement[] = []
  arr: [[]]= [[]]
  constructor(private httpClient: HttpClient) { }

  // @ts-ignore
  getData(): Observable<Measurement[]>{
    return this.httpClient.get<Measurement[]>("http://localhost:9005/data/get")
  }

  getGraph(): Observable<[[]]>{
    return this.httpClient.get<[[]]>("http://localhost:9005/data/getgraph")
  }

}
