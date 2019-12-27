package io.swagger.service;

import java.util.Date;
import java.util.List;

public interface DocumentServiceDetailService {
    List<Integer> collectPaidDocumentIds(Date startDate, Date endDate);

    Date getFirstSubscriptionDate();
}
