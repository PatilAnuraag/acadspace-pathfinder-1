/**
 * Career Matching & Scoring Engine
 * 
 * This module contains the core algorithms for:
 * - RIASEC personality scoring
 * - Subject-based matching
 * - Context fit analysis
 * - Career ranking and recommendation
 * 
 * All scoring is deterministic and based on validated career counseling methods.
 */

import { RiasecScores, Career, TestAnswer, CareerMatch, CareerBucket, ScoringWeights } from '@/types';

// Scoring weights for different factors
export const DEFAULT_WEIGHTS: ScoringWeights = {
  riasec_weight: 0.4,    // 40% - Personality fit
  subject_weight: 0.3,    // 30% - Academic performance
  context_weight: 0.2,    // 20% - Family/social context
  practical_weight: 0.1   // 10% - Practical considerations
};

/**
 * Calculate RIASEC scores from vibematch test answers
 * Uses a 4-point Likert scale (0-3) mapped to RIASEC dimensions
 */
export function calculateRiasecScores(
  vibeAnswers: TestAnswer[], 
  questions: any[]
): RiasecScores {
  const scores: RiasecScores = { R: 0, I: 0, A: 0, S: 0, E: 0, C: 0 };
  
  vibeAnswers.forEach(answer => {
    const question = questions.find(q => q.id === answer.questionId);
    if (!question?.riasec_map) return;
    
    const answerValue = typeof answer.answer === 'number' ? answer.answer : 0;
    
    // Map each RIASEC dimension based on question weights
    Object.entries(question.riasec_map).forEach(([dimension, weight]) => {
      if (dimension in scores) {
        scores[dimension as keyof RiasecScores] += answerValue * (weight as number);
      }
    });
  });
  
  return scores;
}

/**
 * Calculate subject match score between student performance and career requirements
 * Higher scores for better performance in relevant subjects
 */
export function calculateSubjectMatch(
  studentGrades: { [subject: string]: number },
  careerSubjects: string[]
): number {
  if (careerSubjects.length === 0) return 50; // Neutral score if no subjects specified
  
  let totalScore = 0;
  let matchedSubjects = 0;
  
  careerSubjects.forEach(subject => {
    if (studentGrades[subject] !== undefined) {
      totalScore += studentGrades[subject];
      matchedSubjects++;
    }
  });
  
  if (matchedSubjects === 0) return 30; // Lower score if no subject overlap
  
  const avgScore = totalScore / matchedSubjects;
  
  // Convert percentage to 0-100 scale with bonus for high performance
  if (avgScore >= 85) return 95;
  if (avgScore >= 75) return 80;
  if (avgScore >= 65) return 65;
  if (avgScore >= 50) return 50;
  return 30;
}

/**
 * Calculate context fit based on family background and preferences
 * Considers parental careers, family expectations, and student preferences
 */
export function calculateContextFit(
  parentCareers: string[],
  familyPreferences: string[],
  careerBucket: string,
  careerTags: string[]
): number {
  let contextScore = 50; // Base neutral score
  
  // Family career influence (+/-10 points)
  const relatedFields = [
    'IT / Software',
    'Finance / Banking', 
    'Medicine / Healthcare',
    'Business / Trade',
    'Creative Arts'
  ];
  
  if (parentCareers.some(career => 
    careerBucket.toLowerCase().includes(career.toLowerCase().split(' ')[0])
  )) {
    contextScore += 10;
  }
  
  // New-age career acceptance (check if family is open to new careers)
  if (careerTags.includes('new_age')) {
    if (parentCareers.includes('IT / Software') || 
        familyPreferences.includes('Highly encouraged')) {
      contextScore += 15;
    } else if (familyPreferences.includes('Not preferred')) {
      contextScore -= 20;
    }
  }
  
  // Traditional career bonus
  if (careerBucket.includes('Engineering') || 
      careerBucket.includes('Medicine') || 
      careerBucket.includes('Finance')) {
    contextScore += 10;
  }
  
  return Math.max(0, Math.min(100, contextScore));
}

