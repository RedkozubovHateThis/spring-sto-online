import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import { UserService } from './user.service';

@Injectable()
export class ServiceWorkResponseService {

  constructor(private http: HttpClient, private router: Router, private userService: UserService) { }

  updatePrice(documentId: number, serviceWorkId: number, byPrice: boolean, price: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/documents/${documentId}/serviceWork/${serviceWorkId}/price`, null, {
      headers,
      params: {
        byPrice: byPrice.toString(),
        price: price.toString()
      }
    } );
  }

  updateState(documentId: number, documentOutHeaderId: number, state: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/documents/${documentId}/documentOutHeader/${documentOutHeaderId}/state`, null, {
      headers,
      params: {
        state: state.toString()
      }
    } );
  }

  updateUser(documentId: number, documentOutHeaderId: number, userId: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/documents/${documentId}/documentOutHeader/${documentOutHeaderId}/user`, null, {
      headers,
      params: {
        userId: userId.toString()
      }
    } );
  }

  updateManager(documentId: number, documentOutHeaderId: number, managerId: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/documents/${documentId}/documentOutHeader/${documentOutHeaderId}/manager`, null, {
      headers,
      params: {
        managerId: managerId.toString()
      }
    } );
  }

  updateCost(documentId: number, serviceGoodsAddonId: number, cost: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.userService.getApiUrl()}secured/documents/${documentId}/serviceGoodsAddon/${serviceGoodsAddonId}/cost`, null, {
      headers,
      params: {
        cost: cost.toString()
      }
    } );
  }

}
