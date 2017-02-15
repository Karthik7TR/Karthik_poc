package com.thomsonreuters.uscl.ereader.userpreference.domain;

import java.util.Arrays;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public final class UserPreferenceTest
{
    private static final String CSV_RECIPIENTS = "a@a.com,b@b.com,c@c.com";

    @Before
    public void setUp()
    {
        //Intentionally left blank
    }

    @Test
    public void testGetEmailList() throws Exception
    {
        final List<String> emails = Arrays.asList(new String[] {"a@a.com", "b@b.com", "c@c.com"});

        final UserPreference preference = new UserPreference();
        preference.setEmails(CSV_RECIPIENTS);

        // Test the creation of the List<String>
        final List<String> actual = preference.getEmailAddressList();
        Assert.assertEquals(emails, actual);

        // Test the creation of the List<InternetAddress>
        final List<InternetAddress> addrs = preference.getInternetEmailAddressList();
        Assert.assertNotNull(addrs);
        Assert.assertEquals(3, addrs.size());
        Assert.assertTrue(addrs.contains(new InternetAddress("a@a.com")));
        Assert.assertTrue(addrs.contains(new InternetAddress("b@b.com")));
        Assert.assertTrue(addrs.contains(new InternetAddress("c@c.com")));
    }

    @Test
    public void testToStringEmailAddressList()
    {
        // Test a null csv address list
        final String addressCsv = null;
        List<String> list = UserPreference.toStringEmailAddressList(addressCsv);
        Assert.assertNotNull(list);
        Assert.assertEquals(0, list.size());

        // Test a valid csv address
        list = UserPreference.toStringEmailAddressList(CSV_RECIPIENTS);
        Assert.assertNotNull(list);
        Assert.assertEquals(3, list.size());
    }
}
