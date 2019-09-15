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

}
