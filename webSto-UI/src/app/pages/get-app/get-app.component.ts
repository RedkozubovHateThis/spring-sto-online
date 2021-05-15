import {Component, OnInit} from '@angular/core';
import {Router} from '@angular/router';

@Component({
  selector: 'app-get-app',
  templateUrl: './get-app.component.html',
  styleUrls: ['./get-app.component.scss']
})
export class GetAppComponent implements OnInit {

  private userAgent: string = '';

  constructor(private router: Router) {}

  ngOnInit() {
    // @ts-ignore
    const userAgent: string = navigator.userAgent || navigator.vendor || window.opera;
    console.log(userAgent);

    // Windows Phone must come first because its UA also contains "Android"
    if ( /windows phone/i.test(userAgent) ) {
      this.userAgent = 'Windows Phone';
      this.router.navigate(['/']);
    }

    if ( /android/i.test(userAgent) ) {
      this.userAgent = 'Android';
      window.open('https://play.google.com/store/apps/details?id=ru.buromotors', '_blank');
      return;
    }

    // iOS detection from: http://stackoverflow.com/a/9039885/177710
    // @ts-ignore
    if (/iPad|iPhone|iPod/.test(userAgent) && !window.MSStream) {
      this.userAgent = 'iOS';
      window.open('https://apps.apple.com/ru/app/buromotors/id1546116256', '_blank');
      return;
    }

    this.router.navigate(['/']);
  }

}
