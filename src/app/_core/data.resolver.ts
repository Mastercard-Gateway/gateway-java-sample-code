import { Injectable } from '@angular/core';
import { Resolve, ActivatedRouteSnapshot } from '@angular/router';

import { DataService } from './data.service';

/*
* Resolve some data.
*/
@Injectable()
export class DataResolver implements Resolve<any> {

    constructor(
        private dataService: DataService
    ) {}

    resolve(route: ActivatedRouteSnapshot) {
        return this.dataService.get('');
    }
}
