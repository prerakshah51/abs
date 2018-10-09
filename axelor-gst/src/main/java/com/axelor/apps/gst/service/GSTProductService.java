package com.axelor.apps.gst.service;

import com.axelor.apps.account.db.Invoice;

public interface GSTProductService {

  Invoice addressChangeComputes(Invoice invoice);

}
