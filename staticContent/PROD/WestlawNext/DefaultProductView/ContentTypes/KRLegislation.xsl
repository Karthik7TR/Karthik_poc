<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="SimpleContentBlocks.xsl"/>
	<xsl:include href="Cites.xsl"/>
	<xsl:include href="Title.xsl"/>
	<xsl:include href="PreformattedText.xsl"/>
	<xsl:include href="Footnotes.xsl"/>
	<xsl:include href="Prelim.xsl"/>
	<xsl:include href="Copyright.xsl"/>
	<xsl:include href="InternationalLogos.xsl"/>

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="Document">
		<div id="&documentClass;">
			<xsl:call-template name="AddDocumentClasses"/>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />

			<div class="&centerClass;">
				<xsl:apply-templates select="n-docbody/document/fulltext/paragraph/titles"/>
				<xsl:apply-templates select="n-docbody/copyright-message"/>
				<xsl:apply-templates select="n-docbody/document/fulltext_metadata/commencement/start-date"/>
				<xsl:apply-templates select="n-docbody/document/fulltext/paragraph/gazette"/>
				<xsl:apply-templates select="n-docbody/document/fulltext_metadata/image.block"/>
			</div>
			<xsl:comment>&EndOfDocumentHead;</xsl:comment>

			<div>
				<xsl:apply-templates select="n-docbody/document/fulltext/paragraph/title"/>
				<xsl:apply-templates select="n-docbody/document/fulltext/paragraph/para-text"/>
			</div>
			<div class="&paratextMainClass;">&#160;</div>
			<div>
				<xsl:apply-templates select="n-docbody/document/fulltext/paragraph/xref"/>
			</div>


			<!--
				******************************************************************************************************
				* Backlog Item 506268: 
				* Remove all logos from International content. 
				* Add copyright message from royality block and message block centered at the bottom of the document.
				******************************************************************************************************
			-->
			<xsl:apply-templates select="n-docbody/copyright-message"/>
			<xsl:apply-templates select="n-docbody/second-copyright-message"/>

			<div class="&alignHorizontalLeftClass;">
				<xsl:apply-templates select="n-metadata/metadata.block/md.identifiers/md.cites/md.primarycite" />
			</div>

			<xsl:call-template name="EndOfDocument">
				<xsl:with-param name="endOfDocumentCopyrightText">&internationalCopyrightText;</xsl:with-param>
			</xsl:call-template>
			<xsl:call-template name="DisplayInternationalPublisherLogo" />
		</div>
	</xsl:template>


	<xsl:template match="md.primarycite">
		<xsl:apply-templates />
	</xsl:template>


	<xsl:template match="fulltext/paragraph/titles">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="fulltext/paragraph/title">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
		<div>&nbsp;</div>
	</xsl:template>


	<xsl:template match="prelim.title">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--Since much of the data is duplicated, do not display the ordinance.title -->
	<!--<xsl:template match="document.ordinance.title" />-->
	<xsl:template match="document.ordinance.title">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="chapter.title">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="subchapter.title">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="second-copyright-message">
		<div class="&centerClass;">
			<xsl:call-template name="copyrightBlock">
				<xsl:with-param name="copyrightNode" select="." />
			</xsl:call-template>
		</div>
	</xsl:template>

	<xsl:template match="fulltext_metadata/commencement/start-date">
		<div class="&paratextMainClass;">&#160;</div>
		<div>
			<xsl:value-of select="@label" />
		</div>
		<div>
			<xsl:variable name="date">
				<xsl:value-of select="."/>
			</xsl:variable>
			<!--Day -->
			<xsl:value-of select="substring($date, 7, 2)"/>
			<xsl:text>&#160;</xsl:text>
			<!--Month -->
			<xsl:variable name="month" select="substring($date, 5, 2)"/>
			<xsl:choose>
				<xsl:when test="$month=01">January</xsl:when>
				<xsl:when test="$month=02">February</xsl:when>
				<xsl:when test="$month=03">March</xsl:when>
				<xsl:when test="$month=04">April</xsl:when>
				<xsl:when test="$month=05">May</xsl:when>
				<xsl:when test="$month=06">June</xsl:when>
				<xsl:when test="$month=07">July</xsl:when>
				<xsl:when test="$month=08">August</xsl:when>
				<xsl:when test="$month=09">September</xsl:when>
				<xsl:when test="$month=10">October</xsl:when>
				<xsl:when test="$month=11">November</xsl:when>
				<xsl:when test="$month=12">December</xsl:when>
			</xsl:choose>
			<!--<xsl:value-of select="month"/>-->
			<xsl:text>&#160;</xsl:text>
			<!-- Year -->
			<xsl:value-of select="substring($date, 1, 4)"/>
		</div>
		<div class="&paratextMainClass;">&#160;</div>
	</xsl:template>

	<xsl:template match="fulltext/paragraph/gazette">
		<xsl:if test="count(child::*)!=0 or .!=''">
			<div>
				<xsl:apply-templates />
			</div>
			<div class="&paratextMainClass;">&#160;</div>
		</xsl:if>
	</xsl:template>

	<xsl:template match="gazette.label">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="gazette.text">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!-- TODO: Remove?
	<xsl:template match="fulltext_metadata/image.block">
		<xsl:apply-templates />
		
	</xsl:template>
