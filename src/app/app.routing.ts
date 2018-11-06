import { NgModule } from '@angular/core';
import { RouterModule, Router, Routes, NavigationEnd } from '@angular/router';

import { AppComponent } from './app.component';
import { AppGuard } from './app.guard';

import { DataResolver } from './_core/data.resolver';

/*
* Application routing that renders components for certain URLs.
*
* Ref: https://angular.io/guide/router
*/
const routes: Routes = [

    // Need common data for all pages.
    { path: '', component: AppComponent, resolve: {data: DataResolver}, children: [

        // Authenticated access & resolve user details
        { path: '', loadChildren: './home/home.module#HomeModule', canActivate: [AppGuard]}
    ]},

    // 404 URLs fall back to orders list
    { path: '**', redirectTo: '' } // otherwise redirect to home
];

@NgModule({
  imports: [ RouterModule.forRoot(routes) ],
  exports: [ RouterModule ]
})

export class AppRoutingModule {

    constructor(
        private router: Router
    ) {
        router.events.subscribe(event => {
            // React to events

            if (event instanceof NavigationEnd) {
                // Do something
            }
        });
    }
}
