<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>
	<xsl:include href="UkStatutesDocumentType.xsl"/>


	<xsl:template name="BuildDocumentHeaderContent">
		<xsl:choose>
			<xsl:when test="$isKeyLegalConceptsDocument=true()">
				<xsl:call-template name="BuildKeyLegalConceptsHeaderContent" />
			</xsl:when>
			<xsl:otherwise>
				<xsl:variable name="headings" select ="//n-docbody//headings" />
				<h1 class="&titleClass;">
					<xsl:choose>
						<xsl:when test="($isArrangementDocument = true()) and ($isArrangmentOfProvisions = false())">
							<xsl:call-template name ="CreateHeader"/>
						</xsl:when>
						<xsl:when test="$isArrangmentOfProvisions = true()">
							<xsl:call-template name ="CreateHeader">
								<xsl:with-param name="currentHeadingRank" select ="count($headings/heading)-1"/>
								<xsl:with-param name="headings" select ="$headings"/>
							</xsl:call-template>
						</xsl:when>
						<xsl:otherwise>
							<xsl:call-template name="BuildProvisionHeaderContent">
								<xsl:with-param name="headings" select ="$headings"/>
							</xsl:call-template>
						</xsl:otherwise>
					</xsl:choose>
				</h1>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="BuildLoggedOutDocumentHeaderContent">
		<xsl:call-template name="BuildDocumentHeaderContent" />
	</xsl:template>


	<xsl:template name="BuildProvisionHeaderContent">
		<xsl:param name="headings" select ="//n-docbody//headings"/>
		<xsl:variable name="title">
			<xsl:if test="$isDetailsDocument=true()">
				<xsl:value-of select="//md.longtitle/text()"/>
			</xsl:if>
			<xsl:value-of select="''"/>
		</xsl:variable>
		<xsl:call-template name ="CreateHeader">
			<xsl:with-param name="currentHeadingRank" select ="count($headings/heading)"/>
			<xsl:with-param name="currentHeadingText" select ="$title"/>
			<xsl:with-param name="headings" select ="$headings"/>
		</xsl:call-template>
	</xsl:template>

	<xsl:template name="BuildKeyLegalConceptsHeaderContent">
		<span class="&keyLegalConceptsPreHeaderClass;">&keyLegalConceptsPreHeaderText;</span>
		<h2 class="&titleClass;">
			<xsl:value-of select="//n-docbody//note-version/title"/>
		</h2>
		<xsl:call-template name="NoteVersionFirstPara" />
	</xsl:template>

	<xsl:template name="NoteVersionFirstPara">
		<xsl:apply-templates select="//note-version/para[1]/emphasis[1]" />
		<xsl:apply-templates select="//note-version/para[1]/external-link" />
		<xsl:if test="not(starts-with(//note-version/para[1]/emphasis[2], ';'))">
			<xsl:text>;&nbsp;</xsl:text>
		</xsl:if>
		<xsl:apply-templates select="//note-version/para[1]/emphasis[2]" />
	</xsl:template>

	<xsl:template match="note-version/para[1]">
	</xsl:template>




	<xsl:template name="CreateHeader">
		<xsl:param name="currentHeadingRank" select="1"/>
		<xsl:param name="currentHeadingText" select="''"/>
		<xsl:param name="headings" select ="//n-docbody//headings"/>

		<xsl:if test="$currentHeadingRank>1">
			<xsl:variable name="parentArrangementHeading" select ="$headings/heading[@rank=1]" />
			<xsl:call-template name ="CreateLink">
				<xsl:with-param name="docGuid" select="$parentArrangementHeading/link/@tuuid" />
				<xsl:with-param name="linkTitle">
					<xsl:call-template name="CreateHoverOver">
						<xsl:with-param name="headings" select="$headings" />
						<xsl:with-param name="currentRank" select="$currentHeadingRank" />
					</xsl:call-template>
				</xsl:with-param>
				<xsl:with-param name="linkText" select="$parentArrangementHeading" />
			</xsl:call-template>
			<br />
		</xsl:if>
		<xsl:choose>
			<xsl:when test="string-length($currentHeadingText) > 0">
				<xsl:value-of select="$currentHeadingText"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select ="$headings/heading[@rank=$currentHeadingRank]"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="CreateLink">
		<xsl:param name="docGuid"/>
		<xsl:param name="linkTitle"/>
		<xsl:param name="linkText" />
		<a>
			<xsl:attribute name="title">
				<xsl:value-of select ="$linkTitle"/>
			</xsl:attribute>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="$docGuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:value-of select="$linkText"/>
		</a>
	</xsl:template>


	<!--Create hover over message for header-->
	<xsl:template name="CreateHoverOver">
		<xsl:param name="headings"/>
		<xsl:param name="currentRank"/>
		<xsl:for-each select="$headings/heading">
			<xsl:sort select="@rank"/>
			<xsl:if test="./@rank &lt; $currentRank">
				<xsl:if test="position() &gt; 1">
					<xsl:text> &gt; </xsl:text>
				</xsl:if>
				<xsl:value-of select="."/>
			</xsl:if>
		</xsl:for-each>
	</xsl:template>


</xsl:stylesheet>