-->

	<xsl:template match="fulltext/paragraph/para-text">
		<xsl:choose>
			<xsl:when test="count(child::*)=0 and .=''"></xsl:when>
			<xsl:when test="@source='remark'">
				<div class="&paraMainClass;">
					<div class="&paratextMainClass;">
						<xsl:apply-templates />
					</div>
				</div>
			</xsl:when>
			<xsl:when test="@source='docBody'">
				<div class="&paraMainClass;">
					<div class="&paratextMainClass;">
						<xsl:apply-templates />
					</div>
				</div>
			</xsl:when>
			<xsl:otherwise />
		</xsl:choose>
	</xsl:template>

	<xsl:template match="link">
		<xsl:apply-templates />
	</xsl:template>

	<!--******************Document Links***************************-->
	<!--
	* Match the first document link element that has the necessary attributes
	* Do not create a link if the child of another valid link
	-->

	<xsl:template match="xref[(@pubid and @wlserial)]">
		<xsl:call-template name="xrefLink" />
	</xsl:template>

	<!-- overrule the included cite.query template if the node will not create a valid link-->
	<xsl:template match="cite.query[not(@w-pub-number and @w-serial-number)]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="cite.query[ancestor::xref[@pubid and @wlserial]]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="xref[ancestor::cite.query[@w-pub-number and @w-serial-number]]">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="xref">
		<xsl:apply-templates />
	</xsl:template>

	<!--******** HK-LEGIS-AOA Type ********-->
	<xsl:template match="md.title">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="md.longtitle">
		<div class="&titleClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="pgroup-entry">
		<div>
			<xsl:apply-templates select="pgroup-number"/>
			<xsl:text>&nbsp;</xsl:text>
			<xsl:apply-templates select="pgroup-title"/>
		</div>
	</xsl:template>

	<xsl:template match="pgroup-title">

		<xsl:apply-templates />

	</xsl:template>
	<xsl:template match="pgroup-number">
		<xsl:apply-templates/>
	</xsl:template>

	<!--*********** HK-LEGISLOC Type **********-->
	<xsl:template match="heading">
		<div class="&headtextClass;">
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="content">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="entry/citation">
		<div>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<xsl:template match="inforce/title | historic/title">
		<div class="&headtextClass;">
			<xsl:attribute name="id">
				<xsl:value-of select="@anchor"/>
			</xsl:attribute>
			<xsl:apply-templates />
		</div>
	</xsl:template>

	<!--******************Document Links******************-->
	<xsl:template match="link[@tuuid]">
		<a>
			<xsl:attribute name="href">
				<xsl:call-template name="GetDocumentUrl">
					<xsl:with-param name="documentGuid" select="@tuuid"/>
				</xsl:call-template>
			</xsl:attribute>
			<xsl:value-of select="./text()"/>
		</a>
	</xsl:template>

	<xsl:template match="link[@tanchor]">
		<a>
			<xsl:attribute name="href">
				<xsl:text>#</xsl:text>
				<xsl:value-of select="@tanchor"/>
			</xsl:attribute>
			<xsl:value-of select="./text()"/>
		</a>
	</xsl:template>

</xsl:stylesheet>