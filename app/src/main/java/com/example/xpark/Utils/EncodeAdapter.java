package com.example.xpark.Utils;

import android.os.Build;

import androidx.annotation.RequiresApi;

import java.security.MessageDigest;
import java.util.Base64;

public class EncodeAdapter
{
    private final MessageDigest chiper;

    public EncodeAdapter() throws  SecurityException {
        try {
            chiper = MessageDigest.getInstance("MD5", "SUN");
        } catch (Exception e) {
            throw new SecurityException("In EncodeAdapter constructor: " + e);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String encode(String param) throws Exception {
        if (param == null) {
            return null;
        } else {
            try {
                byte[] raw = null;
                byte[] stringBytes = param.getBytes();

                synchronized (chiper) {
                    raw = chiper.digest(stringBytes);
                }

                return new String(Base64.getEncoder().encode(raw));

            } catch (Exception e) {
                throw new Exception("Exception while encrypting: " + e);
            }
        }
    }

    public String decode(String param) {
        throw new RuntimeException("NOT SUPPORTED");
    }
}
