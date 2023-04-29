import {Component, ElementRef, ViewChild} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule, FormGroup,FormBuilder, FormControl, FormArray, NgForm, Validators} from "@angular/forms";
import {timeout} from "rxjs";
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent {
  form: FormGroup;
  loading: boolean = false
  divDisplay = true
  divNewDisplay = false
  divTrans = false
  divNewTrans = true

  cfg = "Загрузить .cfg"
  dat = "Загрузить .dat"
  i = 0;
  onDisplay = false;
  serverUrl: string = "http://localhost:9005/data/sendFiles";
  valueCfg = '';
  constructor(private httpClient: HttpClient, private fb: FormBuilder) {
    this.form = this.fb.group({
      cfg: null,
      dat: null
    })
  }


  ngOnInit() {

  }

  changeCfgName(event: any): void{
   // this.cfg = (event.target as HTMLInputElement).value
    this.cfg = "Файл загружен";
    this.i++;
    if(this.i == 2){
      this.onDisplay = true
    }
if(event.target.files.length > 0){
  let file = event.target.files[0];
  this.form.get('cfg')?.setValue(file)
}
  }
  changeDatName(event: any): void{
    //this.dat = (event.target as HTMLInputElement).value
    this.dat = "Файл загружен"
    this.i++;
    if(this.i == 2){
      this.onDisplay = true
    }
    if(event.target.files.length > 0){
      let file = event.target.files[0];
      this.form.get('dat')?.setValue(file)
    }
  }

  private prepareSave(): any{
    let input = new FormData();
    input.append('cfg', this.form.get('cfg')?.value)
    input.append('dat', this.form.get('dat')?.value)
    return input;
  }


  sendFiles(){
  const formModel = this.prepareSave();
  this.loading = true;
  this.httpClient.post(this.serverUrl, formModel).pipe(timeout(3000)).subscribe((value) => console.log(value))
   this.divTrans = true
    setTimeout(()=> {
     this.changeNewDisplay()
   }, 300)

  }

  changeNewDisplay(){
    this.divDisplay = false
    this.divNewDisplay = true
    setTimeout(()=> {
      this.divNewTrans = false
    }, 300)

  }


}
