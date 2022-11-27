# 1.什么是循环依赖

在Spring里，指两个或多个bean互相依赖。比如有两个Bean(A，B)，A中注入B，B中注入A，这样就形成了循环依赖。Spring默认是支持循环依赖的，本文我们就从Spring源码层面对循环依赖进行分析。

# 2.环境构建

## 1.spring环境

本文是以Spring5.1.x源码环境构建的，或者找一个spring环境的项目，在项目的test下测试。也可以创建一个项目在其中引入spring的核心依赖.

> spring-core：依赖注入IOC与ID的最基本实现
> spring-beans：Bean工厂与bean的装配
> spring-context：spring的context上下文即IOC容器
> spring-expression：spring表达式语言

## 2.相关代码

### 1.bean

Aa类，其中将有参构造方法注释了，因为 **spring循环依赖不支持构造方法注入** 

```java
//@Component("a")
public class Aa {
    @Autowired
    private Bb b;

   /* public Aa(Bb b){
        this.b = b;
    }*/
}
```

Ba中注入Aa类

```java
public class Bb {
    @Autowired
    private Aa a;
}
```

### 2.config

扫描当前环境所在包，并且将Aa，Bb以JavaConfig的方式注入进来。其中 **Ab，Bb中方法名a,b ,也就是注入后对应bean的名称** 

```java
@Configuration
@ComponentScan("com.gaohwang.dependency")
public class Config {

    @Bean
    public Aa a() {
        return new Aa();
    }

    @Bean
    public Bb b() {
        return new Bb();
    }
}
```

### 3.test

```java
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
Aa a = applicationContext.getBean(Aa.class);
Bb b = applicationContext.getBean(Bb.class);
System.out.println(a);
System.out.println(b);
```

### 4.项目结构

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\jiegou.png)



## 3.调试技巧

spring在解决循环依赖的时候使用了三个缓存，位于 **DefaultSingletonBeanRegistry** 中的 **singletonObjects** 、 **singletonFactories** 、 **earlySingletonObjects** 三个Map。  **inCreationCheckExclusions**  用于存储正在创建的Bean的名称，我们也标记以下。

> **singletonObjects**  也就是我们所说的单例容器，里面存储的是完整的Bean
>
> **singletonFactories**  里面存储的是Bean工厂，可能Bean需要被代理，所以存的是工厂比较妥当
>
> **earlySingletonObjects**  存储的是Bean的半成品(此时还不是完整的Bean)

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\cacheMap.png)

我们在属性上加了 **@Autowired** ，Bean在实例化的过程中会进行属性填充(比如说Bb对象中有个Aa属性，spring会通过反射field.set(bean, value))，具体执行的代码在 **AutowiredAnnotationBeanPostProcessor**中的 **inject** 方法

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\field.png)

我们在上面三个Map的put方法，以及inject的时候打上条件断点(断点上右键)，条件均为 **"a".equals(beanName)||"b".equals(beanName)** 。

以及 **DefaultSingletonBeanRegistry** # **getSingleton** (java.lang.String, org.springframework.beans.factory.ObjectFactory<?>)  中的 **beforeSingletonCreation** 方法打上条件断点，这个方法就是检查Bean是否正在创建，并且往 **inCreationCheckExclusions** 中插入正在创建Bean的名称(将 **singletonsCurrentlyInCreation.add** 断点移到外面来，方便调试)

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\createBean.png)

所有的断点

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\duandian.png)



PS：如果你不清楚四个集合put(或add)的位置在哪，你可以**Ctrl+点击(鼠标)左键**，比如说  **singletonObjects** ，就可以看到有哪些地方使用这个集合。

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\singleton.png)

以上条件都准备好，就可以开启Debug调试了。

# 3.源码分析

**根据上面的六个断点，逐一跟踪进行分析。**

## 1.获取单例对象a

方法调用栈

