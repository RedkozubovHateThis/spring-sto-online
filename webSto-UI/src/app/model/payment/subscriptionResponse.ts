import {SubscriptionTypeResponse} from './subscriptionTypeResponse';

export class SubscriptionResponse {
  id: number;
  name: string;
  type: SubscriptionTypeResponse;
  startDate: Date;
  endDate: Date;
  isRenewable: boolean;
  documentCost: number;
  documentsCount: number;
  documentsRemains: number;
  isClosedEarly: boolean;
}

