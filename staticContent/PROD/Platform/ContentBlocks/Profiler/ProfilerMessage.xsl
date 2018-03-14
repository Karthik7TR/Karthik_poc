<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="message.block">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="include.copyright" priority="1">
		<div class="&paraMainClass;">
			<xsl:choose>
				<!-- Replace '"Udpate My Profile" link.' with '"Update Your Profile" link under Related Tools.' (Attorneys and Judges) -->
				<xsl:when test="contains(.,'&quot;Update My Profile&quot; link.')">
					<xsl:variable name="replace.message">
						<xsl:call-template name="replace">
							<xsl:with-param name="string" select="." />
							<xsl:with-param name="pattern" select="'&quot;Update My Profile&quot; link.'" />
							<xsl:with-param name="replacement" select="'&quot;Update Your Profile&quot; link under Related Tools.'" />
						</xsl:call-template>
					</xsl:variable>
					<xsl:value-of select="normalize-space($replace.message)"/>
				</xsl:when>
				<xsl:otherwise>
			<xsl:apply-templates/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
	</xsl:template>

</xsl:stylesheet>