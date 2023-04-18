package com.aurora.utils;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.Random;

public class drewCode {
	private static final char[] codeChar = "ABCDEFGHIJKLMNPQRSTUVWXYZ123456789".toCharArray();
	private static Random random = new Random();
	/**
	 * 获取随机验证码
	 * @param i
	 * @return
	 */
	public String getCode(int i) {
		int index =0;
		String code = "";
		for(int o=0;i>o;o++) {
			index = random.nextInt(codeChar.length);
			code=code+codeChar[index];
		}
		return code;
	}
	/**
	 * 绘制验证码图
	 */
	public BufferedImage creatImg(String code,int width,int height,int fontSize) throws Exception {
		BufferedImage bi = new BufferedImage(width, height,  BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setColor(Color.white);
		g.fillRect(0, 0, width, height);
		for(int i = 0;i<width/1.5;i++) {
		 	g.setColor(new Color(random.nextInt(0xffffff)));
		 	g.drawArc(random.nextInt(width), random.nextInt(height), width, height,random.nextInt(180), random.nextInt(180));
		}
		Font font = new Font("黑体",Font.BOLD,fontSize);
		g.setFont(font);
		for(int o=0;o<code.length();o++) {
			g.setColor(new Color(random.nextInt(0xffffff)));
			g.drawString(code.substring(o,o+1), o*fontSize/2, fontSize+(height-fontSize)/2);
		}
		return bi;
	}
}

