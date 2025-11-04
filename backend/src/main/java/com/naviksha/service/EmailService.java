package com.naviksha.service;

import com.naviksha.model.StudentReport;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    
    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final PDFServiceClient pdfServiceClient;
    
    @Value("${email.enabled:true}")
    private boolean emailEnabled;
    
    @Value("${email.from}")
    private String fromEmail;
    
    @Value("${email.from-name}")
    private String fromName;
    
    /**
     * Send career report PDF via email
     * 
     * @param studentReport The generated student report
     * @param recipientEmail The email address to send the report to
     * @param studentName The name of the student
     */
    public void sendReportEmail(StudentReport studentReport, String recipientEmail, String studentName) {
        if (!emailEnabled) {
            log.info("Email service is disabled, skipping email send for student: {}", studentName);
            return;
        }
        
        try {
            log.info("Sending career report email to: {} for student: {}", recipientEmail, studentName);
            
            // Log report enhancement status for debugging
            log.info("Report enhancement status - AI Enhanced: {}, Has Enhanced Summary: {}, Has Skills: {}, Has Trajectory: {}", 
                studentReport.getAiEnhanced(),
                studentReport.getEnhancedSummary() != null && !studentReport.getEnhancedSummary().isEmpty(),
                studentReport.getSkillRecommendations() != null && !studentReport.getSkillRecommendations().isEmpty(),
                studentReport.getCareerTrajectoryInsights() != null && !studentReport.getCareerTrajectoryInsights().isEmpty());
            
            // Generate PDF using Node.js service (same logic as frontend)
            byte[] pdfBytes = pdfServiceClient.generatePDF(studentReport);
            
            // Create email message
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            // Set email details
            helper.setFrom(fromEmail, fromName);
            helper.setTo(recipientEmail);
            helper.setSubject("Your Career Assessment Report - " + studentName);
            
            // Prepare template context
            Context context = new Context();
            context.setVariable("studentName", studentName);
            context.setVariable("reportDate", java.time.LocalDate.now());
            context.setVariable("topCareers", studentReport.getTop5Buckets().stream()
                .limit(3)
                .map(bucket -> bucket.getTopCareers().get(0).getCareerName())
                .toList());
            
            // Generate HTML content from template
            String htmlContent = templateEngine.process("email/report-template", context);
            helper.setText(htmlContent, true);
            
            // Attach PDF
            ByteArrayResource pdfResource = new ByteArrayResource(pdfBytes);
            helper.addAttachment("Career_Report_" + studentName.replaceAll("\\s+", "_") + ".pdf", pdfResource);
            
            // Send email
            mailSender.send(message);
            
            log.info("Successfully sent career report email to: {} for student: {}", recipientEmail, studentName);
            
        } catch (MessagingException e) {
            log.error("Failed to send email to: {} for student: {}", recipientEmail, studentName, e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending email to: {} for student: {}", recipientEmail, studentName, e);
            throw new RuntimeException("Unexpected error sending email: " + e.getMessage(), e);
        }
    }
    
    /**
     * Send a simple notification email without PDF attachment
     * 
     * @param recipientEmail The email address to send to
     * @param subject The email subject
     * @param templateName The Thymeleaf template name
     * @param contextVariables Variables for the template
     */
    public void sendNotificationEmail(String recipientEmail, String subject, String templateName, Map<String, Object> contextVariables) {
        if (!emailEnabled) {
            log.info("Email service is disabled, skipping notification email to: {}", recipientEmail);
            return;
        }
        
        try {
            log.info("Sending notification email to: {} with subject: {}", recipientEmail, subject);
            
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setFrom(fromEmail, fromName);
            helper.setTo(recipientEmail);
            helper.setSubject(subject);
            
            // Prepare template context
            Context context = new Context();
            contextVariables.forEach(context::setVariable);
            
            // Generate HTML content from template
            String htmlContent = templateEngine.process(templateName, context);
            helper.setText(htmlContent, true);
            
            // Send email
            mailSender.send(message);
            
            log.info("Successfully sent notification email to: {}", recipientEmail);
            
        } catch (MessagingException e) {
            log.error("Failed to send notification email to: {}", recipientEmail, e);
            throw new RuntimeException("Failed to send notification email: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error sending notification email to: {}", recipientEmail, e);
            throw new RuntimeException("Unexpected error sending notification email: " + e.getMessage(), e);
        }
    }
}