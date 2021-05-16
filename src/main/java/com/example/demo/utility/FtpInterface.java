package com.example.demo.utility;

import org.apache.commons.net.ftp.FTPClient;

import java.io.InputStream;
import java.util.ArrayList;

public interface FtpInterface {

    public FTPClient ftp(String ip, String user, String password);

    public ArrayList<String[]> csv(InputStream in);

}
