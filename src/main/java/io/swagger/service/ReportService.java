package io.swagger.service;

import io.swagger.response.exception.DataNotFoundException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;

public interface ReportService {
    byte[] getExecutorsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException;

    byte[] getClientsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException;
}
