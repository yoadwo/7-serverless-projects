import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { AlarmGet, Alarms } from '../models/alarms';
import { Response } from '../models/response';

@Injectable({
  providedIn: 'root'  
})
export class ReminderHttpService {
  private baseUrl: string = 'https://nptfllh460.execute-api.us-east-1.amazonaws.com/dev';

  constructor(private http: HttpClient) { }

  get(userId: string): Observable<Alarms> {
    const url = `${this.baseUrl}/reminders/${userId}`;
    return this.http.get<Response>(url).pipe(
      map((response: Response) => {
        // Assuming the server response contains an array of AlarmGet objects
        return {
          message: response.data as AlarmGet[] // Assuming response.data is an array of AlarmGet
        };
      })
    );
  }

  set(userId: string, message: string, expireOn: Date): Observable<AlarmGet> {
    const url = `${this.baseUrl}/reminders/${userId}`;
    const payload = { userId, message, expireOn };
    return this.http.post<Response>(url, payload).pipe(
      map((response: Response) => {
        // Assuming the server response contains an array of AlarmGet objects
        return response.data as AlarmGet;
      })
    );
  }
}
