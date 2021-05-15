import {Observable} from 'rxjs';

export interface RestService<M> {
  delete(model: M): Observable<void>;
}
