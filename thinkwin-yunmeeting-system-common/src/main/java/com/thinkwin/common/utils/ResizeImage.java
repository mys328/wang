package com.thinkwin.common.utils;


import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 图片处理
 */
public class ResizeImage {


    /**
     * @param im            原始图像
     * @param resizeTimes   需要缩小的倍数，缩小2倍为原来的1/2 ，这个数值越大，返回的图片越小
     * @return              返回处理后的图像
     */
    public static byte[] resizeImageByte(byte[] bytes,float resizeTimes ) {
        Map<String,BufferedImage> imageMap = new HashMap<>();
        BufferedImage im = getBufferedImage(bytes);
        /*原始图像的宽度和高度*/
        int width = im.getWidth();
        int height = im.getHeight();
        int toWidth = (int) (Float.parseFloat(String.valueOf(width)) / resizeTimes);
        int toHeight = (int) (Float.parseFloat(String.valueOf(height)) / resizeTimes);

        /*新生成结果图片*/
        BufferedImage resizedImg = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        //将原图放大或缩小后画下来:并且保持png图片放大或缩小后背景色是透明的而不是黑色
        Graphics2D resizedG = resizedImg.createGraphics();
        resizedImg = resizedG.getDeviceConfiguration().createCompatibleImage(toWidth,toHeight,Transparency.TRANSLUCENT);
        resizedG.dispose();
        resizedG = resizedImg.createGraphics();
        Image from = im.getScaledInstance(toWidth, toHeight, im.SCALE_AREA_AVERAGING);
        resizedG.drawImage(from, 0, 0, null);
        resizedG.dispose();

        //result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH), 0, 0, null);
        //转换数组
        byte[] by = imageToBytes(resizedImg,"png");
        return by;
    }


    /**
     * @param im            原始图像
     * @param resizeTimes   需要缩小的倍数，缩小2倍为原来的1/2 ，这个数值越大，返回的图片越小
     * @return              返回处理后的图像
     */
    public static Map<String,BufferedImage> resizeImage(byte[] bytes,Map<String,String> map) {

        BufferedImage im = getBufferedImage(bytes);

        Map<String,BufferedImage> imageMap = new HashMap<>();
        /*原始图像的宽度和高度*/
        int width = im.getWidth();
        int height = im.getHeight();
        //大图
        if(map.get("big") != null){
            String [] str = map.get("big").toString().split("_");
            int needWidth = Integer.parseInt(str[0]);
            int needHeigth = Integer.parseInt(str[1]);
            double specWidth = (double) width / needWidth;
            double specHeigth = (double) height / needHeigth;
            float resizeTimes = 0;
            if(specHeigth > specWidth){
                resizeTimes = (float) specHeigth;
            } else{
                resizeTimes = (float) specWidth;
            }
            BufferedImage image = getBufferedImage(im,width,height,resizeTimes);
            imageMap.put("big",image);
        }
        //中图
        if(map.get("in") != null){
            String [] str = map.get("in").toString().split("_");
            int needWidth = Integer.parseInt(str[0]);
            int needHeigth = Integer.parseInt(str[1]);
            double specWidth = (double) width / needWidth;
            double specHeigth = (double) height / needHeigth;
            float resizeTimes = 0;
            if(specHeigth > specWidth){
                resizeTimes = (float) specHeigth;
            } else{
                resizeTimes = (float) specWidth;
            }
            BufferedImage image = getBufferedImage(im,width,height,resizeTimes);
            imageMap.put("in",image);
        }
        //小图
        if(map.get("small") != null){
            String [] str = map.get("small").toString().split("_");
            int needWidth = Integer.parseInt(str[0]);
            int needHeigth = Integer.parseInt(str[1]);
            double specWidth = (double) width / needWidth;
            double specHeigth = (double) height / needHeigth;
            float resizeTimes = 0;
            if(specHeigth > specWidth){
                resizeTimes = (float) specHeigth;
            } else{
                resizeTimes = (float) specWidth;
            }
            BufferedImage image = getBufferedImage(im,width,height,resizeTimes);
            imageMap.put("small",image);
        }
        return imageMap;
    }




    public static byte[] resizeImageByte(byte[] bytes, int toWidth, int toHeight) {
        Map<String,BufferedImage> imageMap = new HashMap<>();
        BufferedImage im = getBufferedImage(bytes);
        /*原始图像的宽度和高度*/
        int width = im.getWidth();
        int height = im.getHeight();

        /*新生成结果图片*/
        BufferedImage resizedImg = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        //将原图放大或缩小后画下来:并且保持png图片放大或缩小后背景色是透明的而不是黑色
        Graphics2D resizedG = resizedImg.createGraphics();
        resizedImg = resizedG.getDeviceConfiguration().createCompatibleImage(toWidth,toHeight,Transparency.TRANSLUCENT);
        resizedG.dispose();
        resizedG = resizedImg.createGraphics();
        Image from = im.getScaledInstance(toWidth, toHeight, im.SCALE_AREA_AVERAGING);
        resizedG.drawImage(from, 0, 0, null);
        resizedG.dispose();

        //result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH), 0, 0, null);
        //转换数组
        byte[] by = imageToBytes(resizedImg,"png");
        return by;
    }








