/**
 * Onboarding Page
 * 
 * Welcomes users and explains the assessment process before they begin.
 * Sets expectations and builds confidence before starting the tests.
 */

import React from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  Clock, 
  Brain, 
  FileText, 
  Award,
  ArrowRight,
  CheckCircle2,
  Info
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import uiMicrocopy from '@/data/ui_microcopy.json';

const Onboarding = () => {
  const navigate = useNavigate();

  const assessmentSteps = [
    {
      icon: Brain,
      title: "Personality & Interests",
      description: "Quick questions about what you enjoy and how you work best",
      duration: "5-7 minutes",
      questions: "15 questions"
    },
    {
      icon: FileText,
      title: "Academic & Background",
      description: "Your grades, subjects, and family context for personalized recommendations", 
      duration: "3-5 minutes",
      questions: "12 questions"
    },
    {
      icon: Award,
      title: "Your Career Report",
      description: "Detailed analysis with top career matches and actionable next steps",
      duration: "Instant",
      questions: "Comprehensive report"
    }
  ];

  const tips = [
    "Answer honestly - there are no right or wrong answers",
    "Take your time - quality responses lead to better matches",
    "Your progress is automatically saved", 
    "You can pause and resume anytime"
  ];

  return (
    <div className="min-h-screen bg-background py-8">
      <div className="container mx-auto px-4 max-w-4xl">
        {/* Header */}
        <div className="text-center mb-12">
          <Badge variant="secondary" className="mb-4">
            <Clock className="w-4 h-4 mr-2" />
            8-12 minutes total
          </Badge>
          
          <h1 className="text-3xl md:text-4xl font-bold mb-4 text-gradient">
            Let's Discover Your Perfect Career Path
          </h1>
          
          <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
            We'll ask you some questions about your interests, strengths, and preferences 
            to create a personalized career roadmap just for you.
          </p>
        </div>

        {/* Assessment Steps */}
        <div className="space-y-6 mb-12">
          <h2 className="text-2xl font-semibold text-center mb-8">
            How the Assessment Works
          </h2>
          
          <div className="grid gap-6">
            {assessmentSteps.map((step, index) => {
              const Icon = step.icon;
              return (
                <Card key={index} className="gradient-card border-0 shadow-lg hover:shadow-xl transition-all duration-300">
                  <CardContent className="p-6">
                    <div className="flex items-center gap-4">
                      {/* Step Number */}
                      <div className="w-12 h-12 rounded-full bg-primary text-primary-foreground flex items-center justify-center font-bold text-lg flex-shrink-0">
                        {index + 1}
                      </div>
                      
                      {/* Step Icon */}
                      <div className="w-12 h-12 rounded-full gradient-accent flex items-center justify-center flex-shrink-0">
                        <Icon className="w-6 h-6 text-white" />
                      </div>
                      
                      {/* Step Content */}
                      <div className="flex-1 min-w-0">
                        <h3 className="text-lg font-semibold mb-1">{step.title}</h3>
                        <p className="text-muted-foreground mb-2">{step.description}</p>
                        <div className="flex flex-wrap gap-3 text-sm">
                          <Badge variant="outline">
                            <Clock className="w-3 h-3 mr-1" />
                            {step.duration}
                          </Badge>
                          <Badge variant="outline">
                            <FileText className="w-3 h-3 mr-1" />
                            {step.questions}
                          </Badge>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              );
            })}
          </div>
        </div>

        {/* Tips Section */}
        <Card className="mb-8 border-info/20 bg-info/5">
          <CardHeader>
            <CardTitle className="flex items-center gap-2">
              <Info className="w-5 h-5 text-primary" />
              Tips for Best Results
            </CardTitle>
          </CardHeader>
          <CardContent className="space-y-3">
            {tips.map((tip, index) => (
              <div key={index} className="flex items-start gap-3">
                <CheckCircle2 className="w-5 h-5 text-success flex-shrink-0 mt-0.5" />
                <span className="text-foreground">{tip}</span>
              </div>
            ))}
          </CardContent>
        </Card>

        {/* Data Privacy Note */}
        <Card className="mb-8 border-muted">
          <CardContent className="p-6">
            <div className="flex items-start gap-3">
              <div className="w-8 h-8 rounded-full bg-success/10 flex items-center justify-center flex-shrink-0">
                <CheckCircle2 className="w-4 h-4 text-success" />
              </div>
              <div className="space-y-2">
                <h3 className="font-semibold">Your Privacy Matters</h3>
                <p className="text-sm text-muted-foreground">
                  Your responses are used only to generate your career recommendations. 
                  We don't share your personal information with third parties.
                </p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Action Buttons */}
        <div className="flex flex-col sm:flex-row gap-4 justify-center">
          <Button 
            variant="hero" 
            size="xl"
            onClick={() => navigate('/test/vibematch')}
            className="w-full sm:w-auto group"
          >
            Start Assessment
            <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
          </Button>
          
          <Button 
            variant="ghost" 
            size="xl"
            onClick={() => navigate('/')}
            className="w-full sm:w-auto"
          >
            Back to Home
          </Button>
        </div>

        {/* Progress Indicator */}
        <div className="text-center mt-8 text-sm text-muted-foreground">
          Ready to begin? Click "Start Assessment" to take your first step toward career clarity.
        </div>
      </div>
    </div>
  );
};

export default Onboarding;