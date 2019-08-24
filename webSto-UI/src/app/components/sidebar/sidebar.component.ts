import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';

declare interface RouteInfo {
    path: string;
    title: string;
    icon: string;
    class: string;
}
export const ROUTES: RouteInfo[] = [
    { path: '/dashboard', title: 'Главная',  icon: 'fas fa-tv text-primary', class: '' },
    { path: '/documents', title: 'Заказ-наряды',  icon:'fas fa-list text-red', class: '' },
    { path: '/user-profile', title: 'Профиль',  icon:'fas fa-user text-yellow', class: '' },
    { path: '/users', title: 'Пользователи',  icon:'fas fa-users text-orange', class: '' },
];

@Component({
  selector: 'app-sidebar',
  templateUrl: './sidebar.component.html',
  styleUrls: ['./sidebar.component.scss']
})
export class SidebarComponent implements OnInit {

  public menuItems: any[];
  public isCollapsed = true;

  constructor(private router: Router) { }

  ngOnInit() {
    this.menuItems = ROUTES.filter(menuItem => menuItem);
    this.router.events.subscribe((event) => {
      this.isCollapsed = true;
   });
  }
}
