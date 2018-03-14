<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2016: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLNOpenWeb.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="KnowHow.xsl" forcePlatform="true"/>

	<xsl:template name="RenderTerm">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>
		<xsl:param name="secondaryTerm" select="false()" />
		<xsl:param name="searchWithinTerm" select="false()" />
		<xsl:param name="wordPos" select="@n-wordpos" />
		<xsl:param name="offset" select="@offset" />

		<xsl:copy-of select="$contents" />

	</xsl:template>

	<xsl:template name="RenderSearchTermJumpPoint">
		<xsl:param name="contents">
			<xsl:apply-templates/>
		</xsl:param>


		<xsl:copy-of select="$contents" />
	</xsl:template>

	<xsl:variable name="AccessControl" select="Document/n-metadata/plc.metadata.block/plcmd.access.control/text()" />

	<xsl:template name="CreateBackToTopAnchor">
		<xsl:choose>
			<xsl:when test="$IsIpad = 'true'">
				<div id="&khBackToTop;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
					<a href="&hashTagText;&documentId;">
						<span class="&khIcon; &khIconUpPointer;"></span>
						<xsl:text> &backToTop;</xsl:text>
					</a>
				</div>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="not($AccessControl = 'FREE')">
						<div id="&khBackToTop;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass; &hideState;">
						</div>
					</xsl:when>
					<xsl:otherwise>
						<div id="&khBackToTop;" class="&excludeFromAnnotationsClass; &disableHighlightFeaturesClass;">
							<a href="&hashTagText;&coPageContainer;">
								<span class="&khIcon; &khIconUpPointer;"></span>
								<xsl:text> &backToTop;</xsl:text>
							</a>
						</div>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="n-docbody" priority="1">
		<xsl:call-template name="BuildDocumentHeader"/>
		<div id="&coDocContentBody;">
			<div>
				<xsl:apply-templates select="*/abstract | abstract"/>
				<xsl:if test="$AccessControl = 'FREE'">
					<!--  Moving the internal toc below the abstract as per the story : 838597 -->
					<xsl:if test="$DeliveryMode or $IsMobile">
						<xsl:apply-templates select="*/internal.toc" />
					</xsl:if>
					<xsl:call-template name="DisplayJumpLinks" />
					<xsl:apply-templates select="*/summary"/>
					<xsl:apply-templates select="*/related.drafting.notes"/>
					<xsl:if test="$IsUsWhatsMarketCollection = 'false' and $IsStandardDocument = 'false'">
						<xsl:comment>&EndOfDocumentHead;</xsl:comment>
					</xsl:if>
				</xsl:if>
			</div>
			<xsl:if test="$AccessControl = 'FREE'">
				<xsl:if test="$IsUsWhatsMarketCollection = 'true' or $IsStandardDocument = 'true'">
					<xsl:comment>&EndOfDocumentHead;</xsl:comment>
				</xsl:if>
				<xsl:choose>
					<xsl:when test="$ShowDocument">
						<xsl:apply-templates select="*/body" />
						<xsl:call-template name="AttachedFileForEmptyDocument" />
					</xsl:when>
					<xsl:otherwise>
						<!-- used for DraftingNotesOnly delivery option. If we're not showing the document, we're showing the drafting notes -->
						<xsl:apply-templates select="//drafting.note"/>
					</xsl:otherwise>
				</xsl:choose>
				<xsl:apply-templates select="*/rev.history" />
			</xsl:if>
			<xsl:call-template name="EndOfDocument" />
		</div>
		<xsl:if test="not($DeliveryMode or $IsMobile)">
			<xsl:apply-templates select="*/internal.toc" />
		</xsl:if>
	</xsl:template>	

</xsl:stylesheet>