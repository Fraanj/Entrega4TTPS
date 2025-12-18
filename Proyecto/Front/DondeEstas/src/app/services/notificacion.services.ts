import { Injectable } from '@angular/core';

export interface Toast {
  id: number;
  type: 'success' | 'error' | 'warning' | 'info';
  title: string;
  message: string;
}

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  toasts: Toast[] = [];
  private nextId = 1;

  success(message: string, title: string = 'Éxito') {
    this.showToast(message, title, 'success');
  }

  error(message: string, title: string = 'Error') {
    this.showToast(message, title, 'error');
  }

  warning(message: string, title: string = 'Advertencia') {
    this.showToast(message, title, 'warning');
  }

  info(message: string, title: string = 'Información') {
    this.showToast(message, title, 'info');
  }

  private showToast(message: string, title: string, type: Toast['type']) {
    const toast: Toast = {
      id: this.nextId++,
      type,
      title,
      message
    };

    this.toasts.push(toast);

    // Auto-remover después de 5 segundos
    setTimeout(() => {
      this.remove(toast.id);
    }, 5000);

    console.log(`🔔 Toast: ${title} - ${message}`);
  }

  remove(id: number) {
    this.toasts = this.toasts.filter(t => t.id !== id);
  }

  clear() {
    this.toasts = [];
  }
}