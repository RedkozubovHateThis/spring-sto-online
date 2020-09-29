import {Component, OnInit, ViewChild} from '@angular/core';
import {ActivatedRoute, Router} from '@angular/router';
import {Pagination} from '../pagination';
import {UserService} from '../../api/user.service';
import {ToastrService} from 'ngx-toastr';
import {NgbModal} from '@ng-bootstrap/ng-bootstrap';
import {AdEntityService} from '../../api/ad-entity.service';
import {AdEntityController} from '../../controller/ad-entity.controller';
import {AdEntityResource, AdEntityResourceService} from '../../model/resource/ad-entity.resource.service';
import {DocumentCollection} from 'ngx-jsonapi';
import {UserResource} from '../../model/resource/user.resource.service';

@Component({
  selector: 'app-ad-entities',
  templateUrl: './ad-entities.component.html',
  styleUrls: ['./ad-entities.component.scss']
})
export class AdEntitiesComponent extends Pagination implements OnInit {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  protected routeName = '/adEntities';
  @ViewChild('editModal', {static: false}) private editModal;
  private users: DocumentCollection<UserResource> = new DocumentCollection<UserResource>();

  constructor(protected route: ActivatedRoute, protected router: Router, private edEntityService: AdEntityService,
              private userService: UserService, protected adEntityController: AdEntityController,
              private adEntityResourceService: AdEntityResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, adEntityController);
  }

  ngOnInit() {
    super.ngOnInit();
    this.userService.getAllServiceLeadersAndFreelancers().subscribe( (response) => {
      this.users = response;
    } );
  }

  requestData() {
    this.isLoading = true;
    this.edEntityService.getAll(this.adEntityController.filter).subscribe(data => {
      this.adEntityController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(adEntity: AdEntityResource) {
    this.adEntityController.selectedModel = adEntity;
    this.openModal(this.editModal);
  }

  addNew() {
    this.adEntityController.selectedModel = this.adEntityResourceService.new();
    this.adEntityController.selectedModel.attributes.sideOffer = true;
    this.openModal(this.editModal);
  }

  save(model: AdEntityResource) {
    this.edEntityService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Рекламное объявление успешно сохранено!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения рекламного объявления');
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

  closeModal() {
    this.modalService.dismissAll();
    // this.router.navigate(['/users', id]);
  }

}
