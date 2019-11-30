/**
 * 
 * 
 *
 * 
 * 
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */
/* tslint:disable:no-unused-variable member-ordering */

import { Observable } from "rxjs/Observable";
import 'rxjs/add/operator/map';
import 'rxjs/add/operator/toPromise';
import IHttpClient from "../IHttpClient";
import { inject, injectable } from "inversify";
import { IAPIConfiguration } from "../IAPIConfiguration";
import { Headers } from "../Headers";
import HttpResponse from "../HttpResponse";

import { UserAuthenticationDTO } from '../model/userAuthenticationDTO';

import { COLLECTION_FORMATS }  from '../variables';



@injectable()
export class AuthenticationService {
    private basePath: string = 'https://localhost';

    constructor(@inject("IApiHttpClient") private httpClient: IHttpClient,
        @inject("IAPIConfiguration") private APIConfiguration: IAPIConfiguration ) {
        if(this.APIConfiguration.basePath)
            this.basePath = this.APIConfiguration.basePath;
    }
    /**
     * Authenticate a user and return an access token
     * 
     * @param body User authentication informations
     
     */
    public authenticate(body?: UserAuthenticationDTO, observe?: 'body', headers?: Headers): Observable<any>;
    public authenticate(body?: UserAuthenticationDTO, observe?: 'response', headers?: Headers): Observable<HttpResponse<any>>;
    public authenticate(body?: UserAuthenticationDTO, observe: any = 'body', headers: Headers = {}): Observable<any> {
        headers['Accept'] = 'application/json';
        headers['Content-Type'] = 'application/json';

        const response: Observable<HttpResponse<any>> = this.httpClient.post(`${this.basePath}/core/authenticate`, body , headers);
        if (observe == 'body') {
               return response.map(httpResponse => <any>(httpResponse.response));
        }
        return response;
    }
    /**
     * Logout by discarding a user token
     * 
     * @param Authorization2 Authentication token
     * @param Authorization 
     
     */
    public logout(Authorization2: string, Authorization?: string, observe?: 'body', headers?: Headers): Observable<any>;
    public logout(Authorization2: string, Authorization?: string, observe?: 'response', headers?: Headers): Observable<HttpResponse<any>>;
    public logout(Authorization2: string, Authorization?: string, observe: any = 'body', headers: Headers = {}): Observable<any> {
        if (!Authorization2){
            throw new Error('Required parameter Authorization2 was null or undefined when calling logout.');
        }

        if (Authorization) {
            headers['Authorization'] = String(Authorization);
        }

        if (Authorization2) {
            headers['Authorization'] = String(Authorization2);
        }

        headers['Accept'] = 'application/json';

        const response: Observable<HttpResponse<any>> = this.httpClient.post(`${this.basePath}/core/authenticate/logout`, headers);
        if (observe == 'body') {
               return response.map(httpResponse => <any>(httpResponse.response));
        }
        return response;
    }
}
