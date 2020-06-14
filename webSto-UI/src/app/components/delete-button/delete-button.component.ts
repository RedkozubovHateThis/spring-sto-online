import {Component, EventEmitter, Input, OnInit, Output, ViewChild} from '@angular/core';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {RestService} from '../../api/rest.service';

@Component({
  selector: 'app-delete-button',
  templateUrl: './delete-button.component.html',
  styleUrls: ['./delete-button.component.scss']
})
export class DeleteButtonComponent implements OnInit {

  @ViewChild('content', {static: false}) private content;
  private isDeleting: boolean = false;
  @Input()
  private modalTitle: string;
  @Input()
  private model: any;
  @Input()
  private restService: RestService<any>;
  @Output()
  private onDelete: EventEmitter<any> = new EventEmitter();

  constructor(private userService: UserService, private toastrService: ToastrService, private modalService: NgbModal) { }

  ngOnInit() {
  }

  promptDelete() {
    this.isDeleting = true;
    this.restService.delete(this.model).subscribe( response => {
      this.isDeleting = false;
      this.modalService.dismissAll();
      this.toastrService.success(response.responseText);
      this.onDelete.emit();
    }, error => {
      this.isDeleting = false;
      if ( error.error.responseText )
        this.toastrService.error(error.error.responseText, 'Внимание!');
      else
        this.toastrService.error('Ошибка удаления!', 'Внимание!');
    } );
  }

  open(content) {
    this.modalService.open(content);
  }
}
