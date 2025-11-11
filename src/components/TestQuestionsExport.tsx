import { Button } from '@/components/ui/button';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '@/components/ui/card';
import { Download, FileSpreadsheet } from 'lucide-react';
import { exportTestQuestionsToExcel } from '@/lib/excel-export';
import { toast } from 'sonner';

export const TestQuestionsExport = () => {
  const handleExport = () => {
    try {
      exportTestQuestionsToExcel();
      toast.success('Excel file downloaded successfully!');
    } catch (error) {
      console.error('Export error:', error);
      toast.error('Failed to export questions. Please try again.');
    }
  };

  return (
    <Card className="w-full max-w-2xl mx-auto">
      <CardHeader>
        <div className="flex items-center gap-2">
          <FileSpreadsheet className="h-6 w-6 text-primary" />
          <CardTitle>Export Test Questions</CardTitle>
        </div>
        <CardDescription>
          Download all test questions in Excel format with clear sections, options, and directions
        </CardDescription>
      </CardHeader>
      <CardContent className="space-y-4">
        <div className="bg-muted/50 p-4 rounded-lg space-y-2">
          <h3 className="font-semibold text-sm">Included Sections:</h3>
          <ul className="text-sm space-y-1 ml-4 list-disc text-muted-foreground">
            <li><strong>Overview Sheet:</strong> Export summary, question type legend, and RIASEC model explanation</li>
            <li><strong>VIBEMatch Assessment:</strong> 15 questions covering career interests (RIASEC-based)</li>
            <li><strong>EduStats Assessment:</strong> 15 questions covering educational background and context</li>
          </ul>
        </div>

        <div className="bg-muted/50 p-4 rounded-lg space-y-2">
          <h3 className="font-semibold text-sm">Excel Format Details:</h3>
          <ul className="text-sm space-y-1 ml-4 list-disc text-muted-foreground">
            <li>Question ID, text, type, and required status</li>
            <li>All available options listed clearly</li>
            <li>Instructions and directions for each question</li>
            <li>RIASEC mappings for VIBEMatch questions</li>
            <li>Organized in separate sheets for easy navigation</li>
          </ul>
        </div>

        <Button 
          onClick={handleExport}
          className="w-full"
          size="lg"
        >
          <Download className="mr-2 h-4 w-4" />
          Download Test Questions (Excel)
        </Button>
      </CardContent>
    </Card>
  );
};
