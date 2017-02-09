package com.thomsonreuters.uscl.ereader.mgr.web.controller.admin.generatorswitch;

public class StopGeneratorForm
{
    //private static final Logger log = LogManager.getLogger(KillSwitchForm.class);
    public static final String FORM_NAME = "killSwitchForm";

    private String code;

    public String getCode()
    {
        return code;
    }

    public void setCode(final String code)
    {
        this.code = code;
    }
}
