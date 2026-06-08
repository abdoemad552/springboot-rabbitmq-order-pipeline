import {OrderStatus} from './order-status.enum';

export interface StatusUpdate {
  orderId: number;
  status: OrderStatus;
  message: string;
  timestamp: string;
}
