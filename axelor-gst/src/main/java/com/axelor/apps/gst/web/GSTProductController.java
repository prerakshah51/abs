package com.axelor.apps.gst.web;

import com.axelor.apps.base.db.Product;
import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class GSTProductController {

  public void setProductGstRate(ActionRequest request, ActionResponse response) {
    Product product = request.getContext().asType(Product.class);
    response.setValue("gstRate", product.getProductCategory().getGstRate());
  }
}
