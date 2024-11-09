import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL, httpOptions } from './service-utils';

const FILE_URL = API_URL + 'files';

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

  getFilesInDirectory(id: number): Observable<HttpResponse<File[]>> {
    return this._http.get<HttpResponse<File[]>>(`${FILE_URL}/dir/${id}`, httpOptions);
  }
}
