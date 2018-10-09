package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public class GSTInvoiceLineServiceImpl implements GSTInvoiceLineService {

  @Override
  public InvoiceLine computeInvoiceLineGstValues(Invoice invoice, InvoiceLine invoiceLine) {

    BigDecimal gstRate = invoiceLine.getTaxRate();
    BigDecimal price = invoiceLine.getExTaxTotal();
    BigDecimal ans = price.multiply(gstRate);
    String partnerAddressState = invoice.getAddress().getState().getName();
    String companyAddressState = invoice.getCompany().getAddress().getState().getName();
    if (partnerAddressState != null && companyAddressState != null) {
      if (partnerAddressState.equals(companyAddressState)) {
        invoiceLine.setSgst(ans.divide(new BigDecimal(2)));
        invoiceLine.setCgst(ans.divide(new BigDecimal(2)));
      } else if (!partnerAddressState.equals(companyAddressState)) {
        invoiceLine.setIgst(ans);
      }
    }
    return invoiceLine;
  }
}
