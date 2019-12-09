import {Component, Input} from '@angular/core';
import {ChatMessageResponse} from '../../model/postgres/chatMessageResponse';
import {Router} from '@angular/router';

@Component({
  selector: 'app-link-preview',
  templateUrl: './link-preview.component.html',
  styleUrls: ['./link-preview.component.scss'],
})
export class LinkPreviewComponent {

  constructor(private router: Router) {}
  @Input()
  private message: ChatMessageResponse;
  
  navigateToDocument(documentId: number) {
    this.router.navigate(['/documents', documentId]);
  }

}