/**
 * Calculate practical fit based on study duration, work style preferences, etc.
 * Considers practical constraints and student preferences
 */
export function calculatePracticalFit(
  studyDurationPreference: string,
  workStylePreference: string,
  internationalStudy: string,
  careerQualification: string,
  careerTags: string[]
): number {
  let practicalScore = 50; // Base score
  
  // Study duration compatibility
  const longDurationCareers = ['MBBS', 'B.Arch', 'LLB'];
  const isLongDuration = longDurationCareers.some(qual => 
    careerQualification.includes(qual)
  );
  
  if (isLongDuration && studyDurationPreference === 'Yes') {
    practicalScore += 20;
  } else if (isLongDuration && studyDurationPreference === 'No') {
    practicalScore -= 25;
  }
  
  // Work style compatibility
  if (workStylePreference === 'Remote / Flexible' && careerTags.includes('tech')) {
    practicalScore += 15;
  }
  
  if (workStylePreference === 'Field work / On-site' && careerTags.includes('hands_on')) {
    practicalScore += 15;
  }
  
  // International study compatibility
  if (internationalStudy === 'Yes' && careerTags.includes('new_age')) {
    practicalScore += 10;
  }
  
  return Math.max(0, Math.min(100, practicalScore));
}

/**
 * Calculate final career match score using weighted combination
 */
export function calculateFinalScore(
  riasecMatch: number,
  subjectMatch: number,
  contextFit: number,
  practicalFit: number,
  weights: ScoringWeights = DEFAULT_WEIGHTS
): number {
  const finalScore = 
    (riasecMatch * weights.riasec_weight) +
    (subjectMatch * weights.subject_weight) +
    (contextFit * weights.context_weight) +
    (practicalFit * weights.practical_weight);
  
  return Math.round(Math.max(0, Math.min(100, finalScore)));
}

/**
 * Calculate RIASEC compatibility between student and career
 */
export function calculateRiasecMatch(
  studentRiasec: RiasecScores,
  careerRiasecProfile: string
): number {
  // Parse career RIASEC profile (e.g., "RA", "EI", etc.)
  const careerDimensions = careerRiasecProfile.split('');
  
  let totalMatch = 0;
  let totalPossible = 0;
  
  careerDimensions.forEach((dimension, index) => {
    if (dimension in studentRiasec) {
      const weight = 1 / (index + 1); // First dimension gets more weight
      const studentScore = studentRiasec[dimension as keyof RiasecScores];
      
      // Normalize student score to 0-100 scale (assuming max possible is 42 for 14 questions * 3 points)
      const normalizedScore = (studentScore / 42) * 100;
      
      totalMatch += normalizedScore * weight;
      totalPossible += 100 * weight;
    }
  });
  
  return totalPossible > 0 ? Math.round((totalMatch / totalPossible) * 100) : 0;
}

/**
 * Rank all careers for a student and return top matches
 */
export function rankCareersForStudent(
  studentData: {
    riasecScores: RiasecScores;
    grades: { [subject: string]: number };
    parentCareers: string[];
    familyPreferences: string[];
    studyDurationPreference: string;
    workStylePreference: string;
    internationalStudy: string;
  },
  careers: Career[],
  weights: ScoringWeights = DEFAULT_WEIGHTS
): CareerMatch[] {
  
  return careers.map(career => {
    // Parse career data - handle both string and array formats
    let subjects: string[];
    let tags: string[];
    
    try {
      subjects = Array.isArray(career.primarySubjects) 
        ? career.primarySubjects 
        : JSON.parse(career.primarySubjects);
      tags = Array.isArray(career.tags) 
        ? career.tags 
        : JSON.parse(career.tags);
    } catch {
      // Fallback if JSON parsing fails
      subjects = Array.isArray(career.primarySubjects) 
        ? career.primarySubjects 
        : [career.primarySubjects];
      tags = Array.isArray(career.tags) 
        ? career.tags 
        : [career.tags];
    }
    
    // Calculate component scores
    const riasecMatch = calculateRiasecMatch(studentData.riasecScores, career.riasec_profile);
    const subjectMatch = calculateSubjectMatch(studentData.grades, subjects);
    const contextFit = calculateContextFit(
      studentData.parentCareers,
      studentData.familyPreferences,
      career.bucket,
      tags
    );
    const practicalFit = calculatePracticalFit(
      studentData.studyDurationPreference,
      studentData.workStylePreference,
      studentData.internationalStudy,
      career.minQualification,
      tags
    );
    
    // Calculate final score
    const finalScore = calculateFinalScore(riasecMatch, subjectMatch, contextFit, practicalFit, weights);
    
    return {
      careerName: career.careerName,
      matchScore: finalScore,
      topReasons: generateMatchReasons(career, riasecMatch, subjectMatch, contextFit, practicalFit),
      confidence: getConfidenceLevel(finalScore)
    };
  })
  .sort((a, b) => b.matchScore - a.matchScore);
}

