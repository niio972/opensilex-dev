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

import { UserCreationDTO } from '../model/userCreationDTO';

import { COLLECTION_FORMATS }  from '../variables';



@injectable()
export class UsersService {
    private basePath: string = 'https://localhost';

    constructor(@inject("IApiHttpClient") private httpClient: IHttpClient,
        @inject("IAPIConfiguration") private APIConfiguration: IAPIConfiguration ) {
        if(this.APIConfiguration.basePath)
            this.basePath = this.APIConfiguration.basePath;
    }
    /**
     * Create a user and return it&#39;s URI
     * 
     * @param body User creation informations
     
     */
    public create1(body?: UserCreationDTO, observe?: 'body', headers?: Headers): Observable<any>;
    public create1(body?: UserCreationDTO, observe?: 'response', headers?: Headers): Observable<HttpResponse<any>>;
    public create1(body?: UserCreationDTO, observe: any = 'body', headers: Headers = {}): Observable<any> {
        headers['Accept'] = 'application/json';
        headers['Content-Type'] = 'application/json';

        const response: Observable<HttpResponse<any>> = this.httpClient.post(`${this.basePath}/core/user/create`, body , headers);
        if (observe == 'body') {
               return response.map(httpResponse => <any>(httpResponse.response));
        }
        return response;
    }
}
