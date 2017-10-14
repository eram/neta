package com.netalign.netascutter.utils;

import java.security.*;

/**
 * Implements SHA1 digest encryption
 * <p>
 * This code is Public Domain
 * </p>
 * 
 * @author ccslrd
 * @see IEncryptor
 */
public class SHA1Encryptor implements IEncryptor
{

    private MessageDigest _digest;

    public SHA1Encryptor()
    {
        try
        {
            _digest = MessageDigest.getInstance("SHA");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    /**
     * @see com.netalign.netascutter.utils.IEncryptor#encrypt(String)
     */
    public String encrypt(String data)
    {
        if ( data == null )
        {
            return null;
        }
        try
        {
            _digest.update(data.getBytes());
            return getHexString(_digest.digest());

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    private String getHexString(byte[] bytes)
    {
        StringBuffer sb = new StringBuffer(bytes.length * 2);
        for ( int i = 0; i < bytes.length; i++ )
        {
            sb.append(HEX_CHAR[(bytes[i] & 0xf0) >> 4]);
            sb.append(HEX_CHAR[(bytes[i] & 0x0f)]);
        }
        return sb.toString();
    }

    public static char[] HEX_CHAR = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
            '9', 'a', 'b', 'c', 'd', 'e', 'f' };
}