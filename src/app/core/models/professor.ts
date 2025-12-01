import { Role } from '../../enums/roles';

export interface Professor
{
    id?: number;
    name: string;
    lastname: string;
    email: string;
    password?: string;
    cel: string;
    photoUrl?: string;
    role?: Role;
}
  