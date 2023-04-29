import {Component, OnInit, OnDestroy, ViewEncapsulation} from '@angular/core';
import {Router} from "@angular/router";
import { Location } from '@angular/common';
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss'],
  encapsulation: ViewEncapsulation.None,

})
export class AppComponent {
  title = 'Визуализация неисправностей';
  constructor(private Location:Location) {
    if (this.Location.path() == '/home'){
      this.onHome()
    }else if(this.Location.path() == '/data'){
      this.onData()
    }else if(this.Location.path() == "/graphs"){
      this.onGraphs()
    }else{
      this.onHome()
    }
  }

  onHome(){
    document.body.style.background = '#53ea93';
  }
  onData(){
    document.body.style.background = '#8bbdd3';
  }
  onGraphs(){
    document.body.style.background = '#fcff9b';
  }

}
