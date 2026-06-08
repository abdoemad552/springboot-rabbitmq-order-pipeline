import {Injectable} from '@angular/core';
import {Client, IMessage} from '@stomp/stompjs';
import {Observable, Subject} from 'rxjs';
import {StatusUpdate} from '../shared/models/status-update.model';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private client!: Client;
  private statusSubject = new Subject<StatusUpdate>();

  connect(): void {
    this.client = new Client({
      webSocketFactory: () => {
        const url = window.location.port === '4200'
          ? 'http://localhost:8080/ws'
          : '/ws';
        return new SockJS(url);
      },
      reconnectDelay: 5000,
      onConnect: () => {
        console.log('WebSocket connected');
      },
      onDisconnect: () => {
        console.log('WebSocket disconnected');
      },
      debug: msg => console.log(msg)
    });

    this.client.activate();
  }

  subscribeToOrder(orderId: number): Observable<StatusUpdate> {
    const subject = new Subject<StatusUpdate>();

    const subscribe = () => {
      this.client.subscribe(`/topic/orders/${orderId}`, (message: IMessage) => {
        console.log(JSON.stringify(message.body));
        const statusUpdate: StatusUpdate = JSON.parse(message.body);
        subject.next(statusUpdate);
      });
    }

    if (this.client.connected) {
      subscribe();
    } else {
      this.client.onConnect = () => subscribe();
    }

    return subject.asObservable();
  }

  disconnect(): void {
    if (this.client?.active) {
      this.client.deactivate();
    }
  }
}
