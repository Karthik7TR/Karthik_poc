<?xml version="1.0" encoding="utf-8"?>
<!DOCTYPE stylesheet SYSTEM "../../DocumentXslt.DPA.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
	<xsl:include href="UKStatutes.xsl" forceDefaultProduct="true"/>
	<xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<!-- override to remove href link -->
	<xsl:template name="AnalysisLink">
			<div class="&centerClass;">
				<xsl:text>&ukSupersededLegis;</xsl:text>
				<br></br>
				<!--Analysis Link within the document, instead of an RI tab-->
				<xsl:if test="string-length($analysisDocGuid) &gt; 0">
					<xsl:text>&ukAnalysisLinkText;</xsl:text>
				</xsl:if>
			</div>
			<div>&#160;</div>
	</xsl:template>

	<!-- override to remove href link -->
	<xsl:template name ="parallelHeading">
		<div class="&centerClass; &paraMainClass;">
			<h2>
				<strong>
					<xsl:choose>
						<xsl:when test ="$appType='o'">
							<xsl:text>&ukTextProvisionVariesOnOtherApplication;</xsl:text>
						</xsl:when>
						<xsl:otherwise>
							<xsl:text>&ukParallelTextsRelatingTo;</xsl:text>
						</xsl:otherwise>
					</xsl:choose>
					<xsl:for-each select ="ancestor::n-docbody/descendant::fulltext">
						<xsl:if test ="preceding-sibling::fulltext[1]">
							<xsl:text> | </xsl:text>
						</xsl:if>
						<xsl:choose>
							<xsl:when test ="@application='o' and not (preceding-sibling::fulltext/@application='o')">
								<xsl:text>&ukOtherApplication;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='e'">
								<xsl:text>&ukEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='w'">
								<xsl:text>&ukWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='s'">
								<xsl:text>&ukScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='n'">
								<xsl:text>&ukNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ew'">
								<xsl:text>&ukEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='es'">
								<xsl:text>&ukEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='en'">
								<xsl:text>&ukEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sw'">
								<xsl:text>&ukScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sn'">
								<xsl:text>&ukScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='se'">
								<xsl:text>&ukScotlandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ws'">
								<xsl:text>&ukWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wn'">
								<xsl:text>&ukWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='we'">
								<xsl:text>&ukWalesAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swn'">
								<xsl:text>&ukScotlandWalesAndNorthrenIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swe'">
								<xsl:text>&ukScotlandWalesAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snw'">
								<xsl:text>&ukScotlandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sne'">
								<xsl:text>&ukScotlandNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sew'">
								<xsl:text>&ukScotlandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='sen'">
								<xsl:text>&ukScotlandEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ews'">
								<xsl:text>&ukEnglandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ewn'">
								<xsl:text>&ukEnglandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esw'">
								<xsl:text>&ukEnglandScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esn'">
								<xsl:text>&ukEnglandScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='enw'">
								<xsl:text>&ukEnglandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ens'">
								<xsl:text>&ukEnglandNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>

							<xsl:when test ="@application='ewsn'">
								<xsl:text>&ukEnglandWalesScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ewns'">
								<xsl:text>&ukEnglandWalesNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='eswn'">
								<xsl:text>&ukEnglandScotlandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='esnw'">
								<xsl:text>&ukEnglandScotlandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='enws'">
								<xsl:text>&ukEnglandNorthernIrelandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='ensw'">
								<xsl:text>&ukEnglandNorthernIrelandScotlandAndWales;</xsl:text>
							</xsl:when>

							<xsl:when test ="@application='wesn'">
								<xsl:text>&ukWalesEnglandScotlandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='_wens'">
								<xsl:text>&ukWalesEnglandNorthernIrelandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wsen'">
								<xsl:text>&ukWalesScotlandEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wsne'">
								<xsl:text>&ukWalesScotlandNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wnes'">
								<xsl:text>&ukWalesNorthernIrelandEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='wnse'">
								<xsl:text>&ukWalesNorthernIrelandScotlandAndEngland;</xsl:text>
							</xsl:when>
								
							<xsl:when test ="@application='sewn'">
								<xsl:text>&ukScotlandEnglandWalesAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='senw'">
								<xsl:text>&ukScotlandEnglandNorthernIrelandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swen'">
								<xsl:text>&ukScotlandWalesEnglandAndNorthernIreland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='swne'">
								<xsl:text>&ukScotlandWalesNorthernIrelandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snew'">
								<xsl:text>&ukScotlandNorthernIrelandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='snwe'">
								<xsl:text>&ukScotlandNorthernIrelandWalesAndEngland;</xsl:text>
							</xsl:when>
								
							<xsl:when test ="@application='news'">
								<xsl:text>&ukNorthernIrelandEnglandWalesAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nesw'">
								<xsl:text>&ukNorthernIrelandEnglandScotlandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nwes'">
								<xsl:text>&ukNorthernIrelandWalesEnglandAndScotland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nwse'">
								<xsl:text>&ukNorthernIrelandWalesScotlandAndEngland;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nsew'">
								<xsl:text>&ukNorthernIrelandScotlandEnglandAndWales;</xsl:text>
							</xsl:when>
							<xsl:when test ="@application='nswe'">
								<xsl:text>&ukNorthernIrelandScotlandWalesAndEngland;</xsl:text>
							</xsl:when>
						</xsl:choose>
					</xsl:for-each>
				</strong>
			</h2>
		</div>
	</xsl:template>

</xsl:stylesheet>
