package com.thomsonreuters.uscl.ereader.mgr.web.controller.isbn;

import com.thomsonreuters.uscl.ereader.mgr.web.WebConstants;
import com.thomsonreuters.uscl.ereader.mgr.web.service.isbn.IsbnUpdateService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

@Slf4j
@Data
@Controller
public class IsbnUpdateController {
    @Autowired
    private IsbnUpdateService isbnUpdateService;

    @RequestMapping(value = WebConstants.MVC_UPDATE_ISBNS, method = RequestMethod.GET)
    public ModelAndView updateIsbns() throws IOException, ParseException {
        try {
            isbnUpdateService.updateVersionIsbn();
        } catch (IOException | ParseException e) {
            log.error(e.getMessage());
            throw e;
        }
        return new ModelAndView(WebConstants.VIEW_ISBNS_UPDATED);
    }
}
