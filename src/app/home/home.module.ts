import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { routing } from './home.routing';
import { HomeComponent } from './home.component';

/*
* Module for home functionality.
*/
@NgModule({
imports: [CommonModule, routing],
declarations: [HomeComponent]
})

export class HomeModule {}
