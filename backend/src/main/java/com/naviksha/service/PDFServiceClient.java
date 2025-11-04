package com.naviksha.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.naviksha.model.StudentReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;

import java.util.Map;

/**
 * PDF Service Client
 * 
 * Handles communication with the PDF generation microservice (Node.js)
 * Uses the same jsPDF logic as the frontend
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PDFServiceClient {
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    
    @Value("${pdf.service.url:http://pdf-service:5100}")
    private String pdfServiceUrl;
    
    @Value("${pdf.service.enabled:true}")
    private boolean pdfServiceEnabled;
    
    @Value("${pdf.service.timeout:60000}")
    private int timeout;
    
    /**
     * Generate PDF from StudentReport using the Node.js PDF service
     * (which uses the same jsPDF logic as the frontend)
     * 
     * @param studentReport The student report to generate PDF from
     * @return PDF bytes
     */
    public byte[] generatePDF(StudentReport studentReport) {
        if (!pdfServiceEnabled) {
            log.warn("PDF service is disabled, cannot generate PDF");
            throw new RuntimeException("PDF service is disabled");
        }
        
        try {
            log.info("Calling PDF service to generate PDF for student: {}", studentReport.getStudentName());
            
            // Convert StudentReport to JSON format expected by PDF service
            Map<String, Object> reportData = convertStudentReportToMap(studentReport);
            
            // Prepare request headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            // Create request entity
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(reportData, headers);
            
            // Call PDF service
            ResponseEntity<byte[]> response = restTemplate.exchange(
                pdfServiceUrl + "/generate-pdf",
                HttpMethod.POST,
                requestEntity,
                byte[].class
            );
            
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                log.info("Successfully generated PDF from PDF service");
                return response.getBody();
            } else {
                log.warn("PDF service returned unexpected status: {}", response.getStatusCode());
                throw new RuntimeException("PDF service returned unexpected status: " + response.getStatusCode());
            }
            
        } catch (HttpClientErrorException e) {
            log.error("PDF service client error (4xx): {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("PDF service client error: " + e.getMessage(), e);
            
        } catch (HttpServerErrorException e) {
            log.error("PDF service server error (5xx): {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("PDF service server error: " + e.getMessage(), e);
            
        } catch (ResourceAccessException e) {
            log.error("PDF service connection timeout or unavailable: {}", e.getMessage());
            throw new RuntimeException("PDF service unavailable: " + e.getMessage(), e);
            
        } catch (Exception e) {
            log.error("Unexpected error calling PDF service: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
    
    /**
     * Check if PDF service is healthy
     * 
     * @return true if PDF service is available, false otherwise
     */
    public boolean isHealthy() {
        if (!pdfServiceEnabled) {
            return false;
        }
        
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                pdfServiceUrl + "/health",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<Map<String, Object>>() {}
            );
            
            return response.getStatusCode() == HttpStatus.OK;
            
        } catch (Exception e) {
            log.debug("PDF service health check failed: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * Convert StudentReport to Map format expected by PDF service
     * Matches the ReportData interface in src/lib/pdf-generator.ts
     */
    private Map<String, Object> convertStudentReportToMap(StudentReport studentReport) {
        try {
            // Convert StudentReport to JSON string first, then parse as Map
            // This ensures all nested objects are properly converted
            String json = objectMapper.writeValueAsString(studentReport);
            @SuppressWarnings("unchecked")
            Map<String, Object> map = objectMapper.readValue(json, Map.class);
            
            // Rename fields to match frontend expectations
            if (map.containsKey("vibeScores")) {
                map.put("vibe_scores", map.get("vibeScores"));
            }
            if (map.containsKey("top5Buckets")) {
                map.put("top5_buckets", map.get("top5Buckets"));
            }
            
            return map;
            
        } catch (Exception e) {
            log.error("Error converting StudentReport to Map: {}", e.getMessage());
            throw new RuntimeException("Failed to convert StudentReport to Map", e);
        }
    }
}

