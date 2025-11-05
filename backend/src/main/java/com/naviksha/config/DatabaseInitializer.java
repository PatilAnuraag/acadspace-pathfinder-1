package com.naviksha.config;

import com.naviksha.repository.CareerRepository;
import com.naviksha.service.SeedService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * Database Initializer
 * 
 * Automatically seeds the database with initial data on application startup.
 * - Loads careers from career_mappings.csv
 * - Creates admin user if doesn't exist
 * - Skips seeding if data already exists (idempotent)
 * 
 * This ensures the database is always initialized with required data
 * without requiring manual seeding via admin endpoint.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements ApplicationRunner {
    
    private final SeedService seedService;
    private final CareerRepository careerRepository;
    
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("Checking database initialization...");
        
        // Check if careers already exist
        long existingCareers = careerRepository.count();
        
        if (existingCareers > 0) {
            log.info("Database already contains {} careers. Skipping seed.", existingCareers);
            return;
        }
        
        // Database is empty, proceed with seeding
        log.info("Database is empty. Starting automatic seeding...");
        
        try {
            var result = seedService.seedDatabase();
            log.info("Database seeding completed successfully:");
            log.info("  - Careers imported: {}", result.getCareersImported());
            log.info("  - Tests imported: {}", result.getTestsImported());
            log.info("  - Users created: {}", result.getUsersCreated());
        } catch (Exception e) {
            log.error("Error during automatic database seeding", e);
            // Don't throw - allow application to start even if seeding fails
            // Admin can manually seed via /admin/seed endpoint
        }
    }
}

