import { Routes } from '@angular/router';
import { GetAlarmsComponent } from './get-alarms/get-alarms.component';
import { SetAlarmsComponent } from './set-alarms/set-alarms.component';

export const routes: Routes = [
    { path: 'get', component: GetAlarmsComponent },
    { path: 'set', component: SetAlarmsComponent },
    { path: '', redirectTo: '/get', pathMatch: 'full' },
];
