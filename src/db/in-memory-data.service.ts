import {
    InMemoryDbService,
    RequestInfo,
    ResponseOptions,
    STATUS,
    getStatusText
} from 'angular-in-memory-web-api';

/*
* Development in-memory datasource to provide canned JSON responses to all
* API requests.
*
* This should NOT BE USED in PRODUCTION.
*
* Ref: https://github.com/angular/in-memory-web-api
*/
export class InMemoryDataService implements InMemoryDbService {

    data = {
        response: '200'
    };

    /*
    * Function to load our JSON responses into the in-memory datasource.
    * Import and add any new respones as needed.
    */
    createDb() {
        return {
            data: this.data
        };
    }

    /*
    * Override default GET request handler as URL patterns are different than
    * the default.
    */
    get(reqInfo: RequestInfo) {
        return reqInfo.utils.createResponse$(() => {

            const apis = {
                '/directApi/47/data': this.data
            };

            const url = reqInfo.url;
            const data = apis[url];

            return this.setResponse(reqInfo, data);
        });
    }

    /*
    * Override default POST request handler as URL patterns are different than
    * the default.
    */
    post(reqInfo: RequestInfo) {
        return reqInfo.utils.createResponse$(() => {

            const apis = {
                '/directApi/47/data': {}
            };

            const data = apis[reqInfo.url];

            return this.setResponse(reqInfo, data);
        });
    }

    /*
    * Override default DELETE request handler as URL patterns are different than
    * the default.
    */
    delete(reqInfo: RequestInfo) {
        return reqInfo.utils.createResponse$(() => {

            const apis = {
                '/directApi/47/data': {}
            };

            const data = apis[reqInfo.url];

            return this.setResponse(reqInfo, data);
        });
    }

    /*
    * Helper function to return a response to the API request in the format
    * expected by the InMemoryDbService.
    */
    private setResponse(reqInfo: RequestInfo, data) {
        const dataEncapsulation = reqInfo.utils.getConfig().dataEncapsulation;
        const options: ResponseOptions = data ?
        {
            body: dataEncapsulation ? { data } : data,
            status: STATUS.OK
        } :
        {
            body: { error: `'Object' with not found` },
            status: STATUS.NOT_FOUND
        };

        return this.finishOptions(options, reqInfo);
    }

    private finishOptions(
        options: ResponseOptions,
        {headers, url}: RequestInfo
    ) {
        options.statusText = getStatusText(options.status);
        options.headers = headers;
        options.url = url;
        return options;
    }

}
