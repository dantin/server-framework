package com.demo2do.core.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Class工具类
 *
 * @author David
 */
public abstract class ClassUtil {

    /**
     * 获取某一包内指定目标类的所有子类，包括其实现类
     *
     * @param basePackage 包名
     * @param target      目标类（父类或接口）
     * @return target的所有子类，包括其实现类
     * @throws java.io.IOException
     */
    public static List<Class<?>> getAllSubClass(final String basePackage, final Class<?> target) throws IOException {
        List<Class<?>> classes = new ArrayList<Class<?>>();

        // 获得包内的所有类名
        for (Class<?> clazz : getAllClass(basePackage, true)) {
            // 如果clazz是target的子类
            if (target.isAssignableFrom(clazz) && !target.equals(clazz)) {
                classes.add(clazz);
            }
        }

        return classes;
    }

    /**
     * 获取某一包内的所有类（包括该包的所有子包）
     *
     * @param basePackage 包名
     * @return 包内的所有类
     * @throws java.io.IOException
     */
    public static List<Class<?>> getAllClass(final String basePackage) throws IOException {
        return getAllClass(basePackage, true);
    }

    /**
     * 获取某一包内的所有类
     *
     * @param basePackage 包名
     * @param recursive   是否遍历子包
     * @return 包内所有类
     * @throws java.io.IOException
     */
    public static List<Class<?>> getAllClass(final String basePackage, final boolean recursive) throws IOException {
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        String packagePath = basePackage.replace(".", "/");
        Enumeration<URL> resources = loader.getResources(packagePath);

        List<Class<?>> classes = new ArrayList<Class<?>>();

        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            if (url != null) {
                String type = url.getProtocol();
                if ("file".equals(type)) {
                    scanPackageByFile(classes, basePackage, url.getPath(), recursive);
                } else if ("jar".equals(type)) {
                    scanPackageByJar(classes, basePackage, url, recursive);
                }
            } else {
                scanPackageByJars(classes, basePackage, ((URLClassLoader) loader).getURLs(), recursive);
            }
        }

        return CommonUtil.deduplicate(classes);
    }

    /**
     * 从项目文件中获取某一包内的所有类
     *
     * @param classes     类的保存列表
     * @param packageName 包名
     * @param filePath    文件路径
     * @param recursive   是否遍历子包
     */
    private static void scanPackageByFile(List<Class<?>> classes, final String packageName, final String filePath, final boolean recursive) {
        File file = new File(filePath);
        if (!file.exists() || !file.isDirectory()) {
            return;
        }

        File[] children = file.listFiles(new FileFilter() {
            @Override
            public boolean accept(File file) {
                if (file.isDirectory()) {
                    return recursive;
                }
                return filterClassName(file.getName());
            }
        });

        for (File child : children) {
            StringBuilder current = new StringBuilder(packageName);
            if (child.isDirectory()) {
                current.append(".").append(child.getName());
                scanPackageByFile(classes, current.toString(), child.getAbsolutePath(), recursive);
            } else {
                String fileName = child.getName();
                current.append(".").append(fileName.substring(0, fileName.length() - ".class".length()));
                String className = current.toString();

                Class<?> clazz = null;

                try {
                    clazz = Thread.currentThread().getContextClassLoader().loadClass(className);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                if (clazz != null) {
                    classes.add(clazz);
                }
            }
        }
    }

    /**
     * 从Jar文件中获取某一包内的所有类
     *
     * @param classes     类的保存列表
     * @param packageName 包名
     * @param jarUrl      Jar文件路径
     * @param recursive   是否遍历子包
     */
    private static void scanPackageByJar(List<Class<?>> classes, final String packageName, final URL jarUrl, final boolean recursive) {


        String packagePath = packageName.replace(".", "/");
        try {
            JarFile jarFile = ((JarURLConnection) jarUrl.openConnection()).getJarFile();
            Enumeration<JarEntry> entrys = jarFile.entries();
            while (entrys.hasMoreElements()) {
                JarEntry jarEntry = entrys.nextElement();
                String entryName = jarEntry.getName();
                if (!entryName.startsWith(packagePath) || jarEntry.isDirectory()) {
                    continue;
                }

                if (!recursive && entryName.lastIndexOf('/') != packagePath.length()) {
                    continue;
                }

                String clazzSimpleName = entryName.substring(entryName.lastIndexOf('/') + 1);
                if (filterClassName(clazzSimpleName)) {
                    String clazzName = entryName.replace('/', '.');
                    clazzName = clazzName.substring(0, clazzName.length() - ".class".length());

                    Class<?> clazz = null;

                    try {
                        clazz = Thread.currentThread().getContextClassLoader().loadClass(clazzName);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    if (clazz != null) {
                        classes.add(clazz);
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从所有jar文件中获取某一包内的所有类
     *
     * @param classes     类的保存列表
     * @param packageName 包名
     * @param jarUrls     Jar文件路径
     * @param recursive   是否遍历子包
     */
    private static void scanPackageByJars(List<Class<?>> classes, final String packageName, final URL[] jarUrls, final boolean recursive) {

        if (jarUrls != null) {
            for (URL jarUrl : jarUrls) {
                String urlPath = jarUrl.getPath();
                // 不必搜索classes文件夹
                if (urlPath.endsWith("classes/")) {
                    continue;
                }
                scanPackageByJar(classes, packageName, jarUrl, recursive);
            }
        }
    }

    /**
     * 判断是否为类名
     *
     * @param fileName 文件名
     * @return 若是则返回true；反之返回false
     */
    private static boolean filterClassName(final String fileName) {
        return fileName.endsWith(".class");
    }
}
