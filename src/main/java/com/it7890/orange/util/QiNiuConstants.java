package com.it7890.orange.util;

public class QiNiuConstants {

	/** 七牛云密钥 AK **/
	public static final String ACCESS_KEY = "K5Xav82rEPQWgVaueXEhtBQ8ZRe9CsE_BQVVv6JN";
	
	/** 七牛云密钥 SK **/
	public static final String SECRET_KEY = "J7NNTqlscIrKf0CQQI5FQL4BhtF--hkMlDcWV5S8";
	
	/** 七牛云空间，相当于目录 **/
	public static final String SCOPE = "nian30";
	
	/** 七牛云 资源域名 **/
	public static final String DOMAIN = "http://nian30.qiniudn.com/";
	
	/** 七牛云私有空间 **/
	public static final String SECRET_SCOPE = "toupaituijian";
	
	/** 七牛云私有空间域名 **/
	public static final String SECRET_DOMAIN = "http://7tszrx.com1.z0.glb.clouddn.com/";

	/** 七牛云私有空间简单域名 **/
	public static final String SECRET_SIMPLE_DOMAIN = "7tszrx.com1.z0.glb.clouddn.com";
	
//	/** 七牛云私有空间 **/
//	public static final String TEST_SECRET_SCOPE = "testtoupaituijian";
//	
//	/** 七牛云私有空间域名 **/
//	public static final String TEST_SECRET_DOMAIN = "http://7u2rwx.com1.z0.glb.clouddn.com/";
//	
//	/** 七牛云私有空间简单域名 **/
//	public static final String TEST_SECRET_SIMPLE_DOMAIN = "7u2rwx.com1.z0.glb.clouddn.com";
	
	/** 七牛云触发持久化处理接口 **/
	public static final String PFOP_INTERFACE = "http://api.qiniu.com/pfop/";
	
	/** 七牛云持久化结果查询 **/
	public static final String PREFOP_INTERFACE = "http://api.qiniu.com/status/get/prefop?id=";
}
