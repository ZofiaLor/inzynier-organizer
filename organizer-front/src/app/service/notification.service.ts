import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL, httpOptions } from './service-utils';
import { Notification } from '../model/notification';
import { HttpClient, HttpResponse } from '@angular/common/http';

const NOTIF_URL = API_URL + 'notifs';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {

  constructor(private readonly _http: HttpClient) { }

  getCurrentUsersNotifs(): Observable<HttpResponse<Notification[]>> {
    return this._http.get<HttpResponse<Notification[]>>(NOTIF_URL + '/mynotifs', httpOptions);
  }

  getCurrentUsersNotifsByRead(read: boolean): Observable<HttpResponse<Notification[]>> {
    return this._http.get<HttpResponse<Notification[]>>(`${NOTIF_URL}/mynotifs?read=${read}`, httpOptions);
  }

  getCurrentUsersNotifsByFile(fileId: number): Observable<HttpResponse<Notification[]>> {
    return this._http.get<HttpResponse<Notification[]>>(`${NOTIF_URL}/file/${fileId}`, httpOptions);
  }

  getNotifById(id: number): Observable<HttpResponse<Notification>> {
    return this._http.get<HttpResponse<Notification>>(`${NOTIF_URL}/${id}`, httpOptions);
  }

  createNotif(notif: Notification): Observable<HttpResponse<Notification>> {
    return this._http.post<HttpResponse<Notification>>(NOTIF_URL, notif, httpOptions);
  }

  updateNotif(notif: Notification): Observable<HttpResponse<Notification>> {
    return this._http.put<HttpResponse<Notification>>(NOTIF_URL, notif, httpOptions);
  }

  sendNotifs(): Observable<HttpResponse<null>> {
    return this._http.put<HttpResponse<null>>(NOTIF_URL + '/send', null, httpOptions);
  }

  deleteNotif(id: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${NOTIF_URL}/${id}`, httpOptions);
  }
}
