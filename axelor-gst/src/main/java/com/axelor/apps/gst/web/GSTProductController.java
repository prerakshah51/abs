package com.axelor.apps.gst.web;

import java.lang.invoke.MethodHandles;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.axelor.apps.ReportFactory;
import com.axelor.apps.account.db.Invoice;
import com.axelor.apps.account.db.InvoiceLine;
import com.axelor.apps.account.db.repo.InvoiceRepository;
import com.axelor.apps.base.db.Product;
import com.axelor.apps.base.db.repo.ProductBaseRepository;
import com.axelor.apps.base.service.app.AppBaseService;
import com.axelor.apps.base.service.user.UserService;
import com.axelor.apps.gst.report.IReport;
import com.axelor.apps.report.engine.ReportSettings;
import com.axelor.auth.db.User;
import com.axelor.exception.AxelorException;
import com.axelor.i18n.I18n;
import com.axelor.inject.Beans;
import com.axelor.meta.schema.actions.ActionView;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;
import com.google.common.base.Joiner;
import com.google.inject.Inject;

public class GSTProductController {

  private final Logger logger = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

  @Inject
  ProductBaseRepository productBaseRepo;

  // Get product with all details to InvoiceLine
  @SuppressWarnings({"unused", "unchecked"})
  public void getGstProductToInvoiceLine(ActionRequest request, ActionResponse response) {
    List<Integer> idList = (List<Integer>) request.getContext().get("_ids");
    if (idList != null) {
      List<InvoiceLine> invoiceLineList = new ArrayList<InvoiceLine>();
      int i = 0;
      for (Integer id : idList) {
        long temp = idList.get(i);
        InvoiceLine invoiceLine = new InvoiceLine();
        invoiceLine.setProduct(productBaseRepo.find(temp));
        invoiceLineList.add(invoiceLine);
        invoiceLine.setHsbn(productBaseRepo.find(temp).getHsbn());
        invoiceLine.setPrice(productBaseRepo.find(temp).getSalePrice());
        invoiceLine.setProductName(productBaseRepo.find(temp).getName());
        invoiceLine.setUnit(productBaseRepo.find(temp).getUnit());
        invoiceLine.setQty(new BigDecimal(1));
        invoiceLine
            .setTaxLine(productBaseRepo.find(temp).getProductFamily().getTax().getActiveTaxLine());
        i++;
      }
      response.setView(ActionView.define("Create Invoice").model(Invoice.class.getName())
          .add("form", "gst-invoice-form").context("invoiceitem", invoiceLineList).map());
    } else {
      response.setFlash("No record selected!");
    }
  }

  // Set product with all details to InvoiceLine
  @SuppressWarnings("unchecked")
  public void setGstProductToInvoiceLine(ActionRequest request, ActionResponse response) {
    List<Integer> invoiceLineList = (List<Integer>) request.getContext().get("invoiceitem");
    response.setValue("invoiceLineList", invoiceLineList);
    response.setValue("operationTypeSelect", InvoiceRepository.OPERATION_TYPE_CLIENT_SALE);
    // response.setValue("operationTypeSelect", InvoiceRepository.OPERATION_TYPE_SUPPLIER_PURCHASE);
  }

  // Set product's GST Rate from Product Family to Product
  public void setProductGstRate(ActionRequest request, ActionResponse response) {
    Product product = request.getContext().asType(Product.class);
    if (product.getProductFamily().getTax() != null) {
      BigDecimal taxRate = product.getProductFamily().getTax().getActiveTaxLine().getValue();
      response.setValue("gstRate", taxRate);
    } else {
      response.setFlash("No Tax defined for selected Product Family!");
    }
  }

  // New method to Print Product Catalog with GST Details
  @SuppressWarnings("unchecked")
  public void printGstProductCatalog(ActionRequest request, ActionResponse response)
      throws AxelorException {

    User user = Beans.get(UserService.class).getUser();

    int currentYear = Beans.get(AppBaseService.class).getTodayDateTime().getYear();
    String productIds = "";

    List<Integer> lstSelectedProduct = (List<Integer>) request.getContext().get("_ids");

    if (lstSelectedProduct != null) {
      productIds = Joiner.on(",").join(lstSelectedProduct);
    }

    String name = I18n.get("Product Catalog");

    String fileLink = ReportFactory.createReport(IReport.PRODUCT_CATALOG, name + "-${date}")
        .addParam("UserId", user.getId()).addParam("CurrYear", Integer.toString(currentYear))
        .addParam("ProductIds", productIds)
        .addParam("Locale", ReportSettings.getPrintingLocale(null)).generate().getFileLink();

    logger.debug("Printing " + name);

    response.setView(ActionView.define(name).add("html", fileLink).map());
  }
}
