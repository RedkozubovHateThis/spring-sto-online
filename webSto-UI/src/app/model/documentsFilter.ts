export class DocumentsFilter {

  state: number;
  organization: number;
  vehicle: number;
  sort: string;
  direction: string;

  constructor() {
    this.state = null;
    this.organization = null;
    this.vehicle = null;
    this.sort = 'dateStart';
    this.direction = 'asc';
  }

}

