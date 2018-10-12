package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
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

  // Overridden method to Compute Invoice GST Data
  @Override
  public Invoice compute(Invoice invoice) throws AxelorException {
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    if (invoiceLineList != null) {
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
    }
    return invoice;
  }

  // Method for recompute GST data on-Change of Partner
  public Invoice addressChangeComputes(Invoice invoice) {
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    List<InvoiceLine> invoiceLineListNew = new ArrayList<InvoiceLine>();
    String companyState = invoice.getCompany().getAddress().getState().getName();
    String partnerState = invoice.getAddress().getState().getName();
    for (InvoiceLine invoiceLine : invoiceLineList) {
      InvoiceLine invoiceLineNew = new InvoiceLine();
      invoiceLineNew.setProduct(invoiceLine.getProduct());
      invoiceLineNew.setHsbn(invoiceLine.getHsbn());
      invoiceLineNew.setProductName(invoiceLine.getProductName());
      invoiceLineNew.setUnit(invoiceLine.getUnit());
      invoiceLineNew.setTaxLine(invoiceLine.getTaxLine());
      invoiceLineNew.setQty(invoiceLine.getQty());
      invoiceLineNew.setPrice(invoiceLine.getPrice());
      invoiceLineNew.setExTaxTotal(invoiceLine.getExTaxTotal());
      if (companyState.equals(partnerState)) {
        invoiceLineNew.setSgst(invoiceLine.getIgst().divide(new BigDecimal(2)));
        invoiceLineNew.setCgst(invoiceLine.getIgst().divide(new BigDecimal(2)));
        invoiceLineNew.setIgst(BigDecimal.ZERO);
      } else if (!companyState.equals(partnerState)) {
        invoiceLineNew.setIgst(invoiceLine.getSgst().add(invoiceLine.getCgst()));
        invoiceLineNew.setSgst(BigDecimal.ZERO);
        invoiceLineNew.setCgst(BigDecimal.ZERO);
      }
      invoiceLineListNew.add(invoiceLineNew);
    }
    invoice.setInvoiceLineList(invoiceLineListNew);
    return invoice;
  }
}
