package com.axelor.apps.gst.web;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.gst.service.GSTInvoiceLineService;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GSTInvoiceLineController {

  @Inject
  private GSTInvoiceLineService invoiceLineService;

  public void computeInvoiceLineGstValues(ActionRequest req, ActionResponse res) {
    Invoice invoice = req.getContext().getParent().asType(Invoice.class);
    InvoiceLine invoiceLine = req.getContext().asType(InvoiceLine.class);

    if (invoiceLine.getTaxLine() != null) {
      if (invoice.getAddress().getState() != null
          && invoice.getCompany().getAddress().getState() != null) {
        invoiceLine = invoiceLineService.computeInvoiceLineGstValues(invoice, invoiceLine);
        res.setValue("hsbn", invoiceLine.getProduct().getHsbn());
        res.setValue("igst", invoiceLine.getIgst());
        res.setValue("sgst", invoiceLine.getSgst());
        res.setValue("cgst", invoiceLine.getCgst());
      } else {
        res.setFlash("State field is not defined for the selected Invoice Partner/Company!");
      }
    }
  }
}
