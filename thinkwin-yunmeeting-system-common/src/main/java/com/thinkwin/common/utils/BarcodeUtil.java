package com.thinkwin.common.utils;

import com.swetake.util.Qrcode;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class BarcodeUtil {



public static void main(String [] ags){




}


	/**
	 * 生成二维码(QRCode)图片
	 * @param content 存储内容
	 * @param imgPath 图片路径
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 */
	public static byte [] encoderQRCode(String content, String imgType, int size) {
		byte [] bytes=null;
		try {
			BufferedImage bufImg = qRCodeCommon(content, imgType, size);
		    bytes = imageToBytes(bufImg,imgType);
			/*File imgFile = new File(imgPath);
			// 生成二维码QRCode图片
			ImageIO.write(bufImg, imgType, imgFile);*/
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bytes;
	}


	/**
	 * 生成二维码(QRCode)图片的公共方法
	 * @param content 存储内容
	 * @param imgType 图片类型
	 * @param size 二维码尺寸
	 * @return
	 */
	private static BufferedImage qRCodeCommon(String content, String imgType, int size) {
		BufferedImage bufImg = null;
		try {
			Qrcode qrcodeHandler = new Qrcode();
			// 设置二维码排错率，可选L(7%)、M(15%)、Q(25%)、H(30%)，排错率越高可存储的信息越少，但对二维码清晰度的要求越小
			qrcodeHandler.setQrcodeErrorCorrect('M');
			qrcodeHandler.setQrcodeEncodeMode('B');
			// 设置设置二维码尺寸，取值范围1-40，值越大尺寸越大，可存储的信息越大
			qrcodeHandler.setQrcodeVersion(size);
			// 获得内容的字节数组，设置编码格式
			byte[] contentBytes = content.getBytes("utf-8");
			// 图片尺寸
			int imgSize = 67 + 12 * (size - 1);
			bufImg = new BufferedImage(imgSize, imgSize, BufferedImage.TYPE_INT_RGB);
			Graphics2D gs = bufImg.createGraphics();
			// 设置背景颜色
			gs.setBackground(Color.WHITE);
			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色> BLACK
			gs.setColor(Color.BLACK);
			// 设置偏移量，不设置可能导致解析出错
			int pixoff = 2;
			// 输出内容> 二维码
			if (contentBytes.length > 0 && contentBytes.length < 800) {
				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);
				for (int i = 0; i < codeOut.length; i++) {
					for (int j = 0; j < codeOut.length; j++) {
						if (codeOut[j][i]) {
							gs.fillRect(j * 3 + pixoff, i * 3 + pixoff, 3, 3);
						}
					}
				}
			} else {
				throw new Exception("QRCode content bytes length = " + contentBytes.length + " not in [0, 800].");
			}
			gs.dispose();
			bufImg.flush();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bufImg;
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






	/*public static String barcode(String content,String type, String logo) {
		Calendar calendar = Calendar.getInstance();
		long time = calendar.getTimeInMillis();
		String s = time + ".jpg";
		System.out.println(logo + "===logo");
		String b = PropertyUtil.localRootPath();
		SimpleDateFormat dateformat = new SimpleDateFormat("yyyy-MM-dd");
		String data = dateformat.format(new Date());
		String n = "/"+type+"/" + data + "/";
		String lj = PropertyUtil.remoteFileServer();
		String ljs = PropertyUtil.localRootPath();
		String logoPath = ljs + logo;
		if ("".equals(logo) || logo == null) {
			logoPath = ljs + "/logo/organDefault.png";
		}
		System.out.println(logoPath + "==============logoPath");
		try {
			int version = 6;
			int imgSize = 500 + 12 * (version - 1);
			Qrcode qrcodeHandler = new Qrcode();

			qrcodeHandler.setQrcodeErrorCorrect('M');

			qrcodeHandler.setQrcodeEncodeMode('B');

			qrcodeHandler.setQrcodeVersion(version);

			// System.out.println(content);

			byte[] contentBytes = content.getBytes("utf-8");
			System.out.println(contentBytes.length);
			// 构造一个BufferedImage对象 设置宽、高

			BufferedImage bufImg = new BufferedImage(imgSize, imgSize,
					BufferedImage.TYPE_INT_RGB);

			Graphics2D gs = bufImg.createGraphics();

			gs.setBackground(Color.WHITE);

			gs.clearRect(0, 0, imgSize, imgSize);

			// 设定图像颜色 > BLACK

			gs.setColor(Color.BLACK);

			// 设置偏移量 不设置可能导致解析出错

			int pixoff = 15;

			// 输出内容 > 二维码

			if (contentBytes.length > 0 && contentBytes.length < 200) {

				boolean[][] codeOut = qrcodeHandler.calQrcode(contentBytes);

				for (int i = 0; i < codeOut.length; i++) {

					for (int j = 0; j < codeOut.length; j++) {

						if (codeOut[j][i]) {

							gs.fillRect(j * 13 + pixoff, i * 13 + pixoff, 13,
									13);

						}

					}

				}

			} else {

				System.err.println("QRCode content bytes length = "
						+ contentBytes.length + " not in [ 0,120 ]. ");

				return null;

			}
			System.out.println(logoPath + "========lj+logo");
			File files = new File(logoPath);
			if (!files.exists()) {
				logoPath = ljs + "/logo/organDefault.png";
			}
			Image img = ImageIO.read(new File(logoPath));// 实例化一个Image对象。

			gs.drawImage(img,(imgSize - 120) / 2, (imgSize - 120) / 2, 120,
					120, null);
			gs.dispose();
			bufImg.flush();
			// 生成二维码QRCode图片
			File file = new File(b + n);
			if (!file.exists()) {
				file.mkdir();
			}

			File imgFile = new File(b + n + s);
			ImageIO.write(bufImg, "jpg", imgFile);

			return lj + n + s;
		} catch (Exception e) {
			e.printStackTrace();

			return null;
		}
	}

*/







}