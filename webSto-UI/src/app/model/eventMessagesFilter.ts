export class EventMessagesFilter {

  messageType: string;
  fromId: number;
  toId: number;
  documentId: number;
  sort: string;
  direction: string;

  constructor() {
    this.messageType = null;
    this.fromId = null;
    this.toId = null;
    this.documentId = null;
    this.sort = 'messageDate';
    this.direction = 'asc';
  }

}

