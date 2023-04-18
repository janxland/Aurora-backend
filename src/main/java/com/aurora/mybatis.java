package com.aurora;

import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties.Jedis;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class mybatis {
	public static SqlSessionFactory sqlSessionFactory;
	public static Jedis jedis;
	public mybatis(){
		// 得到配置文件流
		InputStream inputStream = null;
		try {
			// 创建会话工厂，传入 MyBatis 的配置文件信息
			inputStream = Resources.getResourceAsStream("mybatis.cfg.xml");
			log.info("数据库连接成功！");
			new error("数据库连接成功!");
		} catch (Exception e) {
			log.error("数据库连接失败", e);
			new error("数据库连接失败!");
		} finally {
			
		}
		sqlSessionFactory = new SqlSessionFactoryBuilder().build(inputStream);

	}

		private static ThreadLocal<SqlSession> threadLocal = new ThreadLocal<SqlSession>();
		public static SqlSession openSession() {
			return sqlSessionFactory.openSession();
		}
		public static void closeSession(SqlSession session){
			session.close();
		}
		/**
		 * 获取session 并写入线程中 在线程内随时拿取
		 * @return
		 */
	    public static SqlSession getSession() {
	        SqlSession session=threadLocal.get();
	        if(session==null) {
	            threadLocal.set(sqlSessionFactory.openSession());
	        }
	        return threadLocal.get();
//	    	return sqlSessionFactory.openSession();
	    }
		/**
		 * 关闭SqlSession与当前线程分开
		 */
		public static void closeSession(){
			//从当前线程中获取SqlSession对象
			SqlSession sqlSession = threadLocal.get();
			//如果SqlSession对象非空
			if(sqlSession != null){
				//关闭SqlSession对象
				sqlSession.close();
				//分开当前线程与SqlSession对象的关系，目的是让GC尽早回收
				threadLocal.remove();
			}
		}
}