import { Pipe, PipeTransform } from '@angular/core';
import { OrderStatus } from '../models/order-status.enum';

@Pipe({
  name: 'orderStatus',
  standalone: true
})
export class OrderStatusPipe implements PipeTransform {

  transform(status: OrderStatus): string {
    const labels: Record<OrderStatus, string> = {
      [OrderStatus.PENDING]:               '⏳ Pending',
      [OrderStatus.PAYMENT_PROCESSING]:    '💳 Processing Payment',
      [OrderStatus.PAYMENT_SUCCESS]:       '✅ Payment Successful',
      [OrderStatus.PAYMENT_FAILED]:        '❌ Payment Failed',
      [OrderStatus.INVENTORY_PROCESSING]:  '📦 Checking Inventory',
      [OrderStatus.INVENTORY_SUCCESS]:     '✅ Inventory Reserved',
      [OrderStatus.INVENTORY_FAILED]:      '❌ Inventory Unavailable',
      [OrderStatus.SHIPPING_PROCESSING]:   '🚚 Creating Shipment',
      [OrderStatus.SHIPPING_SUCCESS]:      '✅ Shipment Created',
      [OrderStatus.SHIPPING_FAILED]:       '❌ Shipment Failed',
      [OrderStatus.COMPLETED]:             '🎉 Order Completed',
      [OrderStatus.FAILED]:                '❌ Order Failed',
    };
    return labels[status] ?? status;
  }
}
