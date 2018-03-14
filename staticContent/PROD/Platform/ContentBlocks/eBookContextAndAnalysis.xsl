<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="annotations/reference.block/*">
		<xsl:if test="descendant::court.rules.reference.body or 
							descendant::cross.reference.body or 
							descendant::fsg.reference or 
							descendant::law.review.reference or 
							descendant::library.reference.body or 
							descendant::research.reference.body or 
							self::us.sup.ct.reference.block or 
							descendant::usca.reference or 
							descendant::admin.code.reference or
							descendant::author.reference.body">
			<div class="&contextAndAnalysisClass; &disableHighlightFeaturesClass;">
				<xsl:apply-templates select="head"/>
				<xsl:apply-templates select="mv.source.head"/>
				<div>
					<xsl:apply-templates select="court.rules.reference.body | 
																 cross.reference.body | 
																 library.reference.body | 
																 research.reference.body | 
																 law.review.reference | 
																 fsg.reference | 
																 us.sup.ct.reference | 
																 usca.reference | 
																 admin.code.reference |
																 author.reference.body" />
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="annotations/reference.block/ed.note.reference.block">
		<div class="&contextAndAnalysisClass; &disableHighlightFeaturesClass;">
			<xsl:apply-templates select="head"/>
			<xsl:apply-templates select="mv.source.head"/>
			<xsl:if test="ed.note.reference">
					<xsl:apply-templates select="ed.note.reference" />
			</xsl:if>
			<xsl:if test="tbl">
				<xsl:apply-templates select="tbl" />
			</xsl:if>
		</div>
	</xsl:template>
	
	<xsl:template match="ed.note.reference">
			<xsl:apply-templates select="para"/>
	</xsl:template>
	<xsl:template match="court.rules.reference | 
								library.reference | 
								fsg.reference | 
								uniform.law.reference | 
								uniform.law.reference.block/tbl |
								ed.note.reference.block/tbl">
		<div>
			<xsl:apply-templates select="node()"/>
		</div>
	</xsl:template>

	<xsl:template match="law.review.reference | 
								cross.reference |
								us.sup.ct.reference |
								admin.code.reference |
								usca.reference |
								author.reference" name="cascadingReference">
		<xsl:apply-templates select="reference.text"/>
		<xsl:choose>
			<xsl:when test="law.review.reference or 
							cross.reference or
							us.sup.ct.reference or
							admin.code.reference or
							usca.reference or
							author.reference">
				<xsl:call-template name="wrapWithUl">
					<xsl:with-param name="contents">
						<xsl:apply-templates select="law.review.reference | 
									cross.reference |
									us.sup.ct.reference |
									admin.code.reference |
									usca.reference |
									author.reference"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="sub.reference">
				<xsl:call-template name="wrapWithUl">
					<xsl:with-param name="contents">
						<xsl:apply-templates select="sub.reference" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="law.review.reference/law.review.reference | 
								cross.reference/cross.reference |
								us.sup.ct.reference/us.sup.ct.reference |
								admin.code.reference/admin.code.reference |
								usca.reference/usca.reference |
								author.reference/author.reference" priority="1">
		<li>
			<xsl:call-template name="cascadingReference" />
		</li>
	</xsl:template>

	<!--<xsl:template match="us.sup.ct.reference">
		<xsl:choose>
			<xsl:when test="sub.reference">
				<xsl:apply-templates select="reference.text"/>
				<xsl:call-template name="wrapWithUl">
					<xsl:with-param name="contents">
						<xsl:apply-templates select="sub.reference" />
					</xsl:with-param>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="node()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>-->

	<xsl:template match="sub.reference" priority="1" name="subReference">
		<li>
			<xsl:apply-templates select="reference.text"/>
			<xsl:if test="sub.reference">
				<xsl:call-template name="wrapWithUl">
					<xsl:with-param name="contents">
						<xsl:apply-templates select="sub.reference"/>
					</xsl:with-param>
				</xsl:call-template>
			</xsl:if>
		</li>
	</xsl:template>

	<!--Special handling for uniform law reference-->
	<xsl:template match="annotations/reference.block/uniform.law.reference.block">
		<xsl:if test="uniform.law.reference or 
							tbl">
			<div class="&contextAndAnalysisClass; &disableHighlightFeaturesClass;" id="&contextAndAnalysisId;">
				<xsl:apply-templates select="head"/>
				<xsl:apply-templates select="mv.source.head"/>
				<div>
					<xsl:if test="uniform.law.reference">
						<xsl:apply-templates select="uniform.law.reference" />
					</xsl:if>
					<xsl:if test="tbl">
						<xsl:apply-templates select="tbl" />
					</xsl:if>
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<!--<xsl:template match="annotations/reference.block/*" />

	<xsl:template match="sub.reference" />

	<xsl:template match="reference.text" priority="1" />

	<xsl:template match="annotations/reference.block/uniform.law.reference.block/tbl/table/tgroup/tbody/row" priority="1"/>

	<xsl:template match="annotations/reference.block/uniform.law.reference.block/tbl/footnote[not(.//N-HIT or .//N-LOCATE or .//N-WITHIN)]" priority="1"/>-->

</xsl:stylesheet>