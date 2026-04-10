import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { CommitteeDashboardComponent } from './components/committee-dashboard/committee-dashboard.component';
import { ApproverDashboardComponent } from './components/approver-dashboard/approver-dashboard.component';

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'committee-dashboard', component: CommitteeDashboardComponent },
  { path: 'approver-dashboard', component: ApproverDashboardComponent },
  { path: '', redirectTo: '/login', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
