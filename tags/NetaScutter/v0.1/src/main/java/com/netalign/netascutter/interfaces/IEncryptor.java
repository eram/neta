package com.netalign.netascutter.interfaces;

/**
 * Describes a class that carries out basic data encryption
 * <p>
 * This code is Public Domain
 * </p>
 * 
 * @author ccslrd
 */
public interface IEncryptor
{
	/**
	 * Encrypts the given data.
	 * @param data
	 * @return Encrypted data
	 */
    public String encrypt(String data);
}