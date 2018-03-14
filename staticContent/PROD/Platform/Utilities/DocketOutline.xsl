<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:output method="text" indent="no" omit-xml-declaration="yes"/>
  <xsl:include href="Universal.xsl"/>

	<xsl:template match="Document" priority="1">
		<xsl:variable name="content">
			<xsl:if test="/Document/n-docbody/r/case.information.block | /Document/n-docbody/r[not(contains(//doc-type/text(), 'Dockets - Bankruptcy'))]/summary | /Document/n-docbody/r/case.info.block | /Document/n-docbody/r[contains(//doc-type/text(), 'Dockets - Bankruptcy')]">
				<div>
					<a href="#&docketsCaseInformationId;">
						<xsl:text>&docketsCaseInformation;</xsl:text>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/r/appeals.block">
				<div>
					<a href="#&docketsAppealsInformationId;">
						<xsl:text>&docketsAppealsInformation;</xsl:text>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/r/arrests.block">
				<div>
					<a href="#&docketsArrestInformationId;">
						<xsl:text>&docketsArrestInformation;</xsl:text>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/r/attorney.block[1]">
				<div>
					<a href="#&docketsAttorneyInformationId;">
						<xsl:text>&docketsAttorneyInformation;</xsl:text>
					</a>
				</div>
			</xsl:if>
			<xsl:if test="/Document/n-docbody/r/bail.block">
				<div>
					<a href="#&docketsBailInformationId;">
						<xsl:text>&docketsBailInformation;</xsl:text>
					</a>
				</div>
			</xsl:if>
      <xsl:if test="/Document/n-docbody/r/charges.block">
        <div>
          <a href="#&docketsChargeInformationId;">
            <xsl:text>&docketsChargeInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/complaint.block">
        <div>
          <a href="#&docketsComplaintInformationId;">
            <xsl:text>&docketsComplaintInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/docket.proceedings.block | /Document/n-docbody/r/docket.entries.block">
        <div>
          <a href="#&docketsDocketProceedingsId;">
            <xsl:text>&docketsDocketProceedings;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/judgment.block">
				<div>
				<xsl:choose>
					<xsl:when test="/Document/n-docbody/r/case.information.block/court.block/court.norm = 'CA-LA'">
						<a href="#&docketsFilingInformationId;">
							<xsl:text>&docketsFilingInformationLabel;</xsl:text>
						</a>
					</xsl:when>
					<xsl:otherwise>
						<a href="#&docketsJudgmentInformationId;">
							<xsl:text>&docketsJudgmentInformation;</xsl:text>
						</a>
					</xsl:otherwise>
				</xsl:choose>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/lower.court.block">
        <div>
          <a href="#&docketsLowerCourtInformationId;">
            <xsl:text>&docketsLowerCourtInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/matched.block[1] | /Document/n-docbody/r/party.block[not(child::party or contains(//doc-type/text(), 'Dockets - Federal Appellate'))][1]">
        <div>
          <a href="#&docketsParticipantInformationId;">
            <xsl:text>&docketsParticipantInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/notes.block">
        <div>
          <a href="#&docketsNotesCommentsId;">
            <xsl:text>&docketsNotesComments;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/payment.block">
        <div>
          <a href="#&docketsPaymentInformationId;">
            <xsl:text>&docketsPaymentInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/plea.block">
        <div>
          <a href="#&docketsPleaInformationId;">
            <xsl:text>&docketsPleaInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/sentence.block">
        <div>
          <a href="#&docketsSentenceInformationId;">
            <xsl:text>&docketsSentenceInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/calendar.entries.block">
        <div>
          <a href="#&docketsCalendarId;">
            <xsl:text>&docketsCalendar;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/claims.info.block">
        <div>
          <a href="#&docketsClaimsInformationId;">
            <xsl:text>&docketsClaimsInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/creditor.info.block[not(contains(//doc-type/text(), 'Dockets - Federal Judgements'))]">
        <div>
          <a href="#&docketsCreditorId;">
            <xsl:text>&docketsCreditor;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/debtor.info.block">
        <div>
          <a href="#&docketsDebtorInformationId;">
            <xsl:text>&docketsDebtorInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/schedule.341.block">
        <div>
          <a href="#&docketsSchedule341Id;">
            <xsl:text>&docketsSchedule341;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/trustee.info.block">
        <div>
          <a href="#&docketsTrusteeInformationId;">
            <xsl:text>&docketsTrusteeInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/synopsis.block">
        <div>
          <a href="#&docketsSynopsisInformationId;">
            <xsl:text>&docketsSynopsisInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/other.cases">
        <div>
          <a href="#&docketsOtherCasesId;">
            <xsl:text>&docketsOtherCases;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/party.block[contains(//doc-type/text(), 'Dockets - Federal Appellate')]">
        <div>
          <a href="#&docketsNamesId;">
            <xsl:text>&docketsNames;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/court.info.block">
        <div>
          <a href="#&docketsCourtInformationId;">
            <xsl:text>&docketsCourtInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/creditor.info.block[contains(//doc-type/text(), 'Dockets - Federal Judgements')]">
        <div>
          <a href="#&docketsCreditorInformationId;">
            <xsl:text>&docketsCreditorInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/other.info.block">
        <div>
          <a href="#&docketsOtherPartiesId;">
            <xsl:text>&docketsOtherParties;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/judgment.descrip.block">
        <div>
          <a href="#&docketsJudgmentDescriptionId;">
            <xsl:text>&docketsJudgmentDescription;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/calendar.block">
        <div>
          <a href="#&docketsCalendarInformationId;">
            <xsl:text>&docketsCalendarInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/patent.information.block">
        <div>
          <a href="#&docketsPatentInformationId;">
            <xsl:text>&docketsPatentInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/other.ip.information.block">
        <div>
          <a href="#&docketsOtherPropertiesId;">
            <xsl:text>&docketsOtherProperties;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document/n-docbody/r/trademark.information.block">
        <div>
          <a href="#&docketsTrademarkInformationId;">
            <xsl:text>&docketsTrademarkInformation;</xsl:text>
          </a>
        </div>
      </xsl:if>
      <xsl:if test="/Document[contains(//doc-type/text(), 'Dockets - Patent')]/n-docbody/r[child::title.block]">
        <div>
          <a href="#&docketsIPDocketSummariesId;">
            <xsl:text>&docketsIPDocketSummaries;</xsl:text>
          </a>
        </div>
      </xsl:if>
    </xsl:variable>
    <xsl:if test="string-length($content) &gt; 0">
			<div>
				<xsl:copy-of select="$content"/>
			</div>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>