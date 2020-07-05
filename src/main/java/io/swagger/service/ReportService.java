package io.swagger.service;

import io.swagger.response.exception.DataNotFoundException;
import net.sf.jasperreports.engine.JRException;

import java.io.IOException;

public interface ReportService {
    byte[] getOrderReport(Long documentId) throws IOException, JRException, DataNotFoundException;
}
