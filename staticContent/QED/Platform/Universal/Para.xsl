<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2017: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SpecialCharacters.xsl"/>
	<xsl:include href="Leader.xsl"/>
	<xsl:include href="WrappingUtilities.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="para | form.para | stat.para | codes.para | p" name="para">
		<xsl:param name="divId">
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
			</xsl:if>
		</xsl:param>
		<xsl:param name="className" select="'&paraMainClass;'" />
		<xsl:if test="count(node()[not(self::label.name or self::label.designator)]) &gt; 0 and (.//text() or .//leader or .//image.block)">
			<div>
				<xsl:attribute name="class">
					<xsl:value-of select="$className"/>
					<xsl:choose>
						<xsl:when test="@*">
							<xsl:call-template name="addParaClasses"/>
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
				<xsl:if test="string-length($divId) &gt; 0">
					<xsl:attribute name="id">
						<xsl:value-of select="$divId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para[parent::list.item and preceding-sibling::label.designator]" priority="1">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="para[count(paratext) = 1 and count(paratext/text()) = 1 and not(para or label.designator or label.name or descendant::image.block)]">
		<xsl:variable name="translatedText">
			<xsl:call-template name="SpecialCharacterTranslator">
				<xsl:with-param name="textToTranslate" select="paratext"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:if test="string-length(normalize-space($translatedText)) &gt; 0 or (.//text() != paratext/text())">		
			<xsl:call-template name="para"/>
		</xsl:if>
	</xsl:template>

	<!-- Supress this element in general. -->
	<xsl:template match="para/label.designator | para/head/label.designator | form.para/form.head/label.designator | p/head/label.designator
												| para/label.name | para/head/label.name | form.para/form.head/label.name | codes.para/head/label.name | p/label.name | p/head/label.name"/>

	<xsl:template match="label.designator" mode="para-label.designator">
		<xsl:apply-templates />
		<xsl:text><![CDATA[ ]]></xsl:text>
	</xsl:template>

	<xsl:template match="label.name" mode="para-label.designator">
		<strong>
			<xsl:apply-templates />
			<xsl:text><![CDATA[ ]]></xsl:text>
		</strong>
	</xsl:template>

	<xsl:template match="paratext | form.text | para/text.line">
		<xsl:call-template name="renderParagraphTextDiv"/>
	</xsl:template>

	<xsl:template match="paratext[ancestor::list.item and parent::para[preceding-sibling::label.designator]]" priority="1">
		<xsl:choose>
			<xsl:when test="leader">
				<xsl:call-template name="leaderContent">
					<xsl:with-param name="parent" select="." />
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:if test="ancestor::checklist.block">
					<img src="{$Images}&emptyCheckboxPath;" alt="&emptyCheckboxAltText;" class="&alignVerticalMiddleClass;" />
					<xsl:text>&#x200B;</xsl:text>
					 <!-- HACK to make string-length evaluate to greater than 0 --> 
					<xsl:text><![CDATA[ ]]></xsl:text>
				</xsl:if>
				<!--<xsl:apply-templates/> Commented because, it is rendering data as duplicate for key Rules bug#199929-->
			</xsl:otherwise>
		</xsl:choose>
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="paratext[child::leader and text()]" priority="1">
		<xsl:call-template name="leaderContent">
			<xsl:with-param name="parent" select="." />
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="codes.para/head[child::label.designator]" priority="1" name="headWithlabelDesignator">
		<xsl:if test="not(following-sibling::paratext)">
			<xsl:apply-templates />
			<xsl:text><![CDATA[ ]]></xsl:text>
		</xsl:if>
	</xsl:template>

	<!--  We want to apply templates inside the headtext elements in codes.para in order to
				pick up the formatting (such as bolding). This template needs to override the
				headtext template in Head.xsl, which is why it has a high priority and a name
		-->
	<xsl:template match="codes.para//head/headtext" mode="para-label.designator" priority="1" name="codesParaHeadtextMode">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="codes.para//head/headtext" priority="1" name="codesParaHeadtext">
		<xsl:apply-templates />
	</xsl:template>

	<!--	There is a situation for KeyRules that calls for a checkbox image before the 
				paratext if there is an ancestor of checklist.block 
	-->
	
	<xsl:template name="renderParagraphTextDiv">
		<xsl:param name="divId"/>
		<xsl:param name="suppressLabel" select="false()"/>
		<xsl:param name="contents" />

		<xsl:variable name="hasContents">
			<xsl:call-template name="hasParagraphTextContents">
				<xsl:with-param name="contents" select="$contents"/>
			</xsl:call-template>
		</xsl:variable>

		<xsl:if test="$hasContents = 'true'">
			<div>
				<xsl:if test="string-length($divId) &gt; 0">
					<xsl:attribute name="id">
						<xsl:value-of select="$divId"/>
					</xsl:attribute>
				</xsl:if>
				<xsl:attribute name="class">
					<xsl:call-template name="getParatextMainClass"/>
					<xsl:choose>
						<xsl:when test="@*">
							<xsl:call-template name="addParaClasses"/>
						</xsl:when>
					</xsl:choose>
				</xsl:attribute>
				
				<!-- Copy With Reference - Paragraph Pinpointing -->
				<xsl:if test="name() = 'paratext' and not(child::pdc.number)">
					<xsl:variable name="textNodesWithParaSymbol" select=".//text()[contains(.,'&para;') and not(parent::cite.query)]" />
					<xsl:if test="$textNodesWithParaSymbol">
						<xsl:variable name="textNodeWithParaSymbol" select="substring(normalize-space($textNodesWithParaSymbol[1]),1,10)" />
						<xsl:if test="DocumentExtension:IsMatch($textNodeWithParaSymbol,'^[^\w&para;]*?&para;\s+?\d+[^:;.,]((\s)|$)')">
							<input type="hidden" class="&paraNumberTextMetadataItemClass;" value="{$textNodeWithParaSymbol}" alt="&metadataAltText;"/>
						</xsl:if>
					</xsl:if>
				</xsl:if>
				
				<xsl:choose>
					<xsl:when test="string-length(normalize-space($contents)) &gt; 0">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="RenderParaTextAttributes"/>
						<xsl:if test="not($suppressLabel)">
              <!-- Move to separate template to be able to redefine -->
              <xsl:call-template name="RenderParaLabel" />
						</xsl:if>
						<xsl:choose>
							<xsl:when test="leader">
								<xsl:call-template name="leaderContent">
									<xsl:with-param name="parent" select="." />
								</xsl:call-template>
							</xsl:when>
							<xsl:when test="self::text.line">
								<xsl:call-template name="wrapWithDiv">
									<xsl:with-param name="class" select="'&paraIndentLeftClass;'" />
									<xsl:with-param name="contents">
										<xsl:apply-templates />
									</xsl:with-param>
								</xsl:call-template>
							</xsl:when>
							<xsl:otherwise>
								<xsl:if test="ancestor::checklist.block">
									<img src="{$Images}&emptyCheckboxPath;" alt="&emptyCheckboxAltText;" class="&alignVerticalMiddleClass;" />
									<xsl:text>&#x200B;</xsl:text>
									<!-- HACK to make string-length evaluate to greater than 0 -->
									<xsl:text><![CDATA[ ]]></xsl:text>
								</xsl:if>
								<xsl:apply-templates/>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

	<xsl:template name="hasParagraphTextContents">
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test=".//text() or .//leader or string-length($contents) &gt; 0">
				<xsl:value-of select="true()"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="false()"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

  <xsl:template name="RenderParaLabel">
    <xsl:apply-templates select="preceding-sibling::node()[not(self::text())][1][self::label.designator or child::label.designator or self::label.name or child::label.name[not(parent::para)]]" mode="para-label.designator" />
  </xsl:template>
	
	<xsl:template name="RenderParaTextAttributes"/>

	<!-- This template is added so that products can override the class based on product business logic.-->
	<xsl:template name="getParatextMainClass">
		<xsl:text>&paratextMainClass;</xsl:text>
	</xsl:template>

	<!-- This outputs raw text, i.e. it is not contained in an attribute node-->
	<xsl:template name="addParaClasses">
		<xsl:if test="@style">
			<xsl:choose>
				<xsl:when test="@style = 'flush'">
					<xsl:text><![CDATA[ ]]>&paraFlushClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@style = 'first-line-indent'">
					<xsl:text><![CDATA[ ]]>&paraIndentFirstLineClass;1</xsl:text>
				</xsl:when>
				<xsl:when test="@style = 'block'">
					<xsl:text><![CDATA[ ]]>&paraBlockClass;</xsl:text>
				</xsl:when>
				<xsl:when test="@style = 'hanging'">
					<xsl:text><![CDATA[ ]]>&paraIndentHangingClass;1</xsl:text>
				</xsl:when>
			</xsl:choose>
		</xsl:if>
		<xsl:if test="@indent-left and @indent-left &gt; 0">
			<xsl:text><![CDATA[ ]]>&paraIndentLeftClass;</xsl:text>
			<xsl:value-of select="@indent-left"/>
		</xsl:if>
		<xsl:if test="@indent-right and @indent-right &gt; 0">
			<xsl:text><![CDATA[ ]]>&paraIndentRightClass;</xsl:text>
			<xsl:value-of select="@indent-right"/>
		</xsl:if>
		<xsl:if test="@indent-first-line and @indent-first-line &gt; 0">
			<xsl:text><![CDATA[ ]]>&paraIndentFirstLineClass;</xsl:text>
			<xsl:value-of select="@indent-first-line"/>
		</xsl:if>
		<xsl:if test="@indent-hanging and @indent-hanging &gt; 0">
			<xsl:text><![CDATA[ ]]>&paraIndentHangingClass;</xsl:text>
			<xsl:value-of select="@indent-hanging"/>
		</xsl:if>
		<xsl:if test="@align = 'center'">
			<xsl:text><![CDATA[ ]]>&alignHorizontalCenterClass;</xsl:text>
		</xsl:if>
	</xsl:template>

	<xsl:template match="para//para/paratext[count(begin.quote) = 1 and count(end.quote) = 1 and not(begin.quote/preceding-sibling::node()[not(self::starpage.anchor or self::footnote.reference or self::endnote.reference or self::table.footnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN')] or self::eos or self::bos or self::eop or self::bop)]) and not(end.quote/following-sibling::node()[not(self::starpage.anchor or self::footnote.reference or self::endnote.reference or self::table.footnote.reference or self::super[starts-with(normalize-space(text()), 'FN') or starts-with(normalize-space(text()), '[FN')] or self::eos or self::bos or self::eop or self::bop)])]" priority="1">
		<xsl:call-template name="renderParagraphTextDiv">
			<xsl:with-param name="contents">
				<blockquote>
					<div>
						<xsl:apply-templates/>
					</div>
				</blockquote>
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>
