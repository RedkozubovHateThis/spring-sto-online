import {VehicleResponse} from "./vehicleResponse";
import {ServiceWorkTotalResponse} from "./serviceWorkTotalResponse";
import {ServiceGoodsAddonResponse} from "./serviceGoodsAddonResponse";
import {ServiceGoodsAddonTotalResponse} from "./serviceGoodsAddonTotalResponse";
import {GoodsOutClientTotalResponse} from './goodsOutClientTotalResponse';

export class DocumentResponse {

  id: number;
  documentOutHeaderId: number;
  startDate: string;
  endDate: string;
  state: number;
  stateRus: string;
  documentNumber: string;
  style: string;
  client: string;
  vehicle: VehicleResponse;
  sum: number;
  userId: number;
  userFio: string;
  reason:string;
  organization:string;
  serviceWork: ServiceWorkTotalResponse;
  serviceGoodsAddon: ServiceGoodsAddonTotalResponse;
  goodsOutClient: GoodsOutClientTotalResponse;
  isInactive: boolean;

}

