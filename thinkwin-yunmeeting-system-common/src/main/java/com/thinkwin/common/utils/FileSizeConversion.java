package com.thinkwin.common.utils;

/**
  *  将文件大小转换成kb
  *
  *  开发人员:daipengkai
  *  创建时间:2017/6/30
  *
  */
public class FileSizeConversion {

    public static void main (String [] ags){

        long a=1024;

        String s= getPrintSizeGB(a);
        System.out.print(s);
      //fileSizeSwitch("5GB");


    }



    public static String convertFileSize(long size) {

        if(size<=1024){
            return "1";
        }else {
            double f = (double) size / 1024;
            int i=(int)f;
            return String.valueOf(i);
        }

    }


    public static String getPrintSizeMB(long size) {
            double f = (double) size / 1024;
            int i=(int)f;
            return String.valueOf(i);
    }

    public static String getPrintSizeGB(long size) {

        double f = (double) size / 1024;

        double n =  f / 1024;

        return String.valueOf(n);
    }











    public static boolean isNumeric(String str) {
        for (int i = 0; i < str.length(); i++) {
            System.out.println(str.charAt(i));
            if (!Character.isDigit(str.charAt(i))) {
                if(!".".equals(str.charAt(i))){
                    return false;
                }
            }
        }
        return true;
    }
}
