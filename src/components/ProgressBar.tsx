/**
 * ProgressBar Component
 * 
 * Shows test progress with smooth animations and beautiful design.
 * Sticky positioned at the top during tests for constant visibility.
 */

import React from 'react';
import { Progress } from '@/components/ui/progress';
import { Badge } from '@/components/ui/badge';
import { cn } from '@/lib/utils';
import { CheckCircle2, Circle } from 'lucide-react';

interface ProgressBarProps {
  current: number;
  total: number;
  testName?: string;
  className?: string;
  showSteps?: boolean;
}

export const ProgressBar: React.FC<ProgressBarProps> = ({
  current,
  total,
  testName,
  className,
  showSteps = false
}) => {
  const percentage = Math.round((current / total) * 100);
  const isComplete = current >= total;

  return (
    <div className={cn(
      "glass border-b sticky top-0 z-50 bg-background/95 backdrop-blur-sm",
      className
    )}>
      <div className="container mx-auto px-4 py-4">
        <div className="space-y-3">
          {/* Header */}
          <div className="flex items-center justify-between">
            <div>
              {testName && (
                <h3 className="font-semibold text-sm text-foreground">
                  {testName}
                </h3>
              )}
              <div className="flex items-center gap-2 text-sm text-muted-foreground">
                <span>Question {current} of {total}</span>
                <Badge 
                  variant={isComplete ? "default" : "secondary"}
                  className="text-xs"
                >
                  {percentage}%
                </Badge>
              </div>
            </div>

            {isComplete && (
              <div className="flex items-center gap-2 text-success">
                <CheckCircle2 className="w-5 h-5" />
                <span className="text-sm font-medium">Complete!</span>
              </div>
            )}
          </div>

          {/* Progress Bar */}
          <div className="space-y-2">
            <Progress 
              value={percentage} 
              className="h-2 transition-all duration-500"
            />
            
            {/* Step indicators (optional) */}
            {showSteps && (
              <div className="flex justify-between">
                {Array.from({ length: Math.min(total, 10) }).map((_, index) => {
                  const stepNumber = Math.floor((index * total) / 10) + 1;
                  const isStepComplete = current >= stepNumber;
                  
                  return (
                    <div
                      key={index}
                      className={cn(
                        "w-2 h-2 rounded-full transition-colors duration-300",
                        isStepComplete ? "bg-primary" : "bg-muted"
                      )}
                    />
                  );
                })}
              </div>
            )}
          </div>

          {/* Motivational message */}
          {!isComplete && (
            <div className="text-xs text-muted-foreground text-center">
              {getMotivationalMessage(percentage)}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

function getMotivationalMessage(percentage: number): string {
  if (percentage <= 25) return "Just getting started! 🚀";
  if (percentage <= 50) return "Making great progress! 💪";
  if (percentage <= 75) return "Almost there, keep going! ⭐";
  if (percentage < 100) return "Final stretch! You've got this! 🎯";
  return "Amazing work! All done! 🎉";
}