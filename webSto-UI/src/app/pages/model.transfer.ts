import {OnInit} from '@angular/core';
import {TransferService} from '../api/transfer.service';
import {ActivatedRoute} from '@angular/router';
import {Subscription} from 'rxjs';

export abstract class ModelTransfer<M, I> implements OnInit {

  protected model: M;
  protected routeSub: Subscription;
  protected id: I;

  protected constructor(private transferService: TransferService<M>, protected route: ActivatedRoute) {}

  ngOnInit() {

    if ( this.transferService.getTransferModel() != null ) {
      this.model = this.transferService.getTransferModel();
      this.transferService.resetTransferModel();
      this.onTransferComplete();
    }
    else {
      this.routeSub = this.route.params.subscribe(params => {
        this.id = params['id'];
      });

      this.requestData();
    }

  }

  ngOnDestroy() {
    if ( this.routeSub != null )
      this.routeSub.unsubscribe();
  }

  abstract requestData();
  abstract onTransferComplete();

}
