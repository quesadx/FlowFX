package cr.ac.una.flowfx.util;

import cr.ac.una.flowfx.model.ProjectActivityViewModel;
import cr.ac.una.flowfx.model.ProjectViewModel;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for exporting project schedules to Excel format.
 * 
 * <p>This utility provides comprehensive Excel export functionality for project data including:
 * <ul>
 *   <li>Project metadata and stakeholder information</li>
 *   <li>Activity schedules with responsible person details</li>
 *   <li>Professional styling and formatting</li>
 *   <li>Automatic column sizing and freeze panes</li>
 * </ul>
 * 
 * <p>The exported Excel file contains a complete project schedule with activities
 * sorted by execution order and includes planned vs actual dates for progress tracking.
 * 
 * @author FlowFX Development Team
 * @version 1.0
 * @since 3.0
 */
public final class ProjectExcelExportUtil {

    private static final Logger LOGGER = Logger.getLogger(ProjectExcelExportUtil.class.getName());

    // Private constructor to prevent instantiation of utility class
    private ProjectExcelExportUtil() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    /**
     * Exports a project schedule to Excel format.
     * 
     * @param parentStage the parent stage for the file chooser dialog
     * @param projectViewModel the project data to export
     * @param activities the list of project activities
     * @param personNameResolver function to resolve person names by ID
     * @param statusMapper function to map status codes to display text
     * @return true if export was successful, false otherwise
     */
    public static boolean exportProjectSchedule(
            Stage parentStage,
            ProjectViewModel projectViewModel,
            List<ProjectActivityViewModel> activities,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        try {
            configureModuleAccess();
            
            File selectedFile = showExcelSaveDialog(parentStage, projectViewModel.getName());
            if (selectedFile == null) return false;
            
            generateExcelWorkbook(selectedFile, projectViewModel, activities, personNameResolver, statusMapper);
            LOGGER.info("Excel export completed: " + selectedFile.getAbsolutePath());
            return true;
            
        } catch (Exception ex) {
            LOGGER.warning("Excel export failed: " + ex.getMessage());
            return false;
        }
    }

    /**
     * Configures module access for Apache POI.
     */
    private static void configureModuleAccess() {
        try {
            Module currentModule = ProjectExcelExportUtil.class.getModule();
            Module unnamedModule = ProjectExcelExportUtil.class.getClassLoader().getUnnamedModule();
            if (currentModule != null && unnamedModule != null) {
                currentModule.addReads(unnamedModule);
            }
        } catch (Throwable ignored) {
            // Best-effort compatibility fix
        }
    }

    /**
     * Shows Excel save dialog.
     */
    private static File showExcelSaveDialog(Stage parentStage, String projectName) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Project Schedule (.xlsx)");
        fileChooser.getExtensionFilters().add(
            new FileChooser.ExtensionFilter("Excel Workbook (*.xlsx)", "*.xlsx"));
        
        String defaultName = sanitizeFileName(projectName);
        if (defaultName == null || defaultName.isBlank()) {
            defaultName = "project-schedule";
        }
        fileChooser.setInitialFileName(defaultName + "-schedule.xlsx");
        
