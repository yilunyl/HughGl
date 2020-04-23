package com.hugh.io;

import java.io.InputStream;

/**
 * @Classname Resource
 * @Description TODO
 * @Date 2020/4/21 9:42 下午
 * @Created by gule
 */
public class Resource {

    /**
     * 根据配置文件加载字节流
     * @param path 路径
     * @return 输入字节流
     */
    public static InputStream getResourceAsStream(String path){

        return Resource.class.getClassLoader().getResourceAsStream(path);
    }
}
