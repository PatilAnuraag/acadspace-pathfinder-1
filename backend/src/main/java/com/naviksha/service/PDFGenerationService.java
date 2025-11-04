package com.naviksha.service;

import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.events.Event;
import com.itextpdf.kernel.events.IEventHandler;
import com.itextpdf.kernel.events.PdfDocumentEvent;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.naviksha.model.StudentReport;
import com.naviksha.model.CareerBucket;
import com.naviksha.model.CareerMatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class PDFGenerationService {
    
    // Color constants matching email template
    private static final DeviceRgb PRIMARY_BLUE = new DeviceRgb(37, 99, 235); // #2563eb
    private static final DeviceRgb HEADING_BLUE = new DeviceRgb(37, 99, 235); // #2563eb
    private static final DeviceRgb LIGHT_BLUE_BG = new DeviceRgb(239, 246, 255); // #eff6ff (highlight boxes)
    private static final DeviceRgb LIGHT_GRAY_BG = new DeviceRgb(249, 250, 251); // #f9fafb (career list bg)
    private static final DeviceRgb SUCCESS_GREEN = new DeviceRgb(16, 185, 129); // #10b981 (career item border)
    private static final DeviceRgb DARK_TEXT = new DeviceRgb(31, 41, 55); // #1f2937
    private static final DeviceRgb MUTED_TEXT = new DeviceRgb(107, 114, 128); // #6b7280
    private static final DeviceRgb LIGHT_GRAY = new DeviceRgb(200, 200, 200);
    private static final DeviceRgb BORDER_GRAY = new DeviceRgb(229, 231, 235); // #e5e7eb
    
    // Font sizes matching frontend
    private static final float TITLE_SIZE = 20f;
    private static final float HEADING_SIZE = 12f;
    private static final float BODY_SIZE = 9f;
    private static final float SMALL_SIZE = 8f;
    
    // RIASEC descriptions
    private static final Map<String, String> RIASEC_DESCRIPTIONS = Map.of(
        "R", "Realistic - Practical, hands-on problem solver",
        "I", "Investigative - Analytical, research-oriented thinker",
        "A", "Artistic - Creative, innovative, expressive",
        "S", "Social - Helpful, collaborative, people-focused",
        "E", "Enterprising - Leadership, persuasive, goal-driven",
        "C", "Conventional - Organized, detail-oriented, systematic"
    );
    
    /**
     * Generate PDF report from StudentReport data
     * 
     * @param studentReport The student report data
     * @return PDF as byte array
     */
    public byte[] generateReportPDF(StudentReport studentReport) {
        try {
            log.info("Generating PDF report for student: {}", studentReport.getStudentName());
            log.debug("PDF Generation - AI Enhanced: {}, Has Enhanced Summary: {}, Has Skills: {}, Has Trajectory: {}", 
                studentReport.getAiEnhanced(),
                studentReport.getEnhancedSummary() != null && !studentReport.getEnhancedSummary().isEmpty(),
                studentReport.getSkillRecommendations() != null && !studentReport.getSkillRecommendations().isEmpty(),
                studentReport.getCareerTrajectoryInsights() != null && !studentReport.getCareerTrajectoryInsights().isEmpty());
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            PdfWriter writer = new PdfWriter(outputStream);
            PdfDocument pdfDoc = new PdfDocument(writer);
            
            // Add footer handler for page numbers
            FooterHandler footerHandler = new FooterHandler(pdfDoc);
            pdfDoc.addEventHandler(PdfDocumentEvent.END_PAGE, footerHandler);
            
            Document document = new Document(pdfDoc);
            document.setMargins(42.5f, 42.5f, 50f, 42.5f); // ~15mm margins (42.5 points = 15mm)
            
            // Set up fonts
            PdfFont regularFont = PdfFontFactory.createFont();
            PdfFont boldFont = PdfFontFactory.createFont();
            
            // Add cover page
            addCoverPage(document, studentReport, boldFont, regularFont);
            
            // Add student information (no page break - starts on same page as cover)
            addStudentInformation(document, studentReport, boldFont, regularFont);
            
            // Add career profile summary
            addCareerProfileSummary(document, studentReport, boldFont, regularFont);
            
            // Add RIASEC personality profile
            addRiasecProfile(document, studentReport, boldFont, regularFont);
            
            // Add top career recommendations
            addCareerRecommendations(document, studentReport, boldFont, regularFont);
            
            // Add AI skills recommendations (if available)
            if (studentReport.getSkillRecommendations() != null && !studentReport.getSkillRecommendations().isEmpty()) {
                addSkillsRecommendations(document, studentReport, boldFont, regularFont);
            }
            
            // Add career trajectory insights (if available)
            if (studentReport.getCareerTrajectoryInsights() != null && !studentReport.getCareerTrajectoryInsights().isEmpty()) {
                addCareerTrajectoryInsights(document, studentReport, boldFont, regularFont);
            }
            
            // Add detailed career explanations (if available)
            if (studentReport.getDetailedCareerInsights() != null && !studentReport.getDetailedCareerInsights().isEmpty()) {
                addDetailedCareerExplanations(document, studentReport, boldFont, regularFont);
            }
            
            // Add recommended next steps
            addRecommendedNextSteps(document, studentReport, boldFont, regularFont);
            
            document.close();
            
            byte[] pdfBytes = outputStream.toByteArray();
            log.info("Successfully generated PDF report for student: {}, size: {} bytes", 
                studentReport.getStudentName(), pdfBytes.length);
            
            return pdfBytes;
            
        } catch (IOException e) {
            log.error("Error generating PDF for student: {}", studentReport.getStudentName(), e);
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
    }
    
    private void addCoverPage(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        // Header with centered logo and border (matching email template)
        Div headerDiv = new Div();
        headerDiv.setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(30)
            .setPaddingBottom(20)
            .setBorderBottom(new SolidBorder(PRIMARY_BLUE, 3f));
        
        // Logo (Naviksha AI)
        Paragraph logoPara = new Paragraph("Naviksha AI")
            .setFont(boldFont)
            .setFontSize(28f)
            .setFontColor(PRIMARY_BLUE)
            .setTextAlignment(TextAlignment.CENTER)
            .setMarginBottom(10);
        headerDiv.add(logoPara);
        
        // Subtitle
        Paragraph subtitlePara = new Paragraph("Your Dreams to Reality Handbook")
            .setFont(regularFont)
            .setFontSize(16f)
            .setFontColor(MUTED_TEXT)
            .setTextAlignment(TextAlignment.CENTER)
            .setMargin(0);
        headerDiv.add(subtitlePara);
        
        document.add(headerDiv);
        
        // Greeting
        Paragraph greetingPara = new Paragraph("Hello " + report.getStudentName() + "!")
            .setFont(regularFont)
            .setFontSize(18f)
            .setFontColor(DARK_TEXT)
            .setMarginBottom(20);
        document.add(greetingPara);
        
        // Congratulatory message in highlight box
        Div highlightBox = createHighlightBox("Congratulations on completing your career assessment! We're excited to share your personalized career report with you.", regularFont);
        document.add(highlightBox);
    }
    
    private void addStudentInformation(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        Paragraph sectionTitle = new Paragraph("Student Information")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(HEADING_BLUE)
            .setMarginTop(10)
            .setMarginBottom(5);
        document.add(sectionTitle);
        
        // Student info as simple text (matching frontend format)
        Paragraph namePara = new Paragraph("Name: " + report.getStudentName())
            .setFont(boldFont)
            .setFontSize(10f)
            .setMarginBottom(3);
        document.add(namePara);
        
        if (report.getGrade() != null || report.getBoard() != null) {
            String gradeBoard = "";
            if (report.getGrade() != null && report.getBoard() != null) {
                gradeBoard = "Grade: " + report.getGrade() + " • Board: " + report.getBoard();
            } else if (report.getGrade() != null) {
                gradeBoard = "Grade: " + report.getGrade();
            } else if (report.getBoard() != null) {
                gradeBoard = "Board: " + report.getBoard();
            }
            
            Paragraph gradeBoardPara = new Paragraph(gradeBoard)
            .setFont(regularFont)
                .setFontSize(BODY_SIZE)
                .setMarginBottom(3);
            document.add(gradeBoardPara);
        }
        
        Paragraph datePara = new Paragraph("Report Generated: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy")))
            .setFont(regularFont)
            .setFontSize(SMALL_SIZE)
            .setFontColor(MUTED_TEXT)
            .setMarginBottom(10);
        document.add(datePara);
    }
    
    private void addCareerProfileSummary(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        // Use enhancedSummary if available, fallback to summaryParagraph
        String summaryText = null;
        if (report.getEnhancedSummary() != null && !report.getEnhancedSummary().isEmpty()) {
            summaryText = report.getEnhancedSummary();
        } else if (report.getSummaryParagraph() != null && !report.getSummaryParagraph().isEmpty()) {
            summaryText = report.getSummaryParagraph();
        }
        
        if (summaryText != null) {
            // Create highlight box for summary
            Div summaryBox = createHighlightBox("📊 Your Assessment Results", summaryText, boldFont, regularFont);
            document.add(summaryBox);
        } else {
            Div summaryBox = createHighlightBox("📊 Your Assessment Results", "No summary available", boldFont, regularFont);
            document.add(summaryBox);
        }
    }
    
    private void addRiasecProfile(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        Paragraph sectionTitle = new Paragraph("RIASEC Personality & Interest Profile")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(HEADING_BLUE)
            .setMarginTop(5)
            .setMarginBottom(5);
        document.add(sectionTitle);
        
        if (report.getVibeScores() != null && !report.getVibeScores().isEmpty()) {
            for (Map.Entry<String, Integer> entry : report.getVibeScores().entrySet()) {
                String key = entry.getKey();
                Integer score = entry.getValue();
                String description = RIASEC_DESCRIPTIONS.getOrDefault(key, key);
                
                // Extract name (before first dash)
                String name = description.split(" - ")[0];
                
                // Type name and score
                Paragraph namePara = new Paragraph(name + ": " + score + "%")
                    .setFont(boldFont)
                    .setFontSize(BODY_SIZE)
                    .setMarginBottom(2);
                document.add(namePara);
                
                // Description
                String desc = description.contains(" - ") ? description.substring(description.indexOf(" - ") + 3) : "";
                if (!desc.isEmpty()) {
                    Paragraph descPara = new Paragraph(desc)
                        .setFont(regularFont)
                        .setFontSize(SMALL_SIZE)
                        .setMarginBottom(5);
                    document.add(descPara);
                }
                
                // Add light divider between items (except last)
                if (!entry.equals(report.getVibeScores().entrySet().stream().reduce((a, b) -> b).orElse(null))) {
                    addDivider(document, false);
                }
            }
        } else {
            Paragraph noData = new Paragraph("No personality assessment data available")
                .setFont(regularFont)
                .setFontSize(BODY_SIZE)
                .setItalic()
                .setMarginBottom(10);
            document.add(noData);
        }
    }
    
    private void addCareerRecommendations(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        Paragraph sectionTitle = new Paragraph("🎯 Your Top Career Matches")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(new DeviceRgb(5, 150, 105)) // #059669 (green like email template)
            .setMarginTop(5)
            .setMarginBottom(15);
        document.add(sectionTitle);
        
        if (report.getTop5Buckets() != null && !report.getTop5Buckets().isEmpty()) {
            // Create career list box (light gray background like email template)
            Div careerListBox = createCareerListBox();
            
            // Add top 3 careers from first bucket
            if (report.getTop5Buckets().get(0).getTopCareers() != null && !report.getTop5Buckets().get(0).getTopCareers().isEmpty()) {
                List<CareerMatch> topCareers = report.getTop5Buckets().get(0).getTopCareers().size() > 3 
                    ? report.getTop5Buckets().get(0).getTopCareers().subList(0, 3) 
                    : report.getTop5Buckets().get(0).getTopCareers();
                
                for (CareerMatch career : topCareers) {
                    // Create career item (white with green left border)
                    Div careerItem = createCareerItem(career.getCareerName() + " - " + career.getMatchScore() + "% Match", boldFont);
                    careerListBox.add(careerItem);
                }
            }
            
            document.add(careerListBox);
            
            // Add note about detailed analysis in PDF
            Paragraph note = new Paragraph("See below for detailed analysis and recommendations!")
                .setFont(regularFont)
                .setFontSize(SMALL_SIZE)
                .setFontColor(MUTED_TEXT)
                .setItalic()
                .setMarginTop(10)
                .setMarginBottom(10);
            document.add(note);
        }
    }
    
    private void addSkillsRecommendations(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        Paragraph sectionTitle = new Paragraph("AI-Recommended Skills to Develop")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(HEADING_BLUE)
            .setMarginTop(5)
            .setMarginBottom(5);
        document.add(sectionTitle);
        
        if (report.getSkillRecommendations() != null && !report.getSkillRecommendations().isEmpty()) {
            for (int i = 0; i < report.getSkillRecommendations().size(); i++) {
                String skill = report.getSkillRecommendations().get(i);
                Paragraph skillPara = new Paragraph((i + 1) + ". " + skill)
                    .setFont(regularFont)
                    .setFontSize(BODY_SIZE)
                    .setMarginBottom(3);
                document.add(skillPara);
                
                // Add light divider between items (except last)
                if (i < report.getSkillRecommendations().size() - 1) {
                    addDivider(document, false);
                }
            }
        }
    }
    
    private void addCareerTrajectoryInsights(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        Paragraph sectionTitle = new Paragraph("Career Trajectory Insights")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(HEADING_BLUE)
            .setMarginTop(5)
            .setMarginBottom(5);
        document.add(sectionTitle);
        
        if (report.getCareerTrajectoryInsights() != null && !report.getCareerTrajectoryInsights().isEmpty()) {
            Paragraph insights = new Paragraph(report.getCareerTrajectoryInsights())
            .setFont(regularFont)
                .setFontSize(BODY_SIZE)
            .setMarginBottom(10);
            document.add(insights);
        }
    }
    
    private void addDetailedCareerExplanations(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
                
                Paragraph sectionTitle = new Paragraph("Detailed Career Explanations")
                    .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
                    .setFontColor(HEADING_BLUE)
                    .setMarginTop(5)
                    .setMarginBottom(5);
                document.add(sectionTitle);
                
        if (report.getDetailedCareerInsights() != null && !report.getDetailedCareerInsights().isEmpty()) {
            Map<String, Object> insights = report.getDetailedCareerInsights();
            
            // Explanations
            if (insights.containsKey("explanations") && insights.get("explanations") instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> explanations = (Map<String, Object>) insights.get("explanations");
                
                for (Map.Entry<String, Object> entry : explanations.entrySet()) {
                    String careerName = entry.getKey();
                    String explanation = entry.getValue().toString();
                    
                    Paragraph careerTitle = new Paragraph(careerName)
                            .setFont(boldFont)
                        .setFontSize(BODY_SIZE)
                        .setMarginTop(5)
                            .setMarginBottom(2);
                        document.add(careerTitle);
                        
                        Paragraph explanationPara = new Paragraph(explanation)
                            .setFont(regularFont)
                        .setFontSize(SMALL_SIZE)
                            .setMarginBottom(5);
                        document.add(explanationPara);
                    
                    addDivider(document, false);
                }
            }
            
            // Study paths
            if (insights.containsKey("studyPaths") && insights.get("studyPaths") instanceof Map) {
                addDivider(document, true);
                
                Paragraph studyPathsTitle = new Paragraph("Personalized Study Paths")
                    .setFont(boldFont)
                    .setFontSize(HEADING_SIZE)
                    .setFontColor(HEADING_BLUE)
                    .setMarginTop(5)
                    .setMarginBottom(5);
                document.add(studyPathsTitle);
                
                @SuppressWarnings("unchecked")
                Map<String, Object> studyPaths = (Map<String, Object>) insights.get("studyPaths");
                
                for (Map.Entry<String, Object> entry : studyPaths.entrySet()) {
                    String careerName = entry.getKey();
                    Object pathValue = entry.getValue();
                    
                    Paragraph careerTitle = new Paragraph(careerName)
                            .setFont(boldFont)
                        .setFontSize(BODY_SIZE)
                        .setMarginTop(5)
                            .setMarginBottom(2);
                        document.add(careerTitle);
                        
                    if (pathValue instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<String> steps = (List<String>) pathValue;
                        for (int i = 0; i < steps.size(); i++) {
                            Paragraph stepPara = new Paragraph((i + 1) + ". " + steps.get(i))
                                        .setFont(regularFont)
                                .setFontSize(SMALL_SIZE)
                                        .setMarginBottom(1);
                                    document.add(stepPara);
                            }
                        } else {
                        Paragraph pathPara = new Paragraph(pathValue.toString())
                                .setFont(regularFont)
                            .setFontSize(SMALL_SIZE)
                            .setMarginBottom(1);
                            document.add(pathPara);
                    }
                    
                    addDivider(document, false);
                }
            }
        }
    }
    
    private void addRecommendedNextSteps(Document document, StudentReport report, PdfFont boldFont, PdfFont regularFont) {
        addDivider(document, true);
        
        // Create highlight box for next steps (matching email template)
        Div nextStepsBox = new Div();
        nextStepsBox.setBackgroundColor(LIGHT_BLUE_BG)
            .setBorderLeft(new SolidBorder(PRIMARY_BLUE, 4f))
            .setPadding(20f)
            .setMarginTop(20f)
            .setMarginBottom(20f);
        
        // Title
        Paragraph titlePara = new Paragraph("🚀 Next Steps")
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(PRIMARY_BLUE)
            .setMarginBottom(10)
            .setMarginTop(0);
        nextStepsBox.add(titlePara);
        
        // Content
        Paragraph contentPara = new Paragraph("Your career journey starts now! Here's what you can do:")
            .setFont(regularFont)
            .setFontSize(BODY_SIZE)
            .setMarginBottom(10);
        nextStepsBox.add(contentPara);
        
        // Predefined next steps matching email template
        String[] nextSteps = {
            "Review your detailed report (attached PDF)",
            "Research the recommended career paths",
            "Connect with professionals in your areas of interest",
            "Start building relevant skills and experience"
        };
        
        for (int i = 0; i < nextSteps.length; i++) {
            Paragraph stepPara = new Paragraph((i + 1) + ". " + nextSteps[i])
                .setFont(regularFont)
                .setFontSize(BODY_SIZE)
                .setMarginBottom(5);
            nextStepsBox.add(stepPara);
        }
        
        document.add(nextStepsBox);
    }
    
    /**
     * Create a highlight box (matching email template style)
     * Light blue background with left border
     */
    private Div createHighlightBox(String content, PdfFont regularFont) {
        Div highlightBox = new Div();
        highlightBox.setBackgroundColor(LIGHT_BLUE_BG)
            .setBorderLeft(new SolidBorder(PRIMARY_BLUE, 4f))
            .setPadding(20f)
            .setMarginTop(20f)
            .setMarginBottom(20f);
        
        Paragraph contentPara = new Paragraph(content)
            .setFont(regularFont)
            .setFontSize(BODY_SIZE)
            .setMargin(0);
        highlightBox.add(contentPara);
        
        return highlightBox;
    }
    
    /**
     * Create a highlight box with title
     */
    private Div createHighlightBox(String title, String content, PdfFont boldFont, PdfFont regularFont) {
        Div highlightBox = new Div();
        highlightBox.setBackgroundColor(LIGHT_BLUE_BG)
            .setBorderLeft(new SolidBorder(PRIMARY_BLUE, 4f))
            .setPadding(20f)
            .setMarginTop(20f)
            .setMarginBottom(20f);
        
        Paragraph titlePara = new Paragraph(title)
            .setFont(boldFont)
            .setFontSize(HEADING_SIZE)
            .setFontColor(PRIMARY_BLUE)
            .setMarginBottom(10)
            .setMarginTop(0);
        highlightBox.add(titlePara);
        
        Paragraph contentPara = new Paragraph(content)
            .setFont(regularFont)
            .setFontSize(BODY_SIZE)
            .setMargin(0);
        highlightBox.add(contentPara);
        
        return highlightBox;
    }
    
    /**
     * Create a career list box (light gray background)
     */
    private Div createCareerListBox() {
        Div careerListBox = new Div();
        careerListBox.setBackgroundColor(LIGHT_GRAY_BG)
            .setPadding(15f)
            .setMarginTop(15f)
            .setMarginBottom(15f);
        return careerListBox;
    }
    
    /**
     * Create a career item (white background with green left border)
     */
    private Div createCareerItem(String careerName, PdfFont boldFont) {
        Div careerItem = new Div();
        careerItem.setBackgroundColor(ColorConstants.WHITE)
            .setBorderLeft(new SolidBorder(SUCCESS_GREEN, 3f))
            .setPadding(8f)
            .setMarginTop(8f)
            .setMarginBottom(8f);
        
        Paragraph careerPara = new Paragraph(careerName)
            .setFont(boldFont)
            .setFontSize(BODY_SIZE)
            .setMargin(0);
        careerItem.add(careerPara);
        
        return careerItem;
    }
    
    /**
     * Add a horizontal divider line between sections
     * Uses a simple table approach for reliable positioning
     */
    private void addDivider(Document document, boolean isDark) {
        // Add spacing
        Paragraph spacer = new Paragraph(" ")
            .setMarginTop(isDark ? 6f : 5f)
            .setMarginBottom(0);
        document.add(spacer);
        
        // Create a line using a table with border
        Table lineTable = new Table(1);
        Cell lineCell = new Cell();
        lineCell.setBorderTop(new SolidBorder(
            isDark ? BORDER_GRAY : LIGHT_GRAY, 
            isDark ? 1f : 0.5f
        ));
        lineCell.setHeight(0.1f);
        lineCell.setPadding(0);
        lineTable.addCell(lineCell);
        document.add(lineTable);
        
        // Add spacing after
        Paragraph spacerAfter = new Paragraph(" ")
            .setMarginTop(0)
            .setMarginBottom(isDark ? 6f : 5f);
        document.add(spacerAfter);
    }
    
    /**
     * Footer handler for page numbers
     */
    private static class FooterHandler implements IEventHandler {
        private PdfDocument pdfDoc;
        
        public FooterHandler(PdfDocument pdfDoc) {
            this.pdfDoc = pdfDoc;
        }
        
        @Override
        public void handleEvent(Event event) {
            PdfDocumentEvent docEvent = (PdfDocumentEvent) event;
            PdfPage page = docEvent.getPage();
            PdfCanvas canvas = new PdfCanvas(page.newContentStreamBefore(), page.getResources(), pdfDoc);
            Rectangle pageSize = page.getPageSize();
            
            try {
                PdfFont font = PdfFontFactory.createFont();
                int pageNumber = pdfDoc.getPageNumber(page);
                // Calculate total pages - might not be accurate until document is closed, but will be close
                int totalPages = pdfDoc.getNumberOfPages();
                
                // Page number on right
                canvas.beginText()
                    .setFontAndSize(font, 7f)
                    .setFillColor(new DeviceRgb(128, 128, 128))
                    .moveText(pageSize.getWidth() - 40, 10)
                    .showText("Page " + pageNumber + " of " + (totalPages > 0 ? totalPages : "?") + "")
                    .endText();
                
                // Generated by Naviksha AI on left
                canvas.beginText()
                    .setFontAndSize(font, 7f)
                    .setFillColor(new DeviceRgb(128, 128, 128))
                    .moveText(15, 10)
                    .showText("Generated by Naviksha AI")
                    .endText();
                
            } catch (IOException e) {
                // Log but don't fail PDF generation - use logging if available
            }
        }
    }
}