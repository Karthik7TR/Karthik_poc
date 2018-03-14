<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.PLCCommon.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKGeneralBlocks.xsl"/>

	<xsl:variable name ="isArrangementDocument" select ="//n-docbody//locator_metadata/documentType ='&arrangementLocatorDocumentType;' or $infoType='&legisAOAInfoType;' or $infoType='&legisAOPInfoType;' or $infoType='&legisScottishAOAInfoType;' or $infoType='&legisScottishAOPInfoType;'"/>
	<xsl:variable name="isKeyLegalConceptsDocument" select="count(//note-version) > 0" />
	<xsl:variable name="isProvisionDocument" select="($isArrangementDocument=false()) and ($isKeyLegalConceptsDocument=false())"/>
	
	<xsl:variable name ="isFullTextDocument" select ="boolean(//fulltext_metadata)" />
	<xsl:variable name ="isDetailsDocument" select ="boolean(//locator_metadata)" />

	

	<xsl:variable name="documentType">
		<xsl:choose>
			<xsl:when test="$isFullTextDocument=true()">
				<xsl:call-template name="FullTextDocumentType"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="DetailsDocumentType"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>


	<xsl:template name="FullTextDocumentType">
		<xsl:variable name ="typeFacet" select ="//legislation-document-type-facet"/>
		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:value-of select="//metadata.block/md.descriptions/md.longtitle"/>
			</xsl:when>
			<xsl:when test="$typeFacet = '&billTypeFacet;'">
				<xsl:value-of select="'&legisTypeBill;'"/>
			</xsl:when>
			<xsl:when test="$typeFacet = '&actTypeFacet;'">
				<xsl:value-of select="'&legisTypeAct;'"/>
			</xsl:when>
			<xsl:when test="$typeFacet = '&siTypeFacet;'">
				<xsl:value-of select="'&legisTypeSI;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&legisTypeDocument;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:variable name="legislationType" select="//n-docbody/document/locator_metadata/legislationType" />
	<xsl:variable name ="legisType">
		<xsl:choose>
			<xsl:when test="
									$legislationType='&ukStatuteLegiType;' 
								or $legislationType='&scottishStatuteLegiType;' 
								or $legislationType='&welshStatuteLegiType;'  
								or $legislationType='&historicalScottishStatuteLegiType;' ">
				<xsl:value-of select="'&legisTypeAct;'"/>
			</xsl:when>
			<xsl:when test="
									$legislationType='&ukStatutoryInstrumentLegiType;' 
								or $legislationType='&scottishStatutoryInstrumentLegiType;' 
								or $legislationType='&welshStatutoryInstrumentLegiType;' 
								or $legislationType='&northernIrelandStatutoryInstrumentLegiType;'">
				<xsl:value-of select="'&legisTypeSI;'"/>
			</xsl:when>
			<xsl:when test="$legislationType='&westminsterBillLegiType;'">
				<xsl:value-of select="'&legisTypeBill;'"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="'&legisTypeDocument;'"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:variable>

	<xsl:template name="DetailsDocumentType">
		<xsl:choose>
			<xsl:when test="$isArrangementDocument=true()">
				<xsl:value-of select ="concat(concat('&arrangmentOfPrimaryMenu;',$legisType),'&detailsPrimaryMenu;')"/>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select ="concat($legisType,'&detailsPrimaryMenu;')"/>
			</xsl:otherwise>
		</xsl:choose>

	</xsl:template>


	<xsl:variable name="isArrangmentOfAct" select="$documentType=concat('&arrangmentOfPrimaryMenu;','&legisTypeAct;')" />
	<xsl:variable name="isArrangmentOfSI" select="$documentType=concat('&arrangmentOfPrimaryMenu;','&legisTypeSI;')" />
	<xsl:variable name="isArrangmentOfBill" select="$documentType=concat('&arrangmentOfPrimaryMenu;','&legisTypeBill;')" />
	<xsl:variable name="isArrangmentOfProvisions" select="$documentType=concat('&arrangmentOfPrimaryMenu;','&legisTypeProvisions;')" />

</xsl:stylesheet>
