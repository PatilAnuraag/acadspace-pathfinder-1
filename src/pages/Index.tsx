/**
 * Index/Home Page - Career Counseling Landing
 * 
 * Beautiful landing page that welcomes students and guides them to start their career journey.
 * Features hero section, benefits, and clear call-to-action to begin assessments.
 */

import React from 'react';
import { Button } from '@/components/ui/button';
import { Card, CardContent } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { 
  Brain, 
  Target, 
  TrendingUp, 
  Users, 
  Award, 
  Sparkles,
  ArrowRight,
  Play,
  CheckCircle2
} from 'lucide-react';
import { useNavigate } from 'react-router-dom';
import uiMicrocopy from '@/data/ui_microcopy.json';

const Index = () => {
  const navigate = useNavigate();

  const features = [
    {
      icon: Brain,
      title: "AI-Powered Analysis",
      description: "Advanced personality and aptitude assessment using proven RIASEC methodology"
    },
    {
      icon: Target,
      title: "Personalized Matches",
      description: "Get career recommendations tailored to your interests, strengths, and goals"
    },
    {
      icon: TrendingUp,
      title: "Actionable Steps",
      description: "Clear roadmap with next steps, courses, and skill development paths"
    },
    {
      icon: Users,
      title: "Expert Insights",
      description: "Guidance based on real career counseling expertise and industry trends"
    }
  ];

  const benefits = [
    "Discover careers you never knew existed",
    "Understand your unique strengths and interests", 
    "Get specific study and skill recommendations",
    "Build confidence in your career choices",
    "Access emerging and traditional career paths"
  ];

  return (
    <div className="min-h-screen bg-background">
      {/* Hero Section */}
      <section className="relative overflow-hidden">
        <div className="absolute inset-0 gradient-hero opacity-10" />
        
        <div className="container mx-auto px-4 py-16 relative">
          <div className="max-w-4xl mx-auto text-center space-y-8">
            {/* Badge */}
            <Badge variant="secondary" className="mb-4 px-4 py-2">
              <Sparkles className="w-4 h-4 mr-2" />
              AI-Powered Career Guidance
            </Badge>

            {/* Headlines */}
            <div className="space-y-4">
              <h1 className="text-4xl md:text-6xl font-bold text-gradient leading-tight">
                {uiMicrocopy.onboarding.welcomeTitle}
              </h1>
              <p className="text-xl md:text-2xl text-muted-foreground max-w-2xl mx-auto leading-relaxed">
                {uiMicrocopy.onboarding.welcomeSubtitle}
              </p>
            </div>

            {/* CTA Buttons */}
            <div className="flex flex-col sm:flex-row gap-4 justify-center items-center pt-8">
              <Button 
                variant="hero" 
                size="xl" 
                onClick={() => navigate('/onboarding')}
                className="w-full sm:w-auto group"
              >
                <Play className="w-5 h-5 mr-2 group-hover:scale-110 transition-transform" />
                {uiMicrocopy.onboarding.getStartedBtn}
                <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
              </Button>
              
              <Button 
                variant="ghost" 
                size="xl"
                onClick={() => navigate('/demo')}
                className="w-full sm:w-auto"
              >
                <Target className="w-5 h-5 mr-2" />
                View Sample Report
              </Button>
            </div>

            {/* Social Proof */}
            <div className="flex items-center justify-center gap-6 text-sm text-muted-foreground pt-8">
              <div className="flex items-center gap-2">
                <Award className="w-4 h-4 text-accent" />
                <span>Trusted by 10,000+ students</span>
              </div>
              <div className="flex items-center gap-2">
                <CheckCircle2 className="w-4 h-4 text-success" />
                <span>95% accuracy rate</span>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Features Section */}
      <section className="py-16 bg-muted/30">
        <div className="container mx-auto px-4">
          <div className="max-w-6xl mx-auto">
            <div className="text-center mb-12">
              <h2 className="text-3xl md:text-4xl font-bold mb-4">
                How It Works
              </h2>
              <p className="text-lg text-muted-foreground max-w-2xl mx-auto">
                Our comprehensive assessment analyzes multiple dimensions to give you 
                the most accurate career guidance
              </p>
            </div>

            <div className="grid md:grid-cols-2 lg:grid-cols-4 gap-6">
              {features.map((feature, index) => {
                const Icon = feature.icon;
                return (
                  <Card key={index} className="gradient-card border-0 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-105">
                    <CardContent className="p-6 text-center space-y-4">
                      <div className="w-16 h-16 rounded-full gradient-primary flex items-center justify-center mx-auto">
                        <Icon className="w-8 h-8 text-white" />
                      </div>
                      <h3 className="text-lg font-semibold">{feature.title}</h3>
                      <p className="text-muted-foreground">{feature.description}</p>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </div>
        </div>
      </section>

      {/* Benefits Section */}
      <section className="py-16">
        <div className="container mx-auto px-4">
          <div className="max-w-4xl mx-auto">
            <div className="grid lg:grid-cols-2 gap-12 items-center">
              <div className="space-y-6">
                <h2 className="text-3xl md:text-4xl font-bold">
                  Discover Your Perfect Career Match
                </h2>
                <p className="text-lg text-muted-foreground">
                  Don't leave your future to chance. Our scientifically-backed assessment 
                  helps you make informed decisions about your career path.
                </p>
                
                <ul className="space-y-3">
                  {benefits.map((benefit, index) => (
                    <li key={index} className="flex items-center gap-3">
                      <CheckCircle2 className="w-5 h-5 text-success flex-shrink-0" />
                      <span className="text-foreground">{benefit}</span>
                    </li>
                  ))}
                </ul>

                <Button 
                  variant="accent" 
                  size="lg"
                  onClick={() => navigate('/onboarding')}
                  className="group"
                >
                  Start Your Journey
                  <ArrowRight className="w-4 h-4 ml-2 group-hover:translate-x-1 transition-transform" />
                </Button>
              </div>

              <div className="relative">
                <Card className="gradient-card border-0 shadow-2xl p-6 transform rotate-3 hover:rotate-0 transition-all duration-500">
                  <CardContent className="p-0 space-y-4">
                    <div className="flex items-center gap-3">
                      <div className="w-10 h-10 bg-primary rounded-full flex items-center justify-center text-primary-foreground font-bold">
                        1
                      </div>
                      <div>
                        <h4 className="font-semibold">Data Scientist</h4>
                        <p className="text-sm text-muted-foreground">92% Match</p>
                      </div>
                    </div>
                    <div className="space-y-2 text-sm">
                      <div className="flex items-start gap-2">
                        <div className="w-1.5 h-1.5 bg-accent rounded-full mt-2" />
                        <span className="text-muted-foreground">Strong analytical thinking matches your personality</span>
                      </div>
                      <div className="flex items-start gap-2">
                        <div className="w-1.5 h-1.5 bg-accent rounded-full mt-2" />
                        <span className="text-muted-foreground">Excellent math and coding performance</span>
                      </div>
                    </div>
                  </CardContent>
                </Card>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* CTA Section */}
      <section className="py-16 bg-muted/30">
        <div className="container mx-auto px-4">
          <div className="max-w-3xl mx-auto text-center space-y-8">
            <h2 className="text-3xl md:text-4xl font-bold">
              Ready to Find Your Perfect Career?
            </h2>
            <p className="text-lg text-muted-foreground">
              Join thousands of students who have discovered their ideal career path. 
              Take the first step towards your future today.
            </p>
            
            <Button 
              variant="hero" 
              size="xl"
              onClick={() => navigate('/onboarding')}
              className="group pulse-glow"
            >
              <Sparkles className="w-5 h-5 mr-2 group-hover:rotate-12 transition-transform" />
              Start Free Assessment
              <ArrowRight className="w-5 h-5 ml-2 group-hover:translate-x-1 transition-transform" />
            </Button>
          </div>
        </div>
      </section>
    </div>
  );
};

export default Index;