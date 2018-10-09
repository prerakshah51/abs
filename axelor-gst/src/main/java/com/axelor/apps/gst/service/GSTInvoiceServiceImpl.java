package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.account.service.invoice.factory.CancelFactory;
import com.axelor.apps.account.service.invoice.factory.ValidateFactory;
import com.axelor.apps.account.service.invoice.factory.VentilateFactory;
import com.axelor.apps.base.service.alarm.AlarmEngineService;
import com.axelor.apps.businessproject.service.InvoiceServiceProjectImpl;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class GSTInvoiceServiceImpl extends InvoiceServiceProjectImpl {

  @Inject
  public GSTInvoiceServiceImpl(ValidateFactory validateFactory, VentilateFactory ventilateFactory,
      CancelFactory cancelFactory, AlarmEngineService<Invoice> alarmEngineService,
      InvoiceRepository invoiceRepo, AppAccountService appAccountService) {
    super(validateFactory, ventilateFactory, cancelFactory, alarmEngineService, invoiceRepo,
        appAccountService);
    // TODO Auto-generated constructor stub
  }

  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    BigDecimal netIgst = BigDecimal.ZERO;
    BigDecimal netSgst = BigDecimal.ZERO;
    BigDecimal netCgst = BigDecimal.ZERO;
    for (InvoiceLine invoiceLine : invoiceLineList) {
      netIgst = netIgst.add(invoiceLine.getIgst());
      netCgst = netCgst.add(invoiceLine.getCgst());
      netSgst = netSgst.add(invoiceLine.getSgst());
    }
    invoice.setNetIgst(netIgst);
    invoice.setNetCgst(netCgst);
    invoice.setNetSgst(netSgst);
    invoice = super.compute(invoice);
    return invoice;
  }
}
