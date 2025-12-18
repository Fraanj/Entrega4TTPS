import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
import { environment } from '../../environments/environment';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  constructor(private http: HttpClient) {}

  private getHeaders(useAuth: boolean = true): HttpHeaders {
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (useAuth) {
      const token = localStorage.getItem('accessToken');
      if (token) {
        headers = headers.set('Authorization', `Bearer ${token}`);
      }
    }

    return headers;
  }

  async get<T>(endpoint: string, useAuth: boolean = true): Promise<T> {
    const headers = this.getHeaders(useAuth);
    
    return firstValueFrom(
      this.http.get<T>(`${this.baseUrl}${endpoint}`, { headers })
    );
  }

  async post<T>(endpoint: string, data: any, useAuth: boolean = true): Promise<T> {
    const headers = this.getHeaders(useAuth);
    
    return firstValueFrom(
      this.http.post<T>(`${this.baseUrl}${endpoint}`, data, { headers })
    );
  }

  async put<T>(endpoint: string, data: any, useAuth: boolean = true): Promise<T> {
    const headers = this.getHeaders(useAuth);
    
    return firstValueFrom(
      this.http.put<T>(`${this.baseUrl}${endpoint}`, data, { headers })
    );
  }

  async delete<T>(endpoint: string, useAuth: boolean = true): Promise<T> {
    const headers = this.getHeaders(useAuth);
    
    return firstValueFrom(
      this.http.delete<T>(`${this.baseUrl}${endpoint}`, { headers })
    );
  }
}