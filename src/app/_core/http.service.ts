import {HttpClient, HttpParams} from '@angular/common/http';
import {Observable} from 'rxjs/Observable';

/*
* Base service for all API requests.
*/
export class HttpService {

  url: string;
  apiVersion = '47';
  operation: string;
  baseParams?: { [param: string]: string | string[] };

  constructor(protected http: HttpClient) {
  }

  private getUrl(apiId?: string): string {
    return `${this.url}${apiId ? '/' + apiId : ''}`;
  }

  private generateParams(extraParams?: object): HttpParams {
    return new HttpParams({
      fromObject: Object.assign({}, this.baseParams || {}, extraParams || {})
    });
  }

  list(): Observable<any> {
    return this.http.get(
      this.getUrl(),
      {params: this.generateParams()}
    );
  }

  listQuery(queryParams: object): Observable<any> {
    return this.http.get(
      this.getUrl(),
      {params: this.generateParams(queryParams)}
    );
  }

  get(apiId: string): Observable<any> {
    return this.http.get(
      this.getUrl(apiId),
      {params: this.generateParams()}
    );
  }

  delete(apiId: string): Observable<any> {
    return this.http.delete(
      this.getUrl(apiId),
      {params: this.generateParams()}
    );
  }

  create(body: any): Observable<any> {
    return this.http.post(
      this.getUrl(),
      JSON.stringify(body),
      {params: this.generateParams()}
    );
  }

  update(apiId: string, body: any): Observable<any> {
    return this.http.put(
      this.getUrl(apiId),
      JSON.stringify(body),
      {params: this.generateParams()}
    );
  }

}
