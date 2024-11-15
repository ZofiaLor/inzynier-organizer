import { Injectable } from '@angular/core';
import { API_URL, httpOptions } from './service-utils';
import { HttpClient, HttpResponse } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AccessDir } from '../model/access-dir';
import { AccessFile } from '../model/access-file';

const AD_URL = API_URL + 'ad';
const AF_URL = API_URL + 'af';

@Injectable({
  providedIn: 'root'
})
export class AccessService {

  constructor(private readonly _http: HttpClient) { }

  getADsByUserId(userId: number): Observable<HttpResponse<AccessDir[]>> {
    return this._http.get<HttpResponse<AccessDir[]>>(`${AD_URL}/user/${userId}`, httpOptions);
  }

  getAFsByUserId(userId: number): Observable<HttpResponse<AccessFile[]>> {
    return this._http.get<HttpResponse<AccessFile[]>>(`${AF_URL}/user/${userId}`, httpOptions);
  }

  getADsByDirId(dirId: number): Observable<HttpResponse<AccessDir[]>> {
    return this._http.get<HttpResponse<AccessDir[]>>(`${AD_URL}/dir/${dirId}`, httpOptions);
  }

  getAFsByFileId(fileId: number): Observable<HttpResponse<AccessFile[]>> {
    return this._http.get<HttpResponse<AccessFile[]>>(`${AF_URL}/file/${fileId}`, httpOptions);
  }

  getAD(userId: number, dirId: number): Observable<HttpResponse<AccessDir>> {
    return this._http.get<HttpResponse<AccessDir>>(`${AD_URL}/${userId}/${dirId}`, httpOptions);
  }

  getAF(userId: number, fileId: number): Observable<HttpResponse<AccessFile>> {
    return this._http.get<HttpResponse<AccessFile>>(`${AF_URL}/${userId}/${fileId}`, httpOptions);
  }

  modifyAD(ad: AccessDir): Observable<HttpResponse<AccessDir>> {
    return this._http.post<HttpResponse<AccessDir>>(AD_URL, ad, httpOptions);
  }

  modifyAF(af: AccessFile): Observable<HttpResponse<AccessFile>> {
    return this._http.post<HttpResponse<AccessFile>>(AF_URL, af, httpOptions);
  }

  deleteAD(userId: number, dirId: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${AD_URL}/${userId}/${dirId}`, httpOptions);
  }

  deleteAF(userId: number, fileId: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${AF_URL}/${userId}/${fileId}`, httpOptions);
  }
}
