<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:html="http://www.w3.org/1999/xhtml" xmlns:pcl="http://xmlgraphics.apache.org/fop/extensions/pcl" exclude-result-prefixes="html">
	<xsl:import href="xhtml2fo.xsl" forceDefaultProduct="true"/>

	<xsl:output method="xml" version="1.0" encoding="UTF-8" indent="no" omit-xml-declaration="yes" />
	
	<xsl:template name="process-rowspan-attribute">
		<!-- Enable rowspan processing for the Cover page delivery and for Australia documents -->
		<xsl:if test="@rowspan and (contains(ancestor::html:table[1]/@id, 'co_doc_coverpageTable') or ancestor::html:div[contains(@class, '&australiaDocumentClass;')])">
			<xsl:attribute name="number-rows-spanned">
				<xsl:value-of select="@rowspan"/>
			</xsl:attribute>
		</xsl:if>
	</xsl:template>
</xsl:stylesheet>