```properties
getSingleton:266, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\1.png)

在没有指定优先级的时候，首先创建的肯定是a(spring默认按照字母顺序创建对象)。

创建a的时候，spring首先从 **singletonObjects** 中取，如果没有才会创建。在方法调用栈的 **doGetBean** 中有体现，在**doGetBean** 中有两次调用了 **getSingleton** 方法。

### 1.第一次 **getSingleton**  

```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
   // 检查一级缓存singletonObject是否存在
   Object singletonObject = this.singletonObjects.get(beanName);
   // 当前还不存在这个单例对象，
   // 且该对象正在创建中，即在singletonsCurrentlyInCreation正在创建bean列表中
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
         // 检查三级缓存earlySingletonObjects是否存在
         singletonObject = this.earlySingletonObjects.get(beanName);
         if (singletonObject == null && allowEarlyReference) {
            // 检查二级缓存singletonFactory是否可以创建
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
               // 二级缓存的对象工厂创建该对象
               singletonObject = singletonFactory.getObject();
               // 放入三级缓存earlySingletonObjects中 半成品的bean放入三级缓存中
               this.earlySingletonObjects.put(beanName, singletonObject);
               // 移除二级缓存
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}
```

首先从 **singletonObjects **获取Bean，如果没有，会进一步判断这个Bean是否处于创建过程中，也就是检查**inCreationCheckExclusions**  中是否存在当前Bean的名称。第一次 **getSingleton**  肯定是不存在的，因为Bean的名称在等二次 **getSingleton**  才放入 **inCreationCheckExclusions**  中(从上图断点处可看出)。

###  2.第二次 **getSingleton** 

```java
//前面省略。。。
sharedInstance = getSingleton(beanName, () -> {
   try {
      return createBean(beanName, mbd, args);
   } catch (BeansException ex) {
      // Explicitly remove instance from singleton cache: It might have been put there
      // eagerly by the creation process, to allow for circular reference resolution.
      // Also remove any beans that received a temporary reference to the bean.
      destroySingleton(beanName);
      throw ex;
   }
});
//后面省略。。。
```

以lambda表达式的形式，传入了一个接口方法

```java
public Object getSingleton(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(beanName, "Bean name must not be null");
   synchronized (this.singletonObjects) {
      Object singletonObject = this.singletonObjects.get(beanName);
      if (singletonObject == null) {
         if (this.singletonsCurrentlyInDestruction) {
            throw new BeanCreationNotAllowedException(beanName,
                  "Singleton bean creation not allowed while singletons of this factory are in destruction " +
                        "(Do not request a bean from a BeanFactory in a destroy method implementation!)");
         }
         if (logger.isDebugEnabled()) {
            logger.debug("Creating shared instance of singleton bean '" + beanName + "'");
         }
         // 将当前beanName放入singletonsCurrentlyInCreation中，以便解决循环依赖问题
         beforeSingletonCreation(beanName);
         boolean newSingleton = false;
         boolean recordSuppressedExceptions = (this.suppressedExceptions == null);
         if (recordSuppressedExceptions) {
            this.suppressedExceptions = new LinkedHashSet<>();
         }
         try {
            // getObject方法会调用AbstractAutowireCapableBeanFactory的createBean方法
            singletonObject = singletonFactory.getObject();
            newSingleton = true;
         } catch (IllegalStateException ex) {
            // Has the singleton object implicitly appeared in the meantime ->
            // if yes, proceed with it since the exception indicates that state.
            singletonObject = this.singletonObjects.get(beanName);
            if (singletonObject == null) {
               throw ex;
            }
         } catch (BeanCreationException ex) {
            if (recordSuppressedExceptions) {
               for (Exception suppressedException : this.suppressedExceptions) {
                  ex.addRelatedCause(suppressedException);
               }
            }
            throw ex;
         } finally {
            if (recordSuppressedExceptions) {
               this.suppressedExceptions = null;
            }
            //　创建单例的后置回调
            afterSingletonCreation(beanName);
         }
         if (newSingleton) {
            //push到单例容器中
            addSingleton(beanName, singletonObject);
         }
      }
      return singletonObject;
   }
}
```

```java
@FunctionalInterface
public interface ObjectFactory<T> {

