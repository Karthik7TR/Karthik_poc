<?xml version="1.0" encoding="utf-8"?>
<!-- Copyright 2012: Thomson Reuters Global Resources. All Rights Reserved. Proprietary and Confidential information of TRGR. Disclosure, Use or Reproduction without the written authorization of TRGR is prohibited -->
<!DOCTYPE stylesheet SYSTEM "../DocumentXslt.dtd">
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">
  <xsl:include href="GlobalParams.xsl"/>
  <xsl:output method="xml" indent="no" omit-xml-declaration="yes"/>

	<xsl:template match="/Document/document-data/title//n-private-char">
		<xsl:choose>
			<xsl:when test="@charName = 'TLRkey'">
				<xsl:text>k</xsl:text>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="nonMetadataNPrivateChars" />
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
	
  <!-- N-Private-Char -->
  <xsl:template match="n-private-char" name="nonMetadataNPrivateChars">
    <xsl:choose>
      <xsl:when test="@charName = 'TLRAEligscs'">
        <span class="&smallCapsClass;">
          <xsl:text >&aelig;</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="@charName = 'TLRAEligscss'">
        <span class="&smallCapsClass;">
          <xsl:text >&aelig;</xsl:text>
        </span>
      </xsl:when>
      <xsl:when test="@charName = 'TLRAEligss'">
        <xsl:text >&AElig;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRaeligss'">
        <xsl:text >&aelig;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRand'">
        <sup>&#x2227;</sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRbkter'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRblbar'">
        <xsl:text>_</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRbrowse'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRbull1'">
        <xsl:text >&bull;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRbull3'">
        <xsl:text >&bull;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRbull4'">
        <xsl:text >&bull;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRbull7'">
        <xsl:text >&bull;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcents'">
        <xsl:text >&cent;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcentsb'">
        <strong>
          <xsl:text >&cent;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcentss'">
        <xsl:text >&cent;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcentssb'">
        <strong>
          <xsl:text >&cent;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcentssbR'">
        <sup>
          <strong>
            <xsl:text >&cent;</xsl:text>
          </strong>
        </sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcopys'">
        <sup>
          <xsl:text >&copy;</xsl:text>
        </sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcopysrr'">
        <sup>&#x24C5;</sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRcopyswr'">
        <sup>&#x24C2;</sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdarr'">
        <xsl:text>&#x2193;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdarr1'">
        <xsl:text>&#x2193;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdarr1f'">
        <xsl:text>&#x2193;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdash'">
        <xsl:text>-</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdashb'">
        <strong>-</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdashl'">
        <xsl:text >&ndash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdeg'">
        <xsl:text >&deg;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdiameb'">
        <strong>&#x00F8;</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdisk'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRdivide'">
        <strong>
          <xsl:text >&divide;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdollarssR'">
        <sup><strong>$</strong></sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRdotR'">
        <xsl:text>&#x02D9;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp1c'">
        <xsl:text>&#x00D8;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp1l'">
        <xsl:text>&#x00F8;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp2c'">
        <xsl:text>&#x00D8;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp2l'">
        <xsl:text>&#x00F8;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp3c'">
        <strong>&#x00D8;</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemp3l'">
        <strong>&#x00F8;</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRemptyb'">
        <strong>&#x00D8;</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRequals'">
        <strong>=</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflorin'">
        <em>f</em>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr0'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr2'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr3'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr4'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr5'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr6'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRflr7'">
        <xsl:text >&mdash;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRfrslab'">
        <strong>/</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRgt'">
        <strong>
          <xsl:text >&gt;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRgtl'">
        <xsl:text >&gt;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRincare'">
        <xsl:text>&#x2105;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRintbS'">
        <xsl:text>!?</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRinvT'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRKey'">
        <!-- Note the capital 'K' on 'TLRKey', this refers to a larger key than the normal 'TLRkey' -->
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRkey'">
        <xsl:choose>
          <xsl:when test="ancestor::key or ancestor::prior.classification">
            <xsl:text>k</xsl:text>
          </xsl:when>					
          <xsl:when test="ancestor::cite.query">
            <img src="{$Images}&digestKeyPath;" alt="&keyAltText;" class="&digestKeyClass; &alignVerticalMiddleClass;" />
						<xsl:text>&#x200B;</xsl:text>  <!-- HACK to make string-length evaluate to greater than 0 -->
          </xsl:when>
          <xsl:otherwise>
            <img src="{$Images}&referenceKeyPath;" alt="&keyAltText;" class="&referenceKeyClass; &alignVerticalMiddleClass;" />
						<xsl:text>&#x200B;</xsl:text>  <!-- HACK to make string-length evaluate to greater than 0 -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="@charName = 'TLRkeydecimal'">
        <xsl:choose>
          <xsl:when test="ancestor::key or ancestor::prior.classification">
            <xsl:text>.</xsl:text>
          </xsl:when>
          <xsl:when test="ancestor::cite.query">
            <img src="{$Images}&digestKeyPath;" alt="&keyAltText;" class="&digestKeyClass; &alignVerticalMiddleClass;" />
						<xsl:text>&#x200B;</xsl:text>  <!-- HACK to make string-length evaluate to greater than 0 -->
          </xsl:when>
          <xsl:otherwise>
            <img src="{$Images}&referenceKeyPath;" alt="&keyAltText;" class="&referenceKeyClass; &alignVerticalMiddleClass;" />
						<xsl:text>&#x200B;</xsl:text>  <!-- HACK to make string-length evaluate to greater than 0 -->
          </xsl:otherwise>
        </xsl:choose>
      </xsl:when>
      <xsl:when test="@charName = 'TLRlarr1'">
        <xsl:text>&#x2190;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRlarr1f'">
        <xsl:text>&#x2190;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRlarrL'">
        <xsl:text>&#x2190;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRlcub'">
        <xsl:text>{</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRlt'">
        <strong>
          <xsl:text >&lt;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRltl'">
        <xsl:text >&lt;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRltri'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRminus'">
        <strong>-</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRnev'">
        <xsl:text>&#x2262;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRngtV'">
        <xsl:text>&#x226F;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLROEligscs'">
        <span class="&smallCapsClass;">&#x0153;</span>
      </xsl:when>
      <xsl:when test="@charName = 'TLROEligscss'">
        <span class="&smallCapsClass;">&#x0153;</span>
      </xsl:when>
      <xsl:when test="@charName = 'TLROEligss'">
        <xsl:text>&#x0152;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRoeligss'">
        <xsl:text>&#x0153;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRor'">
        <sup>&#x2228;</sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRosqu5'">
        <xsl:text><![CDATA[ ]]></xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRpesoss'">
        <xsl:text>&#x20B1;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRplus'">
        <strong>+</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRplusmn'">
        <xsl:text >&plusmn;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRpoundssm'">
        <xsl:text >&pound;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRrarr1'">
        <xsl:text>&#x2192;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRrarrL'">
        <xsl:text>&#x2192;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRrcub'">
        <xsl:text>}</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRregs'">
        <sup>
          <xsl:text >&reg;</xsl:text>
        </sup>
      </xsl:when>
      <xsl:when test="@charName = 'TLRrtri'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRschwab'">
        <strong>&#x0259;</strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRschwabi'">
        <strong>
          <em>&#x0259;</em>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRschwai'">
        <em>&#x0259;</em>
      </xsl:when>
      <xsl:when test="@charName = 'TLRschwar'">
        <xsl:text>&#x0259;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRscisl'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRService'">
        <xsl:text>&#x2120;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRsetmn'">
        <xsl:text>\</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRsimev'">
        <xsl:text>&#x2243;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRstarfb'">
        <xsl:text>*</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRszlig'">
        <xsl:text >&szlig;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRter'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRtimes'">
        <strong>
          <xsl:text >&times;</xsl:text>
        </strong>
      </xsl:when>
      <xsl:when test="@charName = 'TLRtrade'">
        <xsl:text >&trade;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRtradesc'">
        <xsl:text >&trade;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRuarr'">
        <xsl:text>&#x2191;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRuarr1'">
        <xsl:text>&#x2191;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRuarr1f'">
        <xsl:text>&#x2191;</xsl:text>
      </xsl:when>
      <xsl:when test="@charName = 'TLRWP1'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRWP2'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:when test="@charName = 'TLRWP2tm'">
        <!-- Suppress -->
      </xsl:when>
      <xsl:otherwise>
        <xsl:apply-templates />
      </xsl:otherwise>
    </xsl:choose>
  </xsl:template>
  
</xsl:stylesheet>
