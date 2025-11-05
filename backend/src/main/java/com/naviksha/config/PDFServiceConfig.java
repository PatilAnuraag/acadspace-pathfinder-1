package com.naviksha.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * PDF Service Configuration
 * 
 * Configuration properties for the PDF generation microservice
 */
@Configuration
@ConfigurationProperties(prefix = "pdf.service")
@Data
public class PDFServiceConfig {
    
    /**
     * PDF service base URL
     * Default: http://localhost:5100
     */
    private String url = "http://localhost:5100";
    
    /**
     * Request timeout in milliseconds
     * Default: 60000 (60 seconds) - PDF generation should be relatively fast
     */
    private int timeout = 60000;
    
    /**
     * Whether PDF service is enabled
     * Default: true
     */
    private boolean enabled = true;
    
    /**
     * Get the full URL for the generate PDF endpoint
     */
    public String getGeneratePdfUrl() {
        return url + "/generate-pdf";
    }
    
    /**
     * Get the health check URL
     */
    public String getHealthCheckUrl() {
        return url + "/health";
    }
}

