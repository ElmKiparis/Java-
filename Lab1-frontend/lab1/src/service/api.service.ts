import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { BACKEND_URL } from '../app/config';


@Injectable({
  providedIn: 'root'
})

export class ApiService {

  constructor(private http: HttpClient) { }

  getObjects(): Observable<any> {
    return this.http.get(`${BACKEND_URL}/persons`);
  }

  addObject(person: Object): Observable<any> {
    return this.http.post(`${BACKEND_URL}/persons`, person);
  }

  uploadAvatar(id: Number, formData: FormData): Observable<any> {
    return this.http.post(`${BACKEND_URL}/person/${id}/upload`, formData);
  }

  updateObject(id: Number, person: Object): Observable<any> {
    return this.http.put(`${BACKEND_URL}/persons/${id}`, person);
  }

  deleteObject(id: Number): Observable<any> {
    return this.http.delete(`${BACKEND_URL}/persons/${id}`);
  }

}
