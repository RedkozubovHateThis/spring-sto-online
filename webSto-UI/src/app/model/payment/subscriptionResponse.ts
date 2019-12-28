export class SubscriptionResponse {
  id: number;
  name: string;
  type: string;
  startDate: Date;
  endDate: Date;
  isRenewable: boolean;
  renewalCost: number;
  documentCost: number;
  documentsCount: number;
  documentsRemains: number;
}

