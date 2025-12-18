import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { NotificationService, Toast } from '../../services/notificacion.services';

@Component({
  selector: 'app-toast',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './toast.component.html',
  styleUrls: ['./toast.component.css']
})
export class ToastComponent {
  constructor(public notificationService: NotificationService) {}

  close(toast: Toast) {
    this.notificationService.remove(toast.id);
  }

  trackById(index: number, toast: Toast) {
    return toast.id;
  }
}