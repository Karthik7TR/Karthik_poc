<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="FootnotesCommon.xsl" forceDefaultProduct="true"/>
	
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:variable name="ContentsForFootnotesSection" select="//updatenote[not(.=preceding::updatenote) and not(../ins/update/del and not(../ins/update/ins) and not(../ins/*[name()!='update']) and not(../ins/text())) and not(ancestor::snippet-text) and not(ancestor::del)]
				|//footnote-text[not(ancestor::del) and (not(.=preceding::footnote-text))]|//footnote[not(ancestor::del) and (not(.=preceding::footnote))]
				|//mnote[not(ancestor::del) and (not(.=preceding::mnote))]" />

	<xsl:variable name="ListOfFootnotesVar">
		<xsl:for-each select="$ContentsForFootnotesSection">
			-<xsl:value-of select="position()"/>-<xsl:apply-templates select="text()|child::*" />
		</xsl:for-each>
	</xsl:variable>

	<!-- Override to remove links -->
	<xsl:template name="RenderFootnotes">
		<xsl:param name="footNoteTitle" />
		<!-- Render the footnotes at the bottom of the document (if any) -->
		<xsl:if test="string-length($ListOfFootnotesVar) &gt; 0">
			<div class="&paraMainClass;">&#160;</div>
			<xsl:call-template name="RenderFootnoteSectionMarkup">
				<xsl:with-param name="footNoteTitle" select="$footNoteTitle" />
				<xsl:with-param name="contents">
					<xsl:for-each select="$ContentsForFootnotesSection">
						<xsl:element name="div">
							<xsl:attribute name="class">&paraMainClass;</xsl:attribute>
							<xsl:element name="div">
								<xsl:attribute name="class">
									<xsl:text>&footnoteNumberClass;</xsl:text>
								</xsl:attribute>
								<xsl:element name="span">
									<xsl:attribute name="id">
										<xsl:text>FN</xsl:text>
										<xsl:value-of select="position()"/>
									</xsl:attribute>
									<xsl:value-of select="position()"/>
								</xsl:element>
							</xsl:element>
							<xsl:element name="div">
								<xsl:attribute name="class">&footnoteBodyClass;</xsl:attribute>
								<xsl:apply-templates select="text()|child::*" />
							</xsl:element>
						</xsl:element>
					</xsl:for-each>
				</xsl:with-param>
			</xsl:call-template>

			<div class="&paraMainClass;">&#160;</div>

		</xsl:if>
	</xsl:template>

	<!-- Override to remove links -->
	<xsl:template name="RenderFootnoteSuperscript">
		<xsl:param name="currentFootnote"/>

		<xsl:variable name="footnoteTextBefore" select="substring-before($ListOfFootnotesVar, concat('-', $currentFootnote))"/>
		<xsl:variable name="footnoteTextAfter" select="substring($footnoteTextBefore, string-length($footnoteTextBefore) - 2, 3)"/>
		<xsl:variable name="currentCount" select="substring-after($footnoteTextAfter, '-')"/>

		<xsl:if test="string-length($currentCount) &gt; 0">
			<sup>
				<xsl:value-of select="$currentCount"/>
			</sup>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
