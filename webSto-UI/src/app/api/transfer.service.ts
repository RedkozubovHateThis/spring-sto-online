export interface TransferService<M> {
  getTransferModel();
  resetTransferModel();
  setTransferModel(model: M);
}
