package com.teachingaid.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.properties.TextAlignment;
import com.itextpdf.layout.properties.UnitValue;
import com.teachingaid.ui.ArrayVisualizationPane;
import com.teachingaid.algorithms.LinearSearch;
import com.teachingaid.algorithms.BinarySearch;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

/**
 * Exports search algorithm visualization and analysis to PDF using iText
 */
public class PDFExporter {

    // Colors for PDF styling
    private static final DeviceRgb HEADER_COLOR = new DeviceRgb(102, 126, 234);
    private static final DeviceRgb SECTION_COLOR = new DeviceRgb(68, 68, 68);
    private static final DeviceRgb TABLE_HEADER_COLOR = new DeviceRgb(240, 248, 255);

    /**
     * Exports the current visualization state to a PDF report
     */
    public void exportVisualization(File outputFile, int[] array, int searchValue,
                                    String algorithmName, ArrayVisualizationPane visualPane)
            throws Exception {

        if (outputFile == null) {
            throw new IllegalArgumentException("Output file cannot be null");
        }

        // ✅ Ensure directory exists
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            if (!parentDir.mkdirs()) {
                throw new Exception("Failed to create directories for: " + parentDir.getAbsolutePath());
            }
        }

        PdfWriter writer = null;
        Document document = null;
        
