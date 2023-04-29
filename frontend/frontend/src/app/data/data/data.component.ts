import { Component } from '@angular/core';
import {CommunicationService} from "../../communication.service";

export interface Measurement{
  id: number,
  numberChannel: number,
  channel: string,
  valueCurrent: number,
  time: number
}

@Component({
  selector: 'app-data',
  templateUrl: './data.component.html',
  styleUrls: ['./data.component.scss']
})
export class DataComponent {
  measurements: Measurement[] = []
  showFirst = true
  showSecond: boolean = false
  show= true;
  showS = false
  math: Math = Math

constructor(private service: CommunicationService) {
}



changeShow(){
    this.show = false
  setTimeout(()=>{
    this.showFirst = false
    this.service.getData().subscribe((value) => {
      this.measurements = value
    })

    this.showSecond = true
    setTimeout(() => {this.showS = true}, 300)
  }, 1000)


}


}
