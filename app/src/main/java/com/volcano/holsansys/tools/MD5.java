package com.volcano.holsansys.tools;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5 {
    public String EncryptToMD5(String myOriginalStr) {
        //Defining a byte array
        byte[] mySecretBytes = null;
        
        try 
        {
            //Generating an MD5 encrypted calculation digest
            MessageDigest myMD = MessageDigest.getInstance("MD5");
            
            //Encrypting the string
            myMD.update(myOriginalStr.getBytes());
            
            //Obtaining the encrypted data
            mySecretBytes = myMD.digest();
        } 
        catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("md5 error!");
        }
        
        //Converting the encrypted data into hexadecimal number
        String myMD5Code = new BigInteger(1, mySecretBytes).toString(16);
        
        //Supplementing some 0s in front of the generated number if its bits is less than 32
        for (int i = 0; i < 32 - myMD5Code.length(); i++) {
            myMD5Code = "0" + myMD5Code;
        }
        
        return myMD5Code;
    }
}
