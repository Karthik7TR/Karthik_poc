package com.thomsonreuters.uscl.ereader.format.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCopyright;
import com.thomsonreuters.uscl.ereader.core.book.domain.DocumentCurrency;
import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.XMLContentChangerFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Applies any preprocess transformation on the XML that need to be done to add mark ups
 * or content to the documents
 *
 * @author <a href="mailto:Dong.Kim@thomsonreuters.com">Dong Kim</a> u0155568
 */
public class XMLPreprocessServiceImpl implements XMLPreprocessService {
    private static final Logger LOG = LogManager.getLogger(XMLPreprocessServiceImpl.class);

    private FileHandlingHelper fileHandlingHelper;

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper) {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    /**
     * This method applies XMLFilters to the source XML to apply various
     * preprocess rules to the XML.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param isFinalStage detemines if content is from Final or Review stage
     * @param copyright list of DocumentCopyright used in filter to change copyright message
     * @param currencies list of DocumentCurrency used in filter to change currency message
     *
     * @return the number of documents that were preprocessed
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    @Override
    public int transformXML(
        final File srcDir,
        final File targetDir,
        final boolean isFinalStage,
        final List<DocumentCopyright> copyrights,
        final List<DocumentCurrency> currencies,
        final Set<String> pageVolumes) throws EBookFormatException {
        if (srcDir == null || !srcDir.isDirectory()) {
            throw new IllegalArgumentException("srcDir must be a directory, not null or a regular file.");
        }

        List<DocumentCopyright> copyCopyrights = null;
        List<DocumentCurrency> copyCurrencies = null;

        // Make a copy of the original to check that all have been accounted for
        if (copyrights != null) {
            copyCopyrights = new ArrayList<>(Arrays.asList(new DocumentCopyright[copyrights.size()]));
            Collections.copy(copyCopyrights, copyrights);
        }

        if (currencies != null) {
            copyCurrencies = new ArrayList<>(Arrays.asList(new DocumentCurrency[currencies.size()]));
            Collections.copy(copyCurrencies, currencies);
        }

        //retrieve list of all xml files that need preprocessing
        final List<File> xmlFiles = new ArrayList<>();

        try {
            fileHandlingHelper.getFileList(srcDir, xmlFiles);
        } catch (final FileNotFoundException e) {
            final String errMessage = "No xml files were found in specified directory. "
                + "Please verify that the correct path was specified.";
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }

        if (!targetDir.exists()) {
            targetDir.mkdirs();
        }

        LOG.info("Applying preprocess on xml files...");

        int numDocs = 0;
        for (final File xmlFile : xmlFiles) {
            transformXMLFile(xmlFile, targetDir, isFinalStage, copyrights, copyCopyrights, currencies, copyCurrencies, pageVolumes);
            numDocs++;
        }

        // Check all the guids has been accounted for
        final StringBuffer errMessage = new StringBuffer();

        if ((copyCopyrights != null) && (copyCopyrights.size() > 0)) {
            final StringBuffer unaccountedCopyrights = new StringBuffer();
            for (final DocumentCopyright copyright : copyCopyrights) {
                unaccountedCopyrights.append(copyright.getCopyrightGuid() + ",");
            }
            errMessage.append("Not all copyright guids are accounted for and those are ");
            errMessage.append(unaccountedCopyrights);
            errMessage.append(". ");
        }
        if ((copyCurrencies != null) && (copyCurrencies.size() > 0)) {
            final StringBuffer unaccountedCurrencies = new StringBuffer();
            for (final DocumentCurrency currency : copyCurrencies) {
                unaccountedCurrencies.append(currency.getCurrencyGuid() + ",");
            }
            errMessage.append("Not all currency guids are accounted for and those are ");
            errMessage.append(unaccountedCurrencies);
            errMessage.append(". ");
        }

        if (errMessage.length() > 0) {
            LOG.error(errMessage.toString());
            throw new EBookFormatException(errMessage.toString());
        }

        LOG.info("Preprocess successfully applied to " + numDocs + " files.");
        return numDocs;
    }

    /**
     * This method applies the various XMLFilter(s) to the passed in source file and generates
     * a new file in the target directory.
     *
     * @param sourceFile source file to be transformed
     * @param targetDir target directory where the resulting post transformation file is to be written
     * @param isFinalStage detemines if content is from Final or Review stage
     * @param copyrights list of DocumentCopyright used in filter to change copyright message
     * @param copyCopyrights list of DocumentCopyright used for validation
     * @param currencies list of DocumentCurrency used in filter to change currency message
     * @param copyCurrencies list of DocumentCurrency used validation
     *
     * @throws if any parsing/transformation exception are encountered
     */
    protected void transformXMLFile(
        final File sourceFile,
        final File targetDir,
        final boolean isFinalStage,
        final List<DocumentCopyright> copyrights,
        final List<DocumentCopyright> copyCopyrights,
        final List<DocumentCurrency> currencies,
        final List<DocumentCurrency> copyCurrencies,
        final Set<String> pageVolumes) throws EBookFormatException {
        final String fileName = sourceFile.getName();
        try (FileInputStream inStream = new FileInputStream(sourceFile)) {
            try (FileOutputStream outStream = new FileOutputStream(
                new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".preprocess"))) {
                final SAXParserFactory factory = SAXParserFactory.newInstance();
                factory.setNamespaceAware(true);
                final SAXParser saxParser = factory.newSAXParser();

                final XMLContentChangerFilter contentChangerFilter =
                    new XMLContentChangerFilter(copyrights, copyCopyrights, currencies, copyCurrencies, pageVolumes);
                contentChangerFilter.setParent(saxParser.getXMLReader());

                final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XML);
                props.setProperty("omit-xml-declaration", "yes");

                final Serializer serializer = SerializerFactory.getSerializer(props);
                serializer.setOutputStream(outStream);

                contentChangerFilter.setContentHandler(serializer.asContentHandler());
                contentChangerFilter.parse(new InputSource(inStream));

                LOG.debug("Successfully preprocessed:" + sourceFile.getAbsolutePath());
            }
        } catch (final IOException e) {
            final String errMessage = "Unable to perform IO operations related to following source file: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        } catch (final SAXException e) {
            final String errMessage = "Encountered a SAX Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        } catch (final ParserConfigurationException e) {
            final String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
    }
}
