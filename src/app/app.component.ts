import { Component, OnInit, Renderer2 } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/*
* Main app component that houses all views.
*/
@Component({
    selector: 'app-comp',
    templateUrl: './app.component.html'
})

export class AppComponent implements OnInit {

    store: any;

    constructor(
        private route: ActivatedRoute
    ) {}

    ngOnInit() {}

}
