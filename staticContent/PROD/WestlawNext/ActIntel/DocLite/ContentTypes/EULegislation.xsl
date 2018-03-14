<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="EULegislation.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Override to remove Star Page data -->
	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeAdminDecisionClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
			
			<xsl:apply-templates select="n-docbody" />

			<!-- Display footnotes at bottom of page -->
			<xsl:call-template name="internationalFootnote" />

			<xsl:apply-templates select="n-docbody/metadata/copyright" />

			<div class="&alignHorizontalLeftClass;">
				<xsl:if test ="n-docbody/metadata/citations/cit-doc">
					<xsl:value-of select="n-docbody/metadata/citations/cit-doc/text()" />
				</xsl:if>
				<xsl:if test ="n-docbody/metadata/citations/cit-celex">
					<div>
						<xsl:value-of select="n-docbody/metadata/citations/cit-celex/text()" />
					</div>
				</xsl:if>
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<!-- Build Outline without Destination Links -->
	<xsl:template name="destidDisplay">
		<xsl:param name="value" />
		<xsl:if test="$value">
			<div>
				<xsl:value-of select="$value"/>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="destid">
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<xsl:call-template name="destidDisplay">
				<xsl:with-param name="value">
					<xsl:text>Text outline</xsl:text>
				</xsl:with-param>
			</xsl:call-template>
		</xsl:if>

		<xsl:variable name="Text" select="../section-head-text/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$Text/@value" />
		</xsl:call-template>

		<xsl:variable name="Index" select="index/section-head-index/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$Index/@value" />
		</xsl:call-template>

		<xsl:variable name="Dates" select="dates/section-head-date/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$Dates/@value" />
		</xsl:call-template>

		<xsl:variable name="Bib" select="bibliographic-info/section-head-bib/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$Bib/@value" />
		</xsl:call-template>

		<xsl:variable name="References" select="../references/section-head-reference/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$References/@value" />
		</xsl:call-template>

		<xsl:variable name="NationalMeasures" select="../national-measures/section-head-nat/dest-id"/>
		<xsl:call-template name="destidDisplay">
			<xsl:with-param name="value" select="$NationalMeasures/@value" />
		</xsl:call-template>
	</xsl:template>

	<!-- Overriding without links -->
	<xsl:template name="textOutline">
		<xsl:if test="//dest-id[count(ancestor::*[name() = 'text']) > 0]">
			<div id="co_textOutline">
				<div class="&centerClass;">
					<xsl:text>Text outline</xsl:text>
				</div>
				<xsl:for-each select="//dest-id[ancestor::*[name() = 'text']]">
					<div>
						<xsl:value-of select="@value"/>
					</div>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Overriding without links -->
	<xsl:template match="national-measures/*" mode="contents">
		<xsl:if test="name()!='section-head-nat'">
			<div>
				<xsl:apply-templates select="itemheader"/>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
