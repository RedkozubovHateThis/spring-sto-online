interface PageFilterInterface {
  page: number;
  size: number;
  offset: number;
  prepareFilter(queryParams);
  calculatePagination();
}
