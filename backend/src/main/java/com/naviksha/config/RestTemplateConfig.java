package com.naviksha.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * RestTemplate Configuration
 * 
 * Configures RestTemplate for external service calls
 * Separate RestTemplate instances for AI and PDF services with different timeouts
 */
@Configuration
public class RestTemplateConfig {
    
    /**
     * RestTemplate for AI service calls
     * Uses AI service timeout (default: 5 minutes / 300 seconds)
     */
    @Bean("aiRestTemplate")
    public RestTemplate aiRestTemplate(AIServiceConfig aiServiceConfig) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(aiServiceConfig.getTimeout());
        factory.setReadTimeout(aiServiceConfig.getTimeout());
        
        return new RestTemplate(factory);
    }
    
    /**
     * RestTemplate for PDF service calls
     * Uses PDF service timeout (default: 60 seconds)
     */
    @Bean("pdfRestTemplate")
    public RestTemplate pdfRestTemplate(PDFServiceConfig pdfServiceConfig) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(pdfServiceConfig.getTimeout());
        factory.setReadTimeout(pdfServiceConfig.getTimeout());
        
        return new RestTemplate(factory);
    }
}
