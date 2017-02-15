package com.thomsonreuters.uscl.ereader.smoketest.domain;

public final class SmokeTest
{
    private String name;
    private String address;
    private boolean isRunning;

    public SmokeTest()
    {
        super();
    }

    public String getName()
    {
        return name;
    }

    public void setName(final String name)
    {
        this.name = name;
    }

    public String getAddress()
    {
        return address;
    }

    public void setAddress(final String address)
    {
        this.address = address;
    }

    public boolean isRunning()
    {
        return isRunning;
    }

    public void setIsRunning(final boolean isRunning)
    {
        this.isRunning = isRunning;
    }
}
