import { HttpInterceptorFn } from '@angular/common/http';
import { environment } from '../../environments/environment.development';

export const apiInterceptor: HttpInterceptorFn = (req, next) => {
  req = req.clone({ url: environment.apiUrl + req.url });

  return next(req);
};
