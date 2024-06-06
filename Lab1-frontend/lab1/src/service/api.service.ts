import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

import { BACKEND_URL } from '../app/config';


@Injectable({
  providedIn: 'root'
})

export class ApiService {

  constructor(private http: HttpClient) { }

  getObject(): Observable<any> {
    return this.http.get(`${BACKEND_URL}/object`);
  }

  updateObject(formData: FormData): Observable<any> {
    return this.http.put(`${BACKEND_URL}/object`, formData);
  }

}
