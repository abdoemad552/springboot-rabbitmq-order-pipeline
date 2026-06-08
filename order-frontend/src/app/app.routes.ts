import { Routes } from '@angular/router';
import {OrderFormComponent} from './components/order-form/order-form.component';
import {PipelineTrackerComponent} from './components/pipeline-tracker/pipeline-tracker.component';

export const routes: Routes = [
  {
    path: '',
    component: OrderFormComponent
  },
  {
    path: 'track/:orderId',
    component: PipelineTrackerComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
