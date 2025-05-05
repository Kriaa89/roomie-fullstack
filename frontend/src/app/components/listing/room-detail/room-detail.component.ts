import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';

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
  hostProfileId: number;
  createdAt: string;
  updatedAt: string;
}

@Component({
  selector: 'app-room-detail',
  standalone: true,
  imports: [CommonModule, RouterModule],
  templateUrl: './room-detail.component.html',
  styleUrls: ['./room-detail.component.css']
})
export class RoomDetailComponent implements OnInit {
  room: Room | null = null;
  loading = true;
  error = '';
  currentImageIndex = 0;
  imageUrls: string[] = [];
  isHost = false;
  isRenter = false;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient,
    private authService: AuthService
  ) { }

  ngOnInit(): void {
    this.route.params.subscribe(params => {
      const roomId = +params['id']; // Convert to number
      if (roomId) {
        this.loadRoom(roomId);
      } else {
        this.error = 'Invalid room ID';
        this.loading = false;
      }
    });

    // Check user role
    const currentUser = this.authService.currentUserValue;
    if (currentUser) {
      this.isHost = currentUser.roles.includes(Role.ROOMMATE_HOST);
      this.isRenter = currentUser.roles.includes(Role.RENTER);
    }
  }

  private loadRoom(roomId: number): void {
    this.getRoom(roomId).subscribe({
      next: (room) => {
        this.room = room;
        this.loading = false;

        // Process images
        if (room.images) {
          this.imageUrls = room.images.split(',').map(url => url.trim());
        }

        // Check if current user is the host of this room
        if (this.isHost && this.authService.currentUserValue) {
          const userId = this.authService.currentUserValue.userId;
          this.isHost = room.hostId === userId;
        }
      },
      error: (error) => {
        this.error = 'Failed to load room details';
        this.loading = false;
        console.error('Error loading room:', error);
      }
    });
  }

  // Method to get room from the API
  private getRoom(roomId: number): Observable<Room> {
    return this.http.get<Room>(`${environment.apiUrl}/rooms/${roomId}`);
  }

  // Image navigation methods
  nextImage(): void {
    if (this.imageUrls.length > 1) {
      this.currentImageIndex = (this.currentImageIndex + 1) % this.imageUrls.length;
    }
  }

  prevImage(): void {
    if (this.imageUrls.length > 1) {
      this.currentImageIndex = (this.currentImageIndex - 1 + this.imageUrls.length) % this.imageUrls.length;
    }
  }

  // Navigate to edit page (for hosts)
  editRoom(): void {
    if (this.room) {
      this.router.navigate(['/host/rooms/edit', this.room.id]);
    }
  }

  // Navigate to visit request form (for renters)
  requestVisit(): void {
    if (this.room) {
      this.router.navigate(['/renter/visits/new'], {
        queryParams: { roommateHostId: this.room.hostProfileId }
      });
    }
  }

  // Navigate to host profile
  viewHostProfile(): void {
    if (this.room) {
      this.router.navigate(['/host-profile', this.room.hostProfileId]);
    }
  }
}
