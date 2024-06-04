import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ApiService {
  constructor(private http: HttpClient) { }

  getObject(): Observable<any> {
    return this.http.get('http://localhost:9090/lab1_war_exploded/object');
  }
}
