package com.netalign.netascutter.utils;

import com.netalign.netascutter.interfaces.IEncryptor;
import com.netalign.netascutter.utils.SHA1Encryptor;
import junit.framework.TestCase;

/**
 * 
 * <p>
 * This code is Public Domain
 * </p>
 * 
 * @author ccslrd
 */
public class EncryptorTest extends TestCase
{
    /**
     * Constructor for EncryptorTest.
     * 
     * @param arg0
     */
    public EncryptorTest(String arg0)
    {
        super(arg0);
    }

    public void testSHA1() throws Exception
    {
        IEncryptor enc = new SHA1Encryptor();

        String d = enc.encrypt("mailto:ldodds@ingenta.com");
        assertTrue(d.equals("71b88e951cb5f07518d69e5bb49a45100fbc3ca5"));
    }

    public static void main(String[] args)
    {
        junit.textui.TestRunner.run(EncryptorTest.class);
    }
}