    /**
     * @param im            原始图像
     * @param resizeTimes   需要缩小的倍数，缩小2倍为原来的1/2 ，这个数值越大，返回的图片越小
     * @return              返回处理后的图像
     */
    public static BufferedImage getBufferedImage(BufferedImage image,Integer width,Integer height,float resizeTimes){


        /*调整后的图片的宽度和高度*/
        int toWidth = (int) (Float.parseFloat(String.valueOf(width)) / resizeTimes);
        int toHeight = (int) (Float.parseFloat(String.valueOf(height)) / resizeTimes);

        /*新生成结果图片*/
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = result.createGraphics();
        result = graphics.getDeviceConfiguration().createCompatibleImage(toWidth, toHeight, Transparency.TRANSLUCENT);
        graphics.dispose();
        graphics = result.createGraphics();
        Image from = image.getScaledInstance(toWidth, toHeight, Image.SCALE_SMOOTH);
        graphics.drawImage(from, 0, 0, null);
        graphics.dispose();
        //graphics.drawImage(image.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return result;


    }
    /**
     * @param im            原始图像
     * @param resizeTimes   倍数,比如0.5就是缩小一半,0.98等等double类型
     * @return              返回处理后的图像
     */
    public static BufferedImage zoomImage(BufferedImage im,Integer toWidth,Integer toHeight) {

        /*新生成结果图片*/
        BufferedImage result = new BufferedImage(toWidth, toHeight, BufferedImage.TYPE_INT_RGB);

        result.getGraphics().drawImage(im.getScaledInstance(toWidth, toHeight, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
        return result;
    }

    /**
     * @param path  要转化的图像的文件夹,就是存放图像的文件夹路径
     * @param type  图片的后缀名组成的数组
     * @return
     */
    public  static List<BufferedImage> getImageList(String path, String[] type) throws IOException {
        Map<String,Boolean> map = new HashMap<String, Boolean>();
        for(String s : type) {
            map.put(s,true);
        }
        List<BufferedImage> result = new ArrayList<BufferedImage>();
        File[] fileList = new File(path).listFiles();
        for (File f : fileList) {
            if(f.length() == 0)
                continue;
            if(map.get(getExtension(f.getName())) == null)
                continue;
            result.add(ImageIO.read(f));
        }
        return result;
    }
    public static  BufferedImage getImage(String path) throws IOException{
        return ImageIO.read(new File(path));
    }

    /**
     * 把图片写到磁盘上
     * @param im
     * @param path     eg: C://home// 图片写入的文件夹地址
     * @param fileName DCM1987.jpg  写入图片的名字
     * @return
     */
    public  static boolean writeToDisk(BufferedImage im, String path, String fileName) {
        File f = new File(path);
        String fileType = getExtension(fileName);
        if (fileType == null)
            return false;
        try {
            ImageIO.write(im, fileType, new File(path+fileName));
            im.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }


    public  static boolean writeHighQuality(BufferedImage im, String fileFullPath) {
        /*try {
            *//*输出到文件流*//*
            FileOutputStream newimage = new FileOutputStream(fileFullPath);
            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(newimage);
            JPEGEncodeParam jep = JPEGCodec.getDefaultJPEGEncodeParam(im);
            *//* 压缩质量 *//*
            jep.setQuality(1f, true);
            encoder.encode(im, jep);
           *//*近JPEG编码*//*
            newimage.close();
            return true;
        } catch (Exception e) {
            return false;
        }*/
        return false;
    }

    public static  BufferedImage getBufferedImage(byte[] b) {

        ByteArrayInputStream in = new ByteArrayInputStream(b);

        BufferedImage image = null;
        try {
            image = ImageIO.read(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }






    /**
     * 返回文件的文件后缀名
     * @param fileName
     * @return
     */
    public  static String getExtension(String fileName) {
        try {
            return fileName.split("\\.")[fileName.split("\\.").length - 1];
        } catch (Exception e) {
            return null;
        }
    }
    /**
     * 转换BufferedImage 数据为byte数组
     *
     * @param image
     * Image对象
     * @param format
     * image格式字符串.如"gif","png"
     * @return byte数组
     */
    public static byte[] imageToBytes(BufferedImage bImage, String format) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            ImageIO.write(bImage, format, out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toByteArray();
    }


    //测试
    public static void main(String[] args) throws Exception{


      System.out.println((2/3)*100+"%");
      /*  String inputFoler = "F:\\testimages\\yuan";
         这儿填写你存放要缩小图片的文件夹全地址
        String outputFolder = "F:\\testimages\\ys";
        这儿填写你转化后的图片存放的文件夹
        float times = 0.5f;
        这个参数是要转化成的倍数,如果是1就是转化成1倍


        ResizeImage r = new ResizeImage();

   List<BufferedImage> imageList = r.getImageList(inputFoler,new String[] {"png"});
        for(BufferedImage i : imageList) {

         r.writeHighQuality(r.zoomImage(i,times),outputFolder);
         System.out.println("...");
  }*/
       /* ResizeImage r=new ResizeImage();
        String filepath="E:\\file\\";
        String filename="1.jpg";
        BufferedImage im=r.getImage(filepath+filename);
        r.writeHighQuality(r.zoomImage(im), filepath+"s_"+filename);//为防止覆盖原图片,加s_区分是压缩以后的图片*/
    }
}