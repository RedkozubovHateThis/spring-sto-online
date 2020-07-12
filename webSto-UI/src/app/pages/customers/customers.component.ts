import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {CustomerService} from '../../api/customer.service';
import {CustomerController} from '../../controller/customer.controller';
import {CustomerResource, CustomerResourceService} from '../../model/resource/customer.resource.service';

@Component({
  selector: 'app-customers',
  templateUrl: './customers.component.html',
  styleUrls: ['./customers.component.scss']
})
export class CustomersComponent extends Pagination {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/customers';
  @ViewChild('editModal', {static: false}) private editModal;

  constructor(protected route: ActivatedRoute, protected router: Router, private customerService: CustomerService,
              private userService: UserService, protected customerController: CustomerController,
              private customerResourceService: CustomerResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, customerController);
  }

  requestData() {
    this.isLoading = true;
    this.customerService.getAll(this.customerController.filter).subscribe(data => {
      this.customerController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(customer: CustomerResource) {
    this.customerController.selectedModel = customer;
    this.openModal(this.editModal);
  }

  addNew() {
    this.customerController.selectedModel = this.customerResourceService.new();
    this.openModal(this.editModal);
  }

  save(model: CustomerResource) {
    this.customerService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Заказчик успешно сохранен!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения заказчика');
    } );
  }

  showError(error: any, defaultMessage: string) {
    if ( error.errors && Array.isArray( error.errors ) )
      this.toastrService.error( `${defaultMessage}: ${error.errors[0].detail}`, 'Внимание!' );
    else
      this.toastrService.error( `${defaultMessage}!`, 'Внимание!' );
  }

  openModal(content) {
    this.modalService.open(content, { size: 'lg' });
  }

}
