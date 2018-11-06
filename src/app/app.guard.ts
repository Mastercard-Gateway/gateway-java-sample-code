import { Injectable } from '@angular/core';
import { Router, RouterStateSnapshot, ActivatedRouteSnapshot, CanActivate } from '@angular/router';

/*
* Class to guard some endpoint. Usually, you check for a state e.g. is a user logged in
* and return true to allow request to proceed or not.
*/
@Injectable()
export class AppGuard implements CanActivate {

    constructor(
        private router: Router
    ) {}

    /*
    * Main guard implementation to do the check.
    */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const isUserLoggedIn = true;

        return isUserLoggedIn;
    }
}
