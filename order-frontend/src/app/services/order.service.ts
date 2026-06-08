import {inject, Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Order} from '../shared/models/order.model';
import {Observable} from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class OrderService {
  private readonly http = inject(HttpClient);

  placeOrder(order: Partial<Order>): Observable<Order> {
    return this.http.post<Order>('/api/orders', order);
  }
}
