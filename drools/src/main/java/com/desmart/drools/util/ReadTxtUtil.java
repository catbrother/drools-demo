package com.desmart.drools.util;

import java.io.*;

/**
 * @Author yby
 * @Date 2020/9/6 19:21
 **/
public class ReadTxtUtil {

    /**
     * 从文件中通过流得到字符串
     *
     * @param file
     * @return
     * @throws IOException
     */
    public static String readTxt(File file) throws IOException {
        String s = "";
        InputStreamReader in = new InputStreamReader(new FileInputStream(file), "UTF-8");
        BufferedReader br = new BufferedReader(in);
        StringBuffer content = new StringBuffer();
        while ((s = br.readLine()) != null) {
            content = content.append(s);
        }
        return content.toString();
    }
}
