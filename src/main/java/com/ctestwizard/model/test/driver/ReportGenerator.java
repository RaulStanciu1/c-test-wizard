package com.ctestwizard.model.test.driver;

import com.ctestwizard.MainApplication;
import com.ctestwizard.model.code.entity.*;
import com.ctestwizard.model.test.entity.TCase;
import com.ctestwizard.model.test.entity.TObject;
import com.ctestwizard.model.test.entity.TProject;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.stream.Stream;

public class ReportGenerator {
    public static void generateReport(TProject project, TObject selectedObject, TSummary summary) throws Exception {
        // Generate report
        Document document = new Document();
        String reportPath = project.getTestDriver().getProjectPath() + File.separator + selectedObject.getTestFunction().getName() + "_report.pdf";
        File reportFile = new File(reportPath);
        if (reportFile.exists()) {
           if(!reportFile.delete()){
                throw new Exception("Report file could not be deleted");
           }
        }
        PdfWriter.getInstance(document, new FileOutputStream(reportPath));
        document.open();
        document.addTitle(selectedObject.getTestFunction().getName() + " Test Report");
        document.addAuthor("CTestWizard");
        document.addCreator("CTestWizard");
        document.addCreationDate();
        Paragraph title = new Paragraph("Test Execution Report: "+selectedObject.getTestFunction().getName());
        title.setFont(new Font(Font.FontFamily.HELVETICA, 50, Font.BOLD));
        title.setAlignment(Paragraph.ALIGN_CENTER);
        title.setPaddingTop(10);
        document.add(title);
        document.add(new Paragraph(" "));
        Paragraph resultsVerdict = new Paragraph("Tests Status: " + (summary.getResultsPassed() ? "Passed" : "Failed"));
        resultsVerdict.setFont(new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
        resultsVerdict.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(resultsVerdict);
        if(summary.getCoveragePassed() != -1){
            Paragraph coverageVerdict = new Paragraph("Coverage Status: " + (summary.getCoveragePassed() == 1 ? "Passed" : "Failed"));
            coverageVerdict.setFont(new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
            coverageVerdict.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(coverageVerdict);
        }
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(2);
        addTableHeaderOverview(table);
        table.addCell("Total Test Cases");
        table.addCell(summary.getTotalTestCases().toString());
        table.addCell("Total Test Steps");
        table.addCell(summary.getTotalTestSteps().toString());
        table.addCell("Passed Test Steps");
        table.addCell(summary.getPassedTestSteps().toString());
        document.add(table);


        for(int i = 0; i < summary.getTestResults().size(); i++){
            TResults testResult = summary.getTestResults().get(i);
            Paragraph testCaseTitle = new Paragraph("Test Case #"+(i+1)+": "+(testResult.getResultsPassed() ? "Passed" : "Failed"));
            testCaseTitle.setFont(new Font(Font.FontFamily.HELVETICA, 20, Font.BOLD));
            testCaseTitle.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(testCaseTitle);
            document.add(new Paragraph(" "));
            for(int j = 0; j < testResult.getTestSteps(); j++){
                addTestStepToDocument(document, testResult, selectedObject,i,j);
            }
        }

        document.close();
    }

    private static void addTestStepToDocument(Document document, TResults testResult,TObject object, int tCase,int tStep) throws Exception {
        Paragraph testStepTitle = new Paragraph("Test Step #"+(tStep+1));
        testStepTitle.setFont(new Font(Font.FontFamily.HELVETICA, 16, Font.BOLD));
        testStepTitle.setAlignment(Paragraph.ALIGN_CENTER);
        document.add(testStepTitle);
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(3);
        addTableHeader(table);
        addRows(table, testResult, object.getTestCases().get(tCase) ,tStep);
        document.add(table);

    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("Element", "Actual Value", "Status")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addTableHeaderOverview(PdfPTable table) {
        Stream.of("Property","Value")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addRows(PdfPTable table, TResults testResult, TCase testCase, int tStep) throws Exception {
        for(CElement global : testResult.getGlobalOutputs()){
            // Check the type of the global
            if(global instanceof CVariable variable){
                if(variable.values.get(tStep).valueStatus == 0 || Objects.equals(variable.values.get(tStep).value, "")){
                    continue;
                }
                table.addCell(variable.getName());
                table.addCell(variable.values.get(tStep).value);
                Path path = Paths.get(MainApplication.class.getResource("img/"+(variable.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                img.scalePercent(40);
                PdfPCell imageCell = new PdfPCell(img);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(imageCell);
            }else if(global instanceof CEnumInstance enumInstance){
                if(enumInstance.values.get(tStep).valueStatus == 0 || Objects.equals(enumInstance.values.get(tStep).value, "")){
                    continue;
                }
                table.addCell(enumInstance.getName());
                table.addCell(enumInstance.values.get(tStep).value);
                Path path = Paths.get(MainApplication.class.getResource("img/"+(enumInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                img.scalePercent(40);
                PdfPCell imageCell = new PdfPCell(img);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(imageCell);
            }else if(global instanceof CStructOrUnionInstance structOrUnionInstance){
                if(structOrUnionInstance.getPointers() != 0){
                    if(structOrUnionInstance.values.get(tStep).valueStatus == 0 || Objects.equals(structOrUnionInstance.values.get(tStep).value, "")){
                        continue;
                    }
                    table.addCell(structOrUnionInstance.getName());
                    table.addCell(structOrUnionInstance.values.get(tStep).value);
                    Path path = Paths.get(MainApplication.class.getResource("img/"+(structOrUnionInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                    Image img = Image.getInstance(path.toAbsolutePath().toString());
                    img.scalePercent(40);
                    PdfPCell imageCell = new PdfPCell(img);
                    imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    table.addCell(imageCell);
                }else{
                    for(CElement structElement : structOrUnionInstance.getStructType().getMembers()){
                        addStructRows(table, testResult, testCase, tStep, structElement, structOrUnionInstance.getName()+".");
                    }
                }
            }else if(global instanceof CArray array){
                for(CElement arrayElement : array.getArrayMembers()){
                    addArrayRows(table, testResult, testCase, tStep, arrayElement);
                }
            }
        }
        CElement output = testResult.getOutput();
        if(output instanceof CVariable variable){
            if(variable.values.get(tStep).valueStatus == 0 || Objects.equals(variable.values.get(tStep).value, "")){
                return;
            }
            table.addCell(variable.getName());
            table.addCell(variable.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(variable.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(output instanceof CEnumInstance enumInstance){
            if(enumInstance.values.get(tStep).valueStatus == 0 || Objects.equals(enumInstance.values.get(tStep).value, "")){
                return;
            }
            table.addCell(enumInstance.getName());
            table.addCell(enumInstance.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(enumInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(output instanceof CStructOrUnionInstance structOrUnionInstance){
            if(structOrUnionInstance.getPointers() != 0){
                if(structOrUnionInstance.values.get(tStep).valueStatus == 0 || Objects.equals(structOrUnionInstance.values.get(tStep).value, "")){
                    return;
                }
                table.addCell(structOrUnionInstance.getName());
                table.addCell(structOrUnionInstance.values.get(tStep).value);
                Path path = Paths.get(MainApplication.class.getResource("img/"+(structOrUnionInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                img.scalePercent(40);
                PdfPCell imageCell = new PdfPCell(img);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(imageCell);
            }else{
                for(CElement structElement : structOrUnionInstance.getStructType().getMembers()){
                    addStructRows(table, testResult, testCase, tStep, structElement, structOrUnionInstance.getName()+".");
                }
            }
        }else if(output instanceof CArray array){
            for(CElement arrayElement : array.getArrayMembers()){
                addArrayRows(table, testResult, testCase, tStep, arrayElement);
            }
        }
    }

    private static void addStructRows(PdfPTable table, TResults testResult, TCase testCase, int tStep, CElement element, String name) throws Exception{
        if(element instanceof CVariable variable){
            if(variable.values.get(tStep).valueStatus == 0 || Objects.equals(variable.values.get(tStep).value, "")){
                return;
            }
            table.addCell(name+variable.getName());
            table.addCell(variable.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(variable.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(element instanceof CEnumInstance enumInstance){
            if(enumInstance.values.get(tStep).valueStatus == 0 || Objects.equals(enumInstance.values.get(tStep).value, "")){
                return;
            }
            table.addCell(name+enumInstance.getName());
            table.addCell(enumInstance.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(enumInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(element instanceof CStructOrUnionInstance structOrUnionInstance){
            if(structOrUnionInstance.getPointers() != 0){
                if(structOrUnionInstance.values.get(tStep).valueStatus == 0 || Objects.equals(structOrUnionInstance.values.get(tStep).value, "")){
                    return;
                }
                table.addCell(name+structOrUnionInstance.getName());
                table.addCell(structOrUnionInstance.values.get(tStep).value);
                Path path = Paths.get(MainApplication.class.getResource("img/"+(structOrUnionInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                img.scalePercent(40);
                PdfPCell imageCell = new PdfPCell(img);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(imageCell);
            }else{
                for(CElement structElement : structOrUnionInstance.getStructType().getMembers()){
                    addStructRows(table, testResult, testCase, tStep, structElement, name+structOrUnionInstance.getName()+".");
                }
            }
        }else if(element instanceof CArray array){
            for(CElement arrayElement : array.getArrayMembers()){
                addArrayRows(table, testResult, testCase, tStep, arrayElement);
            }
        }
    }
    private static void addArrayRows(PdfPTable table, TResults testResult, TCase testCase, int tStep, CElement arrayElement) throws Exception{
        if(arrayElement instanceof CVariable variable){
            if(variable.values.get(tStep).valueStatus == 0 || Objects.equals(variable.values.get(tStep).value, "")){
                return;
            }
            table.addCell(variable.getName());
            table.addCell(variable.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(variable.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(arrayElement instanceof CEnumInstance enumInstance){
            if(enumInstance.values.get(tStep).valueStatus == 0 || Objects.equals(enumInstance.values.get(tStep).value, "")){
                return;
            }
            table.addCell(enumInstance.getName());
            table.addCell(enumInstance.values.get(tStep).value);
            Path path = Paths.get(MainApplication.class.getResource("img/"+(enumInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
            Image img = Image.getInstance(path.toAbsolutePath().toString());
            img.scalePercent(40);
            PdfPCell imageCell = new PdfPCell(img);
            imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            table.addCell(imageCell);
        }else if(arrayElement instanceof CStructOrUnionInstance structOrUnionInstance){
            if(structOrUnionInstance.getPointers() != 0){
                if(structOrUnionInstance.values.get(tStep).valueStatus == 0 || Objects.equals(structOrUnionInstance.values.get(tStep).value, "")){
                    return;
                }
                table.addCell(structOrUnionInstance.getName());
                table.addCell(structOrUnionInstance.values.get(tStep).value);
                Path path = Paths.get(MainApplication.class.getResource("img/"+(structOrUnionInstance.values.get(tStep).valueStatus == 1 ? "passed.png" : "failed.png")).toURI());
                Image img = Image.getInstance(path.toAbsolutePath().toString());
                img.scalePercent(40);
                PdfPCell imageCell = new PdfPCell(img);
                imageCell.setHorizontalAlignment(Element.ALIGN_CENTER);
                table.addCell(imageCell);
            }else{
                for(CElement structElement : structOrUnionInstance.getStructType().getMembers()){
                    addStructRows(table, testResult, testCase, tStep, structElement, structOrUnionInstance.getName()+".");
                }
            }
        }else if(arrayElement instanceof CArray array){
            for(CElement arrayElement1 : array.getArrayMembers()){
                addArrayRows(table, testResult, testCase, tStep, arrayElement1);
            }
        }
    }

}
