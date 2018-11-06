import { BrowserModule, Title } from '@angular/platform-browser';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { NgModule } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { RootComponent } from './root.component';
import { AppComponent } from './app.component';
import { AppRoutingModule } from './app.routing';

import { InMemoryWebApiModule } from 'angular-in-memory-web-api';
import { InMemoryDataService } from '../db/in-memory-data.service';

import { AppInterceptor } from './_core/app.interceptor';
import { FooterComponent } from './_core/footer/footer.component';
import { HeaderComponent } from './_core/header/header.component';
import { DataService } from './_core/data.service';
import { DataResolver } from './_core/data.resolver';

import { environment } from '../environments/environment';
import { AppGuard } from './app.guard';

/*
* Main module for the app that boostraps everything.
*/
@NgModule({
    imports: [
        BrowserModule,
        HttpClientModule,
        FormsModule,
        AppRoutingModule,
        BrowserAnimationsModule,
        InMemoryWebApiModule.forRoot(InMemoryDataService, { dataEncapsulation: false }) // Remove to use real HTTP calls
    ],
    declarations: [
        RootComponent,
        HeaderComponent,
        AppComponent,
        FooterComponent,
    ],
    providers: [
        AppGuard,
        DataService, DataResolver,
        {
            provide: HTTP_INTERCEPTORS,
            useClass: AppInterceptor,
            multi: true,
        }
    ],
    bootstrap: [RootComponent]
})

export class AppModule { }
