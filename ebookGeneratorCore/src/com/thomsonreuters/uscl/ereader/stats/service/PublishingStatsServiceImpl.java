package com.thomsonreuters.uscl.ereader.stats.service;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.thomsonreuters.uscl.ereader.StatsUpdateTypeEnum;
import com.thomsonreuters.uscl.ereader.core.CoreConstants;
import com.thomsonreuters.uscl.ereader.core.book.domain.EbookAudit;
import com.thomsonreuters.uscl.ereader.stats.dao.PublishingStatsDao;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStats;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsFilter;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsPK;
import com.thomsonreuters.uscl.ereader.stats.domain.PublishingStatsSort;

import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public class PublishingStatsServiceImpl implements PublishingStatsService {

	// private static final Logger LOG = Logger.getLogger(PublishingStatsServiceImpl.class);
	public static final int MAX_EXCEL_SHEET_ROW_NUM = 65535;
	private static final String[] EXCEL_HEADER = {"TITLE_ID","PROVIEW_DISPLAY_NAME","JOB_INSTANCE_ID","AUDIT_ID","EBOOK_DEFINITION_ID",
		"BOOK_VERSION_SUBMITTED","JOB_HOST_NAME"," JOB_SUBMITTER_NAME"," JOB_SUBMIT_TIMESTAMP","PUBLISH_START_TIMESTAMP","GATHER_TOC_NODE_COUNT",
		"GATHER_TOC_SKIPPED_COUNT","GATHER_TOC_DOC_COUNT","GATHER_TOC_RETRY_COUNT","GATHER_DOC_EXPECTED_COUNT","GATHER_DOC_RETRY_COUNT",
		"GATHER_DOC_RETRIEVED_COUNT","GATHER_META_EXPECTED_COUNT","GATHER_META_RETRIEVED_COUNT","GATHER_META_RETRY_COUNT","GATHER_IMAGE_EXPECTED_COUNT",
		"GATHER_IMAGE_RETRIEVED_COUNT","GATHER_IMAGE_RETRY_COUNT","FORMAT_DOC_COUNT","TITLE_DOC_COUNT","TITLE_DUP_DOC_COUNT",
		"PUBLISH_STATUS","PUBLISH_END_TIMESTAMP","LAST_UPDATED","BOOK_SIZE","LARGEST_DOC_SIZE","LARGEST_IMAGE_SIZE","LARGEST_PDF_SIZE"};

	private PublishingStatsDao publishingStatsDAO;

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findPublishingStatsByJobId(Long JobId) {
		return publishingStatsDAO.findJobStatsByJobId(JobId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> getPubStatsByEbookDefSort(Long EbookDefId){
		return publishingStatsDAO.findPubStatsByEbookDefSort(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public PublishingStats findJobStatsByPubStatsPK(PublishingStatsPK jobIdPK) {
		return publishingStatsDAO.findJobStatsByPubStatsPK(jobIdPK);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStatsByEbookDef(Long EbookDefId) {
		return publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter, PublishingStatsSort sort) {
		return publishingStatsDAO.findPublishingStats(filter, sort);
	}

	@Override
	@Transactional(readOnly = true)
	public List<PublishingStats> findPublishingStats(PublishingStatsFilter filter) {
		return publishingStatsDAO.findPublishingStats(filter);
	}
	
	@Override
	@Transactional(readOnly = true)
	public int numberOfPublishingStats(PublishingStatsFilter filter) {
		return publishingStatsDAO.numberOfPublishingStats(filter);
	}

	@Override
	@Transactional(readOnly = true)
	public EbookAudit findAuditInfoByJobId(Long jobId) {
		return publishingStatsDAO.findAuditInfoByJobId(jobId);
	}

	@Override
	@Transactional
	public void savePublishingStats(PublishingStats jobstats) {
		publishingStatsDAO.saveJobStats(jobstats);

	}

	@Override
	@Transactional(readOnly = false, propagation = Propagation.REQUIRES_NEW)
	public int updatePublishingStats(PublishingStats jobstats,
			StatsUpdateTypeEnum updateType) {
		return publishingStatsDAO.updateJobStats(jobstats, updateType);
	}

	@Override
	@Transactional
	public void deleteJobStats(PublishingStats jobStats) {
		publishingStatsDAO.deleteJobStats(jobStats);
	}
	
	@Override
	@Transactional
	public Long getMaxGroupVersionById(Long ebookDefId) {
		return publishingStatsDAO.getMaxGroupVersionById(ebookDefId);
	}
	
	@Override
	@Transactional(readOnly = true)
	public Boolean hasIsbnBeenPublished(String isbn,  String titleId) {
		String replacedIsbn = "";
		Boolean hasBeenPublished = false;
		
		if(StringUtils.isNotBlank(isbn)) {
			replacedIsbn = isbn.replace("-", "");
		}
		
		List<String> publishedIsbns = publishingStatsDAO.findSuccessfullyPublishedIsbnByTitleId(titleId);
		for(String publishedIsbn : publishedIsbns) {
			if(StringUtils.isNotBlank(publishedIsbn)) {
				String replacedPublishedIsbn = publishedIsbn.replace("-", "");
				if(replacedPublishedIsbn.equalsIgnoreCase(replacedIsbn)) {
					// ISBN has been published
					hasBeenPublished = true;
					break;
				}
			}
		}
		return hasBeenPublished;
	}
	
	@Override
	@Transactional(readOnly = true)
	public Boolean hasSubGroupChanged(String subGroupHeading, Long ebookDefId){
		Boolean hasSubGroupChanged = true;
		List<String> previousSubGroupList = publishingStatsDAO.findSuccessfullyPublishedsubGroupById(ebookDefId);
		for(String previousSubGroupHeading : previousSubGroupList){
			//previousSubGroupHeading could be null as it may be single book in previous version
			if(previousSubGroupHeading != null && previousSubGroupHeading.equalsIgnoreCase(subGroupHeading)) {
				hasSubGroupChanged = false;
				break;
			}
		}
		return hasSubGroupChanged;
	}

	@Override
	@Transactional(readOnly = true)
	public EbookAudit findLastSuccessfulJobStatsAuditByEbookDef(Long EbookDefId) {

		EbookAudit lastAuditSuccessful = null;
		PublishingStats lastSuccessfulPublishingStat = null;
		
		List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);

		if (publishingStats != null && publishingStats.size() > 0) {
			lastSuccessfulPublishingStat = publishingStats.get(0);
			for (PublishingStats publishingStat : publishingStats) {
				if (publishingStat.getJobInstanceId().longValue() >= lastSuccessfulPublishingStat.getJobInstanceId().longValue()
						&& (PublishingStats.SUCCESFULL_PUBLISH_STATUS.equalsIgnoreCase(
								publishingStat.getPublishStatus()) || 
								PublishingStats.SEND_EMAIL_COMPLETE.equalsIgnoreCase(publishingStat.getPublishStatus()))) {
					lastSuccessfulPublishingStat = publishingStat;
					lastAuditSuccessful = publishingStat.getAudit();
				}
			}
		}
		return lastAuditSuccessful;

	}

	@Transactional(readOnly = true)
	public Date findLastPublishDateForBook(Long EbookDefId) {

		Date lastPublishDate = null;
		List<PublishingStats> publishingStats = publishingStatsDAO.findPublishingStatsByEbookDef(EbookDefId);
		PublishingStats lastPublishingStat = null;

		if (publishingStats != null && publishingStats.size() > 0) {
			lastPublishingStat = publishingStats.get(0);
			for (PublishingStats publishingStat : publishingStats) {
				if (lastPublishingStat.getPublishEndTimestamp() == null) {
					lastPublishingStat = publishingStat;
				}
				if (lastPublishingStat.getPublishEndTimestamp() != null
						&& publishingStat.getPublishEndTimestamp() != null
						&& (publishingStat.getPublishEndTimestamp() == lastPublishingStat
								.getPublishEndTimestamp() || publishingStat
								.getPublishEndTimestamp().after(
										lastPublishingStat
												.getPublishEndTimestamp()))) {
					lastPublishingStat = publishingStat;
				}
			}
		}

		if (lastPublishingStat != null) {
			lastPublishDate = lastPublishingStat.getPublishEndTimestamp();
		}

		return lastPublishDate;
	}
	
	@Transactional(readOnly = true) 
	public Workbook createExcelDocument(PublishingStatsFilter filter) {
		List<PublishingStats> stats = publishingStatsDAO.findPublishingStats(filter);
		
		Workbook wb = new HSSFWorkbook();
		CreationHelper createHelper = wb.getCreationHelper();
		Sheet sheet = wb.createSheet("Publishing Stats");
		
	    CellStyle cellStyle = wb.createCellStyle();
	    cellStyle.setDataFormat(
	        createHelper.createDataFormat().getFormat(CoreConstants.DATE_TIME_FORMAT_PATTERN));
		Row row = sheet.createRow(0);
		
		int columnIndex = 0;
		for(String header : EXCEL_HEADER) {
			row.createCell(columnIndex).setCellValue(header);
			sheet.autoSizeColumn(columnIndex);
			columnIndex++;
		}
		
		Cell cell = null;
		
		int rowIndex = 1;
		for(PublishingStats stat : stats) {
			// Create a row and put some cells in it.
		    row = sheet.createRow(rowIndex);
		    row.createCell(0).setCellValue(stat.getAudit().getTitleId());
		    row.createCell(1).setCellValue(stat.getAudit().getProviewDisplayName());
		    if(stat.getJobInstanceId() != null) {
		    	row.createCell(2).setCellValue(stat.getJobInstanceId());
		    }
		    if(stat.getAudit() != null && stat.getAudit().getAuditId() != null) {
		    	row.createCell(3).setCellValue(stat.getAudit().getAuditId());
		    }
		    if(stat.getEbookDefId() != null) {
		    	row.createCell(4).setCellValue(stat.getEbookDefId());
		    }
		    row.createCell(5).setCellValue(stat.getBookVersionSubmitted());
		    row.createCell(6).setCellValue(stat.getJobHostName());
		    row.createCell(7).setCellValue(stat.getJobSubmitterName());
		    if(stat.getJobSubmitTimestamp() != null) {
		    	cell = row.createCell(8);
		    	cell.setCellValue(stat.getJobSubmitTimestamp());
		    	cell.setCellStyle(cellStyle);
		    }
		    if(stat.getPublishStartTimestamp() != null) {
		    	cell = row.createCell(9);
		    	cell.setCellValue(stat.getPublishStartTimestamp());
			 	cell.setCellStyle(cellStyle);
			 }
			 if(stat.getGatherTocNodeCount() != null) {
			 row.createCell(10).setCellValue(stat.getGatherTocNodeCount());
			 }
			 if(stat.getGatherTocSkippedCount() != null) {
			 row.createCell(11).setCellValue(stat.getGatherTocSkippedCount());
			 }
			 if(stat.getGatherTocDocCount() != null) {
				 row.createCell(12).setCellValue(stat.getGatherTocDocCount());
			 }
			if(stat.getGatherTocRetryCount() != null) {
			 row.createCell(13).setCellValue(stat.getGatherTocRetryCount());
			}
			if(stat.getGatherDocExpectedCount() != null) {
			 row.createCell(14).setCellValue(stat.getGatherDocExpectedCount());
			}
			if(stat.getGatherDocRetryCount() != null) {
			 row.createCell(15).setCellValue(stat.getGatherDocRetryCount());
			}
			if(stat.getGatherDocRetrievedCount() != null) {
			 row.createCell(16).setCellValue(stat.getGatherDocRetrievedCount());
			}
			if(stat.getGatherMetaExpectedCount() != null) {
			 row.createCell(17).setCellValue(stat.getGatherMetaExpectedCount());
			}
			if(stat.getGatherMetaRetrievedCount() != null) {
				row.createCell(18).setCellValue(stat.getGatherMetaRetrievedCount());
			}
			if(stat.getGatherMetaRetryCount() != null) {
				row.createCell(19).setCellValue(stat.getGatherMetaRetryCount());
			}
			if(stat.getGatherImageExpectedCount() != null) {
				row.createCell(20).setCellValue(stat.getGatherImageExpectedCount());
			}
			if(stat.getGatherImageRetrievedCount() != null) {
				row.createCell(21).setCellValue(stat.getGatherImageRetrievedCount());
			}
			if(stat.getGatherImageRetryCount() != null ) {
				row.createCell(22).setCellValue(stat.getGatherImageRetryCount());
			}
			if(stat.getFormatDocCount() != null) {
				row.createCell(23).setCellValue(stat.getFormatDocCount());
			}
			if(stat.getTitleDocCount() != null) {
				row.createCell(24).setCellValue( stat.getTitleDocCount() );
			}
		    if(stat.getTitleDupDocCount() != null) {
		    	row.createCell(25).setCellValue( stat.getTitleDupDocCount());
		    }
		    if(stat.getPublishStatus() != null) {
		    	row.createCell(26).setCellValue(stat.getPublishStatus());
		    }
		    
		    if(stat.getPublishEndTimestamp() != null) {
		    	cell = row.createCell(27);
		    	cell.setCellValue(stat.getPublishEndTimestamp());
		    	cell.setCellStyle(cellStyle);
		    }
		    if(stat.getLastUpdated() != null) {
		    	cell = row.createCell(28);
		    	cell.setCellValue(stat.getLastUpdated());
		    	cell.setCellStyle(cellStyle);
		    }
		    if(stat.getBookSize() != null) {
		    	row.createCell(29).setCellValue(stat.getBookSize());
		    } 
		    if(stat.getLargestDocSize() != null) {
		    	row.createCell(30).setCellValue(stat.getLargestDocSize());
		    } 
		    if(stat.getLargestImageSize() != null) {
		    	row.createCell(31).setCellValue(stat.getLargestImageSize());
		    }
		    if(stat.getLargestPdfSize() != null) {
		    	row.createCell(32).setCellValue(stat.getLargestPdfSize());
		    }
		    
		    if(rowIndex == (MAX_EXCEL_SHEET_ROW_NUM - 1)) {
		    	row = sheet.createRow(MAX_EXCEL_SHEET_ROW_NUM);
		    	row.createCell(0).setCellValue("You have reached the maximum amount of rows.  Please reduce the amount of rows by using the filter on the eBook Manager before generating the Excel file.");
		    	break;
		    }
		    rowIndex++;
		}
		
		return wb;
	}

	@Transactional(readOnly = true)
	public List<PublishingStats> findAllPublishingStats() {
		return publishingStatsDAO.findAllPublishingStats();
	}

	@Required
	public void setPublishingStatsDAO(PublishingStatsDao dao) {
		this.publishingStatsDAO = dao;
	}
}
