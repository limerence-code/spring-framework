package com.gaohwang.dependency.test;

import com.gaohwang.dependency.bean.Aa;
import com.gaohwang.dependency.bean.Bb;
import com.gaohwang.dependency.config.Config;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

/**
 * @Description:
 * @Author: gaoh
 * @Date: 2021/3/4 16:41
 * @Version: 1.0
 */
public class DependencyTest {
    public static void main(String[] args) {
        AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
        Aa a = applicationContext.getBean(Aa.class);
        Bb b = applicationContext.getBean(Bb.class);
        System.out.println(a);
        System.out.println(b);
        //NoUniqueBeanDefinitionException
       /* Object o = new Object();
		try {
			TimeUnit.SECONDS.sleep(5);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(ClassLayout.parseInstance(o).toPrintable());
		synchronized (o){
			System.out.println(ClassLayout.parseInstance(o).toPrintable());
		}*/
//		System.out.println(ClassLayout.parseInstance(new Cat()).toPrintable());

		//      0     4        (object header)                           05 01 00 00 (00000101 00000001 00000000 00000000) (261)
		//      4     4        (object header)                           00 00 00 00 (00000000 00000000 00000000 00000000) (0)
		//      8     4        (object header)                           e5 01 00 f8 (11100101 00000001 00000000 11111000) (-134217243)


    }
}
