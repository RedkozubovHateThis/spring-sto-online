package io.swagger.service;

import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ClientResponse;
import io.swagger.response.report.ExecutorResponse;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReportPOIService {
    byte[] getExecutorsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException;
    byte[] getClientsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, DataNotFoundException;
}
