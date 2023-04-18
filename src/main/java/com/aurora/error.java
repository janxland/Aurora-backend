package com.aurora;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class error {
	public error(Exception e){
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			log.error("错误",e);
			FileOutputStream fos=new FileOutputStream(new File("/usr/local/tomcat/logs/generalBus.txt"),true);
			fos.write(("【UACTION.war】"+e.toString()+"\r\n\r\n"+"\n").getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e1) {
		}
		
	}
	public error(String msg){
		try {
			SimpleDateFormat sdf=new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			FileOutputStream fos=new FileOutputStream(new File("/usr/local/tomcat/logs/generalBus.txt"),true);
			log.info(msg);
			fos.write(("【UACTION.war】"+String.valueOf(sdf.format(date))+msg+"\r\n\r\n").getBytes());
			fos.flush();
			fos.close();
		} catch (Exception e1) {
		}
	}
}
