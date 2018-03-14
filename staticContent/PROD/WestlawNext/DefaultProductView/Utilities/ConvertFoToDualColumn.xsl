<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl">

	<!-- Import the platform convert FO to dual column so we can override named template-->
	<xsl:import href="ConvertFoToDualColumn.xsl" forcePlatform="true"/>
	
  <xsl:output method="xml" indent="no" encoding="utf-8" omit-xml-declaration="yes" />

	<!-- Overriding for WLN, so we can exclude inline keycite flags (this was resulting in every flag moving to a new line in PDFs) -->
	<xsl:template match="fo:external-graphic">
		<xsl:choose>
			<!-- Do not do anything for the footer logo or any image where we append ?ignoreDeliveryNewLine (for example, inline keycite flags) -->
			<xsl:when test="not(ancestor :: fo:static-content[@flow-name='xsl-region-after']) and not(contains(@src, '?ignoreDeliveryNewLine'))">
				<fo:block></fo:block><!-- try to move the image down so that it is displayed in its entirety, and not truncated. -->
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>
			</xsl:when>
			<xsl:otherwise><!-- logo in footer: keep as-is -->
				<xsl:copy>
					<xsl:apply-templates select="@*|node()"/>
				</xsl:copy>				
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>

