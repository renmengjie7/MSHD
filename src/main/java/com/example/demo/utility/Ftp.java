package com.example.demo.utility;


import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import org.apache.commons.net.ftp.FTPClient;


public class Ftp implements FtpInterface {

    /**
     *
     * <b>登陆ftp 返回ftpClient事件<b>
     *
     * @param ip
     *            ftp所在ip
     * @param user
     *            登陆名
     * @param password
     *            密码
     */
    public FTPClient ftp(String ip, String user, String password) {

        FTPClient ftpClient = new FTPClient();
        try {
            ftpClient.connect(ip);
            ftpClient.login(user, password);
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!ftpClient.isConnected()) {
            ftpClient = null;
        }

        return ftpClient;
    }

    /**
     * <b>将一个IO流解析，转化数组形式的集合<b>
     *
     * @param in 文件inputStream流
     */
    public ArrayList<String[]> csv(InputStream in) {
        ArrayList<String[]> csvList = new ArrayList<String[]>();
        return  csvList;
    }
}