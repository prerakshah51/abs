package com.axelor.apps.gst.service;

import java.math.BigDecimal;
import java.util.Map;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.TaxLine;
import com.axelor.apps.account.service.AccountManagementAccountService;
import com.axelor.apps.account.service.AnalyticMoveLineService;
import com.axelor.apps.account.service.app.AppAccountService;
import com.axelor.apps.base.service.CurrencyService;
import com.axelor.apps.base.service.PriceListService;
import com.axelor.apps.base.service.tax.AccountManagementService;
import com.axelor.apps.purchase.service.PurchaseProductService;
import com.axelor.apps.supplychain.service.InvoiceLineSupplychainService;
import com.axelor.exception.AxelorException;
import com.google.inject.Inject;

public class GSTInvoiceLineServiceImpl extends InvoiceLineSupplychainService {

  @Inject
  public GSTInvoiceLineServiceImpl(AccountManagementService accountManagementService,
      CurrencyService currencyService, PriceListService priceListService,
      AppAccountService appAccountService, AnalyticMoveLineService analyticMoveLineService,
      AccountManagementAccountService accountManagementAccountService,
      PurchaseProductService purchaseProductService) {
    super(accountManagementService, currencyService, priceListService, appAccountService,
        analyticMoveLineService, accountManagementAccountService, purchaseProductService);
    // TODO Auto-generated constructor stub
  }

  // Overridden method to Calculate InvoiceLine Data
  @Override
  public Map<String, Object> fillProductInformation(Invoice invoice, InvoiceLine invoiceLine)
      throws AxelorException {
    Map<String, Object> productInformation = super.fillProductInformation(invoice, invoiceLine);
    boolean isPurchase = this.isPurchase(invoice);
    if (invoice.getCompany().getAddress().getState() != null
        && invoice.getAddress().getState() != null) {
      TaxLine taxLine;
      if (invoiceLine.getTaxLine() == null) {
        taxLine = invoiceLine.getProduct().getProductFamily().getTax().getActiveTaxLine();
      } else {
        taxLine = invoiceLine.getTaxLine();
      }
      String companyState = invoice.getCompany().getAddress().getState().getName();
      String partnerState = invoice.getAddress().getState().getName();
      productInformation.put("taxLine", taxLine);
      String hsbn = invoiceLine.getProduct().getHsbn();
      productInformation.put("hsbn", hsbn);
      BigDecimal price = this.getExTaxUnitPrice(invoice, invoiceLine, taxLine, isPurchase);
      BigDecimal qty = invoiceLine.getQty();
      BigDecimal exTaxTotal = price.multiply(qty);
      BigDecimal applicableTax = exTaxTotal.multiply(taxLine.getValue());
      if (companyState.equals(partnerState)) {
        BigDecimal sgst = applicableTax.divide(new BigDecimal(2));
        BigDecimal cgst = sgst;
        productInformation.put("sgst", sgst);
        productInformation.put("cgst", cgst);
        productInformation.put("igst", BigDecimal.ZERO);
      } else if (!companyState.equals(partnerState)) {
        BigDecimal igst = applicableTax;
        productInformation.put("igst", igst);
        productInformation.put("sgst", BigDecimal.ZERO);
        productInformation.put("cgst", BigDecimal.ZERO);
      }
      return productInformation;
    }
    return super.fillProductInformation(invoice, invoiceLine);
  }
}
