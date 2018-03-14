<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl" />
	<!-- This included stylesheet is for any rows of the Patent Table format display that need to be collapsible.
			These rows (or sections) will need special markup that will allow them to be expanded and collapsed -->
	
	<xsl:template match="claims.block[.//text()]" priority="1">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="nodeName" select="name(.)" />
					<xsl:variable name="nodeNameKey" select="translate(name(.), '&#46;', '')" />
					<xsl:variable name="defaultText" select="./head/headtext//text()" />
					<xsl:choose>
						<xsl:when test="not($DeliveryMode)">
							<div id="claimsHeading" class="&panelBlockClass;">
								<strong>
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
								</strong>
								<xsl:if test="not($DeliveryMode)">
									<div class="&panelBlockClass;">
										<a class="&hideStateClass;"  id="claimsViewAllLinkTop" href="javascript:void(0)">View All</a>
										<a class="&hideStateClass;"  id="claimsViewLessLinkTop" href="javascript:void(0)">View Less</a>
									</div>
								</xsl:if>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<div class="&panelBlockClass;">
								<strong>
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
								</strong>
							</div>
						</xsl:otherwise>
					</xsl:choose>
				</td>
				<td id="claimsContent" class="&patentsClaimsSection;">
					<xsl:apply-templates select="./*[not(name() = 'head')]" />
					<xsl:if test="not($DeliveryMode)">
						<div class="&panelBlockClass;">
							<a class="&hideStateClass;" id="claimsViewAllLink" href="javascript:void(0)">View All</a>
							<a class="&hideStateClass;" id="claimsViewLessLink" href="#claimsHeading">View Less</a>
						</div>
					</xsl:if>
				</td>
			</tr>
	</xsl:template>

	<xsl:template match="specification[.//text()]" priority="1">
			<tr class="&borderTopClass;">
				<td>
					<xsl:variable name="publicationId" select="/Document/n-metadata/metadata.block/md.publications/md.publication/md.pubid" />
					<xsl:variable name="nodeNameKey">
						<xsl:choose>
							<xsl:when test="$publicationId='4072'">
								<xsl:text>specificationfordesignpatents</xsl:text>
							</xsl:when>
							<xsl:otherwise>
								<xsl:text>specification</xsl:text>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:variable>
					<xsl:variable name="defaultText" select="./head/headtext//text()" />
					<xsl:choose>
						<xsl:when test="not($DeliveryMode)">
							<div id="specificationHeading" class="&panelBlockClass;">
								<strong>
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
								</strong>
								<xsl:if test="not($DeliveryMode)">
									<div class="&panelBlockClass;">
										<a class="&hideStateClass;" id="specificationViewAllLinkTop" href="javascript:void(0)">View All</a>
										<a class="&hideStateClass;" id="specificationViewLessLinkTop" href="javascript:void(0)">View Less</a>
									</div>
								</xsl:if>
							</div>
						</xsl:when>
						<xsl:otherwise>
							<div class="&panelBlockClass;">
								<strong>
									<xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', $nodeNameKey, $defaultText)"/>
								</strong>
							</div>
						</xsl:otherwise>
					</xsl:choose>

				</td>
				<td id="specificationContent" class="&patentsSpecificationSection;">
					<xsl:apply-templates select="./*[not(name() = 'head')]" />
					<xsl:if test="not($DeliveryMode)">
						<div class="&panelBlockClass;">
							<a class="&hideStateClass;" id="specificationViewAllLink" href="javascript:void(0)">View All</a>
							<a class="&hideStateClass;" id="specificationViewLessLink" href="#specificationHeading">View Less</a>
						</div>
					</xsl:if>
				</td>
			</tr>
	</xsl:template>

	<xsl:template match="claims.block//table.placeholder | specification//table.placeholder">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&paratextMainClass;'" />
		</xsl:call-template>
	</xsl:template>
	
</xsl:stylesheet>
