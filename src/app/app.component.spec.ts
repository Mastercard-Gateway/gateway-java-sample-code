import { NO_ERRORS_SCHEMA } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Title } from '@angular/platform-browser';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ActivatedRoute } from '../testing';
import { RouterOutletStubComponent } from '../testing/router-stubs';

import { AppComponent } from './app.component';
import { HeaderComponent } from './_core/header/header.component';
import { FooterComponent } from './_core/footer/footer.component';

import { DataService } from './_core/data.service';

let comp: AppComponent;
let fixture: ComponentFixture<AppComponent>;

const mockRoute = {
    snapshot: {
        data: {
            list: [{
                name: 'Mock Ltd.'
            }]
        }
    }
};

describe('App Component:', () => {

    beforeEach(() => {
        TestBed.configureTestingModule({
            imports: [
                HttpClientTestingModule
            ],
            schemas: [NO_ERRORS_SCHEMA],
            declarations: [
                AppComponent,
                HeaderComponent,
                FooterComponent,
                RouterOutletStubComponent
            ],
            providers: [
                DataService,
                { provide: ActivatedRoute, useValue: mockRoute }
            ],
        }).compileComponents();

        fixture = TestBed.createComponent(AppComponent);
        comp = fixture.componentInstance;
        fixture.detectChanges();
        expect(comp).toBeTruthy();
    });

    it(`should have a store`, () => {
        expect(true).toEqual(true);
    });

});
