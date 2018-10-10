package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;

public class GSTProductServiceImpl implements GSTProductService {

  @Override
  public Invoice addressChangeComputes(Invoice invoice) {
    List<InvoiceLine> invoiceLineListNew = new ArrayList<InvoiceLine>();
    List<InvoiceLine> invoiceLineList = invoice.getInvoiceLineList();
    BigDecimal exTaxTotal = BigDecimal.ZERO;
    BigDecimal igst = BigDecimal.ZERO;
    BigDecimal cgst = BigDecimal.ZERO;
    BigDecimal sgst = BigDecimal.ZERO;
    if (invoice.getCompany().getAddress().getState() != null
        && invoice.getAddress().getState() != null) {
      String companyAddressState = invoice.getCompany().getAddress().getState().getName();
      String invoiceAddressState = invoice.getAddress().getState().getName();
      for (InvoiceLine invoiceLine : invoiceLineList) {
        InvoiceLine invoiceLineNew = new InvoiceLine();
        invoiceLineNew.setProduct(invoiceLine.getProduct());
        invoiceLineNew.setHsbn(invoiceLine.getHsbn());
        invoiceLineNew.setProductName(invoiceLine.getProductName());
        invoiceLineNew.setUnit(invoiceLine.getUnit());
        invoiceLineNew.setTaxLine(invoiceLine.getTaxLine());
        BigDecimal taxRate = invoiceLine.getTaxRate();
        BigDecimal qty = invoiceLine.getQty();
        invoiceLineNew.setQty(qty);
        BigDecimal price = invoiceLine.getPrice();
        invoiceLineNew.setPrice(price);
        exTaxTotal = price.multiply(qty);
        invoiceLineNew.setExTaxTotal(exTaxTotal);
        if (!invoiceAddressState.equals(companyAddressState)) {
          igst = exTaxTotal.multiply(taxRate);
          invoiceLineNew.setIgst(igst);
        } else if (invoiceAddressState.equals(companyAddressState)) {
          BigDecimal temp = BigDecimal.ZERO;
          temp = exTaxTotal.multiply(taxRate);
          sgst = temp.divide(new BigDecimal("2"));
          cgst = sgst;
          invoiceLineNew.setSgst(sgst);
          invoiceLineNew.setCgst(cgst);
        }
        invoiceLineListNew.add(invoiceLineNew);
      }
      invoice.setInvoiceLineList(invoiceLineListNew);
    }
    return invoice;
  }
}
