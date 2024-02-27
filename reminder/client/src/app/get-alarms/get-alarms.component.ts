import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';

import { AlarmGet } from '../models/alarms';
import { ReminderHttpService } from '../reminder-http/reminder-http.service';


@Component({
  standalone: true,
  imports:[FormsModule, CommonModule],
  selector: 'app-get-alarms',
  templateUrl: './get-alarms.component.html',
  styleUrl: './get-alarms.component.css'
})
export class GetAlarmsComponent implements OnInit {
  searchQuery: string;
  alarmData: AlarmGet[];
  alarmFiltered: AlarmGet[];

  constructor(private reminderHttp: ReminderHttpService){
    this.searchQuery = '';
    this.alarmData = [];
    this.alarmFiltered = this.alarmData;
  }

  ngOnInit(): void {
  }

  clearSearch() {
    this.searchQuery = '';
    this.alarmFiltered = this.alarmData;
  }

  search() {
    // Placeholder for search logic
    console.log('Searching for:', this.searchQuery);
    // You can implement actual search logic here
    //this.alarmFiltered = this.alarmData.filter(ad => ad.userId == this.searchQuery);
    this.reminderHttp.get(this.searchQuery).subscribe(alarms => {
      this.alarmFiltered = alarms.message;
    });
  }

}