   /**
    * Return an instance (possibly shared or independent)
    * of the object managed by this factory.
    * @return the resulting instance
    * @throws BeansException in case of creation errors
    */
   T getObject() throws BeansException;

}
```

第二次 **getSingleton**  首先进行加锁，锁的的 **this.singletonObjects** 对象(这说明spring的创建Bean的时候可能开的多线程)。获取锁后

- 首先还是会尝试从 **singletonObjects** 中获取，没有的话，就会将当前获取bean的名称通过 **beforeSingletonCreation** 方法加入到 **inCreationCheckExclusions**  中，标明这个Bean正处于创建过程中。
- 通过 **singletonFactory.getObject()**，回调到之前 **getSingleton** 的 **createBean** 方法。 **createBean** 执行完毕后，才会走下面的代码
- 执行 **afterSingletonCreation** 方法，此时Bean创建完成，将Bean名称从 **inCreationCheckExclusions**  中移除，然后通过 **addSingleton **方法将创建的Bean添加到 **singletonObjects** 中，并从**singletonFactories**、**earlySingletonObjects**中移除

## 2.将当前正在创建的Bean(a)加入二级缓存

方法调用栈

```properties
addSingletonFactory:184, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doCreateBean:579, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\2.png)

从方法调用栈可以看出 **lambda$doGetBean$0** 中就是我们刚才 **getSingleton** 回调 **createBean**的位置。从方法调用栈可以看出，**createBean** -> **doCreateBean** -> **addSingletonFactory** 。 

  **getBean**  ->  **doGetBean**     **createBean** -> **doCreateBean**  这个命名有没有很相似。

首先我们着重分析下 **doCreateBean** 这个方法

### 1.**doCreateBean** 完成Bean的创建

doCreateBean 中涉及的方法众多，里面关乎着整个Bean创建。由于本文只对循环依赖进行分析，所以只对其中的部分代码进行分析

```java
//...
// 1. 调用构造函数创建该bean对象，若不存在构造函数注入，顺利通过
if (instanceWrapper == null) {
   //创建对象   完成对象的创建
   //fixme 第二次调用后置处理器（在里面）
   instanceWrapper = createBeanInstance(beanName, mbd, args);
}
final Object bean = instanceWrapper.getWrappedInstance();

//...

// Spring默認是支持循環依賴的
// Eagerly cache singletons to be able to resolve circular references
// even when triggered by lifecycle interfaces like BeanFactoryAware.
// 急切地缓存单例以便能够解析循环引用
// 即使是由生命周期接口(如BeanFactoryAware)触发的。
boolean earlySingletonExposure = (mbd.isSingleton() && this.allowCircularReferences &&
      isSingletonCurrentlyInCreation(beanName));
if (earlySingletonExposure) {
   if (logger.isTraceEnabled()) {
      logger.trace("Eagerly caching bean '" + beanName +
            "' to allow for resolving potential circular references");
   }
   // 提前暴露一个工厂
   // 2. 在singletonFactories缓存中，放入该bean对象，以便解决循环依赖问题
   //fixme 实例化bean，执行第四次后置处理器 ：为解决循环依赖中，对象提早暴露的问题，一般情况下直接暴漏出来，在有aop动态代理时提前返回代理后的对象，此处有个小瑕疵，虽然代码实在这，实际执行时在populateBean（）的属性注入中
   addSingletonFactory(beanName, () -> getEarlyBeanReference(beanName, mbd, bean));
}
// Initialize the bean instance.
Object exposedObject = bean;
try {
   // 3. populateBean方法：bean对象的属性赋值  属性填充
   populateBean(beanName, mbd, instanceWrapper);
   //主要执行各种生命周期回调方法
   exposedObject = initializeBean(beanName, exposedObject, mbd);
} catch (Throwable ex) {
   if (ex instanceof BeanCreationException && beanName.equals(((BeanCreationException) ex).getBeanName())) {
      throw (BeanCreationException) ex;
   } else {
      throw new BeanCreationException(
            mbd.getResourceDescription(), beanName, "Initialization of bean failed", ex);
   }
}
//...
```

