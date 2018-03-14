<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forcePlatform="true"/>
	<xsl:include href="UKStatuses.xsl"/>
	
	<!--- Provisions table -->
	<xsl:template name="BuildProvisionsTree">
		<xsl:apply-templates select="//provisions-table" />
	</xsl:template>

	<xsl:template match="provisions-table">
		<div class="&paraMainClass;">
			<div class="&provisionsTreeClass;">
				<xsl:apply-templates />
			</div>
		</div>
	</xsl:template>

	<xsl:template match="pgroup">
		<xsl:if test="not(position() = 1 and count(pgroup|pgroup-entry) = 0)">
			<div class="&provisionsTreeGroupClass;">
				<xsl:if test="pgroup-number or pgroup-title">
					<div class="&provisionsTreeHeadingClass;">
						<span>
							<xsl:apply-templates select="pgroup-number|pgroup-title" />
						</span>
					</div>
				</xsl:if>
				<xsl:apply-templates select="pgroup|pgroup-entry" />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="provisions-table/pgroup[1]/pgroup-number">
		<xsl:value-of select="." />
	</xsl:template>

	<xsl:template match="pgroup-number">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="pgroup-title">
		<xsl:if test="preceding-sibling::pgroup-number">
			<xsl:value-of select="'&#160;'"/>
		</xsl:if>
		<span>
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="pgroup-entry">
		<div class="&provisionsTreeItemClass;">
			<xsl:if test="string-length(./@status) &gt; 0">
				<xsl:call-template name="StatusIcon">
					<xsl:with-param name="statusCode" select="./@status"/>
					<xsl:with-param name="isSmall" select="true()"/>
					<xsl:with-param name="onNewLine" select="false()"/>
				</xsl:call-template>
			</xsl:if>
			<a>
				<xsl:attribute name="href">
					<xsl:call-template name="GetDocumentUrl">
						<xsl:with-param name="documentGuid" select="./pgroup-number/link/@tuuid"/>
					</xsl:call-template>
				</xsl:attribute>
				<xsl:value-of select="./pgroup-number/link"/>
				<xsl:value-of select="'&#160;'"/>
				<xsl:value-of select="./pgroup-title"/>
			</a>
		</div>

	</xsl:template>

</xsl:stylesheet>
