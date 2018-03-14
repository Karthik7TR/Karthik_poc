<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/20/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="CanadianUniversal.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- Document Sections (Counsel|Abridgment Classification|Headnote|Annotation|Table of Authorities|Opinion|Disposition -->
	<!-- Counsel -->

	<!-- Abridgement -->
	<xsl:template match="digest.wrapper">
		<xsl:if test="$IncludeAbridgmentClassification">
			<xsl:call-template name="FancyTable">
				<xsl:with-param name="id" select="'&crswAbridgmentId;'"/>
				<xsl:with-param name="headerId" select="'&crswAbridgmentHeaderId;'"/>
				<xsl:with-param name="bodyId" select="'&crswAbridgmentBodyId;'"/>
				<xsl:with-param name="name" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswDigestTextKey;', '&crswDigestText;')"/>
				<xsl:with-param name="subtitle" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswDigestSubtitleTextKey;', '&crswDigestSubtitleText;')"/>
				<xsl:with-param name="includeCount" select="false()"/>
				<xsl:with-param name="collapsed" select="true()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="digests" mode="fancyTable">
		<xsl:call-template name="FancyRow">
			<xsl:with-param name="title" select="digest.keysubject/node()"/>
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="digest.keysubject"/>

	<xsl:template match="digest.classifs">
		<xsl:call-template name="ShowIndentedClassification">
			<xsl:with-param name="classifications" select="*"/>
			<xsl:with-param name="firstClass" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="digest.classifnum">
		<xsl:call-template name="DocumentLink">
			<xsl:with-param name="documentGuid" select="@search_string"/>
			<xsl:with-param name="browsePageUrl" select="'&crswAbridgmentPageUrl;'"/>
			<xsl:with-param name="isSearchLink" select="true()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="digest.classification">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template name="ShowIndentedClassification">
		<xsl:param name="classifications"/>
		<xsl:param name="firstClass" select="false()"/>

		<xsl:if test="$classifications">
			<div>
				<xsl:if test="not($firstClass)">
					<xsl:attribute name="class">
						<xsl:text>&prelimHeadClass;</xsl:text>
					</xsl:attribute>
				</xsl:if>

				<xsl:apply-templates select="$classifications[1]"/>

				<xsl:call-template name="ShowIndentedClassification">
					<xsl:with-param name="classifications" select="$classifications[position() > 1]"/>
				</xsl:call-template>
			</div>
		</xsl:if>
	</xsl:template>

	<!-- Headnote -->
	<xsl:template match="headnote.body/para/paratext" priority="1">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="headnote.wrapper">
		<xsl:if test="$IncludeNonWestHeadnotes">
			<xsl:call-template name="FancyTable">
				<xsl:with-param name="id" select="'&crswHeadnoteId;'"/>
				<xsl:with-param name="headerId" select="'&crswHeadnoteHeaderId;'"/>
				<xsl:with-param name="bodyId" select="'&crswHeadnoteBodyId;'"/>
				<xsl:with-param name="name" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswHeadnotesTextKey;', '&crswHeadnotesText;')"/>
				<xsl:with-param name="includeCount" select="false()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<xsl:template match="headnote" mode="fancyTable">
		<xsl:call-template name="FancyRow">
			<xsl:with-param name="content" select="headnote.body"/>
			<xsl:with-param name="title" select="catchphrase.para/node()"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Annotation -->
	<xsl:template match="editorial.note.block.wrapper">
		<xsl:if test="$IncludeCaseAnnotation">
			<xsl:call-template name="FancyTable">
				<xsl:with-param name="id" select="'&crswAnnotationId;'"/>
				<xsl:with-param name="headerId" select="'&crswAnnotationHeaderId;'"/>
				<xsl:with-param name="bodyId" select="'&crswAnnotationBodyId;'"/>
				<xsl:with-param name="name" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswAnnotationLabelKey;', '&crswAnnotationLabel;')"/>
				<xsl:with-param name="includeCount" select="false()"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>
	<xsl:template match="editorial.note.block" mode="fancyTable">
		<xsl:call-template name="FancyRow">
			<xsl:with-param name="content" select="editorial.note.body"/>
		</xsl:call-template>
	</xsl:template>

	<!-- Table of Authorities-->
	<xsl:template match="reflists.wrapper">
		<xsl:call-template name="FancyTable">
			<xsl:with-param name="id" select="'&crswTableOfAuthoritiesId;'"/>
			<xsl:with-param name="headerId" select="'&crswTableOfAuthoritiesHeaderId;'"/>
			<xsl:with-param name="bodyId" select="'&crswTableOfAuthoritiesBodyId;'"/>
			<xsl:with-param name="name" select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswTableOfAuthoritiesTextKey;', '&crswTableOfAuthoritiesText;')"/>
			<xsl:with-param name="includeCount" select="false()"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="table.of.cases.block/head | table.of.cases.body/case.reference |  code.reference.block/head | code.reference.block/code.reference | analytical.reference.block | wordphrase.block"  mode="fancyTable">
		<xsl:call-template name="FancyRow">
			<xsl:with-param name="content" select="."/>
		</xsl:call-template>
	</xsl:template>


	<!-- FancyTable and FancyRow -->
	<xsl:template name="FancyTable">
		<xsl:param name="id"/>
		<xsl:param name="headerId"/>
		<xsl:param name="bodyId"/>
		<xsl:param name="name"/>
		<xsl:param name="subtitle"/>
		<xsl:param name ="includeCount" select="false()"/>
		<xsl:param name ="collapsed" select="false()"/>
		<div>
			<xsl:attribute name="id">
				<xsl:value-of select="$id"/>
			</xsl:attribute>
			<xsl:attribute name="class">
				<xsl:text>&headnotesClass;</xsl:text>
				<!-- Check for any search terms -->
				<xsl:if test="./descendant::N-HIT[1] or ./descendant::N-LOCATE[1] or ./descendant::N-WITHIN[1]">
					<xsl:text><![CDATA[ ]]>&containsSearchTermsClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<h2>
				<xsl:attribute name="id">
					<xsl:value-of select="$headerId"/>
				</xsl:attribute>
				<xsl:attribute name="class">
					<xsl:text>&headnoteHeaderClass; &printHeadingClass;</xsl:text>
					
					<!-- If the table is supposed to be collapsed, add appropriate CSS -->
					<xsl:if test="not($DeliveryMode) and $collapsed">
						<xsl:text><![CDATA[ ]]>&headnoteInactive;</xsl:text>
					</xsl:if>
				</xsl:attribute>
				<span>
					<xsl:attribute name="class">
						<xsl:text>&headnoteHeaderSpanClass;</xsl:text>

						<xsl:choose>
							<!--If there should be a subtitle, use appropriate CSS-->
							<xsl:when test="$subtitle">
								<xsl:text><![CDATA[ ]]>&crswFancyTableHeaderWithSubtitle;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:attribute>

					<xsl:value-of select="$name"/>
					<!-- Add count to table if needed -->
					<xsl:if test="$includeCount">
						<xsl:text><![CDATA[ ]]></xsl:text>
						<xsl:choose>
							<xsl:when test="headnote">
								<xsl:value-of select="concat('(', count(headnote), ')')"/>
							</xsl:when>
							<xsl:when test="digests">
								<xsl:value-of select="concat('(', count(digests/digest.classifs), ')')"/>
							</xsl:when>
						</xsl:choose>
					</xsl:if>
				</span>
				<!-- Add sub title to table if available -->
				<xsl:if test="$subtitle">
					<span class="&crswFancyTableSubtitleSpan;">
						<xsl:value-of select="$subtitle"/>
					</span>
				</xsl:if> 
			</h2>
			<div class="&headnotesContentContainerClass;">
				<div>
					<xsl:attribute name="id">
						<xsl:value-of select="$bodyId"/>
					</xsl:attribute>
					<!-- Don't show the body if it is supposed to be collapsed -->
					<xsl:if test="not($DeliveryMode) and $collapsed">
						<xsl:attribute name="style">
							<xsl:text>display:none</xsl:text>
						</xsl:attribute>
					</xsl:if>
					<xsl:apply-templates mode="fancyTable"/>
				</div>
			</div>
			<div>
				<xsl:text><![CDATA[ ]]></xsl:text>
			</div>
		</div>
		<xsl:processing-instruction name="chunkMarker"/>
	</xsl:template>

	<xsl:template name="FancyRow">
		<xsl:param name="title"/>
		<xsl:param name="content"/>
		<xsl:call-template name="startUnchunkableBlock" />
		<div>
			<xsl:attribute name="class">
				<xsl:text>&headnoteRowClass;</xsl:text>
			</xsl:attribute>
			<xsl:if test="$title">
				<div>
					<xsl:attribute name="class">
						<xsl:text>&emphasisClass;</xsl:text>
					</xsl:attribute>
					<xsl:apply-templates select="$title"/>
				</div>
			</xsl:if>
			<div>
				<xsl:apply-templates select="$content"/>
				<div class="&clearClass;"></div>
			</div>
		</div>
		<xsl:call-template name="endUnchunkableBlock" />
	</xsl:template>
</xsl:stylesheet>
