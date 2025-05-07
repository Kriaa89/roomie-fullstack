import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { Role } from '../../../models/role.model';
import { RoomService } from '../../../services/room.service';
import { Room } from '../../../models/room.model';

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
    private roomService: RoomService,
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
      this.isHost = currentUser.role === Role.ROOMMATE_HOST;
      this.isRenter = currentUser.role === Role.RENTER;
    }
  }

  private loadRoom(roomId: number): void {
    this.roomService.getRoomById(roomId).subscribe({
      next: (room) => {
        this.room = room;
        this.loading = false;

        // Process images
        if (room.roomImages && typeof room.roomImages === 'string') {
          this.imageUrls = room.roomImages.split(',').map((url: string) => url.trim());
        } else if (room.photos && typeof room.photos === 'string') {
          this.imageUrls = room.photos.split(',').map((url: string) => url.trim());
        } else {
          this.imageUrls = [];
        }

        // Check if current user is the host of this room
        if (this.isHost && this.authService.currentUserValue) {
          const userId = this.authService.currentUserValue.userId;
          this.isHost = room.host.id === userId;
        }
      },
      error: (error) => {
        this.error = 'Failed to load room details';
        this.loading = false;
        console.error('Error loading room:', error);
      }
    });
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
    if (this.room && this.room.host) {
      this.router.navigate(['/renter/visits/new'], {
        queryParams: { roommateHostId: this.room.host.id }
      });
    }
  }

  // Navigate to host profile
  viewHostProfile(): void {
    if (this.room && this.room.host) {
      this.router.navigate(['/host-profile', this.room.host.id]);
    }
  }
}
