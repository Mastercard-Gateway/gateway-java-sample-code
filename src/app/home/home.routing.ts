import { ModuleWithProviders } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';

import { HomeComponent } from './home.component';

const routes: Routes = [
    { path: '', component: HomeComponent, children: [

        // Define children modules here - need to build these out
        // Thoses template get injected into router-outlet of home component template
        // { path: 'child_url', loadChildren: '../child/child.module#ChildModule'},
    ]}
];

export const routing: ModuleWithProviders = RouterModule.forChild(routes);
