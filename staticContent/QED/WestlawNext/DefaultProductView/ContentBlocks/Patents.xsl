<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Image.xsl" />
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- A simple join template. -->
	<xsl:template name="join">
		<xsl:param name="nodes" />
		<!-- Use ", " as the default separator -->
		<xsl:param name="separator" select="', '" />
		<xsl:for-each select="$nodes">
			<xsl:choose>
				<!-- Make the main item in lists bold. -->
				<xsl:when test="substring(name(.), string-length(name(.)) - 4) = '.main'">
					<strong>
						<xsl:apply-templates />
					</strong>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates />
				</xsl:otherwise>
			</xsl:choose>
			<xsl:if test="position() != last()">
				<xsl:value-of select="$separator" />
			</xsl:if>
		</xsl:for-each>
	</xsl:template>

	<!-- Checking Citation Type and getting the Jurisdiction court -->
	<xsl:template name="jurisdiction">
		<xsl:param name="citeType" />
		<xsl:if test="$citeType = 'Westlaw'">
			<xsl:variable name="jurisdictionCourt" select="/Document/n-metadata/metadata.block/md.jurisdictions/md.jurisdiction/md.juriscourt"/>
			<xsl:if test="string-length($jurisdictionCourt) &gt; 0 and not(contains(.., $jurisdictionCourt))">
				<xsl:choose>
					<xsl:when test="starts-with($jurisdictionCourt, '(') and substring($jurisdictionCourt, string-length($jurisdictionCourt)) = ')'">
						<xsl:value-of select="concat(' ', $jurisdictionCourt)" />
					</xsl:when>
					<xsl:otherwise>
						<xsl:value-of select="concat(' (', $jurisdictionCourt, ')')" />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:if>
		</xsl:if>
	</xsl:template>

	<!-- Override the label template to also be strong. -->
	<xsl:template match="label">
		<strong>
			<xsl:call-template name="wrapWithSpan">
				<xsl:with-param name="class" select="'&labelClass;'" />
			</xsl:call-template>
		</strong><xsl:text>&#xA0;</xsl:text>
	</xsl:template>

	<!-- Override the default header styling of bold and underlined and just use an h2 tag. -->
	<xsl:template match="head/headtext[bold/underscore]">
		<h2>
			<xsl:apply-templates />
		</h2>
	</xsl:template>
	
	<xsl:template match="head/headtext/bold">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="head/headtext/bold/underscore">
		<xsl:apply-templates />
	</xsl:template>
	
	<!-- Override the "pinpoint.anchor" template in Universal.xsl to use the uuid attribute in addition to the hashcode attribute. This creates anchor tags that let you link to a specific point in the document. -->
	<xsl:template match="pinpoint.anchor">
		<xsl:if test="@hashcode">
			<xsl:element name="a">
				<xsl:attribute name="id">
					<xsl:value-of select="'&pinpointIdPrefix;'"/>
					<xsl:value-of select="@hashcode" />
				</xsl:attribute>
				<xsl:comment>anchor</xsl:comment>
			</xsl:element>
		</xsl:if>
		<xsl:if test="@uuid">
			<xsl:element name="a">
				<xsl:attribute name="id">
					<xsl:value-of select="'&pinpointIdPrefix;'"/>
					<xsl:value-of select="@uuid" />
				</xsl:attribute>
				<xsl:comment>anchor</xsl:comment>
			</xsl:element>
		</xsl:if>
	</xsl:template>

	<!-- Prelim -->
	<xsl:template match="prelim">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&prelimClass; &titleClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Drawings Info -->
	<xsl:template match="drawings.info">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_drawingsInfo &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Source Language -->
	<xsl:template match="source.lang">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&languageBlockClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Abstract -->
	<xsl:template match="abstract">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_abstract &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<xsl:template match="abstract/para">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>
	
	<!-- Claims Block -->
	<xsl:template match="claims.block">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_claimsBlock &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="num.claims.b">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="claim.para">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paraMainClass; &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Specification -->
	<xsl:template match="specification">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'co_specification &panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="division">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="division.gov">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="division.xref">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&panelBlockClass;'" />
		</xsl:call-template>
	</xsl:template>

	<!-- Patent PDF Image Processing -->
	<xsl:variable name="primaryCite" select="/Document/n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite/md.primarycite.info" />

	<xsl:variable name="filedDate" select="/Document/n-metadata/metadata.block/md.dates/md.filedate" />
	<xsl:template match="md.ip.image.link | md.gateway.image.link">
		<!-- For different image markup the guid parameter that we pass to the image service may be different. -->
		<xsl:variable name="blobGuid">
			<xsl:choose>
				<xsl:when test="name() = 'md.gateway.image.link'">
					<xsl:value-of select="@key"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="@tuuid"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="tType">
		  <xsl:value-of select="@ttype" />
		</xsl:variable>
		<xsl:variable name="imageGuid" select="DocumentExtension:RetrieveImageGuid($blobGuid, $tType)"/>
		<xsl:if test="$imageGuid">
			<!-- We need to wrap the link in Div in order to properly display in delivered RTF document -->
			<div>
				<xsl:call-template name="createDocumentBlobLink">
					<xsl:with-param name="guid" select="$imageGuid"/>
					<xsl:with-param name="targetType" select="@ttype"/>
					<xsl:with-param name="mimeType" select="'&pdfMimeType;'" />
					<xsl:with-param name="contents">
						<xsl:text>&originalDocumentLinkText; </xsl:text>
						<xsl:choose>
							<xsl:when test="$regionalCite">
								<xsl:apply-templates select="$regionalCite" />
							</xsl:when>
							<xsl:when test="$primaryCite/md.display.primarycite[@status = 'nr' or @status = 'slip' or @status = 'dash']">
								<xsl:apply-templates select="$firstParallelCite"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates select="$Cite" />
							</xsl:otherwise>
						</xsl:choose>
						<xsl:text> &pdfLabel;</xsl:text>
					</xsl:with-param>
					<xsl:with-param name="displayIcon" select="'&pdfIconPath;'"/>
					<xsl:with-param name="displayIconClassName" select="'&pdfIconClass;'"/>
					<xsl:with-param name="displayIconAltText" select="'&pdfAltText;'"/>
					<xsl:with-param name="originationContext" select="'&docOriginalImageOriginationContext;'" />
					<xsl:with-param name="prettyName" select="translate($Cite//text(),'&space;', '&lowline;')" />
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!-- End Patent PDF Image Processing -->
	
</xsl:stylesheet>
