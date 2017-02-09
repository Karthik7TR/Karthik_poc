package com.thomsonreuters.uscl.ereader.mgr.web.controller.proviewgroup;

import java.util.List;

import javax.servlet.http.HttpSession;

import com.thomsonreuters.uscl.ereader.deliver.service.ProviewGroup;
import com.thomsonreuters.uscl.ereader.ioutil.BaseExcelExportService;
import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class ProviewGroupExcelExportService extends BaseExcelExportService
{
    public static final String GROUPS_NAME = "ProviewGroups";
    public static final String[] GROUPS_HEADER =
        {"Group Name", "Group ID", "Latest Status", "Total Versions", "Latest Version"};

    public ProviewGroupExcelExportService()
    {
        super();
        SHEET_NAME = GROUPS_NAME;
        EXCEL_HEADER = GROUPS_HEADER;
    }

    @Override
    protected void fillRows(final Sheet sheet, final CellStyle cellStyle, final HttpSession session)
    {
        final List<ProviewGroup> groups = fetchSelectedProviewGroups(session);
        if (groups == null)
        {
            throw new NullPointerException("No Group Information Found");
        }
        int rowIndex = 1;
        for (final ProviewGroup group : groups)
        {
            // Create a row and put some cells in it.
            Row row = sheet.createRow(rowIndex);
            row.createCell(0).setCellValue(group.getGroupName());
            row.createCell(1).setCellValue(group.getGroupId());
            row.createCell(2).setCellValue(group.getGroupStatus());
            row.createCell(3).setCellValue(group.getTotalNumberOfVersions());
            row.createCell(4).setCellValue(group.getGroupVersion());
            if (rowIndex == (MAX_EXCEL_SHEET_ROW_NUM - 1))
            {
                row = sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM);
                row.createCell(0).setCellValue(
                    "You have reached the maximum amount of rows.  Please reduce the amount of rows by using the filter on the eBook Manager before generating the Excel file.");
                break;
            }
            rowIndex++;
        }
    }

    private List<ProviewGroup> fetchSelectedProviewGroups(final HttpSession session)
    {
        return (List<ProviewGroup>) session.getAttribute(WebConstants.KEY_SELECTED_PROVIEW_GROUPS);
    }
}
