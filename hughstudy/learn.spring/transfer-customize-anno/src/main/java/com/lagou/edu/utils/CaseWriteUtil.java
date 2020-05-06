package com.lagou.edu.utils;

/**
 * @Author: Hugh
 * @Date: 2020/5/5
 */
public class CaseWriteUtil {
    /**
     * 首字母转小写
     *
     * @param simpleName
     * @return
     */
    public static String toLowerCaseFirstOne(String simpleName) {
        //去掉Impl
        if(simpleName.endsWith("Impl")){
            simpleName = simpleName.substring(0, simpleName.length() - 4);
        }
        if (Character.isLowerCase(simpleName.charAt(0))) {
            return simpleName;
        } else {
            return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
        }
    }

    public static void main(String[] args) {
        String myImpl = toLowerCaseFirstOne("myImpl");
        System.out.println(myImpl);
    }
}
