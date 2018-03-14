<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!-- I18n In Progress As Of 2/10/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Date.xsl"/>	
	<xsl:include href="Title.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentId;">
			<xsl:call-template name="AddDocumentClasses">
				<xsl:with-param name="contentType" select="'&contentTypeAdminDecisionClass;'"/>
			</xsl:call-template>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates />
			<xsl:call-template name="RenderFootnote" />
			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
		</div>
	</xsl:template>
	


	<!--For Treaty including wli_intl_rules -->
	<xsl:template match="header">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="prelim/image.block"/>
			<xsl:apply-templates select="citation"/>
			<xsl:choose>
				<xsl:when test="//md.infotype/text() = 'wli_bitreaty'">
					<xsl:apply-templates select="language"/>
					<xsl:apply-templates select="second_citation"/>
					<xsl:apply-templates select="title"/>
					<xsl:apply-templates select="prelim/country"/>
					<xsl:apply-templates select="dates"/>
					<xsl:apply-templates select="prelim/copyright"/>
					<xsl:apply-templates select="keywords"/>
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates select="prelim"/>
					<xsl:apply-templates select="LanguageCite"/>
					<xsl:apply-templates select="title"/>
					<xsl:apply-templates select="dates"/>
				</xsl:otherwise>
			</xsl:choose>
		</div>
		<xsl:comment>&EndOfDocumentHead;</xsl:comment>
		<xsl:call-template name="RenderTextOutline" />
	</xsl:template>

	<xsl:template match="citation">
		<div class="&citesClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="second_citation">
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="LanguageCite">
		<xsl:for-each select="xref">
			<div>
				<xsl:apply-templates select="."/>
			</div>
		</xsl:for-each>
	</xsl:template>
	
	<xsl:template match="prelim[//md.infotype/text() != 'wli_bitreaty']">
		<div class="&centerClass;">
			<div>
				<xsl:apply-templates select="institution"/>
				<xsl:apply-templates select="institution-acronym"/>
			</div>
			<xsl:apply-templates select="copyright"/>
			<xsl:apply-templates select="dtype"/>
			<xsl:apply-templates select="country"/>
		</div>
	</xsl:template>

	<xsl:template match="keywords">
		<div class="&keywordsBlockClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="dates">
		<div class="&dateClass;">
			<xsl:apply-templates select="date-adopted | date-effective | date-inforce | date-signed | dates"/>			
		</div>
	</xsl:template>

	<xsl:template match="date-adopted | date-effective | date-inforce | date-signed | dates">
		<div class="&centerClass;">
			<xsl:apply-templates select="preceding-sibling::text()"/>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="institution">
		<span id="co_institution">
			<xsl:apply-templates />
		</span>
	</xsl:template>
	
	<xsl:template match="institution-acronym">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<span>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('co_', translate(text(),'-','_'))"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</span>
	</xsl:template>
	
	<xsl:template match="dtype | country">
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>

	<xsl:template match="copyright">
		<div>
			<xsl:attribute name="class">
				<xsl:text>&copyrightClass;</xsl:text>
				<xsl:if test="//md.infotype/text() = 'wli_bitreaty'">
					<xsl:text><![CDATA[ ]]>&centerClass;</xsl:text>
				</xsl:if>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="language | country[//md.infotype/text() = 'wli_bitreaty']">
		<xsl:call-template name="wrapWithDiv">
			<xsl:with-param name="class" select="'&centerClass;'"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="list[not(child::item)]" priority="1">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="item[not(parent::list)]" >
		<xsl:call-template name="wrapWithDiv"/>
	</xsl:template>
	
	<xsl:template match="list">
		<xsl:if test="item">
			<ul class="&listClass;">
				<xsl:apply-templates select="node()[not(self::list)]" />
			</ul>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/sublist">
		<xsl:if test="item">
			<li>
				<ul class="&listClass;">
					<xsl:apply-templates select="node()[not(self::sublist)]" />
				</ul>
				<xsl:if test="following-sibling::node()[1][self::sublist]">
					<xsl:apply-templates select="following-sibling::node()[1]" />
				</xsl:if>
			</li>
		</xsl:if>
	</xsl:template>

	<xsl:template match="list/item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::list]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>
	
	<xsl:template match="sublist/item">
		<li>
			<xsl:apply-templates />
			<xsl:if test="following-sibling::node()[1][self::sublist]">
				<xsl:apply-templates select="following-sibling::node()[1]" />
			</xsl:if>
		</li>
	</xsl:template>

	<xsl:template match="iref">
		<a>
			<xsl:attribute name="href">
				<xsl:text>#</xsl:text>
				<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</a>
	</xsl:template>

	<xsl:template match="dest-id[not(parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]">
		<a>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
			</xsl:attribute>
		</a>
	</xsl:template>

	<xsl:template match="head1 | head2 | head3 | head4 | head5 | head6 | head7 | head8 | head9 | head10 | article">
		<xsl:call-template name="wrapContentBlockWithGenericClass">
			<xsl:with-param name="id" select="concat('&internalLinkIdPrefix;',translate(translate(dest-id/@dest,'(','_'),')',''))"/> 
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="p">
		<div class="&paraMainClass;">
			<xsl:call-template name="renderParagraphTextDiv"/>
		</div>
	</xsl:template>

	<xsl:template match="u">
		<xsl:call-template name="wrapWithSpan">
			<xsl:with-param name="class">&underlineClass;</xsl:with-param>
			<xsl:with-param name="contents">
				<xsl:apply-templates />
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="b">
		<strong>
			<xsl:apply-templates />
		</strong>
	</xsl:template>

	<xsl:template match="p/c" >
		<div class="&centerClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<!-- Italics -->
	<xsl:template match="p/i">
		<xsl:variable name="contents">
			<xsl:apply-templates />
		</xsl:variable>
		<xsl:if test="string-length($contents) &gt; 0">
			<em>
				<xsl:copy-of select="$contents"/>
			</em>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RenderTextOutline">
		<xsl:if test="count(//dest-id[ancestor::text and (parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]) > 0">
			<div id="co_textOutline">
				<div class="&centerClass;">
					<xsl:text>Document Outline</xsl:text>
				</div>
				<xsl:for-each select="//dest-id[ancestor::text and (parent::article or parent::head1 or parent::head2 or parent::head3 or parent::head4 or parent::head5 or parent::head6 or parent::head7 or parent::head8 or parent::head9 or parent::head10)]">
					<div>
						<a>
							<xsl:attribute name="href">
								<xsl:text>#</xsl:text>
								<xsl:value-of select="concat('&internalLinkIdPrefix;',translate(translate(@dest,'(','_'),')',''))"/>
							</xsl:attribute>
							<xsl:value-of select="following-sibling::*[1]" />
						</a>
					</div>
				</xsl:for-each>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="xref">
		<xsl:call-template name="xrefLink"/>
	</xsl:template>

	<xsl:template match="sup[child::a[starts-with(@name, 'r')]]">
		<xsl:variable name="footnoteNo" select="a/text()" />
		<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
		<sup>
			<xsl:attribute name="id">
				<xsl:value-of select="concat('&footnoteReferenceIdPrefix;', a/@name)"/>
			</xsl:attribute>
			<a href="#co_footnote_{$footnoteLink}" class="&footnoteReferenceClass;">
				<xsl:value-of select="$footnoteNo"/>
			</a>
		</sup>
	</xsl:template>

	<xsl:template match="p[child::sup[child::a[starts-with(@name, 'f')]]]"/>

	<xsl:template name="RenderFootnote">
		<xsl:param name="renderHorizontalRule"/>
		<xsl:if test=".//sup[child::a[starts-with(@name, 'f')]]">
			<xsl:if test="$renderHorizontalRule">
				<hr class="&horizontalRuleClass;"/>
			</xsl:if>
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<table id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<tr>
							<td colspan="2" class="&footnoteSectionTitleClass; &printHeadingClass;">
                <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
              </td>
						</tr>
						<xsl:for-each select="//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteNo" select="a/text()" />
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<tr>
								<td class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;',  a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</td>
								<td class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</td>
							</tr>
						</xsl:for-each>
					</table>
				</xsl:when>
				<xsl:otherwise>
					<div id="&footnoteSectionId;" class="&footnoteSectionClass;">
						<h2 class="&footnoteSectionTitleClass; &printHeadingClass;">
              <xsl:value-of select="DocumentExtension:RetrieveContextValue('&staticTextPropertiesFile;', '&footnoteSectionTitleTextKey;', '&footnoteSectionTitleText;')"/>
            </h2>
						<xsl:for-each select=".//sup[child::a[starts-with(@name, 'f')]]">
							<xsl:variable name="footnoteLink" select="substring(a/@href, 2)" />
							<xsl:variable name="footnoteNo" select="a/text()" />
							<div>
								<div class="&footnoteNumberClass;">
									<span>
										<xsl:attribute name="id">
											<xsl:value-of select="concat('&footnoteIdPrefix;', a/@name)"/>
										</xsl:attribute>
										<a href="#co_footnoteReference_{$footnoteLink}">
											<xsl:value-of select="$footnoteNo"/>
										</a>
									</span>
								</div>
								<div class="&footnoteBodyClass;">
									<xsl:apply-templates select="following-sibling::node()" />
								</div>
							</div>
						</xsl:for-each>
					</div>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:if>
	</xsl:template>
	
</xsl:stylesheet>