/**
 * Group careers by buckets and calculate bucket scores
 */
export function groupCareersByBuckets(rankedCareers: CareerMatch[], careers: Career[]): CareerBucket[] {
  const buckets = new Map<string, CareerMatch[]>();
  
  // Group careers by bucket
  rankedCareers.forEach(match => {
    const career = careers.find(c => c.careerName === match.careerName);
    if (career) {
      if (!buckets.has(career.bucket)) {
        buckets.set(career.bucket, []);
      }
      buckets.get(career.bucket)!.push(match);
    }
  });
  
  // Calculate bucket scores and create result
  const result: CareerBucket[] = [];
  
  buckets.forEach((matches, bucketName) => {
    // Sort careers within bucket by score
    matches.sort((a, b) => b.matchScore - a.matchScore);
    
    // Calculate bucket score as weighted average (top careers get more weight)
    let weightedSum = 0;
    let totalWeight = 0;
    
    matches.slice(0, 5).forEach((match, index) => {
      const weight = 1 / (index + 1);
      weightedSum += match.matchScore * weight;
      totalWeight += weight;
    });
    
    const bucketScore = totalWeight > 0 ? Math.round(weightedSum / totalWeight) : 0;
    
    result.push({
      bucketName,
      bucketScore,
      topCareers: matches.slice(0, 5) // Top 5 careers per bucket
    });
  });
  
  // Sort buckets by score
  return result.sort((a, b) => b.bucketScore - a.bucketScore);
}

/**
 * Generate human-readable reasons for career match
 */
function generateMatchReasons(
  career: Career,
  riasecMatch: number,
  subjectMatch: number,
  contextFit: number,
  practicalFit: number
): string[] {
  const reasons: string[] = [];
  
  if (riasecMatch >= 70) {
    reasons.push(`Strong personality fit (${riasecMatch}%) — your interests align well with this career`);
  }
  
  if (subjectMatch >= 75) {
    reasons.push(`Excellent academic performance in relevant subjects`);
  }
  
  if (contextFit >= 70) {
    reasons.push(`Good fit with your family background and expectations`);
  }
  
  if (practicalFit >= 70) {
    reasons.push(`Matches your practical preferences for study duration and work style`);
  }
  
  // Add career-specific insights
  let tags: string[];
  try {
    tags = Array.isArray(career.tags) ? career.tags : JSON.parse(career.tags);
  } catch {
    tags = Array.isArray(career.tags) ? career.tags : [career.tags];
  }
  
  if (tags.includes('new_age')) {
    reasons.push(`Emerging field with growing opportunities`);
  }
  
  if (tags.includes('hands_on')) {
    reasons.push(`Involves practical, hands-on work you seem to enjoy`);
  }
  
  return reasons.slice(0, 3); // Limit to top 3 reasons
}

/**
 * Get confidence level based on match score
 */
function getConfidenceLevel(score: number): string {
  if (score >= 80) return 'High';
  if (score >= 60) return 'Medium';
  return 'Low';
}