首先我们需要知道什么是实例化和初始化

> 实例化：通过反射创建(newInstance)了对象,还未作任何处理，不是一个完整的Bean
>
> 初始化：是一个完整的Bean，属性填充完毕，完成各种生命周期回调函数

实例化操作就对应以上的 **createBeanInstance** 方法

初始化化操作就对应以上的 **initializeBean** 方法

- 通过**createBeanInstance** 完成Bean的实例化，其中会有推断构造方法，选择合适的构造方法创建Bean实例对象

- **spring默认是支持循环依赖** ，因为 **allowCircularReferences的值默认为true** 。如果创建的Bean是单例，并且正处于创建的过程中(通过isSingletonCurrentlyInCreation判断，之前的第二次getSingleton就放到inCreationCheckExclusions中了)，那么spring就会暴露当前Bean的一个工厂。也就是通过 **addSingletonFactory** 方法 添加到 **singletonFactories** 中。
- 通过populateBean方法完成属性填充，其中会借助各种后置处理器(BeanPostProcessors)完成
- 调用initializeBean方法完成Bean的初始化

### 2.addSingletonFactory 暴露Bean工厂

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(singletonFactory, "Singleton factory must not be null");
   //singletonObjects 一级缓存map
   synchronized (this.singletonObjects) {
      /*
       * 如果单例池当中不存在才会Add,这里主要是为了循环依赖服务的代码
       * 如果bean存在单例池的话,已经是一个完整的bean
       */
      if (!this.singletonObjects.containsKey(beanName)) {
         //将工厂对象put到singletonFactories二级缓存
         this.singletonFactories.put(beanName, singletonFactory);
         /*
          * 从三级缓存earlySingletonObjects中移除掉当前bean
          * 确保唯一性
          */
         this.earlySingletonObjects.remove(beanName);
         this.registeredSingletons.add(beanName);
      }
   }
}
```

## 3.属性填充，获取b

方法调用栈

```properties
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
inject:94, InjectionMetadata (org.springframework.beans.factory.annotation)
postProcessProperties:355, AutowiredAnnotationBeanPostProcessor (org.springframework.beans.factory.annotation)
populateBean:1420, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:586, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\3.png)

从方法调用栈以及上图可以看出，当前创建的仍然是a这个Bean，在 **populateBean** 属性填充的时候，会调用 **AutowiredAnnotationBeanPostProcessor** (因为我们是通过**@Autowired**注入的属性)这个后置处理器的 **postProcessProperties** 方法，其中通过 **inject** 方法完成。

在 **inject** 中，首先获取依赖的属性(b)然后通过 **beanFactory.resolveDependency** 去获取依赖的属性(b)。



**PS:以上就是创建a这个Bean的过程，此时a并不是一个完整的Bean，在属性填充的时候发现依赖b，这时候就要以上面的流程去创建b这个bean。**

## 4.获取依赖对象b

方法调用栈

```properties
getSingleton:266, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support) [2]
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\4.jpg)

走到这一步，说明也是第二次调用getSingletonle方法。和上面创建a的流程以模一样，首先把当前Bean的名称(b),放入 **singletonsCurrentlyInCreation** 标明(b)正在创建。 **此时singletonsCurrentlyInCreation 中有两个正在创建的Bean名称了，分别为a,b**

## **5.将当前正在创建的Bean(b)加入**二级缓存

方法调用栈

```properties
addSingletonFactory:184, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doCreateBean:579, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\5.png)

这个也和创建Bean a的过程以模一样。**这个方法结束后，singletonFactories中就存在a，b的工厂对象。**

## 6.属性填充，获取a

方法调用栈

