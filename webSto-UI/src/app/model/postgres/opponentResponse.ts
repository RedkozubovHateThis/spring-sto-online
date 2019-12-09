export class OpponentResponse {

  id: number;
  fio: string;
  inVacation: boolean;

  lastMessageFromId: number;
  lastMessageText: string;
  lastMessageType: string;

  lastMessageDocumentNumber: string;
  lastMessageServiceWorkName: string;
  lastMessageServiceGoodsAddonName: string;
  lastMessageClientGoodsOutName: string;

  uploadFileName: string;
  roles: string[];

}
