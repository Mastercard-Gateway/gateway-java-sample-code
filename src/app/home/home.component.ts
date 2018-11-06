import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/*
* Home component that renders the landing page of the main app.
* It is used to display the navigation controls and render children
* components and is never rendered itself directly.
*/
@Component({
    selector: 'app-home',
    templateUrl: './home.component.html',
    styleUrls: ['./home.scss']
})

export class HomeComponent implements OnInit {

    constructor(
        private route: ActivatedRoute
    ) {}

    /*
    * Init function
    */
    ngOnInit() {}

}