        File selectedFile = fileChooser.showSaveDialog(parentStage);
        return ensureXlsxExtension(selectedFile);
    }

    /**
     * Ensures file has .xlsx extension.
     */
    private static File ensureXlsxExtension(File file) {
        if (file != null && !file.getName().toLowerCase().endsWith(".xlsx")) {
            return new File(file.getParentFile(), file.getName() + ".xlsx");
        }
        return file;
    }

    /**
     * Sanitizes filename by removing illegal characters.
     */
    private static String sanitizeFileName(String fileName) {
        if (fileName == null) return null;
        
        String sanitized = fileName.trim();
        if (sanitized.isEmpty()) return sanitized;
        
        return sanitized.replaceAll("[\\\\/:*?\"<>|]", "_");
    }

    /**
     * Generates the Excel workbook.
     */
    private static void generateExcelWorkbook(
            File file, 
            ProjectViewModel projectViewModel, 
            List<ProjectActivityViewModel> activities,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) throws Exception {
        
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             FileOutputStream fileOutput = new FileOutputStream(file)) {
            
            Sheet sheet = workbook.createSheet("Schedule");
            ExcelStyleManager styleManager = new ExcelStyleManager(workbook);
            
            int currentRow = writeExcelContent(sheet, styleManager, projectViewModel, activities, personNameResolver, statusMapper);
            configureSheetPresentation(sheet, currentRow, activities.size());
            
            workbook.write(fileOutput);
        }
    }

    /**
     * Writes all Excel content and returns final row number.
     */
    private static int writeExcelContent(
            Sheet sheet, 
            ExcelStyleManager styleManager, 
            ProjectViewModel projectViewModel,
            List<ProjectActivityViewModel> activities,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        int currentRow = 0;
        currentRow = writeProjectHeader(sheet, styleManager, currentRow, projectViewModel);
        currentRow = writeProjectMetadata(sheet, styleManager, currentRow, projectViewModel, personNameResolver);
        currentRow++; // Empty spacer row
        currentRow = writeScheduleData(sheet, styleManager, currentRow, projectViewModel, activities, personNameResolver, statusMapper);
        return currentRow;
    }

    /**
     * Configures sheet presentation (freeze panes, column sizing).
     */
    private static void configureSheetPresentation(Sheet sheet, int totalRows, int activitiesCount) {
        sheet.createFreezePane(0, totalRows - activitiesCount);
        autoSizeColumns(sheet, 8);
    }

    /**
     * Auto-sizes columns in the sheet.
     */
    private static void autoSizeColumns(Sheet sheet, int columnCount) {
        for (int i = 0; i < columnCount; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    /**
     * Writes project header to Excel.
     */
    private static int writeProjectHeader(Sheet sheet, ExcelStyleManager styleManager, int startRow, ProjectViewModel projectViewModel) {
        Row titleRow = sheet.createRow(startRow++);
        String projectName = projectViewModel.getName() == null ? "" : projectViewModel.getName().trim();
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("Project Schedule: " + projectName);
        titleCell.setCellStyle(styleManager.getTitleStyle());
        return startRow;
    }

    /**
     * Writes project metadata to Excel.
     */
    private static int writeProjectMetadata(Sheet sheet, ExcelStyleManager styleManager, int startRow, ProjectViewModel projectViewModel, PersonNameResolver personNameResolver) {
        startRow = writeGenerationDate(sheet, styleManager, startRow);
        startRow = writeStakeholders(sheet, styleManager, startRow, projectViewModel, personNameResolver);
        return startRow;
    }

    /**
     * Writes generation date to Excel.
     */
    private static int writeGenerationDate(Sheet sheet, ExcelStyleManager styleManager, int row) {
        Row generationRow = sheet.createRow(row++);
        Cell generationLabel = generationRow.createCell(0);
        generationLabel.setCellValue("Generated:");
        generationLabel.setCellStyle(styleManager.getMetaLabelStyle());
        
        Cell generationValue = generationRow.createCell(1);
        String currentTime = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm").format(LocalDateTime.now());
        generationValue.setCellValue(currentTime);
        return row;
    }

    /**
     * Writes stakeholder information to Excel.
     */
    private static int writeStakeholders(Sheet sheet, ExcelStyleManager styleManager, int startRow, ProjectViewModel projectViewModel, PersonNameResolver personNameResolver) {
        startRow = writeStakeholder(sheet, styleManager, startRow, "Project Leader:", 
            personNameResolver.resolvePersonName(projectViewModel.getLeaderUserId()));
        startRow = writeStakeholder(sheet, styleManager, startRow, "Technical Leader:", 
            personNameResolver.resolvePersonName(projectViewModel.getTechLeaderId()));
        startRow = writeStakeholder(sheet, styleManager, startRow, "Sponsor:", 
            personNameResolver.resolvePersonName(projectViewModel.getSponsorId()));
        return startRow;
    }

    /**
     * Writes a stakeholder row to Excel.
     */
    private static int writeStakeholder(Sheet sheet, ExcelStyleManager styleManager, int row, String role, String name) {
        Row stakeholderRow = sheet.createRow(row);
        Cell roleCell = stakeholderRow.createCell(0);
        roleCell.setCellValue(role);
        roleCell.setCellStyle(styleManager.getMetaLabelStyle());
        stakeholderRow.createCell(1).setCellValue(name == null ? "" : name);
        return row + 1;
    }

    /**
     * Writes schedule data to Excel.
     */
    private static int writeScheduleData(
            Sheet sheet, 
            ExcelStyleManager styleManager, 
            int startRow, 
            ProjectViewModel projectViewModel,
            List<ProjectActivityViewModel> activities,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        startRow = writeScheduleHeaders(sheet, styleManager, startRow);
        startRow = writeProjectDataRow(sheet, styleManager, startRow, projectViewModel, personNameResolver, statusMapper);
        startRow = writeActivityRows(sheet, styleManager, startRow, activities, personNameResolver, statusMapper);
        return startRow;
    }

    /**
     * Writes schedule headers to Excel.
     */
    private static int writeScheduleHeaders(Sheet sheet, ExcelStyleManager styleManager, int row) {
        Row headerRow = sheet.createRow(row++);
        String[] headers = {
            "Activity", "Responsible (ID)", "Responsible Name", "Status",
            "Planned Start", "Planned End", "Actual Start", "Actual End"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = headerRow.createCell(i);
            headerCell.setCellValue(headers[i]);
            headerCell.setCellStyle(styleManager.getHeaderStyle());
        }
        return row;
    }

    /**
     * Writes project data row to Excel.
     */
    private static int writeProjectDataRow(
            Sheet sheet, 
            ExcelStyleManager styleManager, 
            int row, 
            ProjectViewModel projectViewModel,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        Row projectRow = sheet.createRow(row);
        String projectName = projectViewModel.getName() == null ? "" : projectViewModel.getName().trim();
        long leaderId = projectViewModel.getLeaderUserId();
        String leaderName = personNameResolver.resolvePersonName(leaderId);
        
        int column = 0;
        writeTextCell(projectRow, column++, projectName, styleManager.getTextCellStyle());
        writeTextCell(projectRow, column++, leaderId > 0 ? String.valueOf(leaderId) : "", 
            styleManager.getTextCellStyle());
        writeTextCell(projectRow, column++, leaderName == null ? "" : leaderName, 
            styleManager.getTextCellStyle());
        writeTextCell(projectRow, column++, statusMapper.mapStatus(projectViewModel.getStatus()), 
            styleManager.getTextCellStyle());
        
        writeProjectDateCells(projectRow, column, styleManager, projectViewModel);
        return row + 1;
    }

    /**
     * Writes date cells for project row.
     */
    private static void writeProjectDateCells(Row row, int startColumn, ExcelStyleManager styleManager, ProjectViewModel projectViewModel) {
        writeDateCell(row.createCell(startColumn++), projectViewModel.getPlannedStartDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), projectViewModel.getPlannedEndDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), projectViewModel.getActualStartDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), projectViewModel.getActualEndDate(), 
            styleManager.getDateCellStyle());
    }

    /**
     * Writes activity rows to Excel.
     */
    private static int writeActivityRows(
            Sheet sheet, 
            ExcelStyleManager styleManager, 
            int startRow, 
            List<ProjectActivityViewModel> activities,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        List<ProjectActivityViewModel> sortedActivities = activities.stream()
            .sorted(Comparator.comparingInt(ProjectActivityViewModel::getExecutionOrder))
            .toList();
        
        for (ProjectActivityViewModel activity : sortedActivities) {
            startRow = writeActivityDataRow(sheet, styleManager, startRow, activity, personNameResolver, statusMapper);
        }
        return startRow;
    }

    /**
     * Writes an activity data row to Excel.
     */
    private static int writeActivityDataRow(
            Sheet sheet, 
            ExcelStyleManager styleManager, 
            int row, 
            ProjectActivityViewModel activity,
            PersonNameResolver personNameResolver,
            StatusMapper statusMapper) {
        
        Row activityRow = sheet.createRow(row);
        long responsibleId = activity.getResponsibleId();
        String responsibleName = personNameResolver.resolvePersonName(responsibleId);
        
        int column = 0;
        writeTextCell(activityRow, column++, 
            activity.getDescription() == null ? "" : activity.getDescription(), 
            styleManager.getTextCellStyle());
        writeTextCell(activityRow, column++, responsibleId > 0 ? String.valueOf(responsibleId) : "", 
            styleManager.getTextCellStyle());
        writeTextCell(activityRow, column++, responsibleName == null ? "" : responsibleName, 
            styleManager.getTextCellStyle());
        writeTextCell(activityRow, column++, statusMapper.mapStatus(activity.getStatus()), 
            styleManager.getTextCellStyle());
        
        writeActivityDateCells(activityRow, column, activity, styleManager);
        return row + 1;
    }

    /**
     * Writes date cells for an activity row.
     */
    private static void writeActivityDateCells(Row row, int startColumn, ProjectActivityViewModel activity, ExcelStyleManager styleManager) {
        writeDateCell(row.createCell(startColumn++), activity.getPlannedStartDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), activity.getPlannedEndDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), activity.getActualStartDate(), 
            styleManager.getDateCellStyle());
        writeDateCell(row.createCell(startColumn++), activity.getActualEndDate(), 
            styleManager.getDateCellStyle());
    }

    /**
     * Writes a text cell with style.
     */
    private static void writeTextCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value == null ? "" : value);
        cell.setCellStyle(style);
    }

    /**
     * Writes a date cell with style.
     */
    private static void writeDateCell(Cell cell, Date value, CellStyle dateStyle) {
        if (value == null) {
            cell.setBlank();
        } else {
            cell.setCellValue(value);
            cell.setCellStyle(dateStyle);
        }
    }

    /**
     * Helper class for managing Excel cell styles.
     */
    public static class ExcelStyleManager {
        private final CellStyle titleStyle;
        private final CellStyle headerStyle;
        private final CellStyle textCellStyle;
        private final CellStyle metaLabelStyle;
        private final CellStyle dateCellStyle;
        
        public ExcelStyleManager(XSSFWorkbook workbook) {
            this.titleStyle = createTitleStyle(workbook);
            this.headerStyle = createHeaderStyle(workbook);
            this.textCellStyle = createTextCellStyle(workbook);
            this.metaLabelStyle = createMetaLabelStyle(workbook);
            this.dateCellStyle = createDateCellStyle(workbook);
        }
        
        private CellStyle createTitleStyle(XSSFWorkbook workbook) {
            Font titleFont = workbook.createFont();
            if (titleFont instanceof XSSFFont xssfFont) {
                xssfFont.setBold(true);
                xssfFont.setFontHeightInPoints((short) 14);
            } else {
                titleFont.setBold(true);
            }
            
            CellStyle style = workbook.createCellStyle();
            style.setFont(titleFont);
            style.setAlignment(HorizontalAlignment.LEFT);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }
        
        private CellStyle createHeaderStyle(XSSFWorkbook workbook) {
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            
            CellStyle style = workbook.createCellStyle();
            style.setFont(headerFont);
            style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            style.setAlignment(HorizontalAlignment.CENTER);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            addBorders(style);
            return style;
        }
        
        private CellStyle createTextCellStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            addBorders(style);
            style.setVerticalAlignment(VerticalAlignment.TOP);
            return style;
        }
        
        private CellStyle createMetaLabelStyle(XSSFWorkbook workbook) {
            Font metaFont = workbook.createFont();
            metaFont.setBold(true);
            
            CellStyle style = workbook.createCellStyle();
            style.setFont(metaFont);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            return style;
        }
        
        private CellStyle createDateCellStyle(XSSFWorkbook workbook) {
            CellStyle style = workbook.createCellStyle();
            style.cloneStyleFrom(textCellStyle);
            short dateFormat = workbook.getCreationHelper().createDataFormat().getFormat("yyyy-mm-dd");
            style.setDataFormat(dateFormat);
            return style;
        }
        
        private void addBorders(CellStyle style) {
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
        }
        
        // Getters
        public CellStyle getTitleStyle() { return titleStyle; }
        public CellStyle getHeaderStyle() { return headerStyle; }
        public CellStyle getTextCellStyle() { return textCellStyle; }
        public CellStyle getMetaLabelStyle() { return metaLabelStyle; }
        public CellStyle getDateCellStyle() { return dateCellStyle; }
    }

    /**
     * Functional interface for resolving person names by ID.
     */
    @FunctionalInterface
    public interface PersonNameResolver {
        /**
         * Resolves a person's display name by their ID.
         * 
         * @param personId the person ID to resolve
         * @return the person's display name, or null if not found
         */
        String resolvePersonName(long personId);
    }

    /**
     * Functional interface for mapping status codes to display text.
     */
    @FunctionalInterface
    public interface StatusMapper {
        /**
         * Maps a status code to display text.
         * 
         * @param statusCode the status code to map
         * @return the mapped status display text
         */
        String mapStatus(String statusCode);
    }
}