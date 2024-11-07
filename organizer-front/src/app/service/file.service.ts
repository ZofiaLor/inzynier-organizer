import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

const FILE_URL = 'http://localhost:8080/api/files';

const httpOptions = {
  headers: new HttpHeaders({ 'Content-Type': 'application/json'}),
  observe: 'response' as 'body',
  withCredentials: true
};

@Injectable({
  providedIn: 'root'
})
export class FileService {

  constructor(private readonly _http: HttpClient) { }

  getAllFiles(): Observable<HttpResponse<File[]>> {
    return this._http.get<HttpResponse<File[]>>(FILE_URL, httpOptions);
  }

  getCurrentUsersFiles(): Observable<HttpResponse<File[]>> {
    return this._http.get<HttpResponse<File[]>>(FILE_URL + '/myfiles', httpOptions);
  }
}
