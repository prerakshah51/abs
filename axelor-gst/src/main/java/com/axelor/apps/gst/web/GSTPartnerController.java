package com.axelor.apps.gst.web;

import com.axelor.rpc.ActionRequest;
import com.axelor.rpc.ActionResponse;

public class GSTPartnerController {

  public void setHiddenFields(ActionRequest req, ActionResponse res) {

    res.setAttr("taxNbr", "hidden", " ");
    // res.setAttr("gstin", "hidden", " ");
  }
}
