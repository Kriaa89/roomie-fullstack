import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

// Since we don't have a RoomService yet, we'll define a simple Room interface here
interface Room {
  id: number;
  title: string;
  description: string;
  price: string;
  location: string;
  images: string;
  amenities: string;
  availability: boolean;
  hostId: number;
  hostName: string;
  createdAt: string;
  updatedAt: string;
}

@Component({
  selector: 'app-room-listing',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './room-listing.component.html',
  styleUrls: ['./room-listing.component.css']
})
export class RoomListingComponent implements OnInit {
  rooms: Room[] = [];
  loading = true;
  error = '';

  constructor(private http: HttpClient) { }

  ngOnInit(): void {
    this.loadRooms();
  }

  private loadRooms(): void {
    this.getRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms;
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to load rooms';
        this.loading = false;
        console.error('Error loading rooms:', error);
      }
    });
  }

  // Method to get rooms from the API
  private getRooms(): Observable<Room[]> {
    return this.http.get<Room[]>(`${environment.apiUrl}/rooms`);
  }

  // Method to filter rooms (can be expanded later)
  filterRooms(searchTerm: string): void {
    if (!searchTerm) {
      this.loadRooms();
      return;
    }

    // Simple client-side filtering
    this.getRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms.filter(room =>
          room.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          room.location.toLowerCase().includes(searchTerm.toLowerCase()) ||
          room.description.toLowerCase().includes(searchTerm.toLowerCase())
        );
        this.loading = false;
      },
      error: (error) => {
        this.error = 'Failed to filter rooms';
        this.loading = false;
        console.error('Error filtering rooms:', error);
      }
    });
  }
}
