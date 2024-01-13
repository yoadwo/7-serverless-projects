import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Alarms } from '../alarms';

@Injectable({
  providedIn: 'root'  
})
export class ReminderHttpService {
  private baseUrl: string = 'https://0lxyu1lckj.execute-api.us-east-1.amazonaws.com';

  constructor(private http: HttpClient) { }

  get(userId: string): Observable<Alarms> {
    const url = `${this.baseUrl}/reminder?userId=${userId}`;
    return this.http.get<Alarms>(url);
  }

  set(userId: string, message: string, expireOn: Date, messageType: string): Observable<any> {
    const url = `${this.baseUrl}/reminder`;
    const payload = { userId, message, expireOn };
    return this.http.post(url, payload);
  }
}
