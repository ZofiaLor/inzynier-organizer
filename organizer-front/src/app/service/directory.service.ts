import { Injectable } from '@angular/core';
import { API_URL, httpOptions } from './service-utils';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Directory } from '../model/directory';
import { Observable } from 'rxjs';

const DIR_URL = API_URL + 'directories';

@Injectable({
  providedIn: 'root'
})
export class DirectoryService {

  constructor(private readonly _http: HttpClient) { }

  getCurrentUsersDirs(): Observable<HttpResponse<Directory[]>> {
    return this._http.get<HttpResponse<Directory[]>>(DIR_URL + '/mydirs', httpOptions);
  }

  getCurrentUsersBaseDirs(): Observable<HttpResponse<Directory[]>> {
    return this._http.get<HttpResponse<Directory[]>>(DIR_URL + '/basedirs', httpOptions);
  }

  getDirsByParentId(id: number): Observable<HttpResponse<Directory[]>> {
    return this._http.get<HttpResponse<Directory[]>>(`${DIR_URL}/subdirs/${id}`, httpOptions);
  }

  getDirById(id: number): Observable<HttpResponse<Directory>> {
    return this._http.get<HttpResponse<Directory>>(`${DIR_URL}/${id}`, httpOptions);
  }
}