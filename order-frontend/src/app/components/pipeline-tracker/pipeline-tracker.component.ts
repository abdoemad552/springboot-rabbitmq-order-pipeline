import {Component, computed, inject, OnDestroy, OnInit, signal} from '@angular/core';
import {OrderStatus} from '../../shared/models/order-status.enum';
import {CommonModule} from '@angular/common';
import {ActivatedRoute, RouterModule} from '@angular/router';
import {OrderStatusPipe} from '../../shared/pipes/order-status.pipe';
import {WebsocketService} from '../../services/websocket.service';
import {Subscription} from 'rxjs';
import {StatusUpdate} from '../../shared/models/status-update.model';

interface PipelineStage {
  label: string;
  statuses: OrderStatus[];
  state: 'pending' | 'processing' | 'success' | 'failed';
}

@Component({
  selector: 'app-pipeline-tracker',
  imports: [CommonModule, RouterModule, OrderStatusPipe],
  templateUrl: './pipeline-tracker.component.html',
  styleUrl: './pipeline-tracker.component.css',
})
export class PipelineTrackerComponent implements OnInit, OnDestroy {

  private readonly route = inject(ActivatedRoute);
  private readonly websocketService = inject(WebsocketService);
  private subscription?: Subscription;

  orderId!: number;
  updates = signal<StatusUpdate[]>([]);
  currentStatus = signal<OrderStatus | undefined>(undefined);
  stages = signal<PipelineStage[]>([
    {
      label: 'Payment',
      statuses: [OrderStatus.PAYMENT_PROCESSING, OrderStatus.PAYMENT_SUCCESS, OrderStatus.PAYMENT_FAILED],
      state: 'pending'
    },
    {
      label: 'Inventory',
      statuses: [OrderStatus.INVENTORY_PROCESSING, OrderStatus.INVENTORY_SUCCESS, OrderStatus.INVENTORY_FAILED],
      state: 'pending'
    },
    {
      label: 'Shipping',
      statuses: [OrderStatus.SHIPPING_PROCESSING, OrderStatus.SHIPPING_SUCCESS, OrderStatus.SHIPPING_FAILED],
      state: 'pending'
    }
  ]);

  // ── Computed ──────────────────────────────────────────
  isCompleted = computed(() => this.currentStatus() === OrderStatus.COMPLETED);
  isFailed = computed(() =>
    this.currentStatus() === OrderStatus.FAILED ||
    !!this.currentStatus()?.endsWith('_FAILED')
  );

  ngOnInit(): void {
    this.orderId = Number(this.route.snapshot.paramMap.get('orderId'));
    this.subscription = this.websocketService
      .subscribeToOrder(this.orderId)
      .subscribe((update) => this.handleUpdate(update));
  }

  handleUpdate(update: StatusUpdate): void {
    this.updates.update(prev => [update, ...prev]);
    this.currentStatus.set(update.status);
    this.updateStages(update.status);
  }

  updateStages(status: OrderStatus): void {
    this.stages.update(stages =>
      stages.map(stage => {
        if (!stage.statuses.includes(status)) return stage;
        if (status.endsWith('_PROCESSING')) return { ...stage, state: 'processing' };
        if (status.endsWith('_SUCCESS'))    return { ...stage, state: 'success' };
        if (status.endsWith('_FAILED'))     return { ...stage, state: 'failed' };
        return stage;
      })
    );

    if (status === OrderStatus.COMPLETED) {
      this.stages.update(stages => stages.map(s => ({ ...s, state: 'success' })));
    }

    if (status === OrderStatus.FAILED) {
      this.stages.update(stages =>
        stages.map(s =>
          s.state === 'pending'     ? s :
            s.state === 'processing'  ? { ...s, state: 'failed' } : s
        )
      );
    }
  }

  ngOnDestroy(): void {
    this.subscription?.unsubscribe();
    this.websocketService.disconnect();
  }
}
