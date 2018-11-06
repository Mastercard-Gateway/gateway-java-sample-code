import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

import {HttpService} from './http.service';

/*
* Typical Service for CRUD operations.
*/
@Injectable()
export class DataService extends HttpService {

    private domain: any;

    constructor(
        public http: HttpClient
    ) {
        super(http);
        this.operation = 'data';
        this.url = `/directApi/${this.apiVersion}/${this.operation}`;
    }

    // Prevent usage
    update(domain: any): Observable<any> {
        throw new Error('Domain udpate not permitted.');
    }

    // Prevent usage
    delete(apiId: string): Observable<any> {
        throw new Error('Domain deletion not permitted.');
    }

    // -----------------
    // GETTER & SETTERS
    // -----------------
    getDomain() {
        return this.domain;
    }

    setDomain(domain: any) {
        this.domain = domain;
    }

}
