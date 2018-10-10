package com.axelor.apps.gst.web;

import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.GSTProductService;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GSTInvoiceController {

  @Inject
  GSTProductService invoiceProductService;

  public void showInvoice(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    String name = "Invoice";
    String fileLink = ReportFactory.createReport(IReport.INVOICE, name + "-${date}")
        .addParam("InvoiceId", invoice.getId())
        .addParam("Locale", ReportSettings.getPrintingLocale(null)).generate().getFileLink();
    response.setView(ActionView.define(name).add("html", fileLink).map());
  }

  public void addressChangeComputes(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    if (invoice.getInvoiceLineList() != null) {
      invoice = invoiceProductService.addressChangeComputes(invoice);
      response.setValue("invoiceLineList", invoice.getInvoiceLineList());
    }
  }
}
