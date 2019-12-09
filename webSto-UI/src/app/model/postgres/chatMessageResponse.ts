export class ChatMessageResponse {

  fromFio: string;
  fromId: number;

  toFio: string;
  toId: number;

  messageDate: Date;
  messageText: string;

  uploadFileUuid: string;
  uploadFileName: string;
  uploadFileIsImage: boolean;

  chatMessageType: string;

  documentId: number;
  documentNumber: string;
  documentState: string;
  documentDate: Date;

  serviceWorkId: number;
  serviceWorkName: string;

  serviceGoodsAddonId: number;
  serviceGoodsAddonName: string;

  clientGoodsOutId: number;
  clientGoodsOutName: string;

}
