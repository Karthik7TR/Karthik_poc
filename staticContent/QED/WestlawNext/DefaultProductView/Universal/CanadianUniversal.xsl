<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2014: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!-- I18n Completed  As Of 5/27/2014 -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Universal.xsl"/>
	<xsl:include href="CanadianDocLinks.xsl"/>
	<xsl:include href="CanadianImage.xsl"/>
	<xsl:include href="CanadianPara.xsl"/>
	<xsl:include href="CanadianGlobalParams.xsl"/>
	<xsl:include href="CanadianList.xsl"/>
	<xsl:include href="CanadianTable.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- For content delivery, in some instances styles will not apply to the appropriate element. 
       There is a problem with how FO interprets elements as empty and then applys styles to incorrect elements. 
       Adding a space forces recognition of a block and the styles get applied to the empty space -->
	<xsl:template name="ApplySpaceForDelivery">
		<xsl:if test="$DeliveryMode">
			&#160;
		</xsl:if>
	</xsl:template>

	<!-- Recursive function to traverse text and add non-breaking spaces.-->
	<xsl:template name="addNonBreakingSpacesForDelivery">
		<xsl:param name="str"/>
		<xsl:param name="maxLengthBeforeBreak"/>

		<xsl:choose>
			<xsl:when test="contains($str, ' ')">
				<!-- There is a space, we can traverse it -->
				<xsl:variable name="c1" select="substring-before($str,' ')"/>
				<xsl:variable name="c2" select="substring-after($str, ' ')"/>


				<xsl:call-template name="addNonBreakingSpacesForDelivery">
					<xsl:with-param name="str" select="$c1"/>
					<xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
				</xsl:call-template>
				<xsl:if test="string-length($c2)>0">
					<xsl:text><![CDATA[ ]]></xsl:text>
					<xsl:call-template name="addNonBreakingSpacesForDelivery">
						<xsl:with-param name="str" select="$c2"/>
						<xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
					</xsl:call-template>
				</xsl:if>
			</xsl:when>
			<xsl:otherwise>
				<xsl:choose>
					<xsl:when test="string-length($str)>number($maxLengthBeforeBreak)">
						<xsl:call-template name="addNonBreakingSpaceToStrings">
							<xsl:with-param name="str" select="$str"/>
							<xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
						</xsl:call-template>
					</xsl:when>
					<xsl:otherwise>
						<!-- There is no space, and string is less than max characters before break -->
						<xsl:value-of select="$str"/>
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- A recursive function to add non breaking spaces for print -->
	<xsl:template name="addNonBreakingSpaceToStrings">
		<xsl:param name="str"/>
		<xsl:param name="maxLengthBeforeBreak"/>

		<xsl:if test="string-length($str) > 0">
			<xsl:variable name="c1" select="substring($str, 1, $maxLengthBeforeBreak)"/>
			<xsl:variable name="maxLengthPlusOne" select="number($maxLengthBeforeBreak) + number(1)"/>
			<xsl:variable name="c2" select="substring($str, number($maxLengthPlusOne))"/>

			<xsl:value-of select="$c1"/>
			<xsl:if test="$c2 != '' ">
				<xsl:text>&#8203;</xsl:text>
			</xsl:if>

			<xsl:call-template name="addNonBreakingSpacesForDelivery">
				<xsl:with-param name="str" select="$c2"/>
				<xsl:with-param name="maxLengthBeforeBreak" select="$maxLengthBeforeBreak"/>
			</xsl:call-template>
		</xsl:if>
	</xsl:template>

	<!--Do not show the text for images-->
	<xsl:template match="image.block/image.text" priority="1" />

	<xsl:template match="i">
		<em>
			<xsl:apply-templates />
		</em>
	</xsl:template>

	<xsl:template match="u">
		<span style="text-decoration:underline">
			<xsl:apply-templates />
		</span>
	</xsl:template>

	<xsl:template match="strike">
		<del>
			<xsl:apply-templates />
		</del>
	</xsl:template>

	<!-- Pre tags are preformatted HTML tags in content, 
       we need to not parse beyond them or XSLT will strip whitespace and line breaks -->
	<xsl:template match="pre">
		<!-- We need copy of to retain html elements -->
		<xsl:copy-of select="."/>
	</xsl:template>

	<xsl:template name="EndOfDocument" priority="1">
		<xsl:choose>
			<xsl:when test="$PreviewMode">
				<xsl:call-template name="AdditionalContent" />
				<xsl:if test="$DeliveryMode">
					<xsl:call-template name="LinkBackToDocDisplay" />
				</xsl:if>
			</xsl:when>
			<xsl:when test="not($EasyEditMode)">
				<table id="&endOfDocumentId;">
					<xsl:if test="$DeliveryMode">
						<tr>
							<td colspan="2" style="border-bottom: solid 1px #BBBBBB; width: 100%">&nbsp;</td>
						</tr>
					</xsl:if>
					<tr>
						<td>
							<xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&endOfDocumentTextKey;', '&endOfDocumentText;')"/>
						</td>
						<td class="&endOfDocumentCopyrightClass;">
							<xsl:value-of select="DocumentExtension:RetrieveLocaleValue($Language, '&staticTextPropertiesFile;', '&crswEndOfDocumentCopyrightTextKey;', '&crswEndOfDocumentCopyrightText;')"/>
						</td>
					</tr>
				</table>
			</xsl:when>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="AddProductDocumentClasses" priority="1">
		<xsl:text><![CDATA[ ]]></xsl:text>
		<xsl:text>&crswdocument;</xsl:text>
	</xsl:template>

</xsl:stylesheet>