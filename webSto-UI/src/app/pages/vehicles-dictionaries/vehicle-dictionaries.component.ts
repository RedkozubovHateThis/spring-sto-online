import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {VehicleDictionaryResource, VehicleDictionaryResourceService} from '../../model/resource/vehicle-dictionary.resource.service';
import {VehicleDictionaryService} from '../../api/vehicle.dictionary.service';
import {VehicleDictionaryController} from '../../controller/vehicle-dictionary.controller';

@Component({
  selector: 'app-vehicle-dictionaries',
  templateUrl: './vehicle-dictionaries.component.html',
  styleUrls: ['./vehicle-dictionaries.component.scss']
})
export class VehicleDictionariesComponent extends Pagination {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/vehicles';
  @ViewChild('editModal', {static: false}) private editModal;

  constructor(protected route: ActivatedRoute, protected router: Router, private vehicleDictionaryService: VehicleDictionaryService,
              private userService: UserService, protected vehicleDictionaryController: VehicleDictionaryController,
              private vehicleDictionaryResourceService: VehicleDictionaryResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, vehicleDictionaryController);
  }

  requestData() {
    this.isLoading = true;
    this.vehicleDictionaryService.getAll(this.vehicleDictionaryController.filter).subscribe(data => {
      this.vehicleDictionaryController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(vehicleDictionary: VehicleDictionaryResource) {
    this.vehicleDictionaryController.selectedModel = vehicleDictionary;
    this.openModal(this.editModal);
  }

  addNew() {
    this.vehicleDictionaryController.selectedModel = this.vehicleDictionaryResourceService.new();
    this.openModal(this.editModal);
  }

  save(model: VehicleDictionaryResource) {
    this.vehicleDictionaryService.save(model).subscribe( (savedModel) => {
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
