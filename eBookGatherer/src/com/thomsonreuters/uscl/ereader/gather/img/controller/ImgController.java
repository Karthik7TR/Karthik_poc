package com.thomsonreuters.uscl.ereader.gather.img.controller;

import com.thomsonreuters.uscl.ereader.core.EBConstants;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherImgRequest;
import com.thomsonreuters.uscl.ereader.gather.domain.GatherResponse;
import com.thomsonreuters.uscl.ereader.gather.img.model.ImageRequestParameters;
import com.thomsonreuters.uscl.ereader.gather.img.service.ImageServiceFactory;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ImgController
{
    private static Logger LOG = LogManager.getLogger(ImgController.class);

    private ImageServiceFactory imageServiceFactory;
    private ImageRequestParameters parameters;

    @RequestMapping(value = "/img", method = RequestMethod.POST)
    public ModelAndView fetchImages(@RequestBody final GatherImgRequest imgRequest, final Model model)
    {
        LOG.debug(">>> ImgController");

        parameters.setDocToImageManifestFile(imgRequest.getImgToDocManifestFile());
        parameters.setDynamicImageDirectory(imgRequest.getDynamicImageDirectory());
        parameters.setFinalStage(imgRequest.isFinalStage());
        parameters.setXppSourceImageDirectory(imgRequest.getXppSourceImageDirectory());

        GatherResponse gatherResponse;
        try
        {
            gatherResponse = imageServiceFactory.getImageService(imgRequest.isXpp()).getImages(parameters);
        }
        catch (final Exception e)
        {
            gatherResponse = new GatherResponse();
            LOG.error("Failed to get images", e);
        }
        model.addAttribute(EBConstants.GATHER_RESPONSE_OBJECT, gatherResponse);
        return new ModelAndView(EBConstants.VIEW_RESPONSE);
    }

    @Required
    public void setImageServiceFactory(final ImageServiceFactory imageServiceFactory)
    {
        this.imageServiceFactory = imageServiceFactory;
    }

    @Required
    public void setParameters(final ImageRequestParameters parameters)
    {
        this.parameters = parameters;
    }
}
