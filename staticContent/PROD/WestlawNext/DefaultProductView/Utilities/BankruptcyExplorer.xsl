<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocketsBankruptcy.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:param name="BackToOriginalSearchUrl" />
	<xsl:param name="SectionMode" />
	<xsl:param name="UpdateUrl" />
	<xsl:param name="NavigationPathForFilingsAndOrders" />
	<xsl:param name="NavigationPathForFilingsAndOrdersWithoutReturnTo"/>

	<xsl:variable name="itemsPerMarker" select="number(10)" />

	<xsl:template match="Report">
		<xsl:choose>
			<xsl:when test="$SectionMode">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<div id="&documentId;">
					<div id="&documentId;_0">
						<xsl:call-template name="AddDocumentClasses">
							<xsl:with-param name="contentType" select="'&docketsClass;'"/>
						</xsl:call-template>

						<xsl:call-template name="RenderFixedHeaders" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;1" />

						<xsl:call-template name="RenderCaseOverview" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;2" />

						<xsl:call-template name="RenderCompanyInformation" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;3" />

						<xsl:call-template name="RenderFullTextFilings" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;4" />

						<xsl:call-template name="RenderFullTextCourtOrders" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;5" />

						<xsl:call-template name="RenderDocketProceedings" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;6" />

						<xsl:call-template name="RenderNewsNotes" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;7" />

						<xsl:call-template name="RenderParticipants" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;8" />

						<xsl:call-template name="RenderCreditorList" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;9" />

						<xsl:call-template name="RenderUnsecuredCreditorsCommittee" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;10" />

						<xsl:call-template name="RenderCalendar" />
						<input type="hidden" id="&xhtmlMarkerIdPrefix;11" />

						<xsl:call-template name="RenderPlanSummary" />

						<xsl:call-template name="EndOfDocument" />
					</div>
					<xsl:call-template name="CreateSearchUrl" />
					<xsl:call-template name="CreateUpdateUrl" />
					<xsl:call-template name="RenderOutline" />
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="RenderFixedHeaders">
		<div id="&beHeaderId;">
			<h2 id="&beHeaderTitleId;">
				<span>
					<xsl:text>&beFixedHeaderLeadText;</xsl:text>
				</span>
				<xsl:value-of select="/*/Metadata/CaseTitle" />
			</h2>

			<div class="&beHeaderCitationListClass;">
				<span class="&beHeaderCourtClass;">
					<xsl:value-of select="/*/Metadata/Court"/>
				</span>
				<span class="&beHeaderFileDateClass;">
					<xsl:value-of select="/*/Metadata/FilingDate"/>
				</span>
				<span class="&beHeaderDocketNumberClass;">
					<xsl:if test="$DeliveryMode">
						<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
					</xsl:if>
					<xsl:value-of select="/*/Metadata/DocketNumber"/>
				</span>
			</div>
			<xsl:call-template name="ReportNotifications" />
		</div>
	</xsl:template>

	<xsl:template name="ReportNotifications">
		<div>
			<strong>
				<xsl:value-of select="'&beReportCreationText;'" />
				<span>
					<xsl:value-of select="/*/Metadata/ReportCurrentThrough" />
				</span>
				<xsl:value-of select="'&beDocketCreationText;'" />
				<span>
					<xsl:value-of select="/*/Metadata/DocketCurrentThrough" />
				</span>
			</strong>
		</div>
		<xsl:if test="/*/Metadata/IsChapter11 = 'False'">
			<div>
				<strong>
					<span>
						<xsl:value-of select="'&beChapter7Message;'" />
					</span>
				</strong>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderCaseOverview">
		<xsl:if test="caseOverview">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beCaseOverviewId;'" />
				<xsl:with-param name="heading" select="'&beCaseOverviewHeading;'" />
				<xsl:with-param name="hasResults" select="boolean(caseOverview)" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="caseOverview" />
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="caseOverview/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="caseOverview/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderCompanyInformation">
		<!-- switch back to companyInformationDefault once this is consistently available -->
		<xsl:if test="companyInformationDefault/companyInfoDefault">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beCompanyInformationId;'" />
				<xsl:with-param name="heading" select="'&beCompanyInformationHeading;'" />
				<xsl:with-param name="hasResults" select="boolean(companyInformationDefault/companyInfoDefault)" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="companyInformationDefault" />
					<xsl:apply-templates select="companyInformationMore" />
					<xsl:apply-templates select="companyInformationSubsidiaries" />
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="companyInformationDefault/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="companyInformationDefault/@jsonState" />
				<xsl:with-param name="hasError" select="companyInformationDefault/@hasError" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFullTextFilings">
		<xsl:if test="fullTextFilings">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beFullTextFilingsId;'" />
				<xsl:with-param name="heading" select="concat('&beFullTextFilingsHeading;',' (',fullTextFilings/@sectionDataCount,')')" />
				<xsl:with-param name="hasResults" select="fullTextFilings/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="fullTextFilings/@sectionDataCount > 0">
						<xsl:apply-templates select="fullTextFilings" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="noResultsText">
					<xsl:choose>
						<xsl:when test="fullTextFilings/@wasSearched = 'False'">
							<xsl:text>&beDocketsNoSearchCriteriaEnteredText;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&beDocketsNoSearchFilingsMatchText;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="fullTextFilings/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="fullTextFilings/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderFullTextCourtOrders">
		<xsl:if test="fullTextCourtOrders">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beFullTextCourtOrdersId;'" />
				<xsl:with-param name="heading" select="concat('&beFullTextCourtOrdersHeading;',' (',fullTextCourtOrders/@sectionDataCount,')')" />
				<xsl:with-param name="hasResults" select="fullTextCourtOrders/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="fullTextCourtOrders/@sectionDataCount > 0">
						<xsl:apply-templates select="fullTextCourtOrders" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="noResultsText">
					<xsl:choose>
						<xsl:when test="fullTextCourtOrders/@wasSearched = 'False'">
							<xsl:text>&beDocketsNoSearchCriteriaEnteredText;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&beDocketsNoSearchOrdersMatchText;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="fullTextCourtOrders/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="fullTextCourtOrders/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderDocketProceedings">
		<xsl:if test="docketProceedings">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beDocketProceedingsId;'" />
				<xsl:with-param name="heading" select="concat('&beDocketProceedingsHeading;',' (',docketProceedings/@sectionDataCount,')')" />
				<xsl:with-param name="hasResults" select="docketProceedings/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="docketProceedings/@sectionDataCount > 0">
						<xsl:apply-templates select="docketProceedings" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="noResultsText">
					<xsl:choose>
						<xsl:when test="docketProceedings/@wasSearched = 'False'">
							<xsl:text>&beDocketsNoSearchCriteriaEnteredText;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&beDocketsNoSearchProceedingsMatchText;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="docketProceedings/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="docketProceedings/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderNewsNotes">
		<xsl:if test="newsNotes">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beNewsNotesId;'" />
				<xsl:with-param name="heading" select="concat('&beNewsNotesHeading;',' (',newsNotes/@sectionDataCount,')')" />
				<xsl:with-param name="hasResults" select="newsNotes/@sectionDataCount &gt; 0" />
				<xsl:with-param name="additionalHeading" select="'&beAdditionalHeadingSearchMessage;'" />
				<xsl:with-param name="content">
					<xsl:if test="newsNotes/@sectionDataCount > 0">
						<xsl:apply-templates select="newsNotes" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="newsNotes/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="newsNotes/@jsonState" />
				<xsl:with-param name="hasError" select="newsNotes/@hasError" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderParticipants">
		<xsl:if test="participantsDebtors or participantsTrustees or participantsOthers or participantsCreditors">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beParticipantsId;'" />
				<xsl:with-param name="heading" select="concat('&beParticipantsHeading;', ' (', participantsDebtors/@sectionDataCount + participantsTrustees/@sectionDataCount + participantsOthers/@sectionDataCount + participantsCreditors/@sectionDataCount, ')')" />
				<xsl:with-param name="hasResults" select="(participantsDebtors/@sectionDataCount + participantsTrustees/@sectionDataCount + participantsOthers/@sectionDataCount + participantsCreditors/@sectionDataCount) &gt; 0" />
				<xsl:with-param name="content">
					<xsl:call-template name="RenderParticipantsDebtors" />
					<xsl:call-template name="RenderParticipantsTrustees" />
					<xsl:call-template name="RenderParticipantsOthers" />
					<xsl:call-template name="RenderParticipantsCreditors" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderCreditorList">
		<xsl:if test="creditorList">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beCreditorListId;'" />
				<xsl:with-param name="heading" select="concat('&beCreditorListHeading;', ' (', creditorList/@sectionDataCount, ')')" />
				<xsl:with-param name="hasResults" select="creditorList/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="count(creditorList/creditor.block) &gt; 0">
						<xsl:call-template name="RenderSubSection">
							<xsl:with-param name="id" select="'&beCreditorListInformationId;'" />
							<xsl:with-param name="content">
								<xsl:apply-templates select="creditorList" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="initiallyExpanded" select="creditorList/@initiallyExpanded" />
				<xsl:with-param name="jsonState" select="creditorList/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderUnsecuredCreditorsCommittee">
		<xsl:if test="unsecuredCreditorsCommittee">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beUnsecuredCreditorsCommitteeId;'" />
				<xsl:with-param name="heading" select="concat('&beUnsecuredCreditorsCommitteeHeading;', ' (', unsecuredCreditorsCommittee/@sectionDataCount, ')')" />
				<xsl:with-param name="hasResults" select="unsecuredCreditorsCommittee/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="count(unsecuredCreditorsCommittee/creditor) &gt; 0">
						<xsl:call-template name="RenderSubSection">
							<xsl:with-param name="id" select="'&beUnsecuredCreditorInformationId;'" />
							<xsl:with-param name="content">
								<xsl:apply-templates select="unsecuredCreditorsCommittee" />
							</xsl:with-param>
						</xsl:call-template>
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="unsecuredCreditorsCommittee/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderCalendar">
		<xsl:if test="calendar">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&beCalendarId;'" />
				<xsl:with-param name="heading" select="concat('&beCalendarHeading;',' (',calendar/@sectionDataCount,')')" />
				<xsl:with-param name="hasResults" select="calendar/@sectionDataCount &gt; 0" />
				<xsl:with-param name="content">
					<xsl:if test="calendar/@sectionDataCount > 0">
						<xsl:apply-templates select="calendar" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="calendar/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderPlanSummary">
		<xsl:if test="planSummary">
			<xsl:call-template name="RenderSection">
				<xsl:with-param name="id" select="'&bePlanSummaryId;'" />
				<xsl:with-param name="heading" select="'&bePlanSummaryHeading;'" />
				<xsl:with-param name="hasResults" select="planSummary/@sectionDataCount &gt; 0" />
				<xsl:with-param name="noResultsText" select="'&beDocketsNoPlanSummaryFound;'" />
				<xsl:with-param name="additionalHeading" select="'&beAdditionalHeadingSearchMessage;'" />
				<xsl:with-param name="content">
					<xsl:if test="planSummary/@sectionDataCount > 0">
						<xsl:apply-templates select="planSummary" />
					</xsl:if>
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="planSummary/@jsonState" />
				<xsl:with-param name="hasError" select="planSummary/@hasError" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderSection">
		<xsl:param name="id" />
		<xsl:param name="heading" />
		<xsl:param name="hasResults" select="false()" />
		<xsl:param name="hasError" />
		<xsl:param name="content" />
		<xsl:param name="noResultsText" select="'&beDocketsNoResultsFound;'" />
		<xsl:param name="additionalHeading" />
		<xsl:param name="initiallyExpanded" />
		<xsl:param name="jsonState" />

		<div id="{$id}">
			<xsl:attribute name="class">
				<xsl:text>&beSectionClass; &docketsSubSection;</xsl:text>
				<xsl:if test="$initiallyExpanded and $initiallyExpanded = 'True'">
					<xsl:text> &sectionInitiallyExpandedClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<h2 class="&docketsHeading;">
				<xsl:value-of select="$heading" />
				<xsl:if test="string-length($additionalHeading) &gt; 0">
					<span>
						<xsl:text>&nbsp;</xsl:text>
						<xsl:value-of select="$additionalHeading" />
					</span>
				</xsl:if>
				<a class="&widgetToggleIcon;">
					<xsl:text>&beToggleSection;</xsl:text>
				</a>
			</h2>
			<xsl:if test="string-length($jsonState) &gt; 0">
				<input type="hidden" id="{$id}_State" value="{$jsonState}" />
			</xsl:if>
			<div id="{$id}_Content" class="&beInternalContentClass;">
				<xsl:choose>
					<xsl:when test="$hasError and $hasError = 'True'">
						<xsl:call-template name="ErrorFound" />
					</xsl:when>
					<xsl:when test="$hasResults">
						<xsl:copy-of select="$content" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="NoResultsFound">
							<xsl:with-param name="NoResultsFoundText" select="$noResultsText"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div id="{$id}_Bottom" class="&beInternalToolbarBottomClass; &docketsRowClass; &hideStateClass;">
			</div>
		</div>
	</xsl:template>

	<!-- START Case Overview Templates -->

	<xsl:template match="caseOverview/case.overview">
		<table>
			<xsl:apply-templates select="title.block"/>
			<xsl:apply-templates select="court.block"/>
			<xsl:apply-templates select="case.number.block"/>
			<xsl:apply-templates select="panel.block"/>
			<xsl:apply-templates select="filing.date.block"/>
			<xsl:apply-templates select="office.block"/>
			<xsl:apply-templates select="case.type.block"/>
			<xsl:apply-templates select="case.details.block"/>
		</table>
	</xsl:template>

	<xsl:template match="caseOverview//label">
		<xsl:variable name="label">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:value-of select="translate($label, ':', '')"/>
	</xsl:template>

	<!-- END Case Overview Templates -->

	<!-- START Company Information Templates -->

	<xsl:template match="companyInfoDefault">
		<xsl:choose>
			<xsl:when test="industryCodeDescription">
				<table>
					<xsl:apply-templates select="industryCodeDescription"/>
					<xsl:apply-templates select="revenue"/>
					<xsl:apply-templates select="sales"/>
					<xsl:apply-templates select="companyNumEmployees"/>
					<xsl:apply-templates select="stateInc"/>
					<xsl:apply-templates select="state"/>
					<xsl:apply-templates select="exchanges"/>
					<xsl:apply-templates select="assets"/>
					<xsl:apply-templates select="liabilities"/>
				</table>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="NoResultsFound">
					<xsl:with-param name="NoResultsFoundText" select="'&beDocketsNoCompanyInfoFound;'"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="companyInfoDefault/industryCodeDescription">
		<xsl:variable name="secondaryCode" select="../secondaryIndustryCodeDescription" />
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationIndustry;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<div>
					<xsl:apply-templates />
				</div>
				<xsl:if test="string-length($secondaryCode) &gt; 0">
					<div>
						<xsl:apply-templates select="$secondaryCode" />
					</div>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/revenue">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationRevenue;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/sales">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationSales;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:choose>
					<xsl:when test ="string(number(.)) != 'NaN'">
						<xsl:value-of select="format-number(number(.), '###,###,###,###,###')"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>

			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/companyNumEmployees">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationEmployees;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/stateInc">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationStateInc;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/state">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationStateHeadquarters;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/exchanges">
		<xsl:choose>
			<xsl:when test="exchange">
				<xsl:apply-templates />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DisplayCompanyInfoRow">
					<xsl:with-param name="label">
						<xsl:text>&beDocketsCompanyInformationExchange;: </xsl:text>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="exchange">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationExchange;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="exchangeSymbol">
		<xsl:apply-templates />
		<xsl:text>: </xsl:text>
	</xsl:template>

	<xsl:template match="tickerSymbol">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="companyInfoDefault/assets">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationAssets;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoDefault/liabilities">
		<xsl:call-template name="DisplayCompanyInfoRow">
			<xsl:with-param name="label">
				<xsl:text>&beDocketsCompanyInformationLiabilities;: </xsl:text>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInformationMore">
		<xsl:call-template name="RenderDescription" />
		<xsl:call-template name="RenderFinancials" />
		<xsl:call-template name="RenderSecurityOwnership" />
	</xsl:template>

	<xsl:template name="RenderDescription">
		<xsl:call-template name="RenderSubSection">
			<xsl:with-param name="heading" select="'&beCompanyInformationDescription;'" />
			<xsl:with-param name="id" select="'&beCompInfoDescriptionId;'" />
			<xsl:with-param name="showExpandCollapseText" select="false()" />
			<xsl:with-param name="content">
				<div class="&resultContentSummaryClass;">
					<xsl:apply-templates select="description" />
				</div>
			</xsl:with-param>
			<xsl:with-param name="hasError" select="@hasError" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderFinancials">
		<xsl:call-template name="RenderSubSection">
			<xsl:with-param name="heading" select="'&beCompanyInformationFinancials;'" />
			<xsl:with-param name="id" select="'&beCompInfoFinancialsId;'" />
			<xsl:with-param name="showExpandCollapseText" select="false()" />
			<xsl:with-param name="content">
				<xsl:apply-templates select="financials" />
			</xsl:with-param>
			<xsl:with-param name="hasError" select="@hasError" />
		</xsl:call-template>
		<xsl:if test="@hasError = 'False'">
			<table class="&docketsTable;">
				<tr class="&docketsRowClass;">
					<td>
						<div class="&textRightClass;">
							<xsl:text>&beDocketsCompanyInformationSource;</xsl:text>
						</div>
					</td>
				</tr>
			</table>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderSecurityOwnership">
		<xsl:call-template name="RenderSubSection">
			<xsl:with-param name="heading" select="'&beCompanyInformationSecurities;'" />
			<xsl:with-param name="id" select="'&beCompInfoOwnershipId;'" />
			<xsl:with-param name="showExpandCollapseText" select="false()" />
			<xsl:with-param name="content">
				<div class="&resultContentSummaryClass;">
					<xsl:apply-templates select="securityOwnership" />
				</div>
			</xsl:with-param>
			<xsl:with-param name="hasError" select="@hasError" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInformationSubsidiaries">
		<xsl:call-template name="RenderSubSection">
			<xsl:with-param name="heading" select="'&beCompanyInformationSubsidiaries;'" />
			<xsl:with-param name="id" select="'&beCompInfoSubsidiariesId;'" />
			<xsl:with-param name="showExpandCollapseText" select="false()" />
			<xsl:with-param name="content">
				<table class="&docketsTable;">
					<tr class="&docketsRowClass;">
						<th>
							<xsl:text>&beDocketsCompanyInfoSubsidiaryName;</xsl:text>
						</th>
						<th>
							<xsl:text>&beDocketsCompanyInfoSubsidiaryAddress;</xsl:text>
						</th>
						<th>
							<xsl:text>&beDocketsCompanyInfoSubsidiaryPlaceInc;</xsl:text>
						</th>
					</tr>
					<xsl:apply-templates />
				</table>
			</xsl:with-param>
			<xsl:with-param name="hasError" select="@hasError" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInfoSubsidiary">
		<tr class="&docketsRowClass;">
			<xsl:call-template name="DocketsTableCell">
				<xsl:with-param name="text">
					<xsl:apply-templates select="lastNameFiledUnder" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DocketsTableCell">
				<xsl:with-param name="text">
					<xsl:call-template name="RenderExplicitAddress">
						<xsl:with-param name="street" select="street"/>
						<xsl:with-param name="city" select="city" />
						<xsl:with-param name="state" select="state" />
						<xsl:with-param name="zip" select="zip" />
					</xsl:call-template>
				</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DocketsTableCell">
				<xsl:with-param name="text">
					<xsl:apply-templates select="placeOfIncorporation" />
				</xsl:with-param>
			</xsl:call-template>
		</tr>
	</xsl:template>

	<xsl:template match="companyInformationMore/financials/liab.info.b">
		<xsl:call-template name="RenderTableSubSection">
			<xsl:with-param name="heading" select="'&beCompInfoFinLib;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="companyInformationMore/financials/key.fin.itms.b">
		<xsl:call-template name="RenderTableSubSection">
			<xsl:with-param name="heading" select="'&beCompInfoFinKey;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderTableSubSection">
		<xsl:param name="heading" />
		<div class="&docketsSubSection;">
			<xsl:call-template name="DocketsHeaderRow">
				<xsl:with-param name="text" select="$heading" />
			</xsl:call-template>
			<table class="&docketsTable;">
				<xsl:apply-templates />
			</table>
		</div>
	</xsl:template>

	<xsl:template match="liab.info.b/data.b">
		<xsl:if test="contains(l/text(), 'Accounts Payable') or contains(l/text(), 'Total Current Liabilities') or contains(l/text(), 'Total Liabilities')">
			<tr class="&docketsRowClass;">
				<xsl:apply-templates />
			</tr>
		</xsl:if>
	</xsl:template>

	<xsl:template match="header.b | data.b">
		<tr class="&docketsRowClass;">
			<xsl:apply-templates />
		</tr>
	</xsl:template>

	<xsl:template match="header.b/l | header.b/h1 | header.b/h2 | header.b/h3 | header.b/h4 | header.b/h5 | header.b/h6 | header.b/h7 | header.b/h8 | header.b/h9">
		<th>
			<xsl:apply-templates />
		</th>
	</xsl:template>

	<xsl:template match="data.b/l | data.b/d1 | data.b/d2 | data.b/d3 | data.b/d4 | data.b/d5 | data.b/d6 | data.b/d7 | data.b/d8 | data.b/d9">
		<td>
			<xsl:apply-templates />
		</td>
	</xsl:template>

	<!-- END Company Information Templates -->

	<!-- START Participants Templates -->

	<xsl:template name="RenderSubSection">
		<xsl:param name="id" />
		<xsl:param name="heading" />
		<xsl:param name="content" />
		<xsl:param name="showExpandCollapseText" select="true()" />
		<xsl:param name="hasError" />
		<xsl:param name="jsonState" />

		<div id="{$id}" class="&beSectionClass; &docketsSection; &docketsRowClass;">
			<h2 class="&docketsHeading;">
				<xsl:value-of select="$heading" />
				<xsl:if test="$showExpandCollapseText">
					<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;" />
				</xsl:if>
			</h2>
			<xsl:if test="string-length($jsonState) &gt; 0">
				<input type="hidden" id="{$id}_State" value="{$jsonState}" />
			</xsl:if>
			<div id="{$id}_Content" class="&beInternalContentClass;">
				<xsl:choose>
					<xsl:when test="$hasError and $hasError = 'True'">
						<xsl:call-template name="ErrorFound" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:copy-of select="$content" />
					</xsl:otherwise>
				</xsl:choose>
			</div>
			<div id="{$id}_Bottom" class="&beInternalToolbarBottomClass; &docketsRowClass; &hideStateClass;"></div>
		</div>
	</xsl:template>


	<xsl:template name="RenderParticipantsDebtors">
		<xsl:if test="count(participantsDebtors/debtor) &gt; 0">
			<xsl:call-template name="RenderSubSection">
				<xsl:with-param name="id" select="'&docketsDebtorInformationId;'" />
				<xsl:with-param name="heading" select="'&docketsDebtorInformation;'" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="participantsDebtors" />
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="participantsDebtors/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="participantsDebtors">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="debtor">
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;pd{position()}" />
			</xsl:if>
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<xsl:template name="RenderParticipantsTrustees">
		<xsl:if test="count(participantsTrustees/trustee) &gt; 0">
			<xsl:call-template name="RenderSubSection">
				<xsl:with-param name="id" select="'&docketsTrusteeInformationId;'" />
				<xsl:with-param name="heading" select="'&docketsTrusteeInformation;'" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="participantsTrustees" />
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="participantsTrustees/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="participantsTrustees">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="trustee">
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;pt{position()}" />
			</xsl:if>
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<xsl:template name="RenderParticipantsOthers">
		<xsl:if test="count(participantsOthers/other) &gt; 0">
			<xsl:call-template name="RenderSubSection">
				<xsl:with-param name="id" select="'&docketsOtherPartiesId;'" />
				<xsl:with-param name="heading" select="'&docketsOtherParties;'" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="participantsOthers" />
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="participantsOthers/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="participantsOthers">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="other">
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;po{position()}" />
			</xsl:if>
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<xsl:template name="RenderParticipantsCreditors">
		<xsl:if test="count(participantsCreditors/creditor) &gt; 0">
			<xsl:call-template name="RenderSubSection">
				<xsl:with-param name="id" select="'&docketsCreditorInformationId;'" />
				<xsl:with-param name="heading" select="'&docketsCreditorInformation;'" />
				<xsl:with-param name="content">
					<xsl:apply-templates select="participantsCreditors" />
				</xsl:with-param>
				<xsl:with-param name="jsonState" select="participantsCreditors/@jsonState" />
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="participantsCreditors">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="creditor">
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;pc{position()}" />
			</xsl:if>
			<xsl:choose>
				<xsl:when test="(@unsecured='true')">
					<div class="&docketsSubSection;">
						<h3 class="&docketSubHeading;">
							<a href="#{@id}">
								<xsl:apply-templates select="party.name.block/party.name"/>
							</a>
						</h3>
					</div>
				</xsl:when>
				<xsl:otherwise>
					<div class="&docketsSubSection;">
						<xsl:apply-templates select="." />
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<!-- END Participants Templates -->

	<!-- START Unsecured Creditors Committee Templates -->

	<xsl:template match="unsecuredCreditorsCommittee">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="creditor">
			<div class="&docketsSubSection;" id="{@id}">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<!-- END Unsecured Creditors Committee Templates -->

	<!-- START Docket Proceedings Templates -->

	<xsl:template match="docketProceedings">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<table class="&docketsTable;">
			<tr class="&docketsRowClass;">
				<th>
					<xsl:text>&beDocketsEntry;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsDate;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsDescription;</xsl:text>
				</th>
				<th></th>
			</tr>
			<xsl:for-each select="docket.entry">
				<tr class="&docketsRowClass;">
					<xsl:variable name="index">
						<xsl:choose>
							<xsl:when test="send.runner.link">
								<xsl:value-of select="send.runner.link/@indexvalue" />
							</xsl:when>
							<xsl:when test="ancestor::docketProceedings/@startIndex">
								<xsl:value-of select="ancestor::docketProceedings/@startIndex + position() - 1"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="position()"/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="imageOrLink">
						<xsl:choose>
							<xsl:when test="number.block/image.block">
								<xsl:call-template name="RenderDocketImageForReport">
									<xsl:with-param name="imageBlock" select="number.block/image.block" />
									<xsl:with-param name="index" select="$index" />
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="$index and $HasDocketOrdersAccess">
								<xsl:call-template name="RenderSendRunnerLink">
									<xsl:with-param name="index" select="$index" />
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="number.block/number" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="date" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="docket.description" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:copy-of select="$imageOrLink"/>
						</xsl:with-param>
					</xsl:call-template>
				</tr>
			</xsl:for-each>
		</table>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<xsl:template name="RenderDocketImageForReport">
		<xsl:param name="imageBlock" />
		<xsl:param name="index" />
		<xsl:apply-templates select="$imageBlock" />
		<xsl:variable name="checkSum" select="/Document/n-docbody/persist.info/checksum" />
		<xsl:variable name="docPersistId" select="/Document/n-docbody/persist.info/id" />
		<xsl:variable name="batchPdfHref">
			<xsl:call-template name="BatchPdfHref">
				<xsl:with-param name="documentGuid" select="$Guid" />
				<xsl:with-param name="pdfIndex" select="$index" />
				<xsl:with-param name="checkSum" select="$checkSum" />
				<xsl:with-param name="docPersistId" select="$docPersistId" />
			</xsl:call-template>
		</xsl:variable>
		<a href="{$batchPdfHref}&amp;fromReport=true" class="&docketProceedingsButtonClass;">
			<xsl:text>&docketBatchDownloadText;</xsl:text>
		</a>
	</xsl:template>

	<!-- END Docket Proceedings Templates-->

	<!-- START Calendar Templates -->

	<xsl:template match="calendar">
		<table class="&docketsTable; &docketsRowClass;">
			<tr>
				<th>
					<xsl:text>&beDocketsDate;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsTime;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsRoom;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsJudge;</xsl:text>
				</th>
				<th>
					<xsl:text>&beDocketsCalendarEntry;</xsl:text>
				</th>
			</tr>
			<xsl:for-each select="calendar.entry">
				<tr>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="date" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:apply-templates select="time" />
						</xsl:with-param>
					</xsl:call-template>
					<!-- There is never a room -->
					<xsl:call-template name="DocketsTableCell" />
					<!-- There is never a judge -->
					<xsl:call-template name="DocketsTableCell" />
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:if test="position() mod 50 = 0">
								<input type="hidden" id="&xhtmlMarkerIdPrefix;ca{position()}" />
							</xsl:if>
							<xsl:apply-templates select="description" />
						</xsl:with-param>
					</xsl:call-template>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<!-- END Calendar Templates -->

	<!-- START Creditor List Templates -->

	<xsl:template match="creditorList">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<xsl:for-each select="creditor.block">
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;cl{position()}" />
			</xsl:if>
			<div class="&docketsSubSection;">
				<xsl:apply-templates select="." />
			</div>
		</xsl:for-each>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<!-- END Creditor List Templates -->

	<!-- START Plan Summary Templates -->

	<xsl:template match="planSummary">
		<table class="&docketsTable; &docketsRowClass;">
			<tr>
				<th>
					<xsl:text>Description</xsl:text>
				</th>
				<th>
					<xsl:text>PDF</xsl:text>
				</th>
			</tr>
			<xsl:for-each select="planSummary">
				<xsl:variable name="imageOrLink">
					<xsl:choose>
						<xsl:when test="figure/figure.body/image.block/image.link">
							<xsl:variable name="guid" select="figure/figure.body/image.block/image.link/@target" />
							<xsl:call-template name="createDocumentBlobLink">
								<xsl:with-param name="guid" select="$guid"/>
								<xsl:with-param name="className" select="'&docketProceedingsButtonClass; &docketProceedingsPDFButtonClass;'" />
								<xsl:with-param name="mimeType" select="'&pdfMimeType;'"/>
								<xsl:with-param name="hash" select="//blobData/blob[guid/text() = $guid]/hash/text()" />
								<xsl:with-param name="contents">
									<i></i>
									<xsl:text>&docketViewPDFText;</xsl:text>
								</xsl:with-param>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&beNoLinkFoundText;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:variable>

				<tr>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:value-of select="normalize-space(paratext/text())" />
						</xsl:with-param>
					</xsl:call-template>
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:copy-of select="$imageOrLink"/>
						</xsl:with-param>
					</xsl:call-template>
				</tr>
			</xsl:for-each>
		</table>
	</xsl:template>

	<xsl:template name="substringDate">
		<xsl:param name="text"/>
		<xsl:choose>
			<xsl:when test="contains($text, '(')">
				<xsl:call-template name="substringDate">
					<xsl:with-param name="text" select="substring-after($text, '(')"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="substring-before($text, ')')"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- END Plan Summary Templates -->

	<!-- START Full Text Filings Templates -->

	<xsl:template match="fullTextFilings">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<div class="&docketsRowClass;">
			<div class="&resultCheckboxList;">
				<div>
					<input type="checkbox" id="&beAllFilingsId;" value="allFilings" />
					<label for="&beAllFilingsId;" class="&accessibilityLabel;">
						<xsl:text>&selectAllText; Filings</xsl:text>
					</label>
					<div class="&resultContent;">
						<xsl:value-of select="@sectionDataCount"/>
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:text>Filings contain your </xsl:text>
						<xsl:call-template name="ReturnToSearchPage">
							<xsl:with-param name="ReturnToSearchPageUrl" select="$BackToOriginalSearchUrl" />
							<xsl:with-param name="ReturnToSearchPageText" select="'search criteria'"/>
						</xsl:call-template>
					</div>
				</div>
				<xsl:for-each select="filing">
					<xsl:call-template name="RenderFilingOrOrderRow">
						<xsl:with-param name="prefix" select="'&beFilingPrefixId;'" />
						<xsl:with-param name="sectionName" select="'fullTextFilings'" />
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</div>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<xsl:template name="RenderFilingOrOrderRow">
		<xsl:param name="prefix" />
		<xsl:param name="sectionName" />
		<xsl:variable name="guid" select="guid" />
		<xsl:variable name="rank">
			<xsl:choose>
				<xsl:when test="parent::node()/@startIndex">
					<xsl:value-of select="ancestor::node()/@startIndex + position() - 1"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="position()"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<div>
			<xsl:if test="position() mod $itemsPerMarker = 0">
				<input type="hidden" id="&xhtmlMarkerIdPrefix;{$prefix}{position()}" />
			</xsl:if>
			<input type="checkbox" id="{$prefix}{position()}" >
				<xsl:attribute name="value">
					<xsl:text>{&quot;guid&quot;:&quot;</xsl:text>
					<xsl:value-of select="$guid" />
					<xsl:text>&quot;, &quot;rank&quot;:&quot;</xsl:text>
					<xsl:value-of select="number($rank)" />
					<xsl:text>&quot;, &quot;list&quot;:&quot;</xsl:text>
					<xsl:value-of select="$sectionName" />
					<xsl:text>&quot;, &quot;listSource&quot;:&quot;</xsl:text>
					<xsl:value-of select="'Document'" />
					<xsl:text>&quot;, &quot;navigationPath&quot;:&quot;</xsl:text>
					<xsl:value-of select="$NavigationPathForFilingsAndOrdersWithoutReturnTo" />
					<xsl:text>&quot;}</xsl:text>
				</xsl:attribute>
			</input>
			<label for="{$prefix}{position()}" class="&accessibilityLabel;">
				<xsl:apply-templates select="documentTitle"/>
			</label>
			<xsl:if test="/*/additionalData/data[@guid=$guid]/inPlan/text() = 'false'">
				<div class="&outOfPlanLabel;">
					<xsl:text>&outOfPlanText;</xsl:text>
				</div>
			</xsl:if>
			<div class="&resultContent;">
				<xsl:if test="count(/*/additionalData/data[@guid=$guid]/status/li) &gt; 0">
					<ul class="&documentIndicators;">
						<xsl:copy-of select="/*/additionalData/data[@guid=$guid]/status/li"/>
					</ul>
				</xsl:if>
				<h3>
					<a id="{$prefix}{position()}Link" class="&linkDraggableClass;">
						<xsl:attribute name="href">
							<xsl:call-template name="GetDocumentUrl">
								<xsl:with-param name ="documentGuid" select="$guid" />
								<xsl:with-param name ="navigationPath" select="$NavigationPathForFilingsAndOrders" />
								<xsl:with-param name ="list" select="$sectionName" />
								<xsl:with-param name ="listSource" select="'Document'" />
								<xsl:with-param name ="rank" select="number($rank)" />
							</xsl:call-template>
						</xsl:attribute>
						<span>
							<xsl:apply-templates select="documentTitle" />
						</span>
					</a>
				</h3>
				<div class="&resultContentItemList;">
					<xsl:if test="partyInfo">
						<span>
							<xsl:apply-templates select="partyInfo" />
						</span>
					</xsl:if>
					<xsl:if test="citation">
						<span>
							<xsl:if test="$DeliveryMode">
								<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
							</xsl:if>
							<xsl:apply-templates select="citation" />
						</span>
					</xsl:if>
					<xsl:if test="docketNumber">
						<span>
							<xsl:if test="$DeliveryMode">
								<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
							</xsl:if>							
							<xsl:apply-templates select="docketNumber" />
						</span>
					</xsl:if>
					<xsl:if test="court">
							<span>
							<xsl:if test="$DeliveryMode">
								<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
							</xsl:if>								
							<xsl:apply-templates select="court" />
						</span>
					</xsl:if>
					<xsl:if test="date">							
						<span>
							<xsl:if test="$DeliveryMode">
								<span class="&excludeFromAnnotationsClass;"><![CDATA[ ]]>|<![CDATA[ ]]></span>
							</xsl:if>							
							<xsl:apply-templates select="date" />
						</span>
					</xsl:if>
				</div>
				<div class="&resultContentSummary;">
					<xsl:apply-templates select="snippet"/>
				</div>
			</div>
		</div>
	</xsl:template>

	<!-- End Full Text Filings Templates -->

	<!-- Start Full Text Court Orders Templates -->

	<xsl:template match="fullTextCourtOrders">
		<xsl:apply-templates select="PreviousPageWithTerms" />
		<div class="&docketsRowClass;">
			<div class="&resultCheckboxList;">
				<div>
					<input type="checkbox" id="&beAllOrdersId;" value="allOrders" />
					<label for="&beAllOrdersId;" class="&accessibilityLabel;">
						<xsl:text>&selectAllText; Orders</xsl:text>
					</label>
					<div class="&resultContent;">
						<xsl:value-of select="@sectionDataCount"/>
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:text>Court Orders contain your </xsl:text>
						<xsl:call-template name="ReturnToSearchPage">
							<xsl:with-param name="ReturnToSearchPageUrl" select="$BackToOriginalSearchUrl" />
							<xsl:with-param name="ReturnToSearchPageText" select="'search criteria'"/>
						</xsl:call-template>
					</div>
				</div>
				<xsl:for-each select="order">
					<xsl:call-template name="RenderFilingOrOrderRow">
						<xsl:with-param name="prefix" select="'&beOrderPrefixId;'" />
						<xsl:with-param name="sectionName" select="'fullTextCourtOrders'" />
					</xsl:call-template>
				</xsl:for-each>
			</div>
		</div>
		<xsl:apply-templates select="NextPageWithTerms" />
	</xsl:template>

	<!-- End Full Text Court Orders Templates -->

	<!-- Start News Notes Templates-->
	<xsl:template match="newsNotes">
		<div class="&docketsRowClass;">
			<xsl:for-each select="news">
				<xsl:if test="position() mod $itemsPerMarker = 0">
					<input type="hidden" id="&xhtmlMarkerIdPrefix;nn{position()}" />
				</xsl:if>
				<xsl:call-template name="RenderNewsNotesRow">
				</xsl:call-template>
			</xsl:for-each>
		</div>
	</xsl:template>

	<xsl:template name="RenderNewsNotesRow">
		<div class="&resultContentSummary;">
			<h3>
				<xsl:apply-templates select="headline" />
				<xsl:text><![CDATA[ ]]></xsl:text>
				<span>
					<xsl:apply-templates select="eventDate" />
				</span>
			</h3>
			<div class="&resultContentSummary;">
				<xsl:apply-templates select="body"/>
			</div>
		</div>
	</xsl:template>

	<xsl:template match="headline">
		<xsl:value-of select="substring(., 10)"/>
	</xsl:template>

	<xsl:template match="eventDate">
		<xsl:value-of select="substring(., 12)"/>
	</xsl:template>

	<!-- End News Notes Templates-->

	<xsl:template name="CreateSearchUrl">
		<div>
			<input type="hidden" id="&beSearchUrlId;">
				<xsl:attribute name="value">
					<xsl:value-of select="$BackToOriginalSearchUrl"/>
				</xsl:attribute>
			</input>
		</div>
	</xsl:template>

	<xsl:template name="CreateUpdateUrl">
		<div>
			<input type="hidden" id="&beUpdateUrlId;">
				<xsl:attribute name="value">
					<xsl:value-of select="$UpdateUrl"/>
				</xsl:attribute>
			</input>
		</div>
	</xsl:template>

	<xsl:template name="RenderOutline">
		<div id="&nrsOutlineId;" class="&hideStateClass;">
			<div>
				<div>
					<a href="#&beCaseOverviewId;">
						<xsl:text>&beCaseOverviewHeading;</xsl:text>
					</a>
				</div>
				<xsl:if test="companyInformationDefault/companyInfoDefault">
					<div>
						<a href="#&beCompanyInformationId;">
							<xsl:text>&beCompanyInformationHeading;</xsl:text>
						</a>
					</div>
				</xsl:if>
				<div>
					<a href="#&beFullTextFilingsId;">
						<xsl:text>&beFullTextFilingsHeading; (</xsl:text>
						<xsl:value-of select="fullTextFilings/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beFullTextCourtOrdersId;">
						<xsl:text>&beFullTextCourtOrdersHeading; (</xsl:text>
						<xsl:value-of select="fullTextCourtOrders/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beDocketProceedingsId;">
						<xsl:text>&beDocketProceedingsHeading; (</xsl:text>
						<xsl:value-of select="docketProceedings/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beNewsNotesId;">
						<xsl:text>&beNewsNotesHeading; (</xsl:text>
						<xsl:value-of select="newsNotes/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beParticipantsId;">
						<xsl:text>&beParticipantsHeading; (</xsl:text>
						<xsl:value-of select="participantsDebtors/@sectionDataCount + participantsTrustees/@sectionDataCount + participantsOthers/@sectionDataCount + participantsCreditors/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beCreditorListId;">
						<xsl:text>&beCreditorListHeading; (</xsl:text>
						<xsl:value-of select="creditorList/@sectionDataCount"/>
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beUnsecuredCreditorsCommitteeId;">
						<xsl:text>&beUnsecuredCreditorsCommitteeHeading;</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&beCalendarId;">
						<xsl:text>&beCalendarHeading; (</xsl:text>
						<xsl:value-of select="calendar/@sectionDataCount" />
						<xsl:text>)</xsl:text>
					</a>
				</div>
				<div>
					<a href="#&bePlanSummaryId;">
						<xsl:text>&bePlanSummaryHeading;</xsl:text>
					</a>
				</div>
			</div>
		</div>
	</xsl:template>

	<xsl:template name="NoResultsFound">
		<xsl:param name="NoResultsFoundText"/>
		<table>
			<tr class="&docketsRowClass;">
				<td>
					<div class="&docketsRowContentWrapperClass;">
						<strong>
							<xsl:value-of select="$NoResultsFoundText"/>
						</strong>
					</div>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="ErrorFound">
		<table>
			<tr class="&docketsRowClass;">
				<td>
					<div class="&docketsRowContentWrapperClass;">
						<xsl:text>&beIsErrorText;</xsl:text>
					</div>
				</td>
			</tr>
		</table>
	</xsl:template>

	<xsl:template name="ReturnToSearchPage">
		<xsl:param name="ReturnToSearchPageUrl"/>
		<xsl:param name="ReturnToSearchPageText"/>
		<a>
			<xsl:attribute name="href">
				<xsl:value-of select="$ReturnToSearchPageUrl"/>
			</xsl:attribute>
			<xsl:attribute name="title">
				<xsl:value-of select="$ReturnToSearchPageText"/>
			</xsl:attribute>
			<xsl:value-of select="$ReturnToSearchPageText"/>
		</a>
	</xsl:template>

	<xsl:template match="additionalData" />

	<xsl:template name="DisplayCompanyInfoRow">
		<xsl:param name="label"/>
		<xsl:param name="text"/>
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:copy-of select="$label"/>
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:choose>
					<xsl:when test="string-length($text) &gt; 0">
						<xsl:copy-of select="$text"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&beDocketsNotAvailableText;</xsl:text>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="PreviousPageWithTerms">
		<!-- TODO: see if it works with span element -->
		<div>
			<input type="hidden" class="&searchTermClass; &bePreviousPageClass;" value="{@page}" />
		</div>
	</xsl:template>

	<xsl:template match="NextPageWithTerms">
		<div>
			<input type="hidden" class="&searchTermClass; &beNextPageClass;" value="{@page}" />
		</div>
	</xsl:template>

	<xsl:template match="asset.info.b" />
	<xsl:template match="inc.state.b" />
	<xsl:template match="fnds.flw.stmt.b" />
	<xsl:template match="fn.b" />
	<xsl:template match="securityStock" />
	<xsl:template match="sourceInfo" />

</xsl:stylesheet>