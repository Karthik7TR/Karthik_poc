<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="annotations/reference.block/*[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
		<xsl:if test="descendant::court.rules.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::cross.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::fsg.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::library.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::research.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::us.sup.ct.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::uniform.law.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
							descendant::tbl[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
			<div class="&contextAndAnalysisClass; &disableHighlightFeaturesClass;" id="&contextAndAnalysisId;">
				<xsl:apply-templates select="head"/>
				<xsl:apply-templates select="mv.source.head"/>
				<div>
					<xsl:apply-templates select="court.rules.reference.body[.//N-HIT | .//N-LOCATE or .//N-WITHIN] |
																 cross.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 library.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 research.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 fsg.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 us.sup.ct.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 uniform.law.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
																 tbl[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
				</div>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="court.rules.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
											cross.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
											library.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
											research.reference.body[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
				<xsl:apply-templates select="head"/>
				<xsl:apply-templates select="mv.source.head"/>
				<xsl:apply-templates select="*[.//N-HIT or .//N-LOCATE or .//N-WITHIN]"/>
	</xsl:template>

	<xsl:template match="law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
								cross.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								research.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
								us.sup.ct.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" name="cascadingReference">
		<xsl:choose>
			<xsl:when test="sub.reference and reference.text[N-HIT or N-LOCATE or N-WITHIN] ">
				<xsl:apply-templates select="reference.text"/>
				<div>
					<xsl:call-template name="wrapWithUl">
						<xsl:with-param name="contents">
							<xsl:apply-templates select="sub.reference"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
			</xsl:when>
			<xsl:when test="sub.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
				<xsl:apply-templates select="reference.text"/>
				<div>
					<xsl:call-template name="wrapWithUl">
						<xsl:with-param name="contents">
							<xsl:apply-templates select="sub.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" />
						</xsl:with-param>
					</xsl:call-template>
				</div>
			</xsl:when>
			<xsl:when test="law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
				cross.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or
				admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or
				usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] or 
				research.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]">
				<xsl:apply-templates select="reference.text"/>
				<div>
					<xsl:call-template name="wrapWithUl">
						<xsl:with-param name="contents">
							<xsl:apply-templates select="law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
								cross.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
								research.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]"/>
						</xsl:with-param>
					</xsl:call-template>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:apply-templates select="node()[.//N-HIT or .//N-LOCATE or .//N-WITHIN]"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="law.review.reference/law.review.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] | 
								cross.reference/cross.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								admin.code.reference/admin.code.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN] |
								usca.reference/usca.reference[.//N-HIT or .//N-LOCATE or .//N-WITHIN]" priority="1">
		<li> 
			<xsl:call-template name="cascadingReference" />
		</li>
	</xsl:template>

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

	<xsl:template match="annotations/reference.block/*[not(.//N-HIT or .//N-LOCATE or .//N-WITHIN)]" />

	<xsl:template match="annotations/reference.block/uniform.law.reference.block/tbl/table/tgroup/tbody/row[not(.//N-HIT or .//N-LOCATE or .//N-WITHIN)]" priority="1"/>

	<xsl:template match="annotations/reference.block/uniform.law.reference.block/tbl/footnote[not(.//N-HIT or .//N-LOCATE or .//N-WITHIN)]" priority="1"/>

</xsl:stylesheet>