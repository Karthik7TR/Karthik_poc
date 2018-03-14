<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">

<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&testimonialHistoryDocumentClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="Header" />
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>
			<xsl:call-template name="Body" />
			<xsl:call-template name="Footer" />
		</div>
	</xsl:template>

	<xsl:template name="Header">
		<div class="&alignHorizontalCenterClass;">
			<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/expert.name" />
			<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/document.type.header" />
		</div>
		<div class="&sectionClass; &areaOfExpertiseClass;">
			<div class="&paratextMainClass;">
				<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/expertise.header" />
			</div>
			<div class="&paraMainClass;">
				<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/expertise.block/expertise" />
			</div>
		</div>
		<div class="&paraMainClass;">
			<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/source.header" />
		</div>
	</xsl:template>

	<xsl:template name="Body">
		<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/expert.testimonial.history.count" />
		<xsl:apply-templates select="n-docbody/expert.testimonial.history.document/expert.testimonial.history.list" />
	</xsl:template>

	<xsl:template name="Footer">
		<br />
		<xsl:call-template name="EndOfDocument" />
	</xsl:template>

	<xsl:template match="expert.name">
		<xsl:call-template name="wrapWithDiv" />
	</xsl:template>

	<xsl:template match="document.type.header">
		(<xsl:apply-templates />)
	</xsl:template>

	<xsl:template match="expertise.header">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="expertise.block/expertise">
		<xsl:apply-templates/>
		<xsl:if test="position() != last()">
			<xsl:text>,<![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="expertise.block">
		<xsl:call-template name="TestimonialHistoryListAsTable"/>
	</xsl:template>

	<xsl:template match="source.header">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="expert.testimonial.history.count">
		<div class="&paraMainClass; &testimonialHistoryTotalClass;">
			<strong>
				<xsl:text>&testimonialHistoryTotal;</xsl:text>
				&nbsp;
				<xsl:apply-templates />
			</strong>
		</div>
	</xsl:template>

	<xsl:template match="expert.testimonial.history.list">
		<xsl:call-template name="TestimonialHistoryListAsTable"/>
	</xsl:template>

	<xsl:template name="TestimonialHistoryListAsTable">
		<div class="&fullscreenTableClass;">
			<table class="&testimonialHistoryTableClass;">
				<tr>
					<th>&testimonialHistoryHeaderCount;</th>
					<th>&testimonialHistoryHeaderText;</th>
					<th>&testimonialHistoryHeaderType;</th>
				</tr>
				<xsl:call-template name="TestimonialHistoryListTableRows"/>
			</table>
		</div>
	</xsl:template>

	<xsl:template name="TestimonialHistoryListTableRows">
		<xsl:for-each select="expert.testimonial.history.block">
			<tr>
				<xsl:call-template name="TestimonialHistoryTableCell">
					<xsl:with-param name="textLine1">
						<xsl:number value="position()"/>
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="TestimonialHistoryTableCell">
					<xsl:with-param name="textLine1">
						<xsl:apply-templates select="case.title" />
					</xsl:with-param>
					<xsl:with-param name="textLine2">
						<xsl:apply-templates select="case.detail" />
					</xsl:with-param>
				</xsl:call-template>
				<xsl:call-template name="TestimonialHistoryTableCell">
					<xsl:with-param name="textLine1">
						<xsl:apply-templates select="category" />
					</xsl:with-param>
				</xsl:call-template>
			</tr>
		</xsl:for-each>
	</xsl:template>

	<xsl:template name="TestimonialHistoryTableCell">
		<xsl:param name="textLine1" />
		<xsl:param name="textLine2" />
		<td>
			<xsl:choose>
				<xsl:when test="string-length($textLine1) &gt; 0">
					<xsl:choose>
						<xsl:when test="string-length($textLine2) &gt; 0">
							<div>
								<div>
									<xsl:copy-of select="$textLine1"/>
								</div>
								<div>
									<xsl:copy-of select="$textLine2"/>
								</div>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<xsl:copy-of select="$textLine1"/>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:when>
				<xsl:when test="string-length($textLine2) &gt; 0">
					<div>
						<div>
							<xsl:copy-of select="$textLine2"/>
						</div>
					</div>
				</xsl:when>
				<xsl:when test="$IsIpad">
					<xsl:text><![CDATA[-]]></xsl:text>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:otherwise>
			</xsl:choose>
		</td>
	</xsl:template>

	<xsl:template name="ToOrderTop">
		<div class="&testimonialHistoryToOrderClass;">
			<div>
				<xsl:text>&testimonialHistoryToOrderPart1;</xsl:text>
			</div>
			<div>
				<xsl:text>&testimonialHistoryToOrderPart2;</xsl:text>
			</div>
		</div>
	</xsl:template>

</xsl:stylesheet>
