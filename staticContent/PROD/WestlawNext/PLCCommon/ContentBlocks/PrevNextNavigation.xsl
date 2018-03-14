<?xml version="1.0" encoding="utf-8"?>

<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="DocLinks.xsl" forcePlatform="true"/>

	<!-- Prev next navigation-->
	<xsl:template name="BuildPrevNextNavigation">
		<xsl:param name="prevProvisionGuid" select="//n-metadata/metadata.block/md.references/md.previous.doc.in.sequence"/>
		<xsl:param name="prevProvisionTitle" select="''"/>
		<xsl:param name="nextProvisionGuid" select="//n-metadata/metadata.block/md.references/md.next.doc.in.sequence"/>
		<xsl:param name="nextProvisionTitle" select="''"/>


		<xsl:if test="$prevProvisionGuid or $nextProvisionGuid">
			<div id="&coProvisionNavigationId;" class="&hideStateClass;">
				<xsl:if test="$prevProvisionGuid">
					<div id="&coProvisionNavigationPrevId;" class="&provisionNavigationClass;">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:call-template name="GetDocumentUrl">
									<xsl:with-param name="documentGuid" select="$prevProvisionGuid" />
								</xsl:call-template>
							</xsl:attribute>
							<span class="&hideStateClass;">
								<xsl:call-template name="AddHoverTitleForNavigation">
									<xsl:with-param name="title" select="$prevProvisionTitle" />
								</xsl:call-template>
							</span>
							<span class="&provisionNavigationLeftArrow;"></span>
							<span class="&provisionNavigationText;">
								<xsl:text>&provisionNavigationPrevLabel;</xsl:text>
							</span>
						</xsl:element>
					</div>
				</xsl:if>
				<xsl:if test="$nextProvisionGuid">
					<div id="&coProvisionNavigationNextId;" class="&provisionNavigationClass;">
						<xsl:element name="a">
							<xsl:attribute name="href">
								<xsl:call-template name="GetDocumentUrl">
									<xsl:with-param name="documentGuid" select="$nextProvisionGuid" />
								</xsl:call-template>
							</xsl:attribute>
							<span class="&hideStateClass;">
								<xsl:call-template name="AddHoverTitleForNavigation">
									<xsl:with-param name="title" select="$nextProvisionTitle" />
								</xsl:call-template>
							</span>
							<span class="&provisionNavigationRightArrow;"></span>
							<span class="&provisionNavigationText;">
								<xsl:text>&provisionNavigationNextLabel;</xsl:text>
							</span>
						</xsl:element>
					</div>
				</xsl:if>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="AddHoverTitleForNavigation">
		<xsl:param name="title"/>
		<xsl:choose>
			<xsl:when test="string-length($title) > 25">
				<xsl:value-of select="substring($title, 1, 25)" />...
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$title" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
</xsl:stylesheet>
