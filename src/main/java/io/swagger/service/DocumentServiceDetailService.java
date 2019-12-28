package io.swagger.service;

import io.swagger.postgres.model.security.User;

import java.util.Date;
import java.util.List;

public interface DocumentServiceDetailService {
    List<Integer> collectPaidDocumentIds(Date startDate, Date endDate);

    Date getFirstSubscriptionDate(User user);
}
