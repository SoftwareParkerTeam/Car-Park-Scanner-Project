package com.example.xpark.Utils;

import java.security.MessageDigest;

public class EncodeAdapter
{
    public static String encode(final String message) {

        try {
             MessageDigest digest = java.security.MessageDigest.getInstance("MD5");

             digest.update(message.getBytes());
             byte[] messageDigest = digest.digest();

             StringBuilder sb = new StringBuilder();
             for (byte messageBYTE : messageDigest) {
                 String temp = Integer.toHexString(0xFF & messageBYTE);

                 while (temp.length() < 2)
                     temp = "0" + temp;

                 sb.append(temp);
             }
             return sb.toString();

         } catch (Exception e) {
             e.printStackTrace();
         }
         return message;
    }
}
