package com.axelor.apps.gst.web;

import java.util.ArrayList;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.base.db.repo.ProductBaseRepository;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GSTProductController {

  @Inject
  ProductBaseRepository productBaseRepo;

  public void getGstProductToInvoiceLine(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    List<Integer> idList = (List<Integer>) request.getContext().get("_ids");
    if (idList != null) {
      List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
      int i = 0;
      for (@SuppressWarnings("unused")
      Integer id : idList) {
        long temp = idList.get(i);
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setProduct(productBaseRepo.find(temp));
        invoiceLineList.add(invoiceLine);
        invoiceLine.setHsbn(productBaseRepo.find(temp).getHsbn());
        // invoiceLine.setGstRate(productBaseRepo.find(temp).getGstRate());
        invoiceLine.setPrice(productBaseRepo.find(temp).getSalePrice());
        invoiceLine.setProductName(productBaseRepo.find(temp).getName()
            .concat("[" + productBaseRepo.find(temp).getCode() + "]"));
        i++;
      }
      response.setView(ActionView.define("Create Invoice").model(Invoice.class.getName())
          .add("form", "gst-invoice-form").context("invoiceitem", invoiceLineList).map());
    } else {
      response.setFlash("No record selected!");
    }
  }

  public void setGstProductToInvoiceLine(ActionRequest request, ActionResponse response) {
    @SuppressWarnings("unchecked")
    List<Integer> invoiceLineList = (List<Integer>) request.getContext().get("invoiceitem");
    response.setValue("invoiceLineList", invoiceLineList);
  }
}
