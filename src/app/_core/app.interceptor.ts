import { Injectable } from '@angular/core';
import { HttpEvent, HttpInterceptor, HttpHandler, HttpRequest } from '@angular/common/http';
import { Observable } from 'rxjs/Observable';
import 'rxjs/add/operator/finally';

@Injectable()
export class AppInterceptor implements HttpInterceptor {

	constructor() {}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		
		// Add common header to all requests
		const appReq = req.clone({
			setHeaders: {
				"X-Requested-With": "XMLHttpRequest"
			}, 
			withCredentials: true
		});

		return next.handle(appReq).finally(() => {
			// All requests finished - maybe hide loader
		});
	}
}
