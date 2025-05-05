import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Notification, NotificationUpdateRequest } from '../models/notification.model';
import { environment } from '../../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  private readonly API_URL = `${environment.apiUrl}/notifications`;

  constructor(private http: HttpClient) { }

  // Get all notifications for the current user
  getMyNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.API_URL}/my-notifications`);
  }

  // Get unread notifications for the current user
  getMyUnreadNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.API_URL}/my-notifications/unread`);
  }

  // Get notification by ID
  getNotificationById(id: number): Observable<Notification> {
    return this.http.get<Notification>(`${this.API_URL}/${id}`);
  }

  // Mark notification as read
  markAsRead(id: number): Observable<Notification> {
    const updateRequest: NotificationUpdateRequest = { read: true };
    return this.http.put<Notification>(`${this.API_URL}/${id}`, updateRequest);
  }

  // Mark all notifications as read
  markAllAsRead(): Observable<void> {
    return this.http.put<void>(`${this.API_URL}/mark-all-read`, {});
  }

  // Delete notification
  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${id}`);
  }

  // Delete all notifications
  deleteAllNotifications(): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/delete-all`);
  }
}
