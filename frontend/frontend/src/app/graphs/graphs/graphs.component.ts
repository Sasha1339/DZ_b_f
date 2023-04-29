import {Component, OnInit} from '@angular/core';
import {CommunicationService} from "../../communication.service";
import {Measurement} from "../../data/data/data.component";

export interface DataObject{
  name: string,
  type: string,
  data: any[],
  animationDelay: any
}
@Component({
  selector: 'app-graphs',
  templateUrl: './graphs.component.html',
  styleUrls: ['./graphs.component.scss']
})
export class GraphsComponent {
options: any
  measurements: Measurement[] = []
  data: [[]] = [[]]
  dataForGraph: DataObject[] = []
  legendData: string[] = []
  constructor(private service: CommunicationService) {
    this.options = {
      legend: {
        data: this.legendData,
        align: 'left',
      },
      tooltip: {},
      xAxis: {
        data: this.data[0],
        silent: false,
        splitLine: {
          show: false,
        },

      },
      yAxis: {},
      series: this.dataForGraph,
      animationEasing: 'elasticOut',
      animationDelayUpdate: (idx: any) => idx * 5,
    };
    }



  get(){
    this.service.getGraph().subscribe((value) => {
      this.data = value
    })
    this.service.getData().subscribe((value) => {
      this.measurements = value
    })
    this.calculate()
    this.initGraph()

  }

  calculate(){
      for (let measurement of this.measurements){
        this.legendData.push(measurement.channel)
      }

      for (let i = 1; i < this.data.length; i++){
        this.dataForGraph.push(
          {
            name: this.legendData[i-1],
            animationDelay: (idx: any) => idx * 10 + 100*(i-1),
            data: this.data[i],
            type: "line"

          })
      }

  }


  initGraph(): void {
    this.options = {
      legend: {
        data: this.legendData,
        align: 'left',
      },
      tooltip: {},
      xAxis: {
        data: this.data[0],
        silent: false,
        splitLine: {
          show: false,
        },

      },
      yAxis: {},
      series: this.dataForGraph,
      animationEasing: 'elasticOut',
      animationDelayUpdate: (idx: any) => idx * 5,
    };
  }
}
