<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2013: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited. -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.WLN.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">

	<xsl:output method="xml" indent="no" omit-xml-declaration="yes" />
	<xsl:preserve-space elements="*" />

	<!-- Group collections together and name them for the copyright number they should display. -->
	<xsl:variable name="HasCpyMsgNum794" select="contains('|w_3rd_bnataxco|', concat('|', /Document/document-data/collection, '|'))"/>

	<xsl:variable name="HasCpyMsgNum866" select="contains('|w_3rd_bnamanls|w_3rd_bnadtral|w_3rd_bnadtrrt|w_3rd_bnaebck|w_3rd_bnadenkc|w_3rd_erisaaid|w_3rd_erisagui|w_3rd_bna_bfdkc|w_3rd_hlball|w_3rd_bnamrep|w_3rd_erisafas|w_3rd_erisapay|w_3rd_bnaerisa|w_3rd_bnapaynl|w_3rd_bna|w_3rd_bna_bfd |w_3rd_pvlr|w_3rd_bnatpi|w_3rd_aitmbna|w_3rd_bnaapp|w_3rd_tmirsadv|w_3rd_bnaad|w_3rd_bnaadmeq|w_3rd_bnaadmon|w_3rd_bnadir|w_3rd_bnaeeoc|w_3rd_bnafep|w_3rd_bnafepk|w_3rd_bnaierk|w_3rd_bnalar|w_3rd_bnalrrm|w_3rd_bnamanl2|w_3rd_bnaunp|w_3rd_bnawh|', concat('|', /Document/document-data/collection, '|'))"/>

	<xsl:variable name="HasCpyMsgNum2045" select="contains('|w_3rd_bnaebl|', concat('|', /Document/document-data/collection, '|'))"/>

	<xsl:variable name="HasCpyMsgNum4588" select="contains('|w_3rd_bnaacct|w_3rd_tmegtser|w_3rd_bnainsight|w_3rd_tmforser|w_3rd_bnafpj|w_3rd_bnaintlj|w_3rd_tmirsprt|w_3rd_tmjnl|w_3rd_tmstaid|w_3rd_tmstser|w_3rd_bnainsight|w_3rd_tmstrdig|w_3rd_sttaxreg|w_3rd_tmjnl|w_3rd_bnatpr|w_3rd_tmincaid|w_3rd_tminc|w_3rd_taxovervw|w_3rd_tmmtr|', concat('|', /Document/document-data/collection, '|'))"/>

	<!-- Based on the collection groups above, return the copyright text.  If not in a collection group,   
			 default to 794.  For now, some of the copyright strings are identical. This is not a mistake. -->
	<xsl:template name="BNACopyrightMessage">
		<xsl:choose>
			<xsl:when test="$HasCpyMsgNum866">
				<xsl:text>&bureauOfNationalAffairsCopyrightMsg866;</xsl:text>
			</xsl:when>
			<xsl:when test="$HasCpyMsgNum2045">
				<xsl:text>&bureauOfNationalAffairsCopyrightMsg2045;</xsl:text>
			</xsl:when>
			<xsl:when test="$HasCpyMsgNum4588">
				<xsl:text>&bureauOfNationalAffairsCopyrightMsg4588;</xsl:text>
			</xsl:when>
			<xsl:when test="$HasCpyMsgNum794">
				<xsl:text>&bureauOfNationalAffairsCopyrightMsg794;</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:text>&bureauOfNationalAffairsCopyrightMsg794;</xsl:text>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
