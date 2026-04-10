import { Component } from '@angular/core';

@Component({
  selector: 'app-approver-dashboard',
  template: `
    <div class="dashboard-container">
      <h1>Approver Dashboard</h1>
      <p>Welcome to the Approver Dashboard</p>
    </div>
  `,
  styles: [`
    .dashboard-container {
      padding: 20px;
      text-align: center;
    }
    h1 {
      color: #333;
    }
  `]
})
export class ApproverDashboardComponent {
}
