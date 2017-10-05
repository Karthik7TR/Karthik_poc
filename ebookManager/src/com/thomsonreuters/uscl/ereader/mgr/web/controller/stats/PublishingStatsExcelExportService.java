package com.thomsonreuters.uscl.ereader.mgr.web.controller.stats;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class PublishingStatsExcelExportService extends BaseExcelExportService {
    public static final String STATS_NAME = "Publishing Stats";
    public static final String[] STATS_HEADER = {
        "TITLE_ID",
        "PROVIEW_DISPLAY_NAME",
        "JOB_INSTANCE_ID",
        "AUDIT_ID",
        "EBOOK_DEFINITION_ID",
        "BOOK_VERSION_SUBMITTED",
        "JOB_HOST_NAME",
        " JOB_SUBMITTER_NAME",
        " JOB_SUBMIT_TIMESTAMP",
        "PUBLISH_START_TIMESTAMP",
        "GATHER_TOC_NODE_COUNT",
        "GATHER_TOC_SKIPPED_COUNT",
        "GATHER_TOC_DOC_COUNT",
        "GATHER_TOC_RETRY_COUNT",
        "GATHER_DOC_EXPECTED_COUNT",
        "GATHER_DOC_RETRY_COUNT",
        "GATHER_DOC_RETRIEVED_COUNT",
        "GATHER_META_EXPECTED_COUNT",
        "GATHER_META_RETRIEVED_COUNT",
        "GATHER_META_RETRY_COUNT",
        "GATHER_IMAGE_EXPECTED_COUNT",
        "GATHER_IMAGE_RETRIEVED_COUNT",
        "GATHER_IMAGE_RETRY_COUNT",
        "FORMAT_DOC_COUNT",
        "TITLE_DOC_COUNT",
        "TITLE_DUP_DOC_COUNT",
        "PUBLISH_STATUS",
        "PUBLISH_END_TIMESTAMP",
        "LAST_UPDATED",
        "BOOK_SIZE",
        "LARGEST_DOC_SIZE",
        "LARGEST_IMAGE_SIZE",
        "LARGEST_PDF_SIZE"};

    public PublishingStatsExcelExportService() {
        super();
        EXCEL_HEADER = STATS_HEADER;
        SHEET_NAME = STATS_NAME;
    }

    @Override
    protected void fillRows(final Sheet sheet, final CellStyle cellStyle, final HttpSession session) {
        final List<PublishingStats> stats = fetchPaginatedList(session);
        if (stats == null) {
            throw new NullPointerException("No Publishing Statistics Found");
        }
        Cell cell = null;

        int rowIndex = 1;
        for (final PublishingStats stat : stats) {
            // Create a row and put some cells in it.
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(stat.getAudit().getTitleId());
            row.createCell(1).setCellValue(stat.getAudit().getProviewDisplayName());
            if (stat.getJobInstanceId() != null) {
                row.createCell(2).setCellValue(stat.getJobInstanceId());
            }
            if (stat.getAudit().getAuditId() != null) {
                row.createCell(3).setCellValue(stat.getAudit().getAuditId());
            }
            if (stat.getEbookDefId() != null) {
                row.createCell(4).setCellValue(stat.getEbookDefId());
            }
            row.createCell(5).setCellValue(stat.getBookVersionSubmitted());
            row.createCell(6).setCellValue(stat.getJobHostName());
            row.createCell(7).setCellValue(stat.getJobSubmitterName());
            if (stat.getJobSubmitTimestamp() != null) {
                cell = row.createCell(8);
                cell.setCellValue(stat.getJobSubmitTimestamp());
                cell.setCellStyle(cellStyle);
            }
            if (stat.getPublishStartTimestamp() != null) {
                cell = row.createCell(9);
                cell.setCellValue(stat.getPublishStartTimestamp());
                cell.setCellStyle(cellStyle);
            }
            if (stat.getGatherTocNodeCount() != null) {
                row.createCell(10).setCellValue(stat.getGatherTocNodeCount());
            }
            if (stat.getGatherTocSkippedCount() != null) {
                row.createCell(11).setCellValue(stat.getGatherTocSkippedCount());
            }
            if (stat.getGatherTocDocCount() != null) {
                row.createCell(12).setCellValue(stat.getGatherTocDocCount());
            }
            if (stat.getGatherTocRetryCount() != null) {
                row.createCell(13).setCellValue(stat.getGatherTocRetryCount());
            }
            if (stat.getGatherDocExpectedCount() != null) {
                row.createCell(14).setCellValue(stat.getGatherDocExpectedCount());
            }
            if (stat.getGatherDocRetryCount() != null) {
                row.createCell(15).setCellValue(stat.getGatherDocRetryCount());
            }
            if (stat.getGatherDocRetrievedCount() != null) {
                row.createCell(16).setCellValue(stat.getGatherDocRetrievedCount());
            }
            if (stat.getGatherMetaExpectedCount() != null) {
                row.createCell(17).setCellValue(stat.getGatherMetaExpectedCount());
            }
            if (stat.getGatherMetaRetrievedCount() != null) {
                row.createCell(18).setCellValue(stat.getGatherMetaRetrievedCount());
            }
            if (stat.getGatherMetaRetryCount() != null) {
                row.createCell(19).setCellValue(stat.getGatherMetaRetryCount());
            }
            if (stat.getGatherImageExpectedCount() != null) {
                row.createCell(20).setCellValue(stat.getGatherImageExpectedCount());
            }
            if (stat.getGatherImageRetrievedCount() != null) {
                row.createCell(21).setCellValue(stat.getGatherImageRetrievedCount());
            }
            if (stat.getGatherImageRetryCount() != null) {
                row.createCell(22).setCellValue(stat.getGatherImageRetryCount());
            }
            if (stat.getFormatDocCount() != null) {
                row.createCell(23).setCellValue(stat.getFormatDocCount());
            }
            if (stat.getTitleDocCount() != null) {
                row.createCell(24).setCellValue(stat.getTitleDocCount());
            }
            if (stat.getTitleDupDocCount() != null) {
                row.createCell(25).setCellValue(stat.getTitleDupDocCount());
            }
            if (stat.getPublishStatus() != null) {
                row.createCell(26).setCellValue(stat.getPublishStatus());
            }

            if (stat.getPublishEndTimestamp() != null) {
                cell = row.createCell(27);
                cell.setCellValue(stat.getPublishEndTimestamp());
                cell.setCellStyle(cellStyle);
            }
            if (stat.getLastUpdated() != null) {
                cell = row.createCell(28);
                cell.setCellValue(stat.getLastUpdated());
                cell.setCellStyle(cellStyle);
            }
            if (stat.getBookSize() != null) {
                row.createCell(29).setCellValue(stat.getBookSize());
            }
            if (stat.getLargestDocSize() != null) {
                row.createCell(30).setCellValue(stat.getLargestDocSize());
            }
            if (stat.getLargestImageSize() != null) {
                row.createCell(31).setCellValue(stat.getLargestImageSize());
            }
            if (stat.getLargestPdfSize() != null) {
                row.createCell(32).setCellValue(stat.getLargestPdfSize());
            }

            if (rowIndex == (MAX_EXCEL_SHEET_ROW_NUM - 1)) {
                row = sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM);
                row.createCell(0).setCellValue(
                    "You have reached the maximum amount of rows.  Please reduce the amount of rows by using the filter on the eBook Manager before generating the Excel file.");
                break;
            }
            rowIndex++;
        }
    }

    private List<PublishingStats> fetchPaginatedList(final HttpSession session) {
        final PublishingStatsPaginatedList paginated =
            (PublishingStatsPaginatedList) session.getAttribute(WebConstants.KEY_PAGINATED_LIST);
        return paginated == null ? null : paginated.getList();
    }
}
