<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDockets.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="r">
		<table>
			<xsl:call-template name="FirstSection"></xsl:call-template>
		</table>
		<xsl:apply-templates select="*[not(self::court.block or self::title.block or self::docket.block or self::panel.block or 
		self::filing.date.block or self::case.status.block or self::closed.date.block or self::other.dockets.block)]" />	
	</xsl:template>
	
	<xsl:template name="FirstSection">
		<xsl:apply-templates select="court.block"/>
		<xsl:apply-templates select="title.block"/>
		<xsl:apply-templates select="docket.block"/>
		<xsl:apply-templates select="panel.block"/>
		<xsl:apply-templates select="filing.date.block"/>
		<xsl:apply-templates select="other.dockets.block"/>
		<xsl:apply-templates select="closed.date.block"/>
		<xsl:apply-templates select="case.status.block"/>				
	</xsl:template>
	
	<xsl:template match="case.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.status.flag" mode="displayStatusFlag"/>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.status.flag" mode="displayStatusFlag">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="complaint.block">
		<table>
			<xsl:call-template name="ComplaintBlock"></xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="ComplaintBlock">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label" select="complaint/text()" />
			<xsl:with-param name="text">
				<xsl:apply-templates select="image.block" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="summary" priority="1">
		<h2 id="&docketsCaseInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCaseInformation;</xsl:text>
		</h2>
		<xsl:call-template name="summary"/>				
	</xsl:template>

  <xsl:template match="patent.information.block">
    <div class="&docketsSection;">
      <h2 id="&docketsPatentInformationId;" class="&docketsHeading;">
        <xsl:text>&docketsPatentInformation;</xsl:text>
        <a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
      </h2>
      <xsl:apply-templates />
    </div>
  </xsl:template>

  <xsl:template match="patent.block">
    <div class="&docketsSubSection;">
      <xsl:call-template name="patentInformationRowHeader" />
      <table>
        <xsl:apply-templates />
      </table>
    </div>
  </xsl:template>

  <xsl:template name="patentInformationRowHeader">
    <xsl:call-template name="DocketsHeaderRow">
      <xsl:with-param name="text">
        <xsl:apply-templates select="patent.number.block/patent.number" />
        <xsl:text>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</xsl:text>
        <xsl:apply-templates select="patent.title.block/patent.title" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="patent.number.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="patent.number" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="patent.app.number.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="patent.app.number" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

  <xsl:template match="patent.title.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:text>Title</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="patent.title" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>

    <xsl:template match="abstract">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:text>Abstract</xsl:text>
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="national.class.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:for-each select="national.class.line">
          <xsl:apply-templates select="national.class" />
          <xsl:if test="national.subclass">
            <xsl:text>/</xsl:text>
            <xsl:apply-templates select="national.subclass" />
          </xsl:if>
          <xsl:if test="position() != last()">
            <xsl:text>;&nbsp;</xsl:text>
          </xsl:if>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
  
  <xsl:template match="international.class.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="international.class" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template> 

  <xsl:template match="inventors.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:for-each select="inventor">
          <div>
            <xsl:apply-templates select="name" />
            <xsl:text>&nbsp;&#40;</xsl:text>
            <xsl:apply-templates select="city" />
            <xsl:text>,&nbsp;</xsl:text>
            <xsl:apply-templates select="state" />
            <xsl:text>&#41;</xsl:text>
          </div>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template> 
  
  <xsl:template match="assignees.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:for-each select="assignee">
          <div>
            <xsl:apply-templates select="name" />
            <xsl:text>&nbsp;&#40;</xsl:text>
            <xsl:apply-templates select="city" />
            <xsl:text>,&nbsp;</xsl:text>
            <xsl:apply-templates select="state" />
            <xsl:text>&#41;</xsl:text>
          </div>
        </xsl:for-each>
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template> 
 
  <xsl:template match="patent.block/currency.date.block">
    <xsl:call-template name="DocketsRow">
      <xsl:with-param name="label">
        <xsl:apply-templates select="label" />
      </xsl:with-param>
      <xsl:with-param name="text">
        <xsl:apply-templates select="currency.date" />
      </xsl:with-param>
    </xsl:call-template>
  </xsl:template>
       
	<xsl:template match="plaintiff.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="plaintiff.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="defendant.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="defendant.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="jury.demand.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="jury.demand" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="demand.amount.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:text>$</xsl:text>
				<xsl:apply-templates select="demand.amount" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lead.docket.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:choose>
					<xsl:when test="lead.docket.number.INF">
						<xsl:apply-templates select="lead.docket.number.INF" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates select="lead.docket.number" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="plaintiff.party | other.party | defendant.party">
		<div class="&docketsSubSection;">
			<xsl:apply-templates select="party.name.block"/>
			<xsl:if test="count(*[not(self::party.name.block)]) > 0">
				<table>
					<xsl:apply-templates select="*[not(self::party.name.block)]"/>
				</table>
			</xsl:if>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="party.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsParticipantInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsParticipantInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:apply-templates select="plaintiff.party | other.party | defendant.party"/>
		</div>
	</xsl:template>

	<xsl:template match="party.type[ancestor::party.block and not(preceding-sibling::label[1])]">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label" select="'&docketsType;'"/>
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pending.counts.block">
		<xsl:if test="count">
			<xsl:call-template name="CountsBlockAsTable"/>
		</xsl:if>
		<xsl:if test="offense.level.block/offense.level">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsOffenseLevelOpening;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="offense.level.block/offense.level" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="terminated.counts.block">
		<xsl:if test="count">
			<xsl:call-template name="CountsBlockAsTable"/>
		</xsl:if>		
		<xsl:if test="offense.level.block/offense.level">
			<xsl:call-template name="DocketsRow">
				<xsl:with-param name="label" select="'&docketsOffenseLevelDisposition;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="offense.level.block/offense.level" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="complaints.counts.block">
		<xsl:if test="count">
			<xsl:call-template name="CountsBlockAsTable"/>
		</xsl:if>		
	</xsl:template>

	<xsl:template name="CountsBlockAsTable">
		<xsl:call-template name="CountsBlockTableHeaderRow"/>
		<xsl:call-template name="CountsBlockTableRows"/>	
	</xsl:template>
	
	<xsl:template name="CountsBlockTableHeaderRow">
		<tr class="&docketsRowClass;">
			<th>
				<xsl:apply-templates select="label[1]" />
			</th>
			<th>
				<xsl:apply-templates select="label[3]" />
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="CountsBlockTableRows">
		<xsl:for-each select="count">
			<tr class="&docketsRowClass;">
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="title" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="disposition" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
