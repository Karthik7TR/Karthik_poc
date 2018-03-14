<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2015: Thomson Reuters. All Rights Reserved. Proprietary and Confidential information of Thomson Reuters. Disclosure, Use or Reproduction without the written authorization of Thomson Reuters is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="GlobalParams.xsl"/>
	<xsl:include href="CustomFunctions.xsl"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="text()" name="SpecialCharacterTranslator">
		<xsl:param name="textToTranslate" select="." />
		<xsl:param name="notPreformatted" select="true()" />

		<!-- Replace an &amp;amp; with a valid &amp;. Ifed69c2cf1d911e18b05fdf15589d8e8 and Ifed69c55f1d911e18b05fdf15589d8e8 contain &amp;amp; in the title line. -->
		<xsl:variable name="textWithInvalidAmpSpaceSymbolReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textToTranslate" />
				<xsl:with-param name="pattern" select="'&amp;amp;'" />
				<xsl:with-param name="replacement" select="'&amp;'"/>
			</xsl:call-template>
		</xsl:variable>		

		<!-- Replace an &amp;AMP; with a valid &amp;. -->
		<xsl:variable name="textWithInvalidAMPSpaceSymbolReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidAmpSpaceSymbolReplaced" />
				<xsl:with-param name="pattern" select="'&amp;AMP;'" />
				<xsl:with-param name="replacement" select="'&amp;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Replace an &emsp; with blank. Ifed69c20f1d911e18b05fdf15589d8e8 contains &mdash; in the title line -->
		<xsl:variable name="textWithInvalidEMDashSymbolReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidAMPSpaceSymbolReplaced" />
				<xsl:with-param name="pattern" select="'&amp;mdash;'" />
				<xsl:with-param name="replacement" select="'&#8212;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an &emsp; with blank. I0b1eeb53e39611e08b05fdf15589d8e8 contains &emsp; in 2nd line cite -->
		<xsl:variable name="textWithInvalidEMSpaceSymbolReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidEMDashSymbolReplaced" />
				<xsl:with-param name="pattern" select="'&amp;emsp;'" />
				<xsl:with-param name="replacement" select="' '"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace an &sect; with a valid one. I0b1eeb53e39611e08b05fdf15589d8e8 contains &sect; in fixed header -->
		<xsl:variable name="textWithInvalidSectionSignReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidEMSpaceSymbolReplaced" />
				<xsl:with-param name="pattern" select="'&amp;sect;'" />
				<xsl:with-param name="replacement" select="'&#167;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Replace an invalid bullet entity with a valid one. I988b61b94ebd11de9b8c850332338889 contains &#159; for an invalid bullet -->
		<xsl:variable name="textWithInvalidBulletEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidSectionSignReplaced" />
				<xsl:with-param name="pattern" select="'&amp;#159;'" />
				<xsl:with-param name="replacement" select="'&#8226;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Replace an invalid bullet entity with a valid one. I13afb6981d9c11df9b8c850332338889 contains &#183; for an invalid bullet -->
		<xsl:variable name="textWithAnotherInvalidBulletEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithInvalidBulletEntityReplaced" />
				<xsl:with-param name="pattern" select="'&amp;#183;'" />
				<xsl:with-param name="replacement" select="'&#8226;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace a 'Middle Dot' entity with a hyphen as done in Web2. IF18F5C706FAB11E0BF19F7BCE048A5D4 is an example -->
		<xsl:variable name="textWithMiddleDotEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithAnotherInvalidBulletEntityReplaced" />
				<xsl:with-param name="pattern" select="'&#59101;'" />
				<xsl:with-param name="replacement" select="'&#45;'"/>
			</xsl:call-template>
		</xsl:variable>


		<!-- Replace an invalid quote entity with a valid one. IF7B750FB629B11DA97FAF3F66E4B6844 contains &QUOT; -->
		<xsl:variable name="textWithBadQuoteEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithMiddleDotEntityReplaced" />
				<xsl:with-param name="pattern" select="'&amp;QUOT;'" />
				<xsl:with-param name="replacement" select="'&quot;'"/>
			</xsl:call-template>
		</xsl:variable>
		
		<!-- Replace invalid double quote charater with a valid one. Id22bb2e0dabd11e398db8b09b4f043e0 contains &#x0093 and &#x0094 -->
		<xsl:variable name="textWithBadQuoteCharacterReplaced">
			<xsl:value-of select="translate($textWithMiddleDotEntityReplaced, '&#x0093;&#x0094;', '&#x0022;&#x0022;')"/>
		</xsl:variable>

		<!-- Replace an invalid greater entity with a valid one. I221ADD3FFF1B11DFAA23BCCC834E9520 contains &GT; -->
		<xsl:variable name="textWithBadGreaterThanEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithBadQuoteCharacterReplaced" />
				<xsl:with-param name="pattern" select="'&amp;GT;'" />
				<xsl:with-param name="replacement" select="'&gt;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Replace an invalid Lesser entity with a valid one. IF601331DF9B711E0A9E5BDC02EF2B18E, I81843dc07eea11e18b05fdf15589d8e8(PublicRecords Document) contains &LT; -->
		<xsl:variable name="textWithBadLessThanEntityReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithBadGreaterThanEntityReplaced" />
				<xsl:with-param name="pattern" select="'&amp;LT;'" />
				<xsl:with-param name="replacement" select="'&lt;'"/>
			</xsl:call-template>
		</xsl:variable>

		<!-- Odd characters to replaced with more normal characters (most are a form of spaces) -->
		<!-- The last character is the "zero-width space" character that should be removed completely... make sure it STAYS LAST! -->
		<xsl:variable name="textWithSpacesReplaced">
			<xsl:value-of select="translate($textWithBadLessThanEntityReplaced, '&specialCharactersToBeReplaced;', '&blankSpaceCharacters;')"/>
		</xsl:variable>

		<xsl:variable name="textWithControlCharactersReplaced">
			<xsl:choose>
				<xsl:when test="$notPreformatted">
					<!-- Reduce to normalized spaces -->
					<xsl:call-template name="normalize-space-without-trimming">
						<xsl:with-param name="string">
							<!-- The last two characters are respectively the "line feed" ("new line) and "carriage return" characters that should be removed completely... make sure they STAY LAST! -->
							<xsl:value-of select="translate($textWithSpacesReplaced, '&#x0009;&#x000A;&#x000D;', '&#x0020;')" />
						</xsl:with-param>
					</xsl:call-template>
				</xsl:when>
				<xsl:otherwise>
					<xsl:copy-of select="$textWithSpacesReplaced"/>
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Replace three one dot leaders with horizontal ellipsis.
				 Three dot leaders = Horizontal ellipsis.
				 The three one dot leaders render as hash tags in delivered documents.
				 The Horizontal ellipsis is converted to three full stop charachters: &#46; or &#x002E; -->
		<xsl:variable name="textWithThreeOneDotLeadersReplaced">
			<xsl:call-template name="replace">
				<xsl:with-param name="string" select="$textWithControlCharactersReplaced" />
				<xsl:with-param name="pattern" select="'&#x2024;&#x2024;&#x2024;'" />
				<xsl:with-param name="replacement" select="'&#x2026;'"/>
			</xsl:call-template>
			
		</xsl:variable>

		<!-- Odd checkboxes replaced with nice checkbox images -->
		<xsl:call-template name="replace">
			<xsl:with-param name="string" select="$textWithThreeOneDotLeadersReplaced" />
			<xsl:with-param name="pattern" select="'&#x2610;'" />
			<xsl:with-param name="replacement">
				<img src="{$Images}&emptyCheckboxPath;" alt="&emptyCheckboxAltText;" class="&alignVerticalMiddleClass;" />
				<xsl:text>&#x200B;</xsl:text>
				<!-- HACK to make string-length evaluate to greater than 0 -->
			</xsl:with-param>
		</xsl:call-template>
	</xsl:template>

</xsl:stylesheet>