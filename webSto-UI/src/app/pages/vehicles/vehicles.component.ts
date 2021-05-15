import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {VehicleService} from '../../api/vehicle.service';
import {VehicleController} from '../../controller/vehicle.controller';
import {VehicleResource, VehicleResourceService} from '../../model/resource/vehicle.resource.service';

@Component({
  selector: 'app-vehicles',
  templateUrl: './vehicles.component.html',
  styleUrls: ['./vehicles.component.scss']
})
export class VehiclesComponent extends Pagination {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/vehiclesAdded';
  @ViewChild('editModal', {static: false}) private editModal;

  constructor(protected route: ActivatedRoute, protected router: Router, private vehicleService: VehicleService,
              private userService: UserService, protected vehicleController: VehicleController,
              private vehicleResourceService: VehicleResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, vehicleController);
  }

  requestData() {
    this.isLoading = true;
    this.vehicleService.getAll(this.vehicleController.filter).subscribe(data => {
      this.vehicleController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(vehicle: VehicleResource) {
    this.vehicleController.selectedModel = vehicle;
    this.openModal(this.editModal);
  }

  addNew() {
    this.vehicleController.selectedModel = this.vehicleResourceService.new();
    this.openModal(this.editModal);
  }

  save(model: VehicleResource) {
    this.vehicleService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Автомобиль успешно сохранен!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения автомобиля');
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
