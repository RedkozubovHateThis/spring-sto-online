import {Component, OnInit, ViewChild} from '@angular/core';
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
import {ProfileController} from '../../controller/profile.controller';
import {ProfileResource, ProfileResourceService} from '../../model/resource/profile.resource.service';
import {ProfileService} from '../../api/profile.service';
import {DocumentCollection} from 'ngx-jsonapi';

@Component({
  selector: 'app-profiles',
  templateUrl: './profiles.component.html',
  styleUrls: ['./profiles.component.scss']
})
export class ProfilesComponent extends Pagination implements OnInit {

  private isLoading: boolean = false;
  private isSaving: boolean = false;
  private createdByChanging: boolean = false;
  protected routeName = '/profiles';
  @ViewChild('editModal', {static: false}) private editModal;
  private executors: DocumentCollection<ProfileResource> = new DocumentCollection<ProfileResource>();

  constructor(protected route: ActivatedRoute, protected router: Router, private profileService: ProfileService,
              private userService: UserService, protected profileController: ProfileController,
              private profileResourceService: ProfileResourceService, private modalService: NgbModal,
              private toastrService: ToastrService) {
    super(route, router, profileController);
  }

  ngOnInit() {
    super.ngOnInit();
    this.profileService.getAllExecutors().subscribe( (response) => {
      this.executors = response;
    } );
  }

  requestData() {
    this.isLoading = true;
    this.profileService.getAll(this.profileController.filter).subscribe(data => {
      this.profileController.all = data;

      this.isLoading = false;
    }, error => {
      this.isLoading = false;
    } );
  }

  private navigate(profile: ProfileResource) {
    this.createdByChanging = false;
    this.profileController.selectedModel = profile;
    this.openModal(this.editModal);
  }

  save(model: ProfileResource) {
    this.profileService.save(model).subscribe( (savedModel) => {
      this.isSaving = false;
      this.toastrService.success('Профиль успешно сохранен!');
      this.modalService.dismissAll();
      this.requestData();
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения профиля');
    } );
  }

  saveAs(model: ProfileResource, roleName: string) {
    this.profileService.save(model).subscribe( (savedModel) => {
      this.profileService.register(model, roleName).subscribe( () => {
        this.isSaving = false;
        this.toastrService.success('Профиль успешно сохранен!');
        this.modalService.dismissAll();
        this.requestData();
      }, () => {
        this.isSaving = false;
        this.showError(null, 'Ошибка сохранения профиля');
      } );
    }, (error) => {
      this.isSaving = false;
      this.showError(error, 'Ошибка сохранения профиля');
    } );
  }

  showError(error: any, defaultMessage: string) {
    if ( error && error.errors && Array.isArray( error.errors ) )
      this.toastrService.error( `${defaultMessage}: ${error.errors[0].detail}`, 'Внимание!' );
    else
      this.toastrService.error( `${defaultMessage}!`, 'Внимание!' );
  }

  openModal(content) {
    this.modalService.open(content, { size: 'lg' });
  }

}
