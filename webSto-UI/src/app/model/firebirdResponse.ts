import {VehicleResponse} from "./vehicleResponse";
import {ServiceWorkTotalResponse} from "./serviceWorkTotalResponse";

export class FirebirdResponse {

  id: number;
  startDate: string;
  endDate: string;
  state: string;
  documentNumber: string;
  style: string;
  client: string;
  vehicle: VehicleResponse;
  sum: number;
  reason:string;
  organization:string;
  serviceWork: ServiceWorkTotalResponse;

}

