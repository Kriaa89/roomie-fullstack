import { User } from './user.model';
import { RoommateHostProfile } from './profile.model';
import { Property } from './property.model';

export enum SwipeAction {
  LIKE = 'LIKE',
  DISLIKE = 'DISLIKE'
}

export enum SwipeType {
  RENTER_TO_HOST = 'RENTER_TO_HOST',
  HOST_TO_RENTER = 'HOST_TO_RENTER',
  RENTER_TO_PROPERTY = 'RENTER_TO_PROPERTY'
}

export interface SwipeLike {
  id: number;
  swiper: User;
  action: SwipeAction;
  swipeType: SwipeType;
  targetHostProfile?: RoommateHostProfile;
  targetProperty?: Property;
  targetRenterProfile?: User;
  createdAt: string;
  updatedAt: string;
  isMatch: boolean;
}

export interface SwipeLikeRequest {
  action: SwipeAction;
  swipeType: SwipeType;
  targetHostProfileId?: number;
  targetPropertyId?: number;
  targetRenterProfileId?: number;
}

export interface Match {
  id: number;
  renter: User;
  host?: User;
  owner?: User;
  property?: Property;
  createdAt: string;
  updatedAt: string;
}