```properties
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation) [2]
inject:94, InjectionMetadata (org.springframework.beans.factory.annotation)
postProcessProperties:355, AutowiredAnnotationBeanPostProcessor (org.springframework.beans.factory.annotation)
populateBean:1420, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:586, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation) [1]
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\6.png)

此时Bean b正在创建的时候，属性填充(populateBean)的时候，发现b里面依赖了a，这个时候又会通过 **beanFactory.resolveDependency** 去获取a。

## 7.从二级缓存中获取a

函数调用栈

```properties
getSingleton:233, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
getSingleton:198, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:243, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
inject:94, InjectionMetadata (org.springframework.beans.factory.annotation)
postProcessProperties:355, AutowiredAnnotationBeanPostProcessor (org.springframework.beans.factory.annotation)
populateBean:1420, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:586, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\7.png)

在获取Bean a的时候， **doGetBean中会有两个 getSingleton 方法** 。之前也提到过，由于这点很重要，所以笔者在此把阐述一遍。

**第一个getSingleton方法的作用：从一级缓存(singletonObjects)中获取Bean，如果不存在(存在就直接返回)并且该Bean正处于创建的时候，那么尝试从三级缓存中获取Bean，如果还是没有(存在就直接返回)，就从二级缓存(singletonFactories)中获取并存入三级缓存中，同时移除二级缓存。**

**第一个getSingleton方法的作用：主要是创建Bean，并将Bean放入到一级缓存(singletonObjects)中。**

因为a正处于创建的过程中，并在之前加入到了二级缓存(同时也移除了三级缓存)。所以可以从二级缓存中获取a，但a此时并不是一个完整的Bean，因为属性b还没有赋值。将a添加到三级缓存后，立刻将a从二级缓存中移除。

## 8.通过反射给b设置属性a

方法调用栈

```properties
inject:598, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation) [2]
inject:94, InjectionMetadata (org.springframework.beans.factory.annotation)
postProcessProperties:355, AutowiredAnnotationBeanPostProcessor (org.springframework.beans.factory.annotation)
populateBean:1420, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:586, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation) [1]
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\8.png)

Bean b通过  **beanFactory.resolveDependency** 获取了Bean a 的半成品(因为a中的b属性还是为空！当然，后面肯定会填充的)，通过反射给b中的a属性赋值。

## 9.Bean b创建完成，加入一级缓存

```properties
addSingleton:156, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
getSingleton:299, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
inject:571, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
//下面的省略，下面的就是创建a这个Bean的过程
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\9.png)

此时Bean b以及创建完毕了，此时要做的是将Bean b加入到一级缓存(singletonObjects)中，并移除二三级缓存。PS：三级缓存(earlySingletonObjects)此时是没有Bean b的半成品的，只有Bean a的半成品，二级缓存有Bean b的工厂。因为，Bean a已经在前面从二级缓存中取出，放入到三级缓存中了，而Bean b没有变化。

## 10.通过反射给a设置属性b

方法调用栈

```properties
inject:598, AutowiredAnnotationBeanPostProcessor$AutowiredFieldElement (org.springframework.beans.factory.annotation)
inject:94, InjectionMetadata (org.springframework.beans.factory.annotation)
postProcessProperties:355, AutowiredAnnotationBeanPostProcessor (org.springframework.beans.factory.annotation)
populateBean:1420, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:586, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\10.png)

正准备给Bean a 中的b属性设置值，此时的情况是Bean b中已经注入了Bean a

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\11.png)

当我们将断点往下走一步的时候，发现Bean a 与 Bean b之间的依赖关系已经完美的解决了。

## 11.Bean a创建完成，加入一级缓存

```properties
addSingleton:156, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
getSingleton:299, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support)
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\12.png)

将Bean a，加入到以及缓存中，并且移除二三级缓存。



到此，a ，b的创建已经结束。

# 4.为什么构造方法注入,和原型(prototype)为什么不支持循环依赖？

## 1.原型

因为Spring在初始化的时候已经维护好了那些单例Bean并且已经放在了singletonObjects当中，原型的Bean此时还没创建，并且Spring源码AbstractAutowireCapableBeanFactory#doCreateBean下mbd.isSingleton()也已经判断了。

## 2.构造方法注入

我们将代码改一下

