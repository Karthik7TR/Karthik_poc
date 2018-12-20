#!/bin/bash

echo "************Files to replace namespace*********" > log.txt   
#grep -r '<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">' * >> log.txt
find . -name "*.xsl" | xargs sed -i 's@<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">\|<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="urn:citeQuery" extension-element-prefixes="CiteQuery">\|<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="urn:citeQuery" xmlns:UrlBuilder="urn:urlBuilder" extension-element-prefixes="CiteQuery UrlBuilder">\|<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:DocumentExtension="urn:documentExtension">\|<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:UrlBuilder="urn:urlBuilder" extension-element-prefixes="UrlBuilder">@<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:CiteQuery="xalan://com.thomsonreuters.uscl.ereader.format.links.CiteQueryAdapter" xmlns:UrlBuilder="xalan://com.thomsonreuters.uscl.ereader.format.links.UrlBuilderAdapter" xmlns:DocumentExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.DocumentExtensionAdapter" xmlns:CitationLinkExtension="xalan://com.thomsonreuters.uscl.ereader.format.text.CitationLinkExtensionAdapter" extension-element-prefixes="DocumentExtension CiteQuery UrlBuilder CitationLinkExtension">@g' 


echo "Convert include to import " >> log.txt
sed -i 's@<xsl:include href="Universal.xsl"@<xsl:import href="Universal.xsl"@g' WestlawNext/DefaultProductView/Universal/Universal.xsl
sed -i 's@<xsl:include href="Universal.xsl"@<xsl:import href="Universal.xsl"@g' WestlawNext/DefaultProductView/ContentBlocks/Cites.xsl
sed -i 's@<xsl:include href="Universal.xsl"@<xsl:import href="Universal.xsl"@g' WestlawNext/DefaultProductView/ContentTypes/SelfRegulatoryOrganization.xsl
sed -i 's@<xsl:include href="Universal.xsl"@<xsl:import href="Universal.xsl"@g' Platform/ContentBlocks/Analysis.xsl
sed -i 's@<xsl:include href="Universal.xsl"@<xsl:import href="Universal.xsl"@g' Platform/ContentBlocks/Date.xsl
sed -i 's@<xsl:include href="DocLinks.xsl"@<xsl:import href="DocLinks.xsl"@g' WestlawNext/DefaultProductView/Universal/DocLinks.xsl
sed -i 's@<xsl:include href="WrappingUtilities.xsl"@<xsl:import href="WrappingUtilities.xsl"@g' WestlawNext/DefaultProductView/Universal/WrappingUtilities.xsl

sed -i '/<xsl:include href="StarPages.xsl" forcePlatform="true" \/>/d' WestlawNext/DefaultProductView/Universal/StarPages.xsl
sed -i '/<xsl:stylesheet/a  \
\t<xsl:import href="StarPages.xsl" forcePlatform="true" \/>' WestlawNext/DefaultProductView/Universal/StarPages.xsl

sed -i '/<xsl:include href="Footnotes.xsl" forcePlatform="true" \/>/d' WestlawNext/DefaultProductView/ContentBlocks/Footnotes.xsl
sed -i '/<xsl:stylesheet/a  \
\t<xsl:import href="Footnotes.xsl" forcePlatform="true" \/>' WestlawNext/DefaultProductView/ContentBlocks/Footnotes.xsl

sed -i '/<xsl:include href="FootnoteBlock.xsl" forcePlatform="true" \/>/d' WestlawNext/DefaultProductView/ContentBlocks/FootnoteBlock.xsl
sed -i '/<xsl:stylesheet/a  \
\t<xsl:import href="FootnoteBlock.xsl" forcePlatform="true" \/>' WestlawNext/DefaultProductView/ContentBlocks/FootnoteBlock.xsl

echo "Replace string in Suppressed.xsl" >> log.txt
sed -i '/<xsl:template match="grade.content.section\/grade.notes"\/>/d' Platform/Universal/Suppressed.xsl
sed -i '/<xsl:template match="ed.note.grade"\/>/d' Platform/Universal/Suppressed.xsl

