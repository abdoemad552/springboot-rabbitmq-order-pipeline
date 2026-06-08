import {Component, inject} from '@angular/core';
import {OrderService} from '../../services/order.service';
import {WebsocketService} from '../../services/websocket.service';
import {Router} from '@angular/router';
import {Customer} from '../../shared/models/customer.model';
import {OrderItem} from '../../shared/models/order-item.model';
import {Order} from '../../shared/models/order.model';
import {FormsModule} from '@angular/forms';

@Component({
  selector: 'app-order-form',
  imports: [
    FormsModule
  ],
  templateUrl: './order-form.component.html',
  styleUrl: './order-form.component.css',
})
export class OrderFormComponent {

  private readonly orderService = inject(OrderService);
  private readonly websocketService = inject(WebsocketService);
  private readonly router = inject(Router);

  isSubmitting = false;

  customer: Customer = {
    id: 1,
    name: '',
    email: '',
    shippingAddress: ''
  }

  items: OrderItem[] = [
    {
      id: 1,
      product: {
        id: 1,
        name: 'Wireless Keyboard',
        description: 'Mechanical wireless keyboard',
        price: 79.99,
        stockQuantity: 100
      },
      quantity: 1
    }
  ]

  addItem(): void {
    this.items.push({
      id: this.items.length + 1,
      product: {
        id: 1,
        name: '',
        description: '',
        price: 0,
        stockQuantity: 0
      },
      quantity: 1
    });
  }

  removeItem(index: number): void {
    this.items.splice(index, 1);
  }

  totalAmount(): number {
    return this.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0);
  }

  placeOrder(): void {
    this.isSubmitting = true;

    const order: Partial<Order> = {
      customer: this.customer,
      items: this.items
    };

    this.orderService.placeOrder(order).subscribe({
      next: (placedOrder) => {
        console.log(placedOrder);
        this.websocketService.connect();
        this.router.navigate(['/track', placedOrder.id]);
      },
      error: (err) => {
        console.error('Failed to place order', err);
        this.isSubmitting = false;
      }
    });
  }
}
