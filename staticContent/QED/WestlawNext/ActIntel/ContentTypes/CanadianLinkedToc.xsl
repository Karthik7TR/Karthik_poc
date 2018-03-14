<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
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
						<xsl:if test="$headings &gt; 1">
							<a class="&widgetCollapseIconClass;" href="#"></a>
						</xsl:if>
						<xsl:call-template name="DisplayHeading">
							<xsl:with-param name="headingList" select="*"/>
							<xsl:with-param name="firstHeading" select="1"/>
						</xsl:call-template>
					</div>
				</div>
				<div class="&genericBoxFooterClass;">
				</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>