<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="LinkedToc.xsl" forcePlatform="true"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	
	<!-- Overriding to suppress links, DeliveryMode, and KeyCite -->	
	<xsl:template match="prelim.block" mode="statueHeader">
		<xsl:variable name="head">
			<xsl:apply-templates select="prelim.head/head"/>
		</xsl:variable>
		<xsl:variable name="headText">
			<xsl:apply-templates select="prelim.head/head/head.info/headtext"/>
		</xsl:variable>
		<xsl:variable name="subHead">
			<xsl:apply-templates select="prelim.head/prelim.head"/>
		</xsl:variable>
		<xsl:if test="string-length($head) &gt; 0">
			<div class="&genericBoxClass;">
				<div class="&genericBoxHeaderClass;">
					<span></span>
				</div>
				<div class="&genericBoxContentClass;">
					<div class="&genericBoxContentRightClass;">
						<div>
							<xsl:attribute name="class">
								<xsl:text>&simpleContentBlockClass; &prelimBlockClass;</xsl:text>
								<xsl:if test="string-length($headText) &gt; 0">
									<xsl:choose>
										<xsl:when test="@style='c' or ancestor::head/@style = 'c' or ancestor::form.head/@style = 'c' or @align='center' or ancestor::head/@align = 'center' or ancestor::form.head/@align = 'center' or ancestor::fa.head/@align = 'center'">
											<xsl:text><![CDATA[ ]]>&alignHorizontalCenterClass;</xsl:text>
										</xsl:when>
										<xsl:when test="@style = 'l'">
											<xsl:text><![CDATA[ ]]>&alignHorizontalLeftClass;</xsl:text>
										</xsl:when>
										<xsl:otherwise />
									</xsl:choose>
								</xsl:if>
							</xsl:attribute>
							<xsl:copy-of select="$head"/>
							<xsl:if test="string-length($subHead) &gt; 0">
								<xsl:copy-of select="$subHead"/>
							</xsl:if>
						</div>
					</div>
				</div>
				<div class="&genericBoxFooterClass;">
					<span></span>
				</div>
			</div>
		</xsl:if>
	</xsl:template>
	
	<!-- Suppress the currentness link since it is not applicable here -->
	<xsl:template name="renderCurrentnessLink" />
</xsl:stylesheet>
