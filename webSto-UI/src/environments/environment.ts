// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

export const environment = {
  production: false,
  baseUrl: 'https://local.buromotors.ru:8080/',
  apiBeforeUrl: 'api',
  demoBeforeUrl: 'demo',
  apiUrl: 'https://local.buromotors.ru:8080/api/',
  demoUrl: 'https://local.buromotors.ru:8080/demo/',
  wsUrl: 'https://local.buromotors.ru:8080/ws/',
  wsdUrl: 'https://local.buromotors.ru:8080/wsd/',
  getBeforeUrl: (): string => {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      return environment.demoBeforeUrl;
    }
    else
      return environment.apiBeforeUrl;
  },
  getApiUrl: (): string => {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      return environment.demoUrl;
    }
    else
      return environment.apiUrl;
  },
  getWsUrl: (): string => {
    if ( localStorage.getItem('demoDomain') != null && localStorage.getItem('demoDomain') !== null ) {
      return environment.wsdUrl;
    }
    else
      return environment.wsUrl;
  }
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
