import { Injectable } from '@angular/core';
import { environment } from '../../environments/environment';
import { HTTP_CONFIG } from '../shared/config/http.config';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private baseUrl = environment.apiUrl;

  async get<T>(endpoint: string, useAuth: boolean = true): Promise<T> {
    const headers = useAuth ? HTTP_CONFIG.getAuthHeaders() : HTTP_CONFIG.headers;
    
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'GET',
      headers
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  async post<T>(endpoint: string, data: any, useAuth: boolean = true): Promise<T> {
    const headers = useAuth ? HTTP_CONFIG.getAuthHeaders() : HTTP_CONFIG.headers;
    
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'POST',
      headers,
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  async put<T>(endpoint: string, data: any, useAuth: boolean = true): Promise<T> {
    const headers = useAuth ? HTTP_CONFIG.getAuthHeaders() : HTTP_CONFIG.headers;
    
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'PUT',
      headers,
      body: JSON.stringify(data)
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }

  async delete<T>(endpoint: string, useAuth: boolean = true): Promise<T> {
    const headers = useAuth ? HTTP_CONFIG.getAuthHeaders() : HTTP_CONFIG.headers;
    
    const response = await fetch(`${this.baseUrl}${endpoint}`, {
      method: 'DELETE',
      headers
    });

    if (!response.ok) {
      throw new Error(`HTTP error! status: ${response.status}`);
    }

    return response.json();
  }
}