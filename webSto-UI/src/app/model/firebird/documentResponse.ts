import {VehicleResponse} from "./vehicleResponse";
import {ServiceWorkTotalResponse} from "./serviceWorkTotalResponse";

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
  reason:string;
  organization:string;
  serviceWork: ServiceWorkTotalResponse;

}

