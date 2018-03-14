<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:import href="Para.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>
	<!--For Carswell we should have 5 spaces for label.designator-->
	<xsl:template match="label.designator" mode="para-label.designator" priority="1">
		<xsl:apply-templates />
		<xsl:text>&crswLabelDessignatorSpace;</xsl:text>
	</xsl:template>

	<!--Still render the label.designator if we have no paratext -->
	<xsl:template match="para[string-length(paratext/text()) = 0 and string-length(label.designator/text()) &gt; 0]" name="crswParaWithoutParatext" priority="1">
		<xsl:variable name="labelDesignator" select="normalize-space(label.designator/text())"/>
		<xsl:variable name="paragraphText">
			<xsl:call-template name="renderParagraphTextDiv">
				<xsl:with-param name="contents" select="concat(label.designator,'&crswLabelDessignatorSpace;')"></xsl:with-param>
			</xsl:call-template>
		</xsl:variable>
		<xsl:call-template name="wrapWithDiv">
			<!-- I am using the substring-before function to get rid of all following spaces because the normalize-space doesn't remove all of them. -->
			<!-- The problem with the substring-before is that is doesn't return anything if there is no spaces, so I am adding a space to solve it. -->
			<xsl:with-param name="id" select="concat('&crswParagraphNumberPrefix;',substring-before(concat($labelDesignator,'&#160;'),'&#160;'))"/>
			<xsl:with-param name="class" select="'&crswLabeledParagraph;'"/>
			<xsl:with-param name="contents" select="$paragraphText"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template match="paratext[preceding-sibling::label.designator]" priority="1">
		<!-- Get the label designator -->
		<xsl:variable name="labelDesignator">
			<xsl:call-template name="GetLabelDesignator">
				<!-- Remove non-breaking spaces from label designator -->
				<xsl:with-param name ="label" select="substring-before(concat(normalize-space(preceding-sibling::label.designator),'&#160;'),'&#160;')"/>
			</xsl:call-template>
		</xsl:variable>
		<xsl:variable name="paragraphText">
			<xsl:call-template name="renderParagraphTextDiv" />
		</xsl:variable>

		<xsl:choose>
			<!-- Check if label designator is a number -->
			<xsl:when test="not(string(number($labelDesignator)) = 'NaN')">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="id" select="concat('&crswParagraphNumberPrefix;', $labelDesignator)"/>
					<xsl:with-param name="class" select="'&crswLabeledParagraph;'"/>
					<xsl:with-param name="contents" select="$paragraphText"/>
				</xsl:call-template>
			</xsl:when>
			<!-- Ignore non-numeric designator values -->
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&crswLabeledParagraph;'"/>
					<xsl:with-param name="contents" select="$paragraphText"/>
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="GetLabelDesignator">
		<xsl:param name="label"/>
		<xsl:choose>
			<xsl:when test="starts-with($label, '[')">
				<xsl:value-of select="normalize-space(substring-before(substring-after($label, '['), ']'))"/>
			</xsl:when>
			<xsl:when test="starts-with($label, '(')">
				<xsl:value-of select="normalize-space(substring-before(substring-after($label, '('), ')'))"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="$label"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="paratext[@style] | headtext[@style]">
		<xsl:choose>
			<xsl:when test="@style = 'r'">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&alignHorizontalRightClass;'" />
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@style = 'c'">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&alignHorizontalCenterClass;'" />
				</xsl:call-template>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="FormatParagraph">
		<xsl:choose>
			<xsl:when test="@align='center'">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass; &alignHorizontalCenterClass;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:when test="@align='right'">
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&alignHorizontalRightClass;'"/>
				</xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="wrapWithDiv">
					<xsl:with-param name="class" select="'&paraMainClass;'" />
				</xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="p" priority="1">
		<xsl:choose>
			<!-- If paragraph doesn't have any value add a non-breaking space to it -->
			<xsl:when test="(string-length(normalize-space(translate(self::*,'&#160;',' '))) = 0)">
				<div class="&paraMainClass;">&nbsp;</div>
			</xsl:when>
			<!--Carswell footnote cases-->
			<xsl:when test="sup/a[starts-with(@name, 'f') and starts-with(@href, '#r')] | 
								a[(starts-with(@name, 'f') and starts-with(@href, '#r')) or 
									(starts-with(@name,'N_') and substring(@name,string-length(@name)-1)='a_' and string-length(@href) = 0)
								 ]">
				<xsl:call-template name="FormatParagraph" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="FormatParagraph" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--Carswell override-->
	<xsl:template match="para | form.para | stat.para | codes.para | p" name="para">
		<xsl:param name="divId">
			<xsl:if test="string-length(@id|@ID) &gt; 0">
				<xsl:value-of select="concat('&internalLinkIdPrefix;', @id|@ID)"/>
			</xsl:if>
		</xsl:param>
		<xsl:param name="className" select="'&paraMainClass;'" />
		<xsl:if test="not(paratext/text() = ancestor::Document//md.uuid) and count(node()[not(self::label.name or self::label.designator)]) &gt; 0 and (.//text() or .//leader or .//image.block)">
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
				<xsl:choose>
					<xsl:when test="child::paragr">
						<xsl:call-template name="renderParagraphTextDiv">
							<xsl:with-param name="contents">
								<xsl:apply-templates/>
							</xsl:with-param>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</div>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
