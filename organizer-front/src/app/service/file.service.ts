import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL, httpOptions } from './service-utils';
import { File } from '../model/file';
import { EventFile } from '../model/event';
import { NoteFile } from '../model/note';
import { TaskFile } from '../model/task';

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

  getFileById(id: number): Observable<HttpResponse<File>> {
    return this._http.get<HttpResponse<File>>(`${FILE_URL}/${id}`, httpOptions);
  }

  updateEvent(event: EventFile): Observable<HttpResponse<EventFile>> {
    return this._http.put<HttpResponse<EventFile>>(FILE_URL + '/event', event, httpOptions);
  }

  updateNote(note: NoteFile): Observable<HttpResponse<NoteFile>> {
    return this._http.put<HttpResponse<NoteFile>>(FILE_URL + '/note', note, httpOptions);
  }

  updateTask(task: TaskFile): Observable<HttpResponse<TaskFile>> {
    return this._http.put<HttpResponse<TaskFile>>(FILE_URL + '/task', task, httpOptions);
  }
}
