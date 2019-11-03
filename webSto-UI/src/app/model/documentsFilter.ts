export class DocumentsFilter {

  state: number;
  organization: number;
  vehicle: string;
  vinNumber: string;
  client: number;
  sort: string;
  direction: string;
  fromDate: string;
  toDate: string;

  constructor() {
    this.state = null;
    this.organization = null;
    this.vehicle = null;
    this.client = null;
    this.vinNumber = null;
    this.fromDate = null;
    this.toDate = null;
    this.sort = 'dateStart';
    this.direction = 'desc';
  }

}

