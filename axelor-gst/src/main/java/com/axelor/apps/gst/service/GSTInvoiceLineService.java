package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public interface GSTInvoiceLineService {

  InvoiceLine computeInvoiceLineGstValues(Invoice invoice, InvoiceLine invoiceLine);
}
