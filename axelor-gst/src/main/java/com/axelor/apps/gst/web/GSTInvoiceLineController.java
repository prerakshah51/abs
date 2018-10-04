package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.db.JpaSupport;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class GSTInvoiceLineController extends JpaSupport {

  public void computeInvoiceLineGstValues(ActionRequest req, ActionResponse res) {
    Invoice invoice = req.getContext().getParent().asType(Invoice.class);
    InvoiceLine invoiceLine = req.getContext().asType(InvoiceLine.class);
    BigDecimal gstRate = invoiceLine.getTaxLine().getTax().getActiveTaxLine().getValue();
    BigDecimal price = invoiceLine.getExTaxTotal();
    BigDecimal ans = price.multiply(gstRate);
    String partnerAddressState = invoice.getAddress().getCity().getState().getName();
    String companyAddressState = invoice.getCompany().getAddress().getCity().getState().getName();
    if (partnerAddressState.equals(companyAddressState)) {
      res.setValue("sgst", ans.divide(new BigDecimal(2)));
      res.setValue("cgst", ans.divide(new BigDecimal(2)));
    } else if (!partnerAddressState.equals(companyAddressState)) {
      res.setValue("igst", ans);
    }
  }
}
