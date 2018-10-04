package com.axelor.apps.gst.web;

import java.math.BigDecimal;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.exception.AxelorException;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class GSTInvoiceController {

  public void computeInvoiceGstValues(ActionRequest req, ActionResponse res)
      throws AxelorException {
    Invoice invoice = req.getContext().asType(Invoice.class);
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    BigDecimal netIgst = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    for (InvoiceLine invoiceLine : invoiceLineList) {
      netIgst = netIgst.add(invoiceLine.getIgst());
      netCgst = netCgst.add(invoiceLine.getCgst());
      netSgst = netSgst.add(invoiceLine.getSgst());
    }
    res.setValue("netIgst", netIgst);
    res.setValue("netSgst", netSgst);
    res.setValue("netCgst", netCgst);
  }
}
