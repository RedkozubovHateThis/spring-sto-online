export const environment = {
  production: true,
  baseUrl: 'https://service.buromotors.ru:8080/',
  apiBeforeUrl: 'api/',
  demoBeforeUrl: 'demo/',
  apiUrl: 'https://service.buromotors.ru:8080/api/',
  demoUrl: 'https://service.buromotors.ru:8080/demo/',
  wsUrl: 'https://service.buromotors.ru:8080/ws/',
  wsdUrl: 'https://service.buromotors.ru:8080/wsd/',
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
