package com.product.nexustalk.config;

public class ErrorConf {
    private static ErrorConf instance;

    public static synchronized ErrorConf getInstance()
    {
        if(instance == null)
        {
            instance = new ErrorConf();
        }
        return instance;
    }

    private ErrorConf(){}

    public String DES_409_DUPLICATE_USER = "DUPLICATE USER";
    public String DES_401_INVALID_CREDENTIALS = "INVALID_CREDENTIALS";
    public String DES_401_INVALID_REFRESH_TOKEN = "INVALID_REFRESH_TOKEN";
}