Aa

```java
@Component("a")
public class Aa {
//    @Autowired
    private Bb b;

    public Aa(Bb b){
        this.b = b;
    }
}
```

Config

```java
@Configuration
@ComponentScan("com.gaohwang.dependency")
public class Config {

    /*@Bean
    public Aa a() {
        return new Aa();
    }*/

    @Bean
    public Bb b() {
        return new Bb();
    }
}
```

将Aa中的属性改为构造方法注入。

```properties
Unsatisfied dependency expressed through constructor parameter 0; nested exception is org.springframework.beans.factory.UnsatisfiedDependencyException: Error creating bean with name 'b': Unsatisfied dependency expressed through field 'a'; nested exception is org.springframework.beans.factory.BeanCurrentlyInCreationException: Error creating bean with name 'a': Requested bean is currently in creation: Is there an unresolvable circular reference?
```

报错的意思大概就是b里无法注入a，a中无法注入b，spring在这种情况下直接抛出了异常，以免死循环。

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\13.png)

这里就是报错的位置。

断点停到第一次获取b的时候

函数调用栈

```properties
getSingleton:266, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support) [2]
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
resolveCandidate:277, DependencyDescriptor (org.springframework.beans.factory.config)
doResolveDependency:1242, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveDependency:1165, DefaultListableBeanFactory (org.springframework.beans.factory.support)
resolveAutowiredArgument:857, ConstructorResolver (org.springframework.beans.factory.support)
createArgumentArray:760, ConstructorResolver (org.springframework.beans.factory.support)
autowireConstructor:218, ConstructorResolver (org.springframework.beans.factory.support)
autowireConstructor:1345, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBeanInstance:1184, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
doCreateBean:539, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
createBean:492, AbstractAutowireCapableBeanFactory (org.springframework.beans.factory.support)
lambda$doGetBean$0:318, AbstractBeanFactory (org.springframework.beans.factory.support)
getObject:-1, 1663411182 (org.springframework.beans.factory.support.AbstractBeanFactory$$Lambda$12)
getSingleton:274, DefaultSingletonBeanRegistry (org.springframework.beans.factory.support) [1]
doGetBean:316, AbstractBeanFactory (org.springframework.beans.factory.support)
getBean:192, AbstractBeanFactory (org.springframework.beans.factory.support)
preInstantiateSingletons:848, DefaultListableBeanFactory (org.springframework.beans.factory.support)
finishBeanFactoryInitialization:923, AbstractApplicationContext (org.springframework.context.support)
refresh:580, AbstractApplicationContext (org.springframework.context.support)
<init>:95, AnnotationConfigApplicationContext (org.springframework.context.annotation)
main:16, DependencyTest (com.gaohwang.dependency.test)
```

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\14.png)

从方法调用栈可以看出，Bean a在实例化(createBeanInstance)的时候提前去获取Bean b对象，此时还没将Bean a缓存到二级缓存中。因为，在doCreateBean中，是先执行createBeanInstance然后再执行addSingletonFactory(暴露Bean工厂放入二级缓存)。

这样就会导致创建Bean b的时候属性填充获取Bean a 就无法通过doGetBean中的第一个getSingleton获取到半成品的Bean a，那么就会通过第二个getSingleton创建Bean a。但此时Bean a也正处于创建过程,所以spring直接就抛出了异常，以免死循环。

总的来说，构造方法注入会提前获取依赖，但自身又没有暴露Bean工厂(添加到二级缓存)，导致依赖方在获取依赖时，无法从二级缓存获取半成品Bean，而重新去创建该Bean。

白话文：a通过构造方法注入b，spring会采用有参构造实例化a.实例化的时候,会从容器中获取b(但此时a还未放入二级缓存中)。创建b的时候，需要属性填充，发现又需要a。因为一二三级缓存都没有a，所以又会重新创建a，但a又处于正在创建的过程，所以就有问题了。

## 3.spring三个缓存放入时机

