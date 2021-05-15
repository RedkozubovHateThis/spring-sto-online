export class PaymentResponse {
  orderId: string;
  orderNumber: string;
  amount: number;
  paymentType: string;
  errorMessage: string;
  paymentState: string;
  createDate: Date;
  registerDate: Date;
  actionCodeDescription: string;
}

