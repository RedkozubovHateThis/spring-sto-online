package io.swagger.service;

import io.swagger.response.exception.DataNotFoundException;
import io.swagger.response.report.ClientResponse;
import io.swagger.response.report.ExecutorResponse;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface ReportService {
    byte[] getOrderReport(Long documentId, String orderName) throws IOException, JRException, DataNotFoundException;
    byte[] getOrderPaymentReport(Long documentId) throws IOException, JRException, DataNotFoundException;
    List<ClientResponse> getClientsResponses(Long profileId, Date startDate, Date endDate);
    byte[] getClientsReport(Long profileId, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException;
    List<ClientResponse> getVehiclesResponses(Long profileId, String vinNumber, Date startDate, Date endDate);
    byte[] getVehiclesReport(Long profileId, String vinNumber, Date startDate, Date endDate) throws IOException, JRException, DataNotFoundException;
    List<ExecutorResponse> getRegisteredResponses(Long profileId);
    byte[] getRegisteredReport(Long profileId) throws IOException, JRException, DataNotFoundException;
}
