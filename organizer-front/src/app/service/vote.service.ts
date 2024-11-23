import { HttpClient, HttpHeaders, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { API_URL, httpOptions } from './service-utils';
import { Vote } from '../model/vote';

const VOTE_URL = API_URL + 'votes'

@Injectable({
  providedIn: 'root'
})
export class VoteService {

  constructor(private readonly _http: HttpClient) { }

  getVotesByEventDateId(id: number): Observable<HttpResponse<Vote[]>> {
    return this._http.get<HttpResponse<Vote[]>>(`${VOTE_URL}/ed/${id}`, httpOptions);
  }

  getCurrentUserVoteByEventDateId(id: number): Observable<HttpResponse<Vote>> {
    return this._http.get<HttpResponse<Vote>>(`${VOTE_URL}/myvote/${id}`, httpOptions);
  }

  createVote(vote: Vote): Observable<HttpResponse<Vote>> {
    return this._http.post<HttpResponse<Vote>>(VOTE_URL, vote, httpOptions);
  }

  deleteVote(id: number): Observable<HttpResponse<null>> {
    return this._http.delete<HttpResponse<null>>(`${VOTE_URL}/${id}`, httpOptions);
  }
}
