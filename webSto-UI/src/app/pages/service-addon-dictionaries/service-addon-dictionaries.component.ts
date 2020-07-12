import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {ServiceAddonDictionaryService} from '../../api/service-addon.dictionary.service';
import {ServiceAddonDictionaryController} from '../../controller/service-addon-dictionary.controller';
import {
  ServiceAddonDictionaryResource,
  ServiceAddonDictionaryResourceService
} from '../../model/resource/service-addon-dictionary.resource.service';

@Component({
  selector: 'app-service-addon-dictionaries',
  templateUrl: './service-addon-dictionaries.component.html',
  styleUrls: ['./service-addon-dictionaries.component.scss']
})
export class ServiceAddonDictionariesComponent extends Pagination {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/serviceAddons';
  @ViewChild('editModal', {static: false}) private editModal;

  constructor(protected route: ActivatedRoute, protected router: Router, private serviceAddonDictionaryService: ServiceAddonDictionaryService,
              private userService: UserService, protected serviceAddonDictionaryController: ServiceAddonDictionaryController,
              private serviceAddonDictionaryResourceService: ServiceAddonDictionaryResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, serviceAddonDictionaryController);
  }

  requestData() {
    this.isLoading = true;
    this.serviceAddonDictionaryService.getAll(this.serviceAddonDictionaryController.filter).subscribe(data => {
      this.serviceAddonDictionaryController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(serviceAddonDictionary: ServiceAddonDictionaryResource) {
    this.serviceAddonDictionaryController.selectedModel = serviceAddonDictionary;
    this.openModal(this.editModal);
  }

  addNew() {
    this.serviceAddonDictionaryController.selectedModel = this.serviceAddonDictionaryResourceService.new();
    this.openModal(this.editModal);
  }

  save(model: ServiceAddonDictionaryResource) {
    this.serviceAddonDictionaryService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Товар успешно сохранен!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения товара');
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
