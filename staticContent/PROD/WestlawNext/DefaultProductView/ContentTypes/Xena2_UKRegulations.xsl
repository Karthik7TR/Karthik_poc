<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
			<xsl:apply-templates select="n-docbody"/>

			<!--Section: Citation-->
			<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites" mode="footerCustomCitation" />
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>

	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="//md.cites"/>
			<xsl:apply-templates select="st| so| hcb | hcb1 | hcb2 | hcb3 | hcb4 | dl | til | mx"/>
		</div>
		<xsl:apply-templates select="*[not(self::st or self::so or self::hcb or self::hcb1 or self::hcb2 or self::hcb3 or self::hcb4 or self::dl or self::til or self::mx)]" />
	</xsl:template>

	<!--Supress this display of date-->
	<xsl:template match="n-docbody/doc/dl/d7" priority="1" />

	<xsl:template match="internal.reference" priority="2">
		<xsl:param name="id" select="translate(@ID, ';', '')"/>
		<xsl:param name="refid" select="@refid" />
		<xsl:param name="additionalClass"/>
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test="key('allElementIds', $refid)">
				<a href="{concat('#&internalLinkIdPrefix;', translate($refid, ';', ''))}">
					<xsl:attribute name="class">
						<xsl:text>&internalLinkClass;</xsl:text>
						<xsl:if test="string-length($additionalClass) &gt; 0">
							<xsl:text><![CDATA[ ]]></xsl:text>
							<xsl:value-of select="$additionalClass"/>
						</xsl:if>
					</xsl:attribute>
					<xsl:if test="string-length($id) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', translate($id, ';', ''))"/>
						</xsl:attribute>
					</xsl:if>
					<xsl:choose>
						<xsl:when test="string-length($contents) &gt; 0">
							<xsl:copy-of select="$contents"/>
						</xsl:when>
						<xsl:otherwise>
							<xsl:apply-templates />
						</xsl:otherwise>
					</xsl:choose>
				</a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($contents) &gt; 0">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<!-- Section: Footer Citation -->
	<xsl:template match="md.cites" mode="footerCustomCitation">
		<div class="&citationClass;">
			<xsl:if test="/Document/n-docbody/doc/dl/d7">
				<xsl:variable name="displayDate">
					<xsl:value-of select="/Document/n-docbody/doc/dl/d7" />
				</xsl:variable>
				<xsl:value-of select="$displayDate"/>
			</xsl:if>
		</div>
		<xsl:apply-templates select ="md.primarycite/md.primarycite.info"/>
	</xsl:template>

</xsl:stylesheet>
