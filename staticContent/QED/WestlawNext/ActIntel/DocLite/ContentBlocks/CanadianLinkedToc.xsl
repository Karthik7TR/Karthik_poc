<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianLinkedToc.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Override and remove the collapse/expand icon -->
	<xsl:template match="doc_heading/toc_headings" priority="1">
		<xsl:variable name="headings" select="count(*)"/>
		<xsl:if test="$headings &gt; 0">
			<div class="&genericBoxClass;">
				<div class="&genericBoxHeaderClass;">
				</div>
				<div class="&genericBoxContentClass;">
					<div class="&genericBoxContentRightClass;">
						<xsl:call-template name="DisplayHeading">
							<xsl:with-param name="headingList" select="*"/>
							<xsl:with-param name="firstHeading" select="1"/>
						</xsl:call-template>
					</div>
				</div>
				<div class="&genericBoxFooterClass;">
				</div>
			</div>
			<input type="hidden" id="&keyCiteFlagPlaceHolderId;" />
		</xsl:if>
	</xsl:template>

	<!-- Override and remove headTextClass -->
	<xsl:template name="DisplayHeading">
		<xsl:param name="headingList"/>
		<xsl:param name="firstHeading" select="0"/>

		<xsl:if test="$headingList">
			<xsl:variable name="headingText">
				<xsl:apply-templates select="$headingList[1]"/>
			</xsl:variable>

			<div>
				<!-- Check if there are no more headings (the current one is the very last heading) -->
				<xsl:if test="not($headingList[2])">
					<xsl:attribute name="id">
						<xsl:text>&prelimGoldenLeafClass;</xsl:text>
					</xsl:attribute>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$firstHeading &gt; 0">
						<xsl:attribute name="class">
							<xsl:text>&simpleContentBlockClass; &prelimBlockClass;</xsl:text>
						</xsl:attribute>
					</xsl:when>
					<xsl:otherwise>
						<xsl:attribute name="class">
							<xsl:text>&simpleContentBlockClass; &prelimHeadClass;</xsl:text>
						</xsl:attribute>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="$headingList[1]"/>

				<xsl:choose>
					<xsl:when test="$firstHeading &gt; 0">
						<!-- Add a div block to collapse TOC content -->
						<div id="&prelimContainerId;">
							<xsl:call-template name="DisplayHeading">
								<xsl:with-param name="headingList" select="$headingList[position() > 1]"/>
							</xsl:call-template>
						</div>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="DisplayHeading">
							<xsl:with-param name="headingList" select="$headingList[position() > 1]"/>
						</xsl:call-template>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>