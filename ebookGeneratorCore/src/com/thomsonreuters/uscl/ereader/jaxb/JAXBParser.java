package com.thomsonreuters.uscl.ereader.jaxb;

import java.io.InputStream;
import java.io.OutputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

public final class JAXBParser
{
    public static <T> T parse(final InputStream inStream, final Class<T> clazz) throws JAXBException
    {
        final JAXBContext context = JAXBContext.newInstance(clazz);
        final Unmarshaller unmarshaller = context.createUnmarshaller();
        return (T) unmarshaller.unmarshal(inStream);
    }

    public static <T> void write(final OutputStream outStream, final T obj) throws JAXBException
    {
        final JAXBContext context = JAXBContext.newInstance(obj.getClass());
        final Marshaller marshaller = context.createMarshaller();
        marshaller.marshal(obj, outStream);
    }

    private JAXBParser()
    {
        // static utility class
    }
}
