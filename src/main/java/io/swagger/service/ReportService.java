package io.swagger.service;

import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ClientResponse;
import io.swagger.response.report.ExecutorResponse;
import net.sf.jasperreports.engine.JRException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReportService {
    byte[] getOrderReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderActReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderRequestReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderTaskReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderRequirementReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderInspectionReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderReceiptReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderDefectionReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getOrderTransferReport(Integer documentId) throws IOException, JRException, DataNotFoundException;

    byte[] getExecutorsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException;

    byte[] getClientsReport(Integer organizationId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException;

    List<ExecutorResponse> getExecutorResponses(Integer organizationId, Date startDate, Date endDate);

    List<ExecutorResponse> getExecutorFakeResponses(Date startDate, Date endDate);

    List<ClientResponse> getClientFakeResponses(Date startDate, Date endDate);

    List<ClientResponse> getClientsResponses(Integer organizationId, Date startDate, Date endDate);
}
