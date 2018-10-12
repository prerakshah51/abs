package com.axelor.apps.gst.web;

import java.lang.invoke.MethodHandles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.gst.service.GSTInvoiceServiceImpl;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.exception.AxelorException;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.inject.Inject;

public class GSTInvoiceController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  GSTInvoiceServiceImpl invoiceInvoiceService;

  // New method to print GST Invoice
  public void showInvoice(ActionRequest request, ActionResponse response) throws AxelorException {
    Invoice invoice = request.getContext().asType(Invoice.class);
    String name = "Invoice";
    String fileLink = ReportFactory.createReport(IReport.INVOICE, name + "-${date}")
        .addParam("InvoiceId", invoice.getId())
        .addParam("Locale", ReportSettings.getPrintingLocale(null)).generate().getFileLink();
    logger.debug("Printing " + name);
    response.setView(ActionView.define(name).add("html", fileLink).map());
  }

  // Method for computer GST details on-Change of Partner
  public void addressChangeComputes(ActionRequest request, ActionResponse response) {
    Invoice invoice = request.getContext().asType(Invoice.class);
    if (invoice.getInvoiceLineList() != null) {
      invoice = invoiceInvoiceService.addressChangeComputes(invoice);
      response.setValue("invoiceLineList", invoice.getInvoiceLineList());
    }
  }
}
