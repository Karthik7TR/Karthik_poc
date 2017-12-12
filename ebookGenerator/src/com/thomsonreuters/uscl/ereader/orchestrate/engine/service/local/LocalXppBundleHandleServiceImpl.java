package com.thomsonreuters.uscl.ereader.orchestrate.engine.service.local;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Optional;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
public class LocalXppBundleHandleServiceImpl implements LocalXppBundleHandleService {
    private final JMSClient jmsClient;
    private final JmsTemplate jmsTemplate;
    private final UuidGenerator uuidGenerator;

    @Autowired
    public LocalXppBundleHandleServiceImpl(final JMSClient jmsClient,
                                           final JmsTemplate jmsTemplate,
                                           final UuidGenerator uuidGenerator) {
        this.jmsClient = jmsClient;
        this.jmsTemplate = jmsTemplate;
        this.uuidGenerator = uuidGenerator;
    }

    @Override
    public XppBundleArchive createXppBundleArchive(final String materialNumber, final String srcFile) {
        final XppBundleArchive xppBundleRequest = new XppBundleArchive();
        xppBundleRequest.setMessageId(uuidGenerator.generateUuid());
        xppBundleRequest.setBundleHash(calculateHash(srcFile));
        xppBundleRequest.setMaterialNumber(materialNumber);
        xppBundleRequest.setDateTime(new Date());
        xppBundleRequest.setEBookSrcPath(srcFile);
        xppBundleRequest.setVersion("1.0");
        return xppBundleRequest;
    }

    private String calculateHash(final String srcFile) {
        try (InputStream bookStream = new FileInputStream(srcFile)) {
            return DigestUtils.md5Hex(bookStream);
        } catch (final IOException e) {
            throw new IllegalArgumentException(String.format("Cannot get hash for file: %s", srcFile), e);
        }
    }

    @Override
    public String sendXppBundleJmsMessage(final XppBundleArchive bundleArchive) {
        final String jmsMessageContent = marshall(bundleArchive);
        jmsClient.sendMessageToQueue(jmsTemplate, jmsMessageContent, null);
        return jmsMessageContent;
    }

    private String marshall(final XppBundleArchive xppBundleRequest) {
        try {
            final JAXBContext jaxbContext = JAXBContext.newInstance(XppBundleArchive.class);
            final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
            final StringWriter sw = new StringWriter();
            jaxbMarshaller.marshal(xppBundleRequest, sw);
            return sw.toString();
        } catch (final JAXBException e) {
            throw new IllegalArgumentException(String.format("Xpp bundle cannot be marshaled to xml", xppBundleRequest.toString()), e);
        }
    }

    @Override
    public int processXppBundleDirectory(final String srcDir) {
        final Path dirPath = Optional.ofNullable(srcDir)
            .map(Paths::get)
            .filter(Files::exists)
            .filter(Files::isDirectory)
            .orElseThrow(() -> new IllegalArgumentException(
                String.format("Provided path is not directory or doesn't exist, %s", srcDir)));
        try {
            final LocalXppBundleFileVisitor visitor = new LocalXppBundleFileVisitor(
                this::createXppBundleArchive, this::sendXppBundleJmsMessage);
            Files.walkFileTree(dirPath, visitor);
            return visitor.getHandledFilesCount();
        } catch (final IOException e) {
            throw new IllegalArgumentException(String.format("Cannot walk file tree from %s", srcDir), e);
        }
    }
}
