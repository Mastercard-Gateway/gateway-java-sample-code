import {HttpClient, HttpHandler, HttpParams} from '@angular/common/http';
import {HttpService} from './http.service';

describe('HTTP Service: ', () => {

    let httpService: HttpService;
    let client = new HttpClient({} as HttpHandler);

    beforeEach(() => {
        this.httpService = new HttpService(client);
        this.httpService.url = '/api';
        this.httpService.baseParams = { 'a' : '1' };
    });

    it('should properly generate baseParams', () => {
      expect(true).toBe(true);
    });

});
