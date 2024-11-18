import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL, httpOptions } from './service-utils';
import { EventDate } from '../model/event-date';

const EVDATE_URL = API_URL + 'ed';

@Injectable({
  providedIn: 'root'
})
export class EventDateService {

  constructor(private readonly _http: HttpClient) { }

  getEventDatesByEventId(id: number): Observable<HttpResponse<EventDate[]>> {
    return this._http.get<HttpResponse<EventDate[]>>(`${EVDATE_URL}?eventId=${id}`, httpOptions);
  }

  getEventDateById(id: number): Observable<HttpResponse<EventDate>> {
    return this._http.get<HttpResponse<EventDate>>(`${EVDATE_URL}/${id}`, httpOptions);
  }

  createEventDate(ed: EventDate): Observable<HttpResponse<EventDate>> {
    return this._http.post<HttpResponse<EventDate>>(EVDATE_URL, ed, httpOptions);
  }

  updateEventDate(ed: EventDate): Observable<HttpResponse<EventDate>> {
    return this._http.put<HttpResponse<EventDate>>(EVDATE_URL, ed, httpOptions);
  }

  deleteEventDate(id: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${EVDATE_URL}/${id}`, httpOptions);
  }
}
