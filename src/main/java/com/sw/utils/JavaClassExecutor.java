package com.sw.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Wang Hao
 * @date 2023/7/2 18:57
 * @description 执行外部传过来的一个代表java类的byte数组
 * 执行过程：
 * 1.清空HackSystem中的缓存
 * 2.通过ClassModifier修改需要修改的字节数组
 * 3.调用ClassModifier#modifyUTF8Constant方法修改：
 * java/lang/System -> com/jvm/ch9/remoteinvoke/HackSystem
 * 4.通过类加载器将字节数组加载为Class对象
 * 5.反射调用Class对象的main方法
 * 6.从HackSystem获取返回结果
 */
public class JavaClassExecutor {

    public static String execute(byte[] classBytes, String systemIn) {
        ClassModifier classModifier = new ClassModifier(classBytes);
        byte[] modifyBytes = classModifier.modifyUTF8Constant("java/lang/System", "com/sw/utils/HackSystem");
        modifyBytes = classModifier.modifyUTF8Constant("java/util/Scanner", "com/sw/utils/HackScanner");

        //设置用户传入的标准输入
        ((HackInputStream) HackSystem.in).set(systemIn);

        HotSwapClassLoader classLoader = new HotSwapClassLoader();
        Class clazz = classLoader.loadByte(modifyBytes);

        try {
            Method method = clazz.getMethod("main", new Class[]{String[].class});
            method.invoke(null, new String[]{null});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            /*
            被调用的方法的内部抛出了异常而没有被捕获时，将由此异常接收，
            由于这部分异常是远程执行代码的异常，我们要把异常栈反馈给客户端，
            所以不能使用默认的无参 printStackTrace() 把信息 print 到 System.err 中，
            而是要把异常信息 print 到 HackSystem.err 以反馈给客户端
            */
            e.getCause().printStackTrace(HackSystem.err);
        }

        String res = HackSystem.getBufferString();
        HackSystem.closeBuffer();
        return res;
    }
}
