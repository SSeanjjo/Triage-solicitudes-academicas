import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Rol } from '../models/user.model';

export const roleGuard = (allowedRoles: Rol[]): CanActivateFn => {
  return () => {
    const auth = inject(AuthService);
    const router = inject(Router);
    if (!auth.isAuthenticated()) return router.createUrlTree(['/login']);
    if (auth.hasRole(...allowedRoles)) return true;
    return router.createUrlTree(['/dashboard']);
  };
};
