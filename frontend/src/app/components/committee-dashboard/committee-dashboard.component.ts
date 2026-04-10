import { Component } from '@angular/core';

@Component({
  selector: 'app-committee-dashboard',
  template: `
    <div class="dashboard-container">
      <h1>Committee Dashboard</h1>
      <p>Welcome to the Committee Dashboard</p>
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
export class CommitteeDashboardComponent {
}
