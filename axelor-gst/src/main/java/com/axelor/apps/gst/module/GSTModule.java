package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.account.service.invoice.print.InvoicePrintServiceImpl;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.GSTInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GSTInvoicePrintServiceImpl;
import com.axelor.apps.gst.service.GSTInvoiceServiceImpl;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;

public class GSTModule extends AxelorModule {

  @Override
  protected void configure() {
    // Override
    bind(InvoiceServiceProjectImpl.class).to(GSTInvoiceServiceImpl.class);
    bind(InvoicePrintServiceImpl.class).to(GSTInvoicePrintServiceImpl.class);
    bind(InvoiceLineSupplychainService.class).to(GSTInvoiceLineServiceImpl.class);
  }
}
