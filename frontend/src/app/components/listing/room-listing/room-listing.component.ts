import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { RoomService } from '../../../services/room.service';
import { Room } from '../../../models/room.model';

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

  constructor(private roomService: RoomService) { }

  ngOnInit(): void {
    this.loadRooms();
  }

  private loadRooms(): void {
    this.roomService.getRooms().subscribe({
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

  // Method to filter rooms (can be expanded later)
  filterRooms(searchTerm: string): void {
    if (!searchTerm) {
      this.loadRooms();
      return;
    }

    // Simple client-side filtering
    this.roomService.getRooms().subscribe({
      next: (rooms) => {
        this.rooms = rooms.filter(room =>
          room.title.toLowerCase().includes(searchTerm.toLowerCase()) ||
          room.city.toLowerCase().includes(searchTerm.toLowerCase()) ||
          room.description.toLowerCase().includes(searchTerm.toLowerCase()) ||
          (room.hostName && room.hostName.toLowerCase().includes(searchTerm.toLowerCase()))
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
