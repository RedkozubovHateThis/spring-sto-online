import {Component, Input, OnInit, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ChatMessageResponseService} from '../../api/chatMessageResponse.service';
import {OpponentResponse} from '../../model/postgres/opponentResponse';
import {DocumentResponse} from '../../model/firebird/documentResponse';
import {ServiceWorkResponse} from '../../model/firebird/serviceWorkResponse';
import {ServiceGoodsAddonResponse} from '../../model/firebird/serviceGoodsAddonResponse';
import {GoodsOutClientResponse} from '../../model/firebird/goodsOutClientResponse';

@Component({
  selector: 'app-share-button',
  templateUrl: './share-button.component.html',
  styleUrls: ['./share-button.component.scss']
})
export class ShareButtonComponent implements OnInit {

  @ViewChild('content', {static: false})
  private content;

  @Input()
  private document: DocumentResponse;
  @Input()
  private disabled: boolean;
  @Input()
  private serviceWork: ServiceWorkResponse;
  @Input()
  private serviceGoodsAddon: ServiceGoodsAddonResponse;
  @Input()
  private clientGoodsOut: GoodsOutClientResponse;
  @Input()
  private isLink: boolean = false;

  private messageText: string;
  private opponents: OpponentResponse[];
  private selectedOpponent: OpponentResponse;
  private isSharing: boolean = false;

  constructor(private chatMessageResponseService: ChatMessageResponseService, private userService: UserService,
              private toastrService: ToastrService, private modalService: NgbModal) { }

  ngOnInit() {
    this.userService.getShareOpponents(this.document.id).subscribe( response => {
      this.opponents = response as OpponentResponse[];
    } );
  }

  open(content, event) {
    if ( event != null )
      event.preventDefault();

    if ( this.disabled ) return;

    if ( !this.isSharing )
      this.modalService.open(content, {windowClass : 'lg'});
  }

  share() {
    if ( this.selectedOpponent == null ) return;

    const opponentFio = this.selectedOpponent.fio;

    this.chatMessageResponseService.saveShareMessage( this.selectedOpponent, this.messageText, this.document.id,
      this.serviceWork != null ? this.serviceWork.id : null,
      this.serviceGoodsAddon != null ? this.serviceGoodsAddon.id : null,
      this.clientGoodsOut != null ? this.clientGoodsOut.id : null
    ).subscribe( response => {
        this.toastrService.success(`Ссылка на заказ-наряд успешно отправлена пользователю ${opponentFio}!`);
    }, () => {
      this.toastrService.error(`Ошибка отправки ссылки на заказ-наряд!`, 'Внимание!');
    } );
  }
}
