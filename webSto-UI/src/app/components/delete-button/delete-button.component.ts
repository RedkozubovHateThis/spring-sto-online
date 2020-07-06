import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RestService} from '../../api/rest.service';
import {DocumentCollection, Resource} from 'ngx-jsonapi';

@Component({
  selector: 'app-delete-button',
  templateUrl: './delete-button.component.html',
  styleUrls: ['./delete-button.component.scss']
})
export class DeleteButtonComponent<R extends Resource> implements OnInit {

  @ViewChild('content', {static: false}) private content;
  private isDeleting: boolean = false;
  @Input()
  private isIcon = false;
  @Input()
  private modalTitle: string;
  @Input()
  private model: R;
  @Input()
  private restService: RestService<any>;
  @Input()
  private parentList: Array<R>;
  @Output()
  private onDelete: EventEmitter<any> = new EventEmitter();

  constructor(private userService: UserService, private toastrService: ToastrService, private modalService: NgbModal) { }

  ngOnInit() {
  }

  promptDelete() {
    this.isDeleting = true;

    if ( this.model.is_new && this.parentList ) {
      const index = this.parentList.indexOf( this.model );
      this.parentList.splice(index, 1);
      this.isDeleting = false;
      this.modalService.dismissAll();
      this.toastrService.success('Запись успешно удалена!');
      this.onDelete.emit();
    }
    else {
      this.restService.delete(this.model).subscribe( () => {
        if ( this.parentList ) {
          const index = this.parentList.indexOf( this.model );
          this.parentList.splice(index, 1);
        }
        this.isDeleting = false;
        this.modalService.dismissAll();
        this.toastrService.success('Запись успешно удалена!');
        this.onDelete.emit();
      }, error => {
        this.isDeleting = false;
        if ( error.error.responseText )
          this.toastrService.error(error.error.responseText, 'Внимание!');
        else
          this.toastrService.error('Ошибка удаления!', 'Внимание!');
      } );
    }
  }

  open(content) {
    this.modalService.open(content);
  }
}
