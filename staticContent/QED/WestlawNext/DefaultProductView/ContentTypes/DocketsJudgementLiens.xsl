<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document" priority="1">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&docketsClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="ToOrderTop" />
			<h2 class="&docketsHeading;">
				<xsl:text>&docketsNewYorkJudgementDocketandLienRecords;</xsl:text>
			</h2>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:apply-templates />
			<xsl:call-template name="ToOrderBottom" />
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>

	<xsl:template match="r">
		<xsl:call-template name="FilingInformation" />
		<xsl:call-template name="DebtorBlock" />
		<xsl:call-template name="CreditorBlock" />
		<xsl:call-template name="DetailsBlock" />
	</xsl:template>

	<xsl:template name="FilingInformation">
		<h2 class="&docketSubHeading;">
			<xsl:text>&docketsFilingInformation;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="FilingInformationSection" />
		</table>
	</xsl:template>

	<xsl:template name="FilingInformationSection">
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="ctrl.nbr.b/ctrl.nbr" />
			<xsl:with-param name="labelText" select="'&docketsFilingNumber;'" />
		</xsl:call-template>		
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="filg.typ" />
			<xsl:with-param name="labelText" select="'&docketsFilingType;'" />
		</xsl:call-template>		
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="cs.nbr" />
			<xsl:with-param name="labelText" select="'&docketsCourtIndexNumber;'" />
		</xsl:call-template>		
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="filg.cnty" />
			<xsl:with-param name="labelText" select="'&docketsFilingCounty;'" />
		</xsl:call-template>		
		<xsl:call-template name="GenericDateField">
			<xsl:with-param name="templateMatch" select="entry.d" />
			<xsl:with-param name="labelText" select="'&docketsFilingDate;'" />
		</xsl:call-template>
		<xsl:call-template name="FilingOffice" />
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="bk.nm" />
			<xsl:with-param name="labelText" select="'&docketsBookName;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="FilingOffice">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label" select="'&docketsFilingOffice;'" />
			<xsl:with-param name="text" select="'&docketsNYCountyClerk;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template name="DebtorBlock">
		<xsl:call-template name="DebtorBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="DebtorBlockAsTable">
		<h2 class="&docketSubHeading;">
			<xsl:text>&docketsDebtorInformation;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="DebtorBlockTableHeaderRow"/>
			<xsl:call-template name="DebtorBlockTableRow" />
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="debt.b/debt.t" />
				<xsl:with-param name="labelText" select="'&docketsDebtorType;'" />
			</xsl:call-template>
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="debt.b/debt.tot" />
				<xsl:with-param name="labelText" select="'&docketsDebtorTotal;'" />
			</xsl:call-template>
		</table>		
	</xsl:template>

	<xsl:template name="DebtorBlockTableHeaderRow">
		<tr class="&docketsRowClass;">
			<td>
				<xsl:text>&AdditionalDebtorInfoMessage;</xsl:text>
				<div class="&clearClass;"></div>
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template name="DebtorBlockTableRow">
		<tr class="&docketsRowClass;">
			<td class="&docketsRowLabelClass;">
				<xsl:text>&docketsDebtor;</xsl:text>
			</td>
			<td class="&docketsRowTextClass;">
				<xsl:call-template name="DebtorContentAsTableData" />
			</td>
		</tr>
	</xsl:template>
	
	<xsl:template name="DebtorContentAsTableData">
		<div>
			<xsl:choose>
				<xsl:when test="debt.b/debt.nm.b">
					<xsl:apply-templates select="debt.b/debt.nm.b/debt.lst.nm"/>
					<xsl:text>&nbsp;</xsl:text>
					<xsl:apply-templates select="debt.b/debt.nm.b/debt.fst.nm"/>
					<xsl:text>&nbsp;</xsl:text>
					<xsl:apply-templates select="debt.b/debt.nm.b/m.nm"/>
				</xsl:when>
				<xsl:when test="debt.b/debt.corp.nm">
					<xsl:apply-templates select="debt.b/debt.corp.nm"/>
				</xsl:when>
			</xsl:choose>
		</div>
		<xsl:if test="debt.b/debt.addr.b">
			<div>
				<xsl:apply-templates select="debt.b/debt.addr.b/debt.str.nbr"/>
				<xsl:text>&nbsp;</xsl:text>
				<xsl:apply-templates select="debt.b/debt.addr.b/debt.str.nm"/>
			</div>
			<div>
				<xsl:if test="debt.b/debt.addr.b/debt.cty">
					<xsl:apply-templates select="debt.b/debt.addr.b/debt.cty"/>
					<xsl:text>&nbsp;</xsl:text>
				</xsl:if>

				<xsl:if test="debt.b/debt.addr.b/debt.st">
					<xsl:apply-templates select="debt.b/debt.addr.b/debt.st"/>
					<xsl:text>&nbsp;</xsl:text>
				</xsl:if>

				<xsl:apply-templates select="debt.b/debt.addr.b/debt.zip.b/debt.zip"/>

				<xsl:if test="debt.b/debt/addr.b/debt.zip.b/debt.zip.ext">
					<xsl:text>-</xsl:text>
					<xsl:apply-templates select="debt.b/debt.addr.b/debt.zip.b/debt.zip.ext" />
				</xsl:if>
			</div>
		</xsl:if>
		<div class="&clearClass;"></div>
	</xsl:template>

	<xsl:template name="CreditorBlock">
		<h2 class="&docketSubHeading;">
			<xsl:text>&docketsCreditorInformation;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="cred.b/cred.corp.nm" />
				<xsl:with-param name="labelText" select="'&docketsCreditorLinkText;'" />
			</xsl:call-template>
			<xsl:call-template name="DocketsRowByTemplateMatch">
				<xsl:with-param name="templateMatch" select="cred.b/cred.t" />
				<xsl:with-param name="labelText" select="'&docketsCreditorType;'" />
			</xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="DetailsBlock">
		<h2 class="&docketSubHeading;">
			<xsl:text>&docketsDocumentDetails;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="DetailsBlockContents" />
		</table>
	</xsl:template>

	<xsl:template name="DetailsBlockContents">
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="lien.info.b/srce.cnty" />
			<xsl:with-param name="labelText" select="'&docketsSourceCounty;'" />
		</xsl:call-template>		
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="lien.info.b/amt" />
			<xsl:with-param name="labelText" select="'&docketsTotalAmountAwarded;'" />
		</xsl:call-template>
		<xsl:call-template name="GenericDateField">
			<xsl:with-param name="templateMatch" select="lien.info.b/exp.d" />
			<xsl:with-param name="labelText" select="'&docketsExpirationDate;'" />
		</xsl:call-template>		
		<xsl:call-template name="GenericDateField">
			<xsl:with-param name="templateMatch" select="lien.info.b/sat.d" />
			<xsl:with-param name="labelText" select="'&docketsSatisfactionDate;'" />
		</xsl:call-template>
		<xsl:call-template name="DocketsRowByTemplateMatch">
			<xsl:with-param name="templateMatch" select="lien.info.b/sat.t" />
			<xsl:with-param name="labelText" select="'&docketsSatisfactionType;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="GenericDateField">
		<xsl:param name="templateMatch" />
		<xsl:param name="labelText" />
		<xsl:if test="$templateMatch">
			<tr class="&docketsRowClass;">
				<td class="&docketsRowLabelClass;">
					<xsl:value-of select="$labelText"/>
				</td>
				<td class="&docketsRowTextClass;">
					<xsl:call-template name="DocketsDate">
						<xsl:with-param name="date" select="$templateMatch" />
					</xsl:call-template>
					<div class="&clearClass;"></div>
				</td>
			</tr>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
