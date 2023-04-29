import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import {HomeComponent} from "./home/home/home.component";
import {DataComponent} from "./data/data/data.component";
import {GraphsComponent} from "./graphs/graphs/graphs.component";

const routes: Routes = [
  {path: 'home', loadChildren: () => import('./home/home/home.module').then((item) => item.HomeModule )},
  {path: 'data', component: DataComponent},
  {path: 'graphs', component: GraphsComponent},
  {path: '', pathMatch: "full", redirectTo: "home"}
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
