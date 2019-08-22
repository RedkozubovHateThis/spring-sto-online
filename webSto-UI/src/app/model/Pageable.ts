import {Sort} from "./Sort";

export class Pageable<T> {

  totalPages:number;
  totalElements:number;
  last:boolean;
  first:boolean;
  size:number;
  numberOfElements:number;
  content:T[];
  sort: Sort;

}