> singletonObjects             一级缓存
>
> singletonFactories          二级缓存
>
> earlySingletonObjects    三级缓存
>
> PS：名称暂且按照再spring中的顺序来命名。

### 1.首先暴露Bean工厂，将Bean工厂放入二级缓存，并且移除三级缓存。

AbstractAutowireCapableBeanFactory#doCreateBean调用 addSingletonFactory

```java
protected void addSingletonFactory(String beanName, ObjectFactory<?> singletonFactory) {
   Assert.notNull(singletonFactory, "Singleton factory must not be null");
   //singletonObjects 一级缓存map
   synchronized (this.singletonObjects) {
      /*
       * 如果单例池当中不存在才会Add,这里主要是为了循环依赖服务的代码
       * 如果bean存在单例池的话,已经是一个完整的bean
       */
      if (!this.singletonObjects.containsKey(beanName)) {
         //将工厂对象put到singletonFactories二级缓存
         this.singletonFactories.put(beanName, singletonFactory);
         /*
          * 从三级缓存earlySingletonObjects中移除掉当前bean
          * 确保唯一性
          */
         this.earlySingletonObjects.remove(beanName);
         this.registeredSingletons.add(beanName);
      }
   }
}
```

### 2.紧接着从二级缓存中获取半成品Bean，并从二级缓存中移除，放入三级缓存中

AbstractBeanFactory#doGetBean调用第一个getSingleton

```java
protected Object getSingleton(String beanName, boolean allowEarlyReference) {
   // 检查一级缓存singletonObject是否存在
   Object singletonObject = this.singletonObjects.get(beanName);
   // 当前还不存在这个单例对象，
   // 且该对象正在创建中，即在singletonsCurrentlyInCreation正在创建bean列表中
   if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
      synchronized (this.singletonObjects) {
         // 检查三级缓存earlySingletonObjects是否存在
         singletonObject = this.earlySingletonObjects.get(beanName);
         if (singletonObject == null && allowEarlyReference) {
            // 检查二级缓存singletonFactory是否可以创建
            ObjectFactory<?> singletonFactory = this.singletonFactories.get(beanName);
            if (singletonFactory != null) {
               // 二级缓存的对象工厂创建该对象
               singletonObject = singletonFactory.getObject();
               // 放入三级缓存earlySingletonObjects中 半成品的bean放入三级缓存中
               this.earlySingletonObjects.put(beanName, singletonObject);
               // 移除二级缓存
               this.singletonFactories.remove(beanName);
            }
         }
      }
   }
   return singletonObject;
}
```

### 3.最后将Bean放入一级缓存，并移除二三级缓存

DefaultSingletonBeanRegistry#getSingleton调用addSingleton

```java
protected void addSingleton(String beanName, Object singletonObject) {
   //同步单例容器singletonObjects
   synchronized (this.singletonObjects) {
      this.singletonObjects.put(beanName, singletonObject);
      //移除二级缓存
      this.singletonFactories.remove(beanName);
      //移除三级缓存
      this.earlySingletonObjects.remove(beanName);
      this.registeredSingletons.add(beanName);
   }
}
```

### 4.总结

spring最开始是放入二级缓存，并移除三级缓存；然后从二级缓存中获取半成品Bean放入三级缓存，移除二级缓存；最后放入一级缓存，并移除二三级缓存。

再一二三级缓存中只会有一个缓存中会存在Bean的相关信息，因为每一操作的时候都会移除其他缓存的信息(除了移除一级缓存)。再put到二三级缓存中，此时一级缓存肯定是没有该Bean的，所以没有移除一级。一级缓存中存的是我们最终创建完整的Bean。

# 5.图解

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\dependency\test\assets\15.png)

上图就是笔者根据源码调试一步步绘出的。

# 6.总结

读者可根据上述中的断点一步步调试，多试几次，如果有不清楚的可参照本文。只有自己调试过后，才能知道spring源码设计的精髓。方法的命名get do意义，doGetBean中为什么有两个getSingleton方法，为什么需要三级缓存解决循环依赖等等，阅读源码才过后会有自己的见解。