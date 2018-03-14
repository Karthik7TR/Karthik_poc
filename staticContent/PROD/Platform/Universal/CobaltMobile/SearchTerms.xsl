<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd"[]>
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>

	<xsl:template match="N-HIT" name="nHit">
		<xsl:param name="id" select="concat('&searchTermIdPrefix;',generate-id(.))"/>
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>

		<xsl:choose>
			<xsl:when test="@wordset = $SearchWithinTermsWordset">
				<xsl:call-template name="nWithin">
					<xsl:with-param name="id" select="$id" />
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@wordset = $SecondaryTermsWordset">
				<xsl:call-template name="nLocate">
					<xsl:with-param name="id" select="$id" />
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="RenderTerm">
					<xsl:with-param name="id" select="$id" />
					<xsl:with-param name="contents" select="$contents" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="N-LOCATE" name="nLocate">
		<xsl:param name="id" select="concat('&searchTermIdPrefix;',generate-id(.))"/>
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:call-template name="RenderTerm">
			<xsl:with-param name="id" select="$id" />
			<xsl:with-param name="contents" select="$contents" />
			<xsl:with-param name="secondaryTerm" select="true()" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="N-WITHIN" name="nWithin">
		<xsl:param name="id" select="concat('&searchTermIdPrefix;',generate-id(.))"/>
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:call-template name="RenderTerm">
			<xsl:with-param name="id" select="$id" />
			<xsl:with-param name="contents" select="$contents" />
			<xsl:with-param name="searchWithinTerm" select="true()" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="RenderTerm">		
		<xsl:param name="id" select="concat('&searchTermIdPrefix;',generate-id(.))"/>
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:param name="secondaryTerm" select="false()" />
		<xsl:param name="searchWithinTerm" select="false()" />
		
		<xsl:processing-instruction name="&openingSearchTermTagStart;"/>
		<a>
			<xsl:attribute name="class">
				<xsl:choose>
					<xsl:when test="$searchWithinTerm">
						<xsl:text>&searchWithinTermClass;</xsl:text>
					</xsl:when>
					<xsl:otherwise>
						<xsl:text>&searchTermClass;</xsl:text>
						<xsl:if test="$secondaryTerm">
							<xsl:text> &locateTermClass;</xsl:text>
						</xsl:if>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:attribute>
			<xsl:if test="$id">
				<xsl:attribute name="id">
					<xsl:value-of select="$id"/>
				</xsl:attribute>
			</xsl:if>
				<xsl:copy-of select="$contents" />
			<xsl:processing-instruction name="&openingSearchTermTagEnd;"/>		
		</a>
	</xsl:template>
	
</xsl:stylesheet>
