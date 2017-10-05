package com.thomsonreuters.uscl.ereader.orchestrate.engine.web.controller;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;

import javax.annotation.Resource;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import com.thomsonreuters.uscl.ereader.jms.client.JMSClient;
import com.thomsonreuters.uscl.ereader.request.domain.XppBundleArchive;
import com.thomsonreuters.uscl.ereader.util.UuidGenerator;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Sends JMS message to queue to launch XppBundleQueuePoller for development purposes.
 */
@RestController
public class XppBundleQueueJmsController {
    @Resource
    private JMSClient jmsClient;
    @Resource
    private JmsTemplate jmsTemplate;
    @Resource
    private UuidGenerator uuidGenerator;

    @RequestMapping(value = "/xppbundle", method = {RequestMethod.POST, RequestMethod.GET})
    public XppBundleArchive sendJmsMessage(
        @RequestParam final String materialNumber,
        @RequestParam final String srcFile) throws IOException, JAXBException {
        final XppBundleArchive xppBundleRequest = buildRequest(materialNumber, srcFile);
        jmsClient.sendMessageToQueue(jmsTemplate, marshall(xppBundleRequest), null);
        return xppBundleRequest;
    }

    private XppBundleArchive buildRequest(final String materialNumber, final String srcFile) throws IOException {
        final XppBundleArchive xppBundleRequest = new XppBundleArchive();
        xppBundleRequest.setMessageId(uuidGenerator.generateUuid());
        xppBundleRequest.setBundleHash(calculateHash(srcFile));
        xppBundleRequest.setMaterialNumber(materialNumber);
        xppBundleRequest.setDateTime(new Date());
        xppBundleRequest.setEBookSrcPath(srcFile);
        xppBundleRequest.setVersion("1.0");
        return xppBundleRequest;
    }

    private String marshall(final XppBundleArchive xppBundleRequest) throws JAXBException {
        final JAXBContext jaxbContext = JAXBContext.newInstance(XppBundleArchive.class);
        final Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
        final StringWriter sw = new StringWriter();
        jaxbMarshaller.marshal(xppBundleRequest, sw);
        return sw.toString();
    }

    private String calculateHash(final String srcFile) throws IOException {
        try (InputStream bookStream = new FileInputStream(srcFile)) {
            return DigestUtils.md5Hex(bookStream);
        }
    }

}
