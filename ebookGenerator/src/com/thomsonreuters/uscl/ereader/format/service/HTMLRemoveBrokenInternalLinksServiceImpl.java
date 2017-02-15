package com.thomsonreuters.uscl.ereader.format.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.mail.internet.InternetAddress;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import com.thomsonreuters.uscl.ereader.format.exception.EBookFormatException;
import com.thomsonreuters.uscl.ereader.format.parsinghandler.HTMLUnlinkInternalLinksFilter;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocMetadata;
import com.thomsonreuters.uscl.ereader.gather.metadata.domain.DocumentMetadataAuthority;
import com.thomsonreuters.uscl.ereader.gather.metadata.service.DocMetadataService;
import com.thomsonreuters.uscl.ereader.ioutil.FileExtensionFilter;
import com.thomsonreuters.uscl.ereader.ioutil.FileHandlingHelper;
import com.thomsonreuters.uscl.ereader.util.EmailNotification;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.xml.serializer.Method;
import org.apache.xml.serializer.OutputPropertiesFactory;
import org.apache.xml.serializer.Serializer;
import org.apache.xml.serializer.SerializerFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Applies any post transformation on the HTML that needs to be done to cleanup or make
 * the HTML acceptable for ProView.
 *
 * @author <a href="mailto:Kirsten.Gunn@thomsonreuters.com">Kirsten Gunn</a> u0076257
 */
public class HTMLRemoveBrokenInternalLinksServiceImpl implements HTMLRemoveBrokenInternalLinksService
{
    private static final Logger LOG = LogManager.getLogger(HTMLRemoveBrokenInternalLinksServiceImpl.class);

    private FileHandlingHelper fileHandlingHelper;
    private DocMetadataService docMetadataService;

    public void setfileHandlingHelper(final FileHandlingHelper fileHandlingHelper)
    {
        this.fileHandlingHelper = fileHandlingHelper;
    }

    public void setdocMetadataService(final DocMetadataService docMetadataService)
    {
        this.docMetadataService = docMetadataService;
    }

    public void setEmailNotification(final EmailNotification emailNotification)
    {
        //Intentionally left blank
    }

    /**
     * This method applies HTMLUnlinkInternalLinksFilter to the source HTML to remove
     * unused anchors from the HTML. Emails notification of anchors removed.
     *
     * @param srcDir source directory that contains the html files
     * @param targetDir target directory where the resulting post transformation files are written to
     * @param title title of the book being published
     * @param jobInstanceId the job identifier of the current transformation run
     * @param envName the current execution environment name, like ci or prod
     * @param emailRecipients who to notify when things go wrong
     * @return the number of documents that had post transformations run on them
     *
     * @throws if no source files are found or any parsing/transformation exception are encountered
     */
    @Override
    public int transformHTML(
        final File srcDir,
        final File targetDir,
        final String title,
        final Long jobInstanceId,
        final String envName,
        final Collection<InternetAddress> emailRecipients) throws EBookFormatException
    {
        if (srcDir == null || !srcDir.isDirectory())
        {
            throw new IllegalArgumentException("srcDir must be a directory, not null or a regular file.");
        }

        //retrieve list of all transformed files that need HTML wrappers
        final List<File> htmlFiles = new ArrayList<File>();

        try
        {
            final FileExtensionFilter fileExtFilter = new FileExtensionFilter();
            fileExtFilter.setAcceptedFileExtensions(new String[] {"postanchor"}); // lowercase compare
            fileHandlingHelper.setFilter(fileExtFilter);

            fileHandlingHelper.getFileList(srcDir, htmlFiles);
        }
        catch (final FileNotFoundException e)
        {
            final String errMessage = "No html files were found in specified directory. "
                + "Please verify that the correct path was specified.";
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }

        if (!targetDir.exists())
        {
            targetDir.mkdirs();
        }

        LOG.info("Unlinking (removing links) on transformed files...");

        final File anchorTargetListFile = new File(srcDir.getAbsolutePath(), "anchorTargetUnlinkFile");

        final Map<String, Set<String>> targetAnchors = readTargetAnchorFile(anchorTargetListFile);
        final List<String> unlinkDocMetadataList = new ArrayList<String>();

        final File anchorDupListFile = new File(srcDir.getAbsolutePath(), "anchorDupFile");

        final Map<String, String> anchorDupTargets = readReplaceTargetAnchorFile(anchorDupListFile);

        final DocumentMetadataAuthority documentMetadataAuthority =
            docMetadataService.findAllDocMetadataForTitleByJobId(jobInstanceId);

        int numDocs = 0;
        for (final File htmlFile : htmlFiles)
        {
            transformHTMLFile(
                htmlFile,
                targetDir,
                title,
                jobInstanceId,
                documentMetadataAuthority,
                targetAnchors,
                unlinkDocMetadataList,
                anchorDupTargets);
            numDocs++;
        }

        if (unlinkDocMetadataList != null && unlinkDocMetadataList.size() > 0)
        {
            // Send notification for existing anchors.
            final File anchorUnlinkTargetListFile =
                new File(targetDir.getAbsolutePath(), title + "_" + jobInstanceId + "_anchorTargetUnlinkFile.csv");
            writeUnlinkAnchorReport(
                title,
                jobInstanceId,
                envName,
                unlinkDocMetadataList,
                anchorUnlinkTargetListFile,
                emailRecipients);
        }

        LOG.info("Unlinking transformation successfully applied to " + numDocs + " files.");
        return numDocs;
    }

