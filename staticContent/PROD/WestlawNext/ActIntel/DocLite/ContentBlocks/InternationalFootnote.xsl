<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="InternationalFootnote.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Overriding without links -->
	<xsl:template match="footnote-text | footnote | fn/fnn">
		<xsl:variable name="footnoteNo">
			<xsl:call-template name="FootnoteNumberReference" />
		</xsl:variable>
		<xsl:variable name="footnoteDislay">
			<xsl:call-template name="FootnoteNumberDisplay" />
		</xsl:variable>
		<sup id="sourcefn{$footnoteNo}" >
			<xsl:value-of select="$footnoteDislay"/>
		</sup>
	</xsl:template>

	<!-- Overriding without links -->
	<xsl:template match="footnote | footnote-text | fn/fnt" mode="intFootnote">
		<xsl:variable name="footnoteNo">
			<xsl:call-template name="FootnoteNumberReference" />
		</xsl:variable>
		<xsl:variable name="footnoteDislay">
			<xsl:call-template name="FootnoteNumberDisplay" />
		</xsl:variable>
		<xsl:choose>
			<!-- If the footnote has label, we can test and avoid duplicate footnotes -->
			<xsl:when test="@label">
				<xsl:if test="count(preceding::footnote[@label = $footnoteDislay]) = 0">
					<div>
						<div class="&footnoteNumberClass;">
							<span id="targetfn{$footnoteNo}">
								<xsl:value-of select="$footnoteDislay"/>
							</span>
						</div>
						<div class="&footnoteBodyClass;">
							<xsl:call-template name="renderFootnoteBody" />
						</div>
					</div>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<div>
					<div class="&footnoteNumberClass;">
						<span id="targetfn{$footnoteNo}">
							<xsl:value-of select="$footnoteDislay"/>
						</span>
					</div>
					<div class="&footnoteBodyClass;">
						<xsl:call-template name="renderFootnoteBody" />
					</div>
				</div>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
