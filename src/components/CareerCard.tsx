/**
 * CareerCard Component
 * 
 * Displays a single career recommendation with match score, reasons, and actions.
 * Used in the results page to show career matches in an attractive card format.
 */

import React from 'react';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';
import { Badge } from '@/components/ui/badge';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Star, TrendingUp, BookOpen, ExternalLink } from 'lucide-react';
import { CareerMatch } from '@/types';
import { cn } from '@/lib/utils';

interface CareerCardProps {
  career: CareerMatch;
  rank: number;
  onExplore?: () => void;
  className?: string;
}

export const CareerCard: React.FC<CareerCardProps> = ({ 
  career, 
  rank, 
  onExplore,
  className 
}) => {
  const getConfidenceColor = (confidence?: string) => {
    switch (confidence?.toLowerCase()) {
      case 'high': return 'bg-success text-success-foreground';
      case 'medium': return 'bg-accent text-accent-foreground'; 
      case 'low': return 'bg-muted text-muted-foreground';
      default: return 'bg-muted text-muted-foreground';
    }
  };

  const getMatchScoreColor = (score: number) => {
    if (score >= 80) return 'text-success';
    if (score >= 60) return 'text-accent';
    return 'text-muted-foreground';
  };

  return (
    <Card className={cn(
      "gradient-card border-0 shadow-lg hover:shadow-xl transition-all duration-300 hover:scale-102",
      rank === 1 && "ring-2 ring-primary shadow-primary",
      className
    )}>
      <CardHeader className="pb-3">
        <div className="flex items-start justify-between">
          <div className="flex items-center gap-3">
            <div className="flex items-center justify-center w-8 h-8 rounded-full bg-primary text-primary-foreground text-sm font-bold">
              {rank}
            </div>
            <div>
              <CardTitle className="text-lg text-gradient">{career.careerName}</CardTitle>
              {career.confidence && (
                <Badge variant="secondary" className={cn("mt-1 text-xs", getConfidenceColor(career.confidence))}>
                  {career.confidence} Confidence
                </Badge>
              )}
            </div>
          </div>
          
          <div className="text-right">
            <div className={cn("text-2xl font-bold", getMatchScoreColor(career.matchScore))}>
              {career.matchScore}%
            </div>
            <div className="text-xs text-muted-foreground">Match Score</div>
          </div>
        </div>
      </CardHeader>

      <CardContent className="space-y-4">
        {/* Match Score Progress */}
        <div>
          <div className="flex justify-between text-sm mb-2">
            <span>Career Fit</span>
            <span className="font-medium">{career.matchScore}%</span>
          </div>
          <Progress 
            value={career.matchScore} 
            className="h-2"
          />
        </div>

        {/* Top Reasons */}
        {career.topReasons && career.topReasons.length > 0 && (
          <div>
            <div className="flex items-center gap-2 text-sm font-medium mb-2">
              <Star className="w-4 h-4 text-accent" />
              Why this fits you
            </div>
            <ul className="space-y-1">
              {career.topReasons.slice(0, 3).map((reason, index) => (
                <li key={index} className="text-sm text-muted-foreground flex items-start gap-2">
                  <div className="w-1.5 h-1.5 bg-accent rounded-full mt-2 flex-shrink-0" />
                  <span>{reason}</span>
                </li>
              ))}
            </ul>
          </div>
        )}

        {/* First Steps Preview */}
        {career.first3Steps && career.first3Steps.length > 0 && (
          <div>
            <div className="flex items-center gap-2 text-sm font-medium mb-2">
              <TrendingUp className="w-4 h-4 text-primary" />
              Next Steps
            </div>
            <div className="text-sm text-muted-foreground">
              {career.first3Steps[0]}
            </div>
            {career.first3Steps.length > 1 && (
              <div className="text-xs text-muted-foreground mt-1">
                +{career.first3Steps.length - 1} more steps
              </div>
            )}
          </div>
        )}

        {/* Action Button */}
        <div className="pt-2">
          <Button 
            variant="career" 
            className="w-full" 
            onClick={onExplore}
          >
            <BookOpen className="w-4 h-4" />
            Explore Career Path
            <ExternalLink className="w-4 h-4" />
          </Button>
        </div>
      </CardContent>
    </Card>
  );
};