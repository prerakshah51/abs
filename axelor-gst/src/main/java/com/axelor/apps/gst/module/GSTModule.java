package com.axelor.apps.gst.module;

import com.axelor.app.AxelorModule;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.apps.gst.service.GSTInvoiceLineService;
import com.axelor.apps.gst.service.GSTInvoiceLineServiceImpl;
import com.axelor.apps.gst.service.GSTInvoiceServiceImpl;
import com.axelor.apps.gst.service.GSTProductService;
import com.axelor.apps.gst.service.GSTProductServiceImpl;

public class GSTModule extends AxelorModule {

  @Override
  protected void configure() {
    bind(GSTInvoiceLineService.class).to(GSTInvoiceLineServiceImpl.class);
    bind(GSTProductService.class).to(GSTProductServiceImpl.class);
    bind(InvoiceServiceProjectImpl.class).to(GSTInvoiceServiceImpl.class);
  }
}