echo "Replace string in AddedDeletedMaterial.xsl">> log.txt
sed -i 's@<xsl:template match="added.material" priority="1">@<xsl:template match="added.material | centa" priority="1">@g' WestlawNext/DefaultProductView/ContentBlocks/AddedDeletedMaterial.xsl
sed -i 's@<xsl:template match="deleted.material" priority="1">@<xsl:template match="deleted.material | centd" priority="1">@g' WestlawNext/DefaultProductView/ContentBlocks/AddedDeletedMaterial.xsl


echo "Replace string in Annotations.xsl">> log.txt
sed -i 's@<div class="&printHeadingClass;">@<div eBookEditorNotes="true" class="\&printHeadingClass;">@g' Platform/ContentBlocks/Annotations.xsl

echo "Add string in AnalyticalTreatisesAndAnnoCodes.xsl">> log.txt
sed -i '/<\/xsl:stylesheet>/i \
\t<!-- suppress the label.designator when within codes.para --> \
\t<xsl:template match="codes.para\/head[following-sibling::node()[1][self::paratext] and child::label.designator]" \/>' WestlawNext/DefaultProductView/ContentTypes/AnalyticalTreatisesAndAnnoCodes.xsl

echo "Add string in AnalyticalTreatisePracticeGuides.xsl">> log.txt
sed -i '/<\/xsl:stylesheet>/i \
\t<!-- suppress the label.designator when within codes.para --> \
\t<xsl:template match="codes.para\/head[following-sibling::node()[1][self::paratext] and child::label.designator]" \/>' WestlawNext/DefaultProductView/ContentTypes/AnalyticalTreatisePracticeGuides.xsl


echo "Replace string in Leader.xsl">> log.txt
sed -i "1,/<\/xsl:variable>/ {/<\/xsl:variable>/a \
<xsl:if test=\"\$contents = '.' or \$contents = '_' or \$contents = '-'\">\n\t<xsl:attribute name=\"style\">\n\t\t<xsl:text>opacity:0</xsl:text>\n\t</xsl:attribute>\n</xsl:if>
}" Platform/Universal/Leader.xsl
sed -i "/<xsl:if test =\"\$contents =','\">/i \
<xsl:if test=\"\$contents = '.' or \$contents = '_' or \$contents = '-'\">\n\t<xsl:attribute name=\"style\">\n\t\t<xsl:text>opacity:0</xsl:text>\n\t</xsl:attribute>\n</xsl:if>" Platform/Universal/Leader.xsl

echo "Replace string in HistoryNotes.xsl">> log.txt
sed -i '/<xsl:template match="annotations\/hist.note.block\/\*\[not(.\/\/N-HIT or .\/\/N-LOCATE or .\/\/N-WITHIN)]" \/>/d' Platform/ContentBlocks/HistoryNotes.xsl

echo "Remove duplicated currency from AnalyticalALRIndex.xsl">> log.txt
sed -i 's/<xsl:apply-templates select="\/\/cmd.currency.default" mode="cmdCurrency"\/>//g' WestlawNext/DefaultProductView/ContentTypes/AnalyticalALRIndex.xsl

echo "Add Footnotes.xsl to AnalyticalALRIndex.xsl">> log.txt
sed -i '/<xsl:include href="Title.xsl"\/>/i \
\t<xsl:include href="Footnotes.xsl"\/>' WestlawNext/DefaultProductView/ContentTypes/AnalyticalALRIndex.xsl

echo "Add styles to hide duplicated elements in AnalyticalALRIndex books">> log.txt
sed -i '/\.co_analyticalALR \.co_divide {/i \
\.co_docDisplay>#coid_website_documentWidgetDiv>\.co_analyticalALRIndex>\.co_section>\.co_contentBlock>\.co_headtext { \
\ttext-align: left; }' document.css

echo "Replace absolute line-height with relative">> log.txt
sed -i 's@line-height: 17px; }@line-height: 1em; }@g' document.css

