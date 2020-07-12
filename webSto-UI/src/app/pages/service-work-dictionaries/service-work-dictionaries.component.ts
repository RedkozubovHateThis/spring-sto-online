import {Component, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {DocumentController} from '../../controller/document.controller';
import {ServiceDocumentResource} from '../../model/resource/service-document.resource.service';
import {DocumentService} from '../../api/document-service.service';
import {ServiceWorkDictionaryController} from '../../controller/service-work-dictionary.controller';
import {ServiceWorkDictionaryService} from '../../api/service-work.dictionary.service';
import {
  ServiceWorkDictionaryResource,
  ServiceWorkDictionaryResourceService
} from '../../model/resource/service-work-dictionary.resource.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';

@Component({
  selector: 'app-service-work-dictionaries',
  templateUrl: './service-work-dictionaries.component.html',
  styleUrls: ['./service-work-dictionaries.component.scss']
})
export class ServiceWorkDictionariesComponent extends Pagination {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/serviceWorks';
  @ViewChild('editModal', {static: false}) private editModal;

  constructor(protected route: ActivatedRoute, protected router: Router, private serviceWorkDictionaryService: ServiceWorkDictionaryService,
              private userService: UserService, protected serviceWorkDictionaryController: ServiceWorkDictionaryController,
              private serviceWorkDictionaryResourceService: ServiceWorkDictionaryResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, serviceWorkDictionaryController);
  }

  requestData() {
    this.isLoading = true;
    this.serviceWorkDictionaryService.getAll(this.serviceWorkDictionaryController.filter).subscribe(data => {
      this.serviceWorkDictionaryController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(serviceWorkDictionary: ServiceWorkDictionaryResource) {
    this.serviceWorkDictionaryController.selectedModel = serviceWorkDictionary;
    this.openModal(this.editModal);
  }

  addNew() {
    this.serviceWorkDictionaryController.selectedModel = this.serviceWorkDictionaryResourceService.new();
    this.openModal(this.editModal);
  }

  save(model: ServiceWorkDictionaryResource) {
    this.serviceWorkDictionaryService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Работа успешно сохранена!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения работы');
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
