import {Customer} from './customer.model';
import {OrderItem} from './order-item.model';
import {OrderStatus} from './order-status.enum';

export interface Order {
  id: number;
  customer: Customer;
  items: OrderItem[];
  totalAmount: number;
  status: OrderStatus;
  createdAt: string;
}
