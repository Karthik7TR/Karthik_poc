<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:template match="nod.block" priority="2">
		<xsl:if test="descendant::nod.body/nod.body//N-HIT or descendant::nod.body/nod.body//N-LOCATE or descendant::nod.body/nod.body//N-WITHIN">
			<div class="&notesOfDecisionsClass; &disableHighlightFeaturesClass; &excludeFromAnnotationsClass;" id="&notesOfDecisionsId;">
				<xsl:apply-templates select="head"/>
				<div>
					<xsl:apply-templates select="nod.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
				</div>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>