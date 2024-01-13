import { Component } from '@angular/core';
import { FormGroup, FormControl, Validators } from '@angular/forms';
import { ReactiveFormsModule} from '@angular/forms';
import { ReminderHttpService } from '../reminder-http/reminder-http.service';


@Component({
  selector: 'app-set-alarms',
  standalone: true,
  imports: [ReactiveFormsModule],
  templateUrl: './set-alarms.component.html',
  styleUrl: './set-alarms.component.css'
})
export class SetAlarmsComponent {
  selectedProjectType: string;
  reminderForm = new FormGroup({
    userIdInput: new FormControl(null, Validators.required),
    messageInput: new FormControl(null, Validators.required),
    reminderTimeInput: new FormControl(null, Validators.required)
  });

  constructor(private reminderHttp: ReminderHttpService){
    this.selectedProjectType = '';
  }

  onSelected(value: string) {
    this.selectedProjectType = value;
  }

  onSubmit() {
    // TODO: Use EventEmitter with form value
    console.log(this.reminderForm.value);
    this.reminderHttp.set(
        this.reminderForm.value.userIdInput!,
        this.reminderForm.value.messageInput!,
        this.reminderForm.value.reminderTimeInput!,
        this.selectedProjectType)
      .subscribe((resp: any) => {
        alert(resp.message);
      })
  }

}
