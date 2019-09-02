import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {Router} from "@angular/router";
import { UserService } from './user.service';

@Injectable()
export class ServiceWorkResponseService {

  constructor(private http: HttpClient, private router: Router, private userService:UserService) { }
  private baseUrl: string = 'http://localhost:8181/';

  updatePrice(documentId: number, serviceWorkId: number, byPrice: boolean, price: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.baseUrl}secured/documents/${documentId}/serviceWork/${serviceWorkId}/price`, null, {
      headers,
      params: {
        byPrice: byPrice.toString(),
        price: price.toString()
      }
    } );
  }

  updateState(documentId: number, documentOutHeaderId: number, state: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.baseUrl}secured/documents/${documentId}/documentOutHeader/${documentOutHeaderId}/state`, null, {
      headers,
      params: {
        state: state.toString()
      }
    } );
  }

  updateCost(documentId: number, serviceGoodsAddonId: number, cost: number) {
    const headers = this.userService.getHeaders();

    return this.http.put( `${this.baseUrl}secured/documents/${documentId}/serviceGoodsAddon/${serviceGoodsAddonId}/cost`, null, {
      headers,
      params: {
        cost: cost.toString()
      }
    } );
  }

}
