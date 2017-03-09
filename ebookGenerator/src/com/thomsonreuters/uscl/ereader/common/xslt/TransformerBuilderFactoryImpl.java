package com.thomsonreuters.uscl.ereader.common.xslt;

import javax.annotation.PostConstruct;
import javax.xml.transform.TransformerFactory;

import org.springframework.stereotype.Component;

@Component("transformerBuilderFactory")
public class TransformerBuilderFactoryImpl implements TransformerBuilderFactory
{
    private TransformerFactory factory;

    @PostConstruct
    public void init()
    {
        factory = TransformerFactory
            .newInstance("net.sf.saxon.TransformerFactoryImpl", TransformerBuilderFactoryImpl.class.getClassLoader());
    }

    @Override
    public TransformerBuilder create()
    {
        return new TransformerBuilder(factory);
    }
}
