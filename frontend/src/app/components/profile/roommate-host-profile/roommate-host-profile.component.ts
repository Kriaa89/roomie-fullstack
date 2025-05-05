import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../services/auth.service';
import { UserService } from '../../../services/user.service';
import { ProfileService } from '../../../services/profile.service';
import { User } from '../../../models/user.model';
import { RoommateHostProfile, RoommateHostProfileUpdateRequest, Skill, LifestylePreference } from '../../../models/profile.model';

@Component({
  selector: 'app-roommate-host-profile',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './roommate-host-profile.component.html',
  styleUrls: ['./roommate-host-profile.component.css']
})
export class RoommateHostProfileComponent implements OnInit {
  currentUser: User | null = null;
  profile: RoommateHostProfile | null = null;
  profileForm: FormGroup;
  loading = {
    user: true,
    profile: true,
    submit: false
  };
  error = {
    user: '',
    profile: '',
    submit: ''
  };
  success = '';
  editMode = false;

  // Enums for dropdowns
  skills = Object.values(Skill);
  lifestylePreferences = Object.values(LifestylePreference);

  constructor(
    private authService: AuthService,
    private userService: UserService,
    private profileService: ProfileService,
    private formBuilder: FormBuilder,
    private router: Router
  ) {
    this.profileForm = this.formBuilder.group({
      bio: ['', [Validators.required, Validators.maxLength(500)]],
      city: ['', Validators.required],
      profilePicture: [''],
      skillsOffered: [[], Validators.required],
      lifestylePreferences: [[], Validators.required],
      visibility: [true]
    });
  }

  ngOnInit(): void {
    this.loadUserData();
  }

  private loadUserData(): void {
    this.userService.getCurrentUser().subscribe({
      next: (user) => {
        this.currentUser = user;
        this.loading.user = false;

        if (user && user.id) {
          this.loadProfile(user.id);
        }
      },
      error: (error) => {
        this.error.user = 'Failed to load user data';
        this.loading.user = false;
        console.error('Error loading user data:', error);
      }
    });
  }

  private loadProfile(userId: number): void {
    this.profileService.getRoommateHostProfile(userId).subscribe({
      next: (profile) => {
        this.profile = profile;
        this.loading.profile = false;

        // Populate form with profile data
        this.profileForm.patchValue({
          bio: profile.bio,
          city: profile.city,
          profilePicture: profile.profilePicture,
          skillsOffered: profile.skillsOffered,
          lifestylePreferences: profile.lifestylePreferences,
          visibility: profile.visibility
        });
      },
      error: (error) => {
        // If profile doesn't exist yet, it's not an error
        if (error.status === 404) {
          this.loading.profile = false;
          this.editMode = true; // Enable edit mode to create a new profile
        } else {
          this.error.profile = 'Failed to load profile data';
          this.loading.profile = false;
          console.error('Error loading profile data:', error);
        }
      }
    });
  }

  toggleEditMode(): void {
    this.editMode = !this.editMode;

    if (!this.editMode && this.profile) {
      // Reset form to original values when canceling edit
      this.profileForm.patchValue({
        bio: this.profile.bio,
        city: this.profile.city,
        profilePicture: this.profile.profilePicture,
        skillsOffered: this.profile.skillsOffered,
        lifestylePreferences: this.profile.lifestylePreferences,
        visibility: this.profile.visibility
      });
    }
  }

  onSubmit(): void {
    if (this.profileForm.invalid) {
      return;
    }

    this.loading.submit = true;
    this.error.submit = '';
    this.success = '';

    const profileData: RoommateHostProfileUpdateRequest = {
      bio: this.profileForm.value.bio,
      city: this.profileForm.value.city,
      profilePicture: this.profileForm.value.profilePicture,
      skillsOffered: this.profileForm.value.skillsOffered,
      lifestylePreferences: this.profileForm.value.lifestylePreferences,
      visibility: this.profileForm.value.visibility
    };

    if (this.profile) {
      // Update existing profile
      this.profileService.updateRoommateHostProfile(this.currentUser!.id, profileData).subscribe({
        next: (updatedProfile) => {
          this.profile = updatedProfile;
          this.loading.submit = false;
          this.success = 'Profile updated successfully';
          this.editMode = false;
        },
        error: (error) => {
          this.error.submit = 'Failed to update profile';
          this.loading.submit = false;
          console.error('Error updating profile:', error);
        }
      });
    } else {
      // Create new profile
      this.profileService.createRoommateHostProfile(profileData as any).subscribe({
        next: (newProfile) => {
          this.profile = newProfile;
          this.loading.submit = false;
          this.success = 'Profile created successfully';
          this.editMode = false;
        },
        error: (error) => {
          this.error.submit = 'Failed to create profile';
          this.loading.submit = false;
          console.error('Error creating profile:', error);
        }
      });
    }
  }

  // Helper methods for form controls
  isSkillSelected(skill: Skill): boolean {
    return this.profileForm.value.skillsOffered?.includes(skill) || false;
  }

  toggleSkill(skill: Skill): void {
    const currentSkills = [...(this.profileForm.value.skillsOffered || [])];
    const index = currentSkills.indexOf(skill);

    if (index === -1) {
      currentSkills.push(skill);
    } else {
      currentSkills.splice(index, 1);
    }

    this.profileForm.patchValue({ skillsOffered: currentSkills });
  }

  isLifestyleSelected(preference: LifestylePreference): boolean {
    return this.profileForm.value.lifestylePreferences?.includes(preference) || false;
  }

  toggleLifestyle(preference: LifestylePreference): void {
    const currentPreferences = [...(this.profileForm.value.lifestylePreferences || [])];
    const index = currentPreferences.indexOf(preference);

    if (index === -1) {
      currentPreferences.push(preference);
    } else {
      currentPreferences.splice(index, 1);
    }

    this.profileForm.patchValue({ lifestylePreferences: currentPreferences });
  }
}
