<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SharedDocketsWithDisclaimer.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
		
	<xsl:template match="division.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="division" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="secondary.docket.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="secondary.docket.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.department.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.department" />
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
				<xsl:if test="demand.amount">
					<xsl:text>$</xsl:text>
				</xsl:if>
				<xsl:apply-templates select="demand.amount" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="location.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="location" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="case.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.status.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="case.status.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="disposition.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="disposition.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="disposition.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="disposition" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="consolidated.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="consolidated" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.location.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.location" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="case.misc.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:for-each select="case.misc">
					<xsl:value-of select="."/>
					<xsl:text>&#160;</xsl:text>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="docket.closed.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="docket.closed.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="assignment.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="assignment.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="related.case.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="related.case.number.INF" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="pretrial.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="pretrial.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trial.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="trial.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="trial.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="trial.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.block" name="generalPartyBlock" mode="render">
		<div class="&docketsSubSection;">
			<!-- There are two different possible XML paths here.  We need to check for both before trying to
			display the table.  Otherwise the text will either show up twice, or not at all. -->
			<xsl:choose>
				<xsl:when test="party.name.block">
					<xsl:apply-templates select="party.name.block" />
				</xsl:when>
				<xsl:when test="*/party.name.block">
					<xsl:apply-templates select="*/party.name.block"/>
				</xsl:when>
			</xsl:choose>
			<table>
				<xsl:call-template name="ChoosePartyNameBlock" />
			</table>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<!--
		party.block ALWAYS proceeds attorney.block on the UI display, but not in the XML doc.  this handles the case when we 
		have no preceding attorney.block in our XML.  it simplys passes the match on to our same match (party.block[1]) in 
		render mode (like attorney.block[1] does).
	-->
	<xsl:template match="party.block[1]">
		<xsl:if test="not(preceding-sibling::attorney.block)">
			<xsl:apply-templates select="." mode="render" />
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="party.block">
		<!-- eat any party.block other than the default processor party.block[1] match above -->
	</xsl:template>

	<!-- 
		this template drives ALL display for party.block.  this is because we want it displayed BEFORE any attorney.block
		matches.  Here we create the Section <div> and then let the generalPartyBlock render all the SubSections inside 
		the Report <div>.
	-->
	<xsl:template match="party.block[1]" priority="1" mode="render">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsParticipantInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsParticipantInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalPartyBlock"/>
			<xsl:apply-templates select="following-sibling::party.block" mode="render"/>
		</div>
	</xsl:template>

	<xsl:template name="ChoosePartyNameBlock">
		<xsl:choose>
			<xsl:when test="party.name.block">
				<xsl:apply-templates select="*[name()!='party.name.block']" />
			</xsl:when>
			<xsl:when test="*/party.name.block">
				<xsl:apply-templates select="*/*[name()!='party.name.block']" />
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="party.name.block">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.address.combined">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="participant.id.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="participant.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.info.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="other.info" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="other.id.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="other.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.misc.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.misc.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="party.address.block" priority="1">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderExplicitAddress">
					<xsl:with-param name="street" select="party.street" />
					<xsl:with-param name="city" select="party.city" />
					<xsl:with-param name="state" select="party.state" />
					<xsl:with-param name="zip" select="party.zip" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aka.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="aka.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="aka.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="aka.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="gender.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="gender" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="origin.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="origin" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="height.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="height" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="weight.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="weight" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="hair.color.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="hair.color" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="eye.color.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="eye.color" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="wcn.complaint.block">
		<table>
			<xsl:call-template name="WcnComplaintBlock" />
		</table>
	</xsl:template>

	<xsl:template name="WcnComplaintBlock">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
				<br />
				<xsl:if test="not(image.block/image.link[@ttype = 'state-docket-pdf'])" >
					<xsl:call-template name="PDFLink"/>
				</xsl:if>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="PDFLink">
		<xsl:if test="not($IsMobile) and $IsPublicRecords = false()">
			<xsl:variable name="index">
				<xsl:choose>
					<xsl:when test="send.runner.link">
						<xsl:value-of select="send.runner.link/@indexvalue" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="0"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			<xsl:choose>
				<xsl:when test="number.block/image.block">
					<xsl:call-template name="RenderGreenTriangleIconIfPdfIsOnNovus" />
					<xsl:call-template name="RenderDocketImage">
						<xsl:with-param name="imageBlock" select="number.block/image.block" />
						<xsl:with-param name="index" select="$index" />
					</xsl:call-template>
				</xsl:when>
				<xsl:when test="$index and $HasDocketOrdersAccess and IsPublicRecords = false()">
					<xsl:call-template name="RenderSendRunnerLink">
						<xsl:with-param name="index" select="$index" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>

	<xsl:template match="attorney.block" name="generalAttorneyBlock" mode="render">
		<xsl:choose>
			<xsl:when test="attorney.name.block">
				<div class="&docketsSubSection;">
					<xsl:apply-templates select="attorney.name.block"/>
					<xsl:if test="*[not(self::attorney.name.block)]!=''">
						<table>
							<xsl:apply-templates select="*[not(self::attorney.name.block)]" />
						</table>
					</xsl:if>
				</div>
			</xsl:when>
			<xsl:when test="firm.block/firm.name.block">
				<div class="&docketsSubSection;">
					<xsl:apply-templates select="firm.block/firm.name.block" mode="header" />
					<table>
						<xsl:apply-templates />
					</table>
				</div>
			</xsl:when>
			<xsl:when test="attorney.status.block">
				<div class="&docketsSubSection;">
					<xsl:apply-templates select="attorney.status.block" mode="header" />
					<table>
						<xsl:apply-templates select="*[not(self::attorney.status.block)]" />
					</table>
				</div>
			</xsl:when>
		</xsl:choose>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="attorney.block">
		<!-- eat any that are not called by name or mode -->
	</xsl:template>

	<xsl:template match="attorney.block[1]" priority="1">
		<xsl:if test="following-sibling::party.block">
			<xsl:apply-templates select="following-sibling::party.block[1]" mode="render" />
		</xsl:if>
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsAttorneyInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsAttorneyInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalAttorneyBlock"/>
			<xsl:apply-templates select="following-sibling::attorney.block" mode="render"/>
		</div>
	</xsl:template>

	<xsl:template match="attorney.name.block">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.name.block" mode="header">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.block">
		<xsl:choose>
			<xsl:when test="parent::attorney.block and not(preceding-sibling::attorney.name.block)">
				<xsl:apply-templates select="*[not(self::firm.name.block)]" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="attorney.status.block" mode="header">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.address.combined">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderExplicitAddress">
					<xsl:with-param name="street" select="attorney.street" />
					<xsl:with-param name="city" select="attorney.city | attorney.city.state" />
					<xsl:with-param name="state" select="attorney.state" />
					<xsl:with-param name="zip" select="attorney.zip" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="email.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="email.address" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bar.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="bar.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="attorney.department.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="attorney.department" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="firm.web.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="firm.web.address" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.block" name="generalMatchedBlock" mode="render">
		<div class="&docketsSubSection;">
			<xsl:apply-templates select="matched.party.block/matched.party.name.block | matched.party.block/matched.plaintiff.party.block/matched.party.name.block | matched.party.block/matched.defendant.party.block/matched.party.name.block | matched.party.block/matched.other.party.block/matched.party.name.block" mode="matchedPartyNameBlock"/>
			<table>
				<xsl:apply-templates />
			</table>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template match="matched.block">
		<!-- eat any that are not called by name or mode -->
	</xsl:template>

	<xsl:template match="matched.block[1]" priority="1">
		<xsl:processing-instruction name="chunkMarker"/>
		<div class="&docketsSection;">
			<h2 id="&docketsParticipantInformationId;" class="&docketsHeading;">
				<xsl:text>&docketsParticipantInformation;</xsl:text>
				<a class="&widgetToggleText; &excludeFromAnnotationsClass; &disableHighlightFeaturesClass;"></a>
			</h2>
			<xsl:call-template name="generalMatchedBlock"/>
			<xsl:apply-templates select="following-sibling::matched.block" mode="render"/>
		</div>
	</xsl:template>

	<xsl:template match="matched.party.name.block" mode="matchedPartyNameBlock">
		<xsl:call-template name="DocketsHeaderRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.party.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.name.block"/>

	<xsl:template match="matched.party.address.combined">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.party.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.party.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.other.id.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.other.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.party.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match ="matched.death.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.death.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.participant.id.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.participant.id" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.address.block | matched.secondary.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderExplicitAddress">
					<xsl:with-param name="street" select="matched.party.street | matched.street" />
					<xsl:with-param name="city" select="matched.party.city | matched.party.city.state | matched.city" />
					<xsl:with-param name="state" select="matched.party.state | matched.state" />
					<xsl:with-param name="zip" select="matched.party.zip | matched.zip" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.party.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.party.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.secondary.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.secondary.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.associated.claims.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.associated.claims" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.gender.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.gender" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.origin.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.origin" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.height.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.height" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.weight.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.weight" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.hair.color.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.hair.color" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.eye.color.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.eye.color" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.other.info.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.other.info" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.attorney.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.attorney.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.address.combined">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderExplicitAddress">
					<xsl:with-param name="street" select="matched.attorney.street" />
					<xsl:with-param name="city" select="matched.attorney.city | matched.attorney.city.state" />
					<xsl:with-param name="state" select="matched.attorney.state" />
					<xsl:with-param name="zip" select="matched.attorney.zip" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.attorney.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.fax.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.attorney.fax.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.attorney.department.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.attorney.department" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- wouldn't need this template if XML was matched.attorney.email.block -->
	<xsl:template match="matched.email.block" >
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.email.address" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>


	<xsl:template match="matched.firm.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.firm.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.firm.fax.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.firm.fax.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.firm.status.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.firm.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.firm.address.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="RenderExplicitAddress">
					<xsl:with-param name="street" select="matched.firm.street" />
					<xsl:with-param name="city" select="matched.firm.city | matched.firm.city.state" />
					<xsl:with-param name="state" select="matched.firm.state" />
					<xsl:with-param name="zip" select="matched.firm.zip" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.firm.phone.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.firm.phone" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.bar.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.bar.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.aka.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.aka.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="matched.aka.type.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="matched.aka.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="calendar.block">
		<xsl:call-template name="CalendarSectionAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="CalendarSectionAsTable">
		<h2 id="&docketsCalendarInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsCalendarInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="calendar.entry"/>
			</xsl:call-template>
		</h2>
		<table class="&docketsTable;">
			<xsl:call-template name="CalendarSectionTableHeaderRow"/>
			<xsl:call-template name="CalendarSectionTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="CalendarSectionTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsDateTime;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsLocation;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsJudge;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="CalendarSectionTableRows">
		<xsl:for-each select="calendar.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:if test="event/date">
							<div>
								<xsl:apply-templates select="event/date" />
							</div>
						</xsl:if>
						<xsl:if test="event/time">
							<div>
								<xsl:apply-templates select="event/time" />
							</div>
						</xsl:if>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="calendar.description" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="location.info.block/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="judge" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="calendar.description">
		<xsl:apply-templates select="event.block "/>
		<xsl:apply-templates select="minutes.block" />
		<xsl:apply-templates select="calendar.disposition.block "/>
		<xsl:apply-templates select="calendar.misc.block "/>
	</xsl:template>

	<xsl:template match="event.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="event.description" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="minutes.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="minutes" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="calendar.disposition.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="calendar.disposition" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="calendar.misc.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="calendar.misc" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="court.room.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="court.room" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="location.info.location.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="location.info.location" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="department.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="department" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.block">
		<xsl:call-template name="JudgmentBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="JudgmentBlockAsTable">
		<xsl:choose>
			<xsl:when test="../case.information.block/court.block/court.norm = 'CA-LA'">
		<h2 id="&docketsFilingInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsFilingInformationLabel;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="judgment.entry"/>
			</xsl:call-template>
		</h2>
			</xsl:when>
			<xsl:otherwise>
		<h2 id="&docketsJudgmentInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsJudgmentInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="judgment.entry"/>
			</xsl:call-template>
		</h2>
				</xsl:otherwise>
			</xsl:choose>
		<table class="&docketsTable;">
			<xsl:call-template name="JudgmentBlockTableHeaderRow"/>
			<xsl:call-template name="JudgmentBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="JudgmentBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
			<th>
			<xsl:choose>
				<xsl:when test="../case.information.block/court.block/court.norm = 'CA-LA'">
					<xsl:text>&docketsParty;</xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text>&docketsAmount;</xsl:text>
				</xsl:otherwise>
			</xsl:choose>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="JudgmentBlockTableRows">
		<xsl:for-each select="judgment.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="judgment.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="judgment.description/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="amount.block/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
						<xsl:if test="amount.block and judgment.amount.misc.block">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:apply-templates select="judgment.amount.misc.block" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="type.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.status.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="judgment.status" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="satisfaction.date.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="satisfaction.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="signed.by.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="signed.by" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.misc.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="judgment.misc" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.party.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label" select="judgment.party.type.block/label" />
			<xsl:with-param name="text">
				<xsl:apply-templates select="judgment.party.type.block/judgment.party.type" />
				<xsl:text><![CDATA[ ]]></xsl:text>
				<xsl:apply-templates select="judgment.party.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="frequency.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="frequency" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="fees.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="fees" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="costs.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="costs" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="total.judgment.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:if test="total.judgment">
					<xsl:text>$</xsl:text>
				</xsl:if>
				<xsl:apply-templates select="total.judgment" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="judgment.amount.misc.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="judgment.amount.misc" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arrests.block">
		<h2 id="&docketsArrestInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsArrestInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="arrest.name.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arrest.name" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arrest.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arrest.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arrest.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arrest.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arrest.offense.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arrest.offense" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arrest.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arrest.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="arresting.agency.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="arresting.agency" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="warrant.outstanding.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="warrant.outstanding" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="warrant.description.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="warrant.description" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="warrant.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="warrant.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bail.block">
		<h2 id="&docketsBailInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsBailInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="bail.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="BailBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="BailBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="BailBlockTableHeaderRow"/>
			<xsl:call-template name="BailBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="BailBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsTypeAmountOfBailPosted;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDateBailPosted;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsBondNumber;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsBailBondStatus;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsPersonPostingBail;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="BailBlockTableRows">
		<xsl:for-each select="bail.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="bail.posted/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="bail.posted.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="bond.number" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="bail.status" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="person.posting.bail" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="bail.number">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label" select="'&docketsBailNumber;'" />
			<xsl:with-param name="text">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="bail.posted/party.number.block | appeal.description.block/party.number.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="party.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="charges.block">
		<h2 id="&docketsChargeInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsChargeInformation;</xsl:text>
		</h2>
		<table>
			<xsl:apply-templates />
		</table>
	</xsl:template>

	<xsl:template match="statute.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="statute" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="statute.amended.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="statute.amended" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="offense.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="offense" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="offense.amended.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="offense.amended" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="offense.class.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="offense.class" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="offense.class.amended.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="offense.class.amended" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="offense.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:call-template name="DocketsDate">
					<xsl:with-param name="date" select="offense.date" />
				</xsl:call-template>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="count.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="count.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="charge.court.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="charge.court" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="total.count.number.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="total.count.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="charge.disposition.date.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="charge.disposition.date" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="charge.disposition.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="charge.disposition" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="misc.info.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="misc.info" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="plea.block">
		<h2 id="&docketsPleaInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsPleaInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="plea.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="PleaBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="PleaBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="PleaBlockTableHeaderRow"/>
			<xsl:call-template name="PleaBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="PleaBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsPlea;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsPleaType;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsPleaDate;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="PleaBlockTableRows">
		<xsl:for-each select="plea.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="plea" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="plea.type" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="plea.date" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="plea">
		<xsl:if test="plea.name">
			<xsl:apply-templates select="plea.name" />
			<xsl:text>: </xsl:text>
		</xsl:if>
		<xsl:apply-templates select="plea.description" />
	</xsl:template>

	<xsl:template match="sentence.block">
		<h2 id="&docketsSentenceInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsSentenceInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="sentence.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="SentenceBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="SentenceBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="SentenceBlockTableHeaderRow"/>
			<xsl:call-template name="SentenceBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="SentenceBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsSentenceDetailsDescription;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsSentenceConsecutiveConcurrent;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsSentenceDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsSentenceLength;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsSentenceLocation;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="SentenceBlockTableRows">
		<xsl:for-each select="sentence.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="sentence.description" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="consecutive.or.concurrent" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="sentence.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="sentence.length" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="sentence.location" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="sentence.description">
		<xsl:if test="sentence or sentence.description.name">
			<xsl:call-template name="DocketsTableData">
				<xsl:with-param name="label" select="'&docketsSentence;'" />
				<xsl:with-param name="text">
					<xsl:for-each select="sentence | sentence.description.name">
						<xsl:apply-templates select="."/>
						<xsl:if test="position() != last()">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
		<xsl:if test="sentence.number">
			<xsl:call-template name="DocketsTableData">
				<xsl:with-param name="label" select="'&docketsSentenceNumber;'" />
				<xsl:with-param name="text">
					<xsl:apply-templates select="sentence.number" />
				</xsl:with-param>
			</xsl:call-template>
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
		<xsl:if test="party.number.block">
			<xsl:call-template name="DocketsTableData">
				<xsl:with-param name="label">
					<xsl:apply-templates select="label" />
				</xsl:with-param>
				<xsl:with-param name="text">
					<xsl:apply-templates select="party.number" />
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="minimum.length">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="minimum" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="maximum.length">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="maximum" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="total.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="total" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="notes.block">
		<h2 id="&docketsNotesCommentsId;" class="&docketsHeading;">
			<xsl:text>&docketsNotesComments;</xsl:text>
		</h2>
		<table>
			<xsl:call-template name="NotesBlock"></xsl:call-template>
		</table>
	</xsl:template>

	<xsl:template name="NotesBlock">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label" select="'&docketsNotes;'" />
			<xsl:with-param name="text">
				<xsl:for-each select="note">
					<xsl:apply-templates select="." />
					<xsl:if test="position() != last()">
						<xsl:text>; </xsl:text>
					</xsl:if>
				</xsl:for-each>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="available.image.block">
		<h2 id="&docketsDocketAvailableImagesId;" class="&docketsHeading;">
			<xsl:text>&docketsDocketAvailableImages;</xsl:text>
		</h2>
		<table class="&docketsTable;">
			<xsl:call-template name="DocketAvailableImageBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="DocketAvailableImageBlockTableRows">
		<xsl:for-each select="available.image">
			<tr>
				<xsl:variable name="imageOrLink">
					<xsl:if test=" $IsPublicRecords = false()">
						<xsl:variable name="index">
							<xsl:value-of select="position()"/>
						</xsl:variable>
							<xsl:if test="image.block/image.gateway.link">
								<xsl:call-template name="RenderGreenTriangleIconIfPdfIsOnNovus" />
								<xsl:call-template name="RenderDocketImage">
									<xsl:with-param name="imageBlock" select="image.block" />
									<xsl:with-param name="index" select="$index" />
								</xsl:call-template>
							</xsl:if>
					</xsl:if>
				</xsl:variable>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="available.image.description" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:if test="$IsPublicRecords = false() and not($IsIpad) and not($IsIphone)">
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:copy-of select="$imageOrLink"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="docket.proceedings.block">
		<xsl:call-template name="renderCalendaringSection"/>
		<h2 id="&docketsDocketProceedingsId;" class="&docketsHeading;">
			<xsl:text>&docketsDocketProceedings;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="docket.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="DocketProceedingsBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="DocketProceedingsBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="DocketProceedingsBlockTableHeaderRow"/>
			<xsl:call-template name="DocketProceedingsBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="DocketProceedingsBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsEntry;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDateDocketed;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsParty;</xsl:text>
			</th>
			<xsl:if test="$IsPublicRecords = false() and not($IsIpad)">
				<th></th>
			</xsl:if>
		</tr>
	</xsl:template>

	<xsl:template name="DocketProceedingsBlockTableRows">
		<xsl:for-each select="docket.entry">
			<tr>
				<xsl:variable name="imageOrLink">
					<xsl:if test="$IsPublicRecords = false() and not($IsIpad)">
						<xsl:variable name="index">
							<xsl:choose>
								<xsl:when test="send.runner.link">
									<xsl:value-of select="send.runner.link/@indexvalue" />
								</xsl:when>
								<xsl:otherwise>
									<xsl:value-of select="position()"/>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:variable>
						<xsl:choose>
							<xsl:when test="docket.description/docket.entry.description.block/image.block">
								<xsl:apply-templates select="docket.description/docket.entry.description.block/image.block" />
							</xsl:when>
							<xsl:when test="docket.entry.number.block/image.block/image.gateway.link">
								<xsl:call-template name="RenderGreenTriangleIconIfPdfIsOnNovus" />
								<xsl:call-template name="RenderDocketImage">
									<xsl:with-param name="imageBlock" select="docket.entry.number.block/image.block" />
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
					</xsl:if>
				</xsl:variable>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="docket.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select=".//docket.entry.number" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:variable name="documentguid" select="$Guid" />
						<xsl:variable name="docguid" select="'IA1C26C38351E11E1A84FF3E97352C397'" />
						<xsl:choose>
							<xsl:when test ="$docguid = $documentguid">
								<xsl:choose>
									<xsl:when test="$ShowUnreleasedFeatures">
										<span style="color:blue">
											<xsl:apply-templates select="docket.description" />
										</span >
									</xsl:when>
									<xsl:otherwise>
										<xsl:apply-templates select="docket.description" />
									</xsl:otherwise >
								</xsl:choose>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="docket.description" />
							</xsl:otherwise >
						</xsl:choose>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="docketed.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="docket.party.block/party">
							<xsl:apply-templates select="docket.party.type" />
							<xsl:if test="docket.party.type and docket.party.name">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
							<xsl:apply-templates select="docket.party.name" />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:if test="$IsPublicRecords = false() and not($IsIpad) and not($IsIphone)">
					<xsl:call-template name="DocketsTableCell">
						<xsl:with-param name="text">
							<xsl:copy-of select="$imageOrLink"/>
						</xsl:with-param>
					</xsl:call-template>
				</xsl:if>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="docket.entry.description.block/image.block" priority="1">
		<xsl:if test="$IsPublicRecords = false()">
			<xsl:apply-templates select="image.link">
				<xsl:with-param name="text">
					<i></i>
					<xsl:text>&docketViewPDFText;</xsl:text>
				</xsl:with-param>
				<xsl:with-param name="className" select="'&docketProceedingsButtonClass; &docketProceedingsPDFButtonClass;'"/>
			</xsl:apply-templates>
		</xsl:if>
	</xsl:template>

	<xsl:template match="available.image.description">
		<xsl:apply-templates select="text()" />
	</xsl:template>

	<xsl:template match="docket.description">
		<xsl:apply-templates select="docket.entry.description.block" />
		<xsl:apply-templates select="document.description.block" />
		<xsl:apply-templates select="docket.judge.name.block" />
		<xsl:apply-templates select="docket.description.disposition.block" />
		<xsl:apply-templates select="insurance.block" />
		<xsl:apply-templates select="misc.date.block" />
		<xsl:apply-templates select="misc.time.block" />
		<xsl:apply-templates select="docket.misc.block" />
		<xsl:apply-templates select="room.block" />
		<xsl:apply-templates select="docket.description.location.block" />
		<xsl:apply-templates select="docket.attorney.name.block" />
		<xsl:apply-templates select="file.type.block" />
	</xsl:template>

	<xsl:template match="docket.entry.description.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.entry.description" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="document.description.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="document.description" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="docket.judge.name.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.judge.name" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="docket.description.disposition.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.description.disposition" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="insurance.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="insurance" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="misc.date.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="misc.date" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="misc.time.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="misc.time" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="docket.misc.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.misc" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="room.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="room" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="docket.attorney.name.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.attorney.name" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="docket.description.location.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="docket.description.location" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="fee.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="fee" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="file.type.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="file.type" />
			</xsl:with-param>
		</xsl:call-template>
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="payment.block">
		<h2 id="&docketsPaymentInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsPaymentInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="payment.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="PaymentBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="PaymentBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="PaymentBlockTableHeaderRow"/>
			<xsl:call-template name="PaymentBlockTableRows"/>
		</table>
	</xsl:template>

	<xsl:template name="PaymentBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsAmount;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="PaymentBlockTableRows">
		<xsl:for-each select="payment.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="payment.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="payment.description.block/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:if test="payment.amount.block/payment.amount">
							<xsl:text>$</xsl:text>
						</xsl:if>
						<xsl:apply-templates select="payment.amount.block/payment.amount" />
						<xsl:if test="payment.amount.block/payment.amount and payment.amount.misc.block">
							<xsl:text><![CDATA[ ]]></xsl:text>
						</xsl:if>
						<xsl:apply-templates select="payment.amount.misc.block" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="payment.type.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="payment.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="receipt.number.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="receipt.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="payment.amount.misc.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="payment.amount.misc" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lc.court.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="court" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lc.filing.county">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="filing.county" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="lc.judge.block">
		<xsl:call-template name="DocketsRow">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="lc.judge" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="complaint.block">
		<h2 id="&docketsComplaintInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsComplaintInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="complaint.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="ComplaintBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="ComplaintBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="ComplaintBlockTableHeaderRow"/>
			<xsl:call-template name="ComplaintBlockTableRows"/>
		</table>
	</xsl:template>


	<xsl:template name="ComplaintBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsServiceDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsServiceType;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsAnswerDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="ComplaintBlockTableRows">
		<xsl:for-each select="complaint.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="complaint.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="disposition.status" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell" />
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="complaint.description.info" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="complaint.number.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="complaint.number" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="complaint.type.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="complaint.type" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="appeals.block">
		<h2 id="&docketsAppealsInformationId;" class="&docketsHeading;">
			<xsl:text>&docketsAppealsInformation;</xsl:text>
			<xsl:call-template name="DisplayCount">
				<xsl:with-param name="nodes" select="appeal.entry"/>
			</xsl:call-template>
		</h2>
		<xsl:call-template name="AppealsBlockAsTable"></xsl:call-template>
	</xsl:template>

	<xsl:template name="AppealsBlockAsTable">
		<table class="&docketsTable;">
			<xsl:call-template name="AppealsBlockTableHeaderRow"/>
			<xsl:call-template name="AppealsBlockTableRows"/>
		</table>
	</xsl:template>


	<xsl:template name="AppealsBlockTableHeaderRow">
		<tr>
			<th>
				<xsl:text>&docketsAppealDate;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDescription;</xsl:text>
			</th>
			<th>
				<xsl:text>&docketsDispositionInformation;</xsl:text>
			</th>
		</tr>
	</xsl:template>

	<xsl:template name="AppealsBlockTableRows">
		<xsl:for-each select="appeal.entry">
			<tr>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="appeal.date" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:for-each select="appeal.description.block/node()">
							<xsl:apply-templates select="." />
							<xsl:if test="position() != last()">
								<xsl:text><![CDATA[ ]]></xsl:text>
							</xsl:if>
						</xsl:for-each>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="DocketsTableCell">
					<xsl:with-param name="text">
						<xsl:apply-templates select="disposition.info" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template match="additional.appeal.info.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="additional.appeal.info" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="disposition.info.disposition.block">
		<xsl:call-template name="DocketsTableData">
			<xsl:with-param name="label">
				<xsl:apply-templates select="label" />
			</xsl:with-param>
			<xsl:with-param name="text">
				<xsl:apply-templates select="disposition.info.disposition" />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
