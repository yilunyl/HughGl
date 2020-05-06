package com.lagou.edu.context;


import com.lagou.edu.anno.GlAutowired;
import com.lagou.edu.anno.GlService;
import com.lagou.edu.anno.GlTransactional;
import com.lagou.edu.factory.ProxyFactory;
import com.lagou.edu.utils.CaseWriteUtil;
import com.lagou.edu.utils.ClassUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: Hugh
 * @Date: 2020/5/5
 */
@Slf4j
@SuppressWarnings("unchecked")
public class GlApplicationContext {

    private String packagePath;
    private ConcurrentHashMap<String, Object> singletonBean = new ConcurrentHashMap<>(30);
    private Set<Class<?>> transactionalClass = new HashSet<>(10);
    public GlApplicationContext(String packagePath) throws Exception {
        this.packagePath = packagePath;
        initBean();
        initEntryField();
    }

    private void initBean() throws Exception {

        Set<Class<?>> packageAllClass = ClassUtil.getClasses(packagePath);
        for (Class classInfo : packageAllClass) {
            GlService glervice = (GlService) classInfo.getDeclaredAnnotation(GlService.class);
            GlTransactional glTransactional = (GlTransactional) classInfo.getDeclaredAnnotation(GlTransactional.class);
            // 判断该类上属否存在 @GlService
            if(Objects.nonNull(glervice) && Objects.isNull(glTransactional)) {
                doCreateServiceBean(classInfo, glervice.value());
            }
            // 判断该类上属否存在 @GlTransactional
            if(Objects.nonNull(glTransactional)) {
                transactionalClass.add(classInfo);
                continue;
            }
            //判断方法上是否存在@GlTransactional
            Method[] methods = classInfo.getMethods();
            for(Method method:methods){
                GlTransactional annotation = method.getAnnotation(GlTransactional.class);
                if(Objects.nonNull(annotation)) {
                    transactionalClass.add(classInfo);
                    break;
                }
            }
        }
    }

    /**
     * 创建对象
     * @param  classInfo classInfo
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    private void doCreateServiceBean(Class classInfo, String name) throws IllegalAccessException, InstantiationException {
        if(Objects.isNull(classInfo)){
            return;
        }
        // 初始化对象
        Object newInstance = null;
        // 通过反射获取到该类对象
        newInstance = classInfo.newInstance();
        String beanId = name;
        if(StringUtils.isEmpty(name)) {
            // beanId = 类名首字母转小写
            beanId = CaseWriteUtil.toLowerCaseFirstOne(classInfo.getSimpleName());
        }
        singletonBean.put(beanId, newInstance);
    }


    /**
     *  初始化属性
     * @throws Exception Exception
     */
    private void initEntryField() throws Exception {
        // 1.遍历所有的bean容器对象
        for (Map.Entry<String, Object> entry : singletonBean.entrySet()) {
            // 2.判断属性上面是否有加注解GlService 自动注入
            Object bean = entry.getValue();
            attriAssign(bean);
        }
        //2.初始化事务bean
        for(Class classInfo : transactionalClass){
            initTransactionalBean(classInfo);
        }
        //3.清理事务类的集合
        transactionalClass.clear();
    }

    /**
     * 最后初始化事务bean
     * @param classInfo 类信息
     * @throws IllegalAccessException exceptoin
     * @throws InstantiationException exceptoin
     */
    private void initTransactionalBean(Class classInfo) throws Exception {
        //从生成好的bean中获取ProxyFactory
        Object instance = classInfo.newInstance();
        attriAssign(instance);
        ProxyFactory proxyFactory = (ProxyFactory)singletonBean.get("proxyFactory");
        if(Objects.nonNull(proxyFactory)){
            GlTransactional glTransactional = (GlTransactional) classInfo.getDeclaredAnnotation(GlTransactional.class);
            Object aopProxy = proxyFactory.getAopProxy(instance, Objects.nonNull(glTransactional));
            String beanId = CaseWriteUtil.toLowerCaseFirstOne(classInfo.getSimpleName());
            singletonBean.put(beanId, aopProxy);
        }
    }

    /**
     * 依赖注入
     * @param object 对象
     * @throws Exception Exception
     */
    private void attriAssign(Object object) throws Exception {
        //使用反射机制,获取当前类的所有属性
        Class<? extends Object> classInfo = object.getClass();
        Field[] declaredFields = classInfo.getDeclaredFields();

        //判断当前类属性是否存在注解
        for (Field field : declaredFields) {
            GlAutowired extResource = field.getAnnotation(GlAutowired.class);
            if (extResource != null) {
                // 获取属性名称
                String beanId = extResource.value();
                if(StringUtils.isEmpty(beanId)) {
                    beanId = field.getName();
                }
                Object bean = getBean(beanId);
                // 默认使用属性名称，查找bean容器对象 1参数 当前对象 2参数给属性赋值
                field.setAccessible(true);
                field.set(object, bean);

            }
        }
    }

    private Object getBean(String beanId) throws Exception {
        if (StringUtils.isEmpty(beanId)){
            throw new Exception("bean Id 不能为空");
        }
        //从spring 容器初始化对像
        Object object = singletonBean.get(beanId);
        if (object == null) {
            throw  new Exception("Class not found");
        }
        return object;
    }

    public Object getBeanByName(String name){
        try {
            return getBean(name);
        }catch (Exception e){
            return null;
        }
    }
}
