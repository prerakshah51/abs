package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.base.db.Product;

public class GSTProductServiceImpl implements GSTProductService {

  @Override
  public Invoice addressChangeComputes(Invoice invoice) {
    if (invoice.getInvoiceLineList() != null) {
      List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
      BigDecimal netAmount = BigDecimal.ZERO;
      BigDecimal igst = BigDecimal.ZERO;
      BigDecimal cgst = BigDecimal.ZERO;
      BigDecimal sgst = BigDecimal.ZERO;
      BigDecimal grossAmount = BigDecimal.ZERO;

      String companyAddressState = invoice.getCompany().getAddress().getState().getName();
      String invoiceAddressState = invoice.getAddress().getState().getName();

      for (int i = 0; i < invoice.getInvoiceLineList().size(); i++) {
        InvoiceLine invoiceLine = new InvoiceLine();
        Product product = invoice.getInvoiceLineList().get(i).getProduct();
        invoiceLine.setProduct(product);
        String hsbn = invoice.getInvoiceLineList().get(i).getHsbn();
        invoiceLine.setHsbn(hsbn);
        String item = invoice.getInvoiceLineList().get(i).getProductName();
        invoiceLine.setProductName(item);
        BigDecimal qty = invoice.getInvoiceLineList().get(i).getQty();
        invoiceLine.setQty(qty);
        BigDecimal price = invoice.getInvoiceLineList().get(i).getPrice();
        invoiceLine.setPrice(price);
        netAmount = price.multiply(qty);
        invoiceLine.setExTaxTotal(netAmount);
        TaxLine gstRate = invoice.getInvoiceLineList().get(i).getTaxLine();
        invoiceLine.setTaxLine(gstRate);

        if (!invoiceAddressState.equals(companyAddressState)) {
          igst = netAmount.multiply(new BigDecimal(1));
          invoiceLine.setIgst(igst);
          grossAmount = netAmount.add(igst);
          invoiceLine.setPrice(grossAmount);
        }

        else if (invoiceAddressState.equals(companyAddressState)) {
          BigDecimal temp = BigDecimal.ZERO;
          temp = netAmount.multiply(new BigDecimal(1));
          sgst = temp.divide(new BigDecimal("2"));
          cgst = sgst;
          invoiceLine.setSgst(sgst);
          invoiceLine.setCgst(cgst);
          temp = sgst.add(cgst);
          grossAmount = temp.add(netAmount);
          invoiceLine.setPrice(grossAmount);
        }

        invoiceLineList.add(invoiceLine);
      }

      invoice.setInvoiceLineList(invoiceLineList);
    }

    return invoice;
  }

}
