<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="Xena2Shared.xsl"/>
	<xsl:include href="DisableAddedDeletedMaterial.xsl"/>
	<xsl:include href="NPrivateChar.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="StarPageMetadata" />
			<xsl:apply-templates select="n-docbody"/>
			<xsl:apply-templates select="message.block"/>
			<xsl:call-template name="EndOfDocument" />
		</div>
	</xsl:template>
	
	<xsl:template match="doc">
		<div class="&documentHeadClass;">
			<xsl:apply-templates select="st| so| hcb[not(preceding-sibling::hcb)] | hcb1 | hcb2 | hcb3 | hcb4 | dl | til | mx"/>
		</div>
		<xsl:apply-templates select="*[not(self::st or self::so or self::hcb1 or self::hcb or self::hcb2 or self::hcb3 or self::hcb4 or self::dl or self::til or self::mx)] | hcb[preceding-sibling::hcb]" />
	</xsl:template>

	<xsl:template match="internal.reference" priority="2">
		<xsl:param name="id" select="translate(@ID, ';', '')"/>
		<xsl:param name="refid" select="@refid" />
		<xsl:param name="additionalClass"/>
		<xsl:param name="contents" />
		<xsl:choose>
			<xsl:when test="key('allElementIds', $refid)">
				<span>
					<xsl:if test="string-length($id) &gt; 0">
						<xsl:attribute name="id">
							<xsl:value-of select="concat('&internalLinkIdPrefix;', translate($id, ';', ''))"/>
						</xsl:attribute>
					</xsl:if>
					<a href="{concat('#&internalLinkIdPrefix;', translate($refid, ';', ''))}">
						<xsl:attribute name="class">
							<xsl:text>&internalLinkClass;</xsl:text>
							<xsl:if test="string-length($additionalClass) &gt; 0">
								<xsl:text><![CDATA[ ]]></xsl:text>
								<xsl:value-of select="$additionalClass"/>
							</xsl:if>
						</xsl:attribute>
						<xsl:choose>
							<xsl:when test="string-length($contents) &gt; 0">
								<xsl:copy-of select="$contents"/>
							</xsl:when>
							<xsl:otherwise>
								<xsl:apply-templates />
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</span>
			</xsl:when>			
			<xsl:otherwise>
				<xsl:if test="string-length($id) &gt; 0">
					<a id="{concat('&internalLinkIdPrefix;', $id)}">
						<xsl:comment>anchor</xsl:comment>
					</a>
				</xsl:if>
				<xsl:choose>				
					<xsl:when test="string-length($contents) &gt; 0">
						<xsl:copy-of select="$contents"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:apply-templates />
					</xsl:otherwise>
				</xsl:choose>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="include.copyright" priority="5">
			<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="text()" priority="2">

		<!-- 
				Character Information: "▼" - BLACK DOWN-POINTING TRIANGLE
				Unicode Range: Geometric Shapes
				Unicode Binary: 0x25BC
				UCS-2 Binary: 0xBC25 							
				HTML Hexadecimal: &#x25BC;
				HTML Decimal: &#9660;
				
				This character renders as # in delivered documents.
				Thus, converting to bullet for delivery only.
		-->
		<xsl:variable name="textWithBlackDownPointingTriangleReplaced">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="."  />
						<xsl:with-param name="pattern" select="'&#9660;'" />
						<xsl:with-param name="replacement" select="'&#8226;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!-- 
				Character Information: "▶" - BLACK RIGHT-POINTING TRIANGLE
				Unicode Range: Geometric Shapes
				Unicode Binary: 0x25B6
				UCS-2 Binary: 0xB625 							
				HTML Hexadecimal: &#x25B6;
				HTML Decimal: &#9654;
				
				This character renders as # in delivered documents.
				Thus, converting to bullet for delivery only.				
		-->
		<xsl:variable name="textWithBlackRightPointingTriangleReplaced">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="$textWithBlackDownPointingTriangleReplaced" />
						<xsl:with-param name="pattern" select="'&#9654;'" />
						<xsl:with-param name="replacement" select="'&#8226;'"/>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>

		<!--
					Character Information: "․" - ONE DOT LEADER
					Unicode Range: General Punctuation
					Unicode Binary: 0x2024
					UCS-2 Binary: 0x2420
					HTML Hexadecimal: &#x2024;
					HTML Decimal: &#8228;
 
					Replacing three one dot leaders with horizontal ellipsis in delivered documents only.
					horizontal ellipsis = three dot leader 
					Calling global SpecialCharacter.xsl style sheet
		-->
		<xsl:variable name="textWithThreeOneDotLeadersReplaced">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:call-template name="SpecialCharacterTranslator">
						<xsl:with-param name="textToTranslate" select="$textWithBlackRightPointingTriangleReplaced" />
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!--
					Character Information: "□" - WHITE SQUARE
					Unicode Range: Geometric Shapes
					HTML Hexadecimal: &#x25A1;
					HTML Decimal: &#9633;
 
					Replacing white squares with square brackets [] in delivered documents only.
		-->
		<xsl:variable name="textWithWhiteSquareReplaced">
			<xsl:choose>
				<xsl:when test="$DeliveryMode">
					<xsl:call-template name="replace">
						<xsl:with-param name="string" select="$textWithThreeOneDotLeadersReplaced" />
						<xsl:with-param name="pattern" select="'&#x25A1;'" />
						<xsl:with-param name="replacement" select="'&#x005B;&#x005D;'"/>
					</xsl:call-template>	
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="."/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Replace an invalid checkbox entity with a valid one. I8a562cfa624d11dd935de7477da167c1 contains &#9724; for an invalid checkbox -->
		<xsl:call-template name="replace">
			<xsl:with-param name="string" select="$textWithWhiteSquareReplaced" />
			<xsl:with-param name="pattern" select="'&#9724;'" />
			<xsl:with-param name="replacement">
				<img src="{$Images}&checkedPath;" alt="&checkboxAltText;" class="&alignVerticalMiddleClass;" />
				<xsl:text>&#x200B; </xsl:text>
				<!-- HACK to make string-length evaluate to greater than 0 -->
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

	<!-- Making adjustments to the table to add padding-->
	<xsl:template match="tbl[/Document/document-data/collection = 'w_3rd_rvinsnd']" priority="2">
		<xsl:if test=".//text()">
			<div>
				<xsl:if test="@id or @ID">
					<xsl:attribute name="id">
						<xsl:value-of select="concat('&internalLinkIdPrefix;', @id | @ID)"/>
					</xsl:attribute>
					<xsl:attribute name="class">&extraPaddingClass;</xsl:attribute>
				</xsl:if>
				<xsl:apply-templates />
			</div>
		</xsl:if>
	</xsl:template>

	<!--Not to wrap the column contents when they are less than 10 chars long-->
	<xsl:template match="tbody/row/entry[string-length(descendant::paratext) &lt; 10 and /Document/document-data/collection = 'w_3rd_rvinsnd']" priority="2">
		<xsl:param name="columnInfo" />
		<xsl:param name="colalign" />
		<xsl:param name="colposition" />
		<xsl:param name="colwidth" />
		<td>
			<xsl:variable name="class">
				<xsl:text>&noWrapClass;</xsl:text>
			</xsl:variable>
			<xsl:call-template name="RenderTableCell">
				<xsl:with-param name="columnInfo" select="$columnInfo"/>
				<xsl:with-param name="colalign" select="$colalign" />
				<xsl:with-param name="colposition" select="$colposition" />
				<xsl:with-param name="colwidth" select="$colwidth" />
				<xsl:with-param name="class" select="$class" />
			</xsl:call-template>
		</td>
	</xsl:template>

	<xsl:template match="starpage.anchor" priority="3"/>

	<xsl:template match="n-private-char" priority="5">

		<xsl:choose>
			<xsl:when test="$DeliveryMode"></xsl:when>
			<xsl:otherwise>

				<xsl:choose>
					<xsl:when test="@charName='TLRKey' and not($DeliveryMode)">
						<xsl:value-of select="'&#xF231;'"/>
					</xsl:when>
					<xsl:otherwise>
						<xsl:call-template name="nonMetadataNPrivateChars" />
					</xsl:otherwise>
				</xsl:choose>

			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