    /**
     * This method applies the various XMLFilter(s) to the passed in source file and generates
     * a new file in the target directory. It also parses out all the static image references
     * and saves them off in a set to be serialized later.
     *
     * @param sourceFile source file to be transformed
     * @param targetDir target directory where the resulting post transformation file is to be written
     * @param staticImgRef set to which a list of referenced static files will be added to
     * @param titleID title of the book being published
     * @param jobIdentifier identifier of the job that will be used to retrieve the image metadata
     * @param documentMetadataAuthority
     *
     * @throws if any parsing/transformation exception are encountered
     */
    protected void transformHTMLFile(
        final File sourceFile,
        final File targetDir,
        final String titleID,
        final Long jobIdentifier,
        final DocumentMetadataAuthority documentMetadataAuthority,
        final Map<String, Set<String>> targetAnchors,
        final List<String> unlinkDocMetadataList,
        final Map<String, String> anchorDupTargets) throws EBookFormatException
    {
        final String fileName = sourceFile.getName();
        final String guid = fileName.substring(0, fileName.indexOf("."));
        FileInputStream inStream = null;
        FileOutputStream outStream = null;
        try
        {
            final DocMetadata docMetadata =
                docMetadataService.findDocMetadataByPrimaryKey(titleID, jobIdentifier, guid);

            final SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setNamespaceAware(true);
            final SAXParser saxParser = factory.newSAXParser();

            final HTMLUnlinkInternalLinksFilter unlinkFilter = new HTMLUnlinkInternalLinksFilter();
            unlinkFilter.setParent(saxParser.getXMLReader());
            if (docMetadata != null)
            {
                unlinkFilter.setCurrentGuid(docMetadata.getProViewId());
                unlinkFilter.setUnlinkDocMetadata(docMetadata);
            }
            else
            {
                unlinkFilter.setCurrentGuid(guid);
            }
            unlinkFilter.setTargetAnchors(targetAnchors);
            unlinkFilter.setUnlinkDocMetadataList(unlinkDocMetadataList);
            unlinkFilter.setAnchorDupTargets(anchorDupTargets);
            unlinkFilter.setDocMetadataKeyedByProViewId(documentMetadataAuthority.getDocMetadataKeyedByProViewId());

            final Properties props = OutputPropertiesFactory.getDefaultMethodProperties(Method.XHTML);
            props.setProperty("omit-xml-declaration", "yes");

            final Serializer serializer = SerializerFactory.getSerializer(props);
            outStream =
                new FileOutputStream(new File(targetDir, fileName.substring(0, fileName.indexOf(".")) + ".postUnlink"));
            serializer.setOutputStream(outStream);

            unlinkFilter.setContentHandler(serializer.asContentHandler());

            inStream = new FileInputStream(sourceFile);

            unlinkFilter.parse(new InputSource(inStream));
        }
        catch (final IOException e)
        {
            final String errMessage = "Unable to perform IO operations related to following source file: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final SAXException e)
        {
            final String errMessage = "Encountered a SAX Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        catch (final ParserConfigurationException e)
        {
            final String errMessage = "Encountered a SAX Parser Configuration Exception while processing: " + fileName;
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        finally
        {
            try
            {
                if (inStream != null)
                {
                    inStream.close();
                }
                if (outStream != null)
                {
                    outStream.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error("Unable to close files related to the " + fileName + " file post transformation.", e);
            }
        }
    }

    protected Map<String, Set<String>> readTargetAnchorFile(final File anchorTargetListFile) throws EBookFormatException
    {
        final Map<String, Set<String>> anchors = new HashMap<>();
        if (anchorTargetListFile.length() == 0)
        {
            return null;
        }
        else
        {
            BufferedReader reader = null;
            try
            {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(anchorTargetListFile), "UTF-8"));
                String input = reader.readLine();
                while (input != null)
                {
                    final String[] line = input.split("\\|", -1);
                    if (!line[1].equals(""))
                    {
                        final Set<String> anchorSet = new HashSet<>();
                        final String[] anchorList = line[1].split(",");
                        if (line[1].contains("REPLACEWITH"))
                        {
                            for (final String anchorVal : anchorList)
                            {
                                final String[] anchorReplaceList = anchorVal.split("REPLACEWITH");
                                anchorSet.add(anchorReplaceList[0]);
                                anchorSet.add(anchorVal);
                            }
                        }
                        else
                        {
                            for (final String anchorVal : anchorList)
                            {
                                anchorSet.add(anchorVal);
                            }
                        }
                        anchors.put(line[0], anchorSet);
                    }
                    else
                    {
                        final String message = "Please verify that each document GUID in the following file has "
                            + "at least one anchor associated with it: "
                            + anchorTargetListFile.getAbsolutePath();
                        LOG.error(message);
                        throw new EBookFormatException(message);
                    }
                    input = reader.readLine();
                }
                LOG.info("Generated a map for " + anchors.size() + " guids that have anchors.");
            }
            catch (final IOException e)
            {
                final String message =
                    "Could not read the DOC guid to anchors file: " + anchorTargetListFile.getAbsolutePath();
                LOG.error(message);
                throw new EBookFormatException(message, e);
            }
            finally
            {
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                }
                catch (final IOException e)
                {
                    LOG.error("Unable to close file reader.", e);
                }
            }
        }
        return anchors;
    }

    protected Map<String, String> readReplaceTargetAnchorFile(final File anchorTargetListFile)
        throws EBookFormatException
    {
        final Map<String, String> anchors = new HashMap<>();
        if (anchorTargetListFile.length() == 0)
        {
            return null;
        }
        else
        {
            BufferedReader reader = null;
            try
            {
                reader = new BufferedReader(new InputStreamReader(new FileInputStream(anchorTargetListFile), "UTF-8"));
                String input = reader.readLine();
                while (input != null)
                {
                    final String[] line = input.split("\\|", -1);
                    if (!line[1].equals(""))
                    {
                        final Set<String> anchorSet = new HashSet<>();
                        final String[] anchorList = line[1].split(",");
                        if (line[1].contains("REPLACEWITH"))
                        {
                            for (final String anchorVal : anchorList)
                            {
                                final String[] anchorReplaceList = anchorVal.split("REPLACEWITH");
                                anchorSet.add(anchorReplaceList[0]);
                                anchors.put(anchorReplaceList[0], anchorReplaceList[1]);
                            }
                        }
                    }
                    else
                    {
                        final String message = "Please verify that each document GUID in the following file has "
                            + "at least one anchor associated with it: "
                            + anchorTargetListFile.getAbsolutePath();
                        LOG.error(message);
                        throw new EBookFormatException(message);
                    }
                    input = reader.readLine();
                }
                LOG.info("Generated a map for " + anchors.size() + " guids that have anchors.");
            }
            catch (final IOException e)
            {
                final String message =
                    "Could not read the DOC guid to anchors file: " + anchorTargetListFile.getAbsolutePath();
                LOG.error(message);
                throw new EBookFormatException(message, e);
            }
            finally
            {
                try
                {
                    if (reader != null)
                    {
                        reader.close();
                    }
                }
                catch (final IOException e)
                {
                    LOG.error("Unable to close file reader.", e);
                }
            }
        }
        return anchors;
    }

    protected void writeUnlinkAnchorReport(
        final String title,
        final Long jobInstanceId,
        final String envName,
        final List<String> unlinkDocMetadataList,
        final File anchorUnlinkTargetListFile,
        final Collection<InternetAddress> emailRecipients) throws EBookFormatException
    {
        BufferedWriter writer = null;
        try
        {
            writer =
                new BufferedWriter(new OutputStreamWriter(new FileOutputStream(anchorUnlinkTargetListFile), "UTF-8"));

            writer.write(
                "Document Guid, Family Guid, Normalized Firstline Cite, Serial Number, Collection Name, "
                    + "Removed Link, Target Document Guid, Target Doc Family Guid, Target "
                    + "Normalized Firstline Cite, Target Serial Number");

            writer.newLine();

            for (final String udml : unlinkDocMetadataList)
            {
                writer.write(udml.toString());
                writer.newLine();
            }
            writer.flush();
        }
        catch (final IOException e)
        {
            final String errMessage =
                "Encountered an IO Exception while processing: " + anchorUnlinkTargetListFile.getAbsolutePath();
            LOG.error(errMessage);
            throw new EBookFormatException(errMessage, e);
        }
        finally
        {
            try
            {
                if (writer != null)
                {
                    writer.close();
                }
            }
            catch (final IOException e)
            {
                LOG.error("Unable to close anchor target list file.", e);
            }
        }

        final String subject = String.format(
            "Anchor links removed for title \"%s\", job: %s, env: %s",
            title,
            jobInstanceId.toString(),
            envName);
        final String emailBody =
            "Attached is the file of links removed this book. Format is comma seperated list of guids and Proview enhanced links (relevant portion follows the slash).";
        LOG.debug("Notification email recipients : " + emailRecipients);
        LOG.debug("Notification email subject : " + subject);
        LOG.debug("Notification email body : " + emailBody);
        final List<String> filenames = new ArrayList<>();
        filenames.add(anchorUnlinkTargetListFile.getAbsolutePath());
        EmailNotification.sendWithAttachment(emailRecipients, subject, emailBody, filenames);
    }
}
