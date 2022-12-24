package com.project.tests;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.ArrayList;

public class RunTests {
    public static void main(String a[]) {
        ArrayList<Test> tests = new ArrayList<>();
        String currentPath = RunTests.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String packageName = RunTests.class.getPackageName().replace('.', '/');
        File[] testFiles = new File(currentPath+packageName).listFiles();
        if(testFiles==null) {
            System.out.println("can't find folder in tests/RunTests");
            return;
        }
        for(File file: testFiles) {
            if(file.isFile() && file.getName().endsWith(".class")) {
                String className = packageName.replace('/', '.')+"."+file.getName().substring(0, file.getName().length()-6);
                try {
                    Class<?> aClass = Class.forName(className);
                    if(aClass.getInterfaces().length<1) continue;
                    if (Test.class.equals(aClass.getInterfaces()[0])) {
                        Constructor<?> constructor = aClass.getConstructors()[0];
                        Test test = (Test) constructor.newInstance();
                        tests.add(test);
                    }
                }
                catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }
        for(Test test: tests) {
            boolean output = test.run();
            if(!output) {
                System.out.println(test.getClass().getName() + " fails");
            }
        }
    }
}