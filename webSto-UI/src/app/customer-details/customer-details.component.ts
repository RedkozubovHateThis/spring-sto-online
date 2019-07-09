import { Component, OnInit, Input } from '@angular/core';
import { CustomerService } from '../customer.service';
import { Customer } from '../customer';

import { CustomersListComponent } from '../customers-list/customers-list.component';

@Component({
  selector: 'customer-details',
  templateUrl: './customer-details.component.html',
  styleUrls: ['./customer-details.component.css']
})
export class CustomerDetailsComponent implements OnInit {

  @Input() customer: Customer;

  constructor(private customerService: CustomerService, private listComponent: CustomersListComponent) { }

  ngOnInit() {
  }

  updateActive(isActive: boolean) {
    this.customerService.updateCustomer(this.customer.id,
      { first_name: this.customer.first_name, last_name: this.customer.last_name, active: isActive })
      .subscribe(
        data => {
          console.log(data);
          this.customer = data as Customer;
        },
        error => console.log(error));
  }

  deleteCustomer() {
    this.customerService.deleteCustomer(this.customer.id)
      .subscribe(
        data => {
          console.log(data);
          this.listComponent.reloadData();
        },
        error => console.log(error));
  }
}
