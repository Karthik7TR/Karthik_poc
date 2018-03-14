<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" >
	<xsl:include href="List.xsl" forcePlatform="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="para[parent::list]" priority="3">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="list | ul" priority="1">
		<div>
			<xsl:attribute name="class">
				<xsl:value-of select="'&paratextMainClass; &indentLeft2Class;'"/>
			</xsl:attribute>
			<xsl:apply-templates/>
		</div>
	</xsl:template>

</xsl:stylesheet>
