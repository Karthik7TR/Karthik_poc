package com.thomsonreuters.uscl.ereader.mgr.web.controller.security;

import org.apache.commons.lang3.StringUtils;

/**
 * The form used for login data entry and to hold
 * the j_username and j_password parameters used by
 * the j_spring_security_check filter intercepter.
 */
public class LoginForm
{
    public static final String FORM_NAME = "loginForm";

    private String username;
    private String password;

    /** The property that Spring Security authentication expects for the username */
    public String getJ_username()
    {
        return getUsername();
    }

    /** The property that Spring Security authentication expects for the password */
    public String getJ_password()
    {
        return getPassword();
    }

    public String getUsername()
    {
        return username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(final String password)
    {
        this.password = password;
    }

    public void setUsername(final String username)
    {
        this.username = username;
    }

    @Override
    public String toString()
    {
        return "username=" + username + ",password=" + (StringUtils.isNotBlank(password) ? "<not-blank>" : password);
    }
}