        try {
            // Create PDF document
            writer = new PdfWriter(outputFile);
            PdfDocument pdfDoc = new PdfDocument(writer);
            document = new Document(pdfDoc);

            // ✅ Set safe fonts
            PdfFont titleFont = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
            PdfFont headerFont = PdfFontFactory.createFont(StandardFonts.HELVETICA);
            PdfFont normalFont = PdfFontFactory.createFont(StandardFonts.TIMES_ROMAN);

            // Add sections
            addTitlePage(document, titleFont, headerFont, algorithmName);
            addArraySection(document, headerFont, normalFont, array, searchValue);
            addAlgorithmSection(document, headerFont, normalFont, algorithmName, array);
            addStepByStepAnalysis(document, headerFont, normalFont, array, searchValue, algorithmName);
            addPerformanceComparison(document, headerFont, normalFont, array.length);
            addEducationalContent(document, headerFont, normalFont, algorithmName);
            addFooter(document, normalFont);

            // ✅ Debug log
            System.out.println("PDF successfully saved at: " + outputFile.getAbsolutePath());

        } catch (Exception e) {
            throw new Exception("Failed to generate PDF: " + e.getMessage(), e);
        } finally {
            // ✅ Always close the document in finally block
            if (document != null) {
                document.close();
            }
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println("Error closing PDF writer: " + e.getMessage());
                }
            }
        }
    }

    private void addTitlePage(Document document, PdfFont titleFont, PdfFont headerFont, String algorithmName) {
        Paragraph title = new Paragraph("Searching Algorithm Visualizer")
                .setFont(titleFont)
                .setFontSize(28)
                .setFontColor(HEADER_COLOR)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(100);
        document.add(title);

        Paragraph subtitle = new Paragraph("Analysis Report: " + algorithmName)
                .setFont(headerFont)
                .setFontSize(20)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(20);
        document.add(subtitle);

        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMMM dd, yyyy 'at' HH:mm"));
        Paragraph timestampPara = new Paragraph("Generated on " + timestamp)
                .setFont(headerFont)
                .setFontSize(12)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(50);
        document.add(timestampPara);

        Paragraph description = new Paragraph(
                "This report contains a comprehensive analysis of the " + algorithmName.toLowerCase() +
                        " algorithm, including step-by-step execution, time complexity analysis, and educational insights."
        )
                .setFont(headerFont)
                .setFontSize(14)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginBottom(100);
        document.add(description);

        document.add(new AreaBreak());
    }

    private void addArraySection(Document document, PdfFont headerFont, PdfFont normalFont,
                                 int[] array, int searchValue) {
        Paragraph header = new Paragraph("Input Data Analysis")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10);
        document.add(header);

        Table infoTable = new Table(UnitValue.createPercentArray(new float[]{30, 70}))
                .setWidth(UnitValue.createPercentValue(100));

        infoTable.addHeaderCell(createTableCell("Property", headerFont, TABLE_HEADER_COLOR, true));
        infoTable.addHeaderCell(createTableCell("Value", headerFont, TABLE_HEADER_COLOR, true));

        infoTable.addCell(createTableCell("Array Size", normalFont, null, false));
        infoTable.addCell(createTableCell(String.valueOf(array.length), normalFont, null, false));

        infoTable.addCell(createTableCell("Search Value", normalFont, null, false));
        infoTable.addCell(createTableCell(String.valueOf(searchValue), normalFont, null, false));

        infoTable.addCell(createTableCell("Array Elements", normalFont, null, false));
        infoTable.addCell(createTableCell(Arrays.toString(array), normalFont, null, false));

        infoTable.addCell(createTableCell("Is Sorted", normalFont, null, false));
        infoTable.addCell(createTableCell(BinarySearch.isSorted(array) ? "Yes" : "No", normalFont, null, false));

        if (array.length > 0) {
            infoTable.addCell(createTableCell("Min Value", normalFont, null, false));
            infoTable.addCell(createTableCell(String.valueOf(Arrays.stream(array).min().orElse(0)), normalFont, null, false));

            infoTable.addCell(createTableCell("Max Value", normalFont, null, false));
            infoTable.addCell(createTableCell(String.valueOf(Arrays.stream(array).max().orElse(0)), normalFont, null, false));
        }

        document.add(infoTable);
        document.add(new Paragraph("\n"));
    }

    private void addAlgorithmSection(Document document, PdfFont headerFont, PdfFont normalFont,
                                     String algorithmName, int[] array) {
        Paragraph header = new Paragraph("Algorithm Information")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10);
        document.add(header);

        String algorithmInfo;
        if ("Linear Search".equals(algorithmName)) {
            algorithmInfo = LinearSearch.getAlgorithmInfo();
        } else {
            algorithmInfo = BinarySearch.getAlgorithmInfo();
        }

        Paragraph infoPara = new Paragraph(algorithmInfo)
                .setFont(normalFont)
                .setFontSize(11)
                .setMarginBottom(20);
        document.add(infoPara);
    }

    private void addStepByStepAnalysis(Document document, PdfFont headerFont, PdfFont normalFont,
                                       int[] array, int searchValue, String algorithmName) {
        Paragraph header = new Paragraph("Step-by-Step Execution")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10);
        document.add(header);

        String[] steps;
        if ("Linear Search".equals(algorithmName)) {
            steps = LinearSearch.getSearchTrace(array, searchValue);
        } else {
            steps = BinarySearch.getSearchTrace(array, searchValue);
        }

        for (String step : steps) {
            Paragraph stepPara = new Paragraph("• " + step)
                    .setFont(normalFont)
                    .setFontSize(10)
                    .setMarginBottom(5)
                    .setMarginLeft(15);
            document.add(stepPara);
        }
        document.add(new Paragraph("\n"));

        Paragraph summaryHeader = new Paragraph("Execution Summary")
                .setFont(headerFont)
                .setFontSize(14)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(5);
        document.add(summaryHeader);

        int result;
        if ("Linear Search".equals(algorithmName)) {
            result = LinearSearch.linearSearch(array, searchValue);
        } else {
            result = BinarySearch.binarySearch(array, searchValue);
        }

        String resultText = result != -1 ?
                String.format("✓ Search successful! Value %d found at index %d.", searchValue, result) :
                String.format("✗ Search unsuccessful. Value %d not found in the array.", searchValue);

        Paragraph resultPara = new Paragraph(resultText)
                .setFont(normalFont)
                .setFontSize(12)
                .setMarginBottom(10);
        document.add(resultPara);

        int comparisons = steps.length - 2;
        if (comparisons > 0) {
            Paragraph comparisonsPara = new Paragraph(
                    String.format("Total comparisons made: %d", Math.max(0, comparisons / 2))
            )
                    .setFont(normalFont)
                    .setFontSize(12);
            document.add(comparisonsPara);
        }
    }

    private void addPerformanceComparison(Document document, PdfFont headerFont, PdfFont normalFont, int arraySize) {
        Paragraph header = new Paragraph("Performance Analysis")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10);
        document.add(header);

        Table perfTable = new Table(UnitValue.createPercentArray(new float[]{25, 25, 25, 25}))
                .setWidth(UnitValue.createPercentValue(100));

        perfTable.addHeaderCell(createTableCell("Algorithm", headerFont, TABLE_HEADER_COLOR, true));
        perfTable.addHeaderCell(createTableCell("Best Case", headerFont, TABLE_HEADER_COLOR, true));
        perfTable.addHeaderCell(createTableCell("Average Case", headerFont, TABLE_HEADER_COLOR, true));
        perfTable.addHeaderCell(createTableCell("Worst Case", headerFont, TABLE_HEADER_COLOR, true));

        perfTable.addCell(createTableCell("Linear Search", normalFont, null, false));
        perfTable.addCell(createTableCell("O(1)", normalFont, null, false));
        perfTable.addCell(createTableCell("O(n)", normalFont, null, false));
        perfTable.addCell(createTableCell("O(n)", normalFont, null, false));

        perfTable.addCell(createTableCell("Binary Search", normalFont, null, false));
        perfTable.addCell(createTableCell("O(1)", normalFont, null, false));
        perfTable.addCell(createTableCell("O(log n)", normalFont, null, false));
        perfTable.addCell(createTableCell("O(log n)", normalFont, null, false));

        document.add(perfTable);

        if (arraySize > 0) {
            Paragraph practicalHeader = new Paragraph("\nPractical Analysis for Array Size " + arraySize)
                    .setFont(headerFont)
                    .setFontSize(14)
                    .setFontColor(SECTION_COLOR)
                    .setMarginBottom(5);
            document.add(practicalHeader);

            int maxLinearComparisons = arraySize;
            int maxBinaryComparisons = BinarySearch.getMaxComparisons(arraySize);

            Paragraph practicalInfo = new Paragraph(
                    String.format("• Linear Search: Maximum %d comparisons needed\n", maxLinearComparisons) +
                            String.format("• Binary Search: Maximum %d comparisons needed\n", maxBinaryComparisons) +
                            String.format("• Binary search is up to %.1fx faster for this array size",
                                    (double) maxLinearComparisons / maxBinaryComparisons)
            )
                    .setFont(normalFont)
                    .setFontSize(11);
            document.add(practicalInfo);
        }
    }

    private void addEducationalContent(Document document, PdfFont headerFont, PdfFont normalFont, String algorithmName) {
        Paragraph header = new Paragraph("Educational Insights")
                .setFont(headerFont)
                .setFontSize(18)
                .setFontColor(SECTION_COLOR)
                .setMarginBottom(10);
        document.add(header);

        String insights;
        if ("Linear Search".equals(algorithmName)) {
            insights =
                    "Key Learning Points for Linear Search:\n\n" +
                            "1. Simplicity: Linear search is the most straightforward search algorithm to understand and implement.\n\n" +
                            "2. Versatility: Works on both sorted and unsorted data, making it universally applicable.\n\n" +
                            "3. Space Efficiency: Requires no additional memory beyond the input array.\n\n" +
                            "4. When to Use: Best for small datasets or when data arrives in an unsorted stream.\n\n" +
                            "5. Real-world Applications: Looking through a small contact list, checking items in a shopping cart, or searching through log files.";
        } else {
            insights =
                    "Key Learning Points for Binary Search:\n\n" +
                            "1. Efficiency: Dramatically reduces search time by eliminating half the search space in each step.\n\n" +
                            "2. Prerequisites: Requires sorted data, which is a common trade-off in computer science.\n\n" +
                            "3. Divide and Conquer: Demonstrates the power of the divide-and-conquer algorithmic strategy.\n\n" +
                            "4. Logarithmic Growth: Shows how logarithmic algorithms scale much better than linear ones.\n\n" +
                            "5. Real-world Applications: Database indexing, dictionary lookups, finding elements in sorted arrays, and many system optimizations.";
        }

        Paragraph insightsPara = new Paragraph(insights)
                .setFont(normalFont)
                .setFontSize(11);
        document.add(insightsPara);
    }

    private void addFooter(Document document, PdfFont normalFont) {
        document.add(new Paragraph("\n\n"));

        Paragraph footer = new Paragraph(
                "This report was generated by the Searching Algorithm Visualizer teaching aid.\n" +
                        "For educational purposes - helping students understand and compare search algorithms."
        )
                .setFont(normalFont)
                .setFontSize(10)
                .setTextAlignment(TextAlignment.CENTER)
                .setMarginTop(20);
        document.add(footer);
    }

    private Cell createTableCell(String content, PdfFont font, DeviceRgb backgroundColor, boolean isHeader) {
        Cell cell = new Cell().add(new Paragraph(content).setFont(font));

        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }

        if (isHeader) {
            cell.setFontSize(12).setBold();
        } else {
            cell.setFontSize(10);
        }

        return cell;
    }
}