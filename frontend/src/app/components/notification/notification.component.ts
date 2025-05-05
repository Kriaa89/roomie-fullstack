import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { NotificationService } from '../../services/notification.service';
import { Notification } from '../../models/notification.model';

@Component({
  selector: 'app-notification',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.css']
})
export class NotificationComponent implements OnInit {
  notifications: Notification[] = [];
  loading = true;
  error = '';

  constructor(private notificationService: NotificationService) { }

  ngOnInit(): void {
    this.loadNotifications();
  }

  private loadNotifications(): void {
    this.notificationService.getMyNotifications().subscribe({
      next: (notifications) => {
        this.notifications = notifications;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load notifications';
        this.loading = false;
        console.error('Error loading notifications:', error);
      }
    });
  }

  markAsRead(notificationId: number): void {
    this.notificationService.markAsRead(notificationId).subscribe({
      next: (updatedNotification) => {
        // Update the notification in the local array
        const index = this.notifications.findIndex(n => n.id === notificationId);
        if (index !== -1) {
          this.notifications[index] = updatedNotification;
        }
      },
      error: (error) => {
        console.error('Error marking notification as read:', error);
      }
    });
  }

  markAllAsRead(): void {
    this.notificationService.markAllAsRead().subscribe({
      next: () => {
        // Update all notifications in the local array
        this.notifications = this.notifications.map(notification => {
          return { ...notification, read: true };
        });
      },
      error: (error) => {
        console.error('Error marking all notifications as read:', error);
      }
    });
  }

  deleteNotification(notificationId: number): void {
    this.notificationService.deleteNotification(notificationId).subscribe({
      next: () => {
        // Remove the notification from the local array
        this.notifications = this.notifications.filter(n => n.id !== notificationId);
      },
      error: (error) => {
        console.error('Error deleting notification:', error);
      }
    });
  }

  getNotificationIcon(type: string): string {
    switch (type) {
      case 'MATCH':
        return '❤️';
      case 'VISIT_REQUEST':
        return '🏠';
      case 'VISIT_RESPONSE':
        return '✅';
      case 'SYSTEM':
        return '🔔';
      case 'MESSAGE':
        return '✉️';
      default:
        return '📌';
    }
  }
}