echo "Remove strings from 'xsl:if' in HistoryNotes.xsl" >> log.txt
sed -i '/<div class="&historyNotesClass; &disableHighlightFeaturesClass;" id="&historyNotesId;">/d' WestlawNext/DefaultProductView/ContentBlocks/HistoryNotes.xsl
sed -i '/<xsl:apply-templates select="head\/head.info\/headtext"\/>/d' WestlawNext/DefaultProductView/ContentBlocks/HistoryNotes.xsl
sed -i '/<xsl:apply-templates select="hist.note.body" \/>/d' WestlawNext/DefaultProductView/ContentBlocks/HistoryNotes.xsl
sed -i '/<\/div>/d' WestlawNext/DefaultProductView/ContentBlocks/HistoryNotes.xsl
sed -i '/<\/xsl:template>/i \
\<div class="&historyNotesClass; &disableHighlightFeaturesClass;" id="&historyNotesId;"> \
\t<xsl:apply-templates select="head/head.info/headtext" \/> \
\t<xsl:apply-templates select="hist.note.body" \/> \
\<\/div>' WestlawNext/DefaultProductView/ContentBlocks/HistoryNotes.xsl

echo "Comment out h2 in Head.xsl" >> log.txt
sed -i '/<h2>/i \
\t<!--' Platform/Universal/Head.xsl
sed -i '/<\/h2>/a \
\t-->' Platform/Universal/Head.xsl

echo "Add head condition in Head.xsl" >> log.txt
sed -i "s/<xsl:template match=\"head | prop.head | form.head | fa.head\" name=\"head\">/<xsl:template match=\"head[not(following-sibling::paratext)] | prop.head | form.head | fa.head\" name=\"head\">/g"

echo "Remove duplicated templates" >> log.txt
sed -i 's/<xsl:template name="historicalStatutesAndRegulationsHeader" \/>//g' Platform/Universal/HistoricalHeader.xsl
sed -i 's/<xsl:template name="additionalResourcesHeader">/<xsl:template name="additionalResourcesHeader_unduplicated">/g' WestlawNext/DefaultProductView/ContentBlocks/SimpleContentBlocks.xsl
sed -i 's/<xsl:call-template name="additionalResourcesHeader"/<xsl:call-template name="additionalResourcesHeader_unduplicated"/g' WestlawNext/DefaultProductView/ContentBlocks/SimpleContentBlocks.xsl

sed -i 's/<xsl:template match="analysis" name="analysis">/<xsl:template match="analysis" name="analysis_unduplicated">/g' WestlawNext/DefaultProductView/ContentBlocks/Analysis.xsl
sed -i 's/<xsl:template name="additionalResourcesHeader">/<xsl:template name="additionalResourcesHeader_unduplicated">/g' WestlawNext/DefaultProductView/ContentBlocks/SimpleContentBlocks/SimpleContentBlocks_Base.xsl
sed -i 's/<xsl:template match="doc.title" name="docTitle" priority="2">/<xsl:template match="doc.title" name="docTitle_unduplicated" priority="2">/g' WestlawNext/DefaultProductView/ContentTypes/CommentaryOConnors.xsl
sed -i 's/<xsl:template name="renderSectionFrontDocTitle">/<xsl:template name="renderSectionFrontDocTitle_unduplicated">/g' WestlawNext/DefaultProductView/ContentTypes/CommentaryOConnors.xsl

echo "Add missing templates" >> log.txt
sed -i 's/<\/xsl:stylesheet>/\
	<xsl:template match="author.line | author.block" name="author">\
		<xsl:call-template name="wrapContentBlockWithCobaltClass" \/>\
	<\/xsl:template>\
    \
	<xsl:template match="research.references" name="researchReferences">\
		<xsl:call-template name="wrapContentBlockWithCobaltClass">\
			<xsl:with-param name="id">\
				<xsl:if test="@ID">\
					<xsl:value-of select="concat('"'"'\&internalLinkIdPrefix;'"'"',@ID)" \/>\
				<\/xsl:if>\
			<\/xsl:with-param>\
		<\/xsl:call-template>\
	<\/xsl:template>\
    \
&/g' WestlawNext/DefaultProductView/ContentTypes/Commentary.xsl

echo "Replace ampersands" >> log.txt
sed -i 's/&P/\&amp;P/g' ContentTypeMapData.xml
sed -i 's/&A/\&amp;A/g' ContentTypeMapData.xml
sed -i 's/& /\&amp; /g' ContentTypeMapData.xml

echo "**************Done**********" >> log.txt
