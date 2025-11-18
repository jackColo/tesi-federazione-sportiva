import { inject } from '@angular/core';
import { Router, CanActivateFn } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Role } from '../../enums/role.enum';

export const roleGuard = (requiredRoles: Role[]): CanActivateFn => {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);

    if (!authService.isLoggedIn()) {
      router.navigate(['/auth/login']);
      return false;
    }

    const userRole = authService.userRole();

    if (!userRole) {
      router.navigate(['/dashboard']);
      return false;
    }

    const currentUserRole = userRole as Role;
    const isAuthorized = requiredRoles.includes(currentUserRole);

    if (isAuthorized) {
      return true;
    } else {
      router.navigate(['/dashboard']);
      return false;
    }
  };
};
