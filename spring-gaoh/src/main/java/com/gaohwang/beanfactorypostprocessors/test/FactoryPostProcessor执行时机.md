# 1.介绍

 **BeanFactoryPostProcessor** BeanFactory后置处理器 

官方解释

> 允许自定义修改应用程序上下文的Bean定义，以适应上下文基础Bean工厂的Bean属性值。 <p>应用程序上下文可以在其Bean定义中自动检测BeanFactoryPostProcessor Bean，并在创建任何其他Bean之前应用它们。 <p>对于面向系统管理员的自定义配置文件很有用，这些文件覆盖了在应用程序上下文中配置的Bean属性。 <p>请参阅PropertyResourceConfigurer及其具体实现，以了解解决此类配置需求的即用型解决方案。 <p> BeanFactoryPostProcessor可以与Bean定义进行交互并对其进行修改，但不能与Bean实例进行交互。这样做可能会导致bean实例化过早，从而违反了容器并造成了意想不到的副作用。

```java
@FunctionalInterface
public interface BeanFactoryPostProcessor {

   /**
    * Modify the application context's internal bean factory after its standard
    * initialization. All bean definitions will have been loaded, but no beans
    * will have been instantiated yet. This allows for overriding or adding
    * properties even to eager-initializing beans.
    * @param beanFactory the bean factory used by the application context
    * @throws org.springframework.beans.BeansException in case of errors
    */
   void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException;
}
```

方法名称： **postProcessBeanFactory** 

> 在标准初始化之后，修改应用程序上下文的内部Bean工厂。所有bean定义都将被加载，但尚未实例化任何bean。这甚至可以覆盖或添加属性，甚至可以用于初始化bean。

参数:  **ConfigurableListableBeanFactory**   

> 应用程序上下文使用的bean工厂



BeanFactoryPostProcessor 是Spring的扩展点之一，可以在Bean创建之前对 **BeanDefinition** 进行操作。

# 2.结构

![img](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\1.png)

## 1.bean

Demo1

```java
@Component
public class Demo1 {

   private Demo2 demo2;

   public void setDemo2(Demo2 demo2) {
      this.demo2 = demo2;
   }

   public Demo2 getDemo2() {
      return demo2;
   }

   private ApplicationContext context;

   public void setContext(ApplicationContext context) {
      this.context = context;
   }

   public ApplicationContext getContext() {
      return context;
   }
}
```

> Demo1中依赖了Demo2，并提供了set方法，目的就是通过BeanFactoryPostProcessor中的AUTOWIRE_BY_TYPE实现自动注入.

Demo2

```java
@Component
public class Demo2 {
}
```

## 2.config

```java
@Configuration
@ComponentScan("com.gaohwang.beanfactorypostprocessors")
public class Config {
}
```

## 3.processors

MyBeanDefinitionRegistryPostProcessor

```java
@Component
public class MyBeanDefinitionRegistryPostProcessor implements BeanDefinitionRegistryPostProcessor {

   @Override
   public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
      System.out.println("MyBeanDefinitionRegistryPostProcessor#postProcessBeanDefinitionRegistry...");
   }


   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      System.out.println("MyBeanDefinitionRegistryPostProcessor#postProcessBeanFactory...");
   }
}
```

MyBeanDefinitionRegistryPostProcessorOrder

```java
@Component
public class MyBeanDefinitionRegistryPostProcessorOrder implements BeanDefinitionRegistryPostProcessor, Ordered {

   @Override
   public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
      System.out.println("MyBeanDefinitionRegistryPostProcessorOrder#postProcessBeanDefinitionRegistry...");
   }


   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      System.out.println("MyBeanDefinitionRegistryPostProcessorOrder#postProcessBeanFactory...");
   }

   @Override
   public int getOrder() {
      return Integer.MAX_VALUE;
   }
}
```

MyBeanFactoryPostProcessor

```java
@Component
public class MyBeanFactoryPostProcessor implements BeanFactoryPostProcessor {

   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      System.out.println("MyBeanFactoryPostProcessor#postProcessBeanFactory...");
      GenericBeanDefinition beanDefinition = (GenericBeanDefinition) beanFactory.getBeanDefinition("demo1");
      beanDefinition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
   }
}
```

> 以上分别提供了三个BeanFactoryPostProcessor(子)接口的实现类
>
> MyBeanDefinitionRegistryPostProcessor 实现了BeanDefinitionRegistryPostProcessor接口
>
> MyBeanDefinitionRegistryPostProcessorOrder 实现了 BeanDefinitionRegistryPostProcessor, Ordered这两个接口
>
> MyBeanFactoryPostProcessor 实现了 BeanFactoryPostProcessor
>
> 其中BeanDefinitionRegistryPostProcessor继承了BeanFactoryPostProcessor

![img](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\2.png)

### 1.BeanDefinitionRegistryPostProcessor

```java
public interface BeanDefinitionRegistryPostProcessor extends BeanFactoryPostProcessor {

   /**
    * Modify the application context's internal bean definition registry after its
    * standard initialization. All regular bean definitions will have been loaded,
    * but no beans will have been instantiated yet. This allows for adding further
    * bean definitions before the next post-processing phase kicks in.
    * @param registry the bean definition registry used by the application context
    * @throws org.springframework.beans.BeansException in case of errors
    */
   void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
```

BeanDefinitionRegistryPostProcessor从名字我们可以得知这个接口就是注册Bean的定义信息(BeanDefinition)。对于BeanDefinition这里就不做详解，BeanDefinition是Spring中的建模对象。一般我们创建Bean的时候，需要根据BeanDefinition去实例化Bean。

## 4.test

```java
AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext();
//向容器中添加BeanFactoryPostProcessor
applicationContext.addBeanFactoryPostProcessor((beanFactory) -> {
   System.out.println("Lambda#postProcessBeanFactory...");
});
//向容器中添加BeanDefinitionRegistryPostProcessor
applicationContext.addBeanFactoryPostProcessor(new BeanDefinitionRegistryPostProcessor() {
   @Override
   public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
      System.out.println("匿名#postProcessBeanDefinitionRegistry...");
   }
   @Override
   public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
      System.out.println("匿名#postProcessBeanDefinitionRegistry...");
   }
});
applicationContext.register(Config.class);
applicationContext.refresh();
Demo1 bean = applicationContext.getBean(Demo1.class);
System.out.println(bean.getDemo2());
System.out.println(bean.getContext());
```

测试的时候，向容器中通过Lambda表达式注册了两个后置处理器。一个实现了BeanFactoryPostProcessor接口，另一个实现了BeanDefinitionRegistryPostProcessor接口，重写了里面的相关方法.

# 3.源码分析

## 1.**BeanFactoryPostProcessor** 执行位置

> AbstractApplicationContext#refresh->invokeBeanFactoryPostProcessors
>
> ​	PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors->invokeBeanFactoryPostProcessors

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\3.png)

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\4.png)

由上可知最终执行 **BeanFactoryPostProcessors**  位置在 **PostProcessorRegistrationDelegate#invokeBeanFactoryPostProcessors** 

## 2.源码

```java
public static void invokeBeanFactoryPostProcessors(
      ConfigurableListableBeanFactory beanFactory, List<BeanFactoryPostProcessor> beanFactoryPostProcessors) {

   // Invoke BeanDefinitionRegistryPostProcessors first, if any. 存储已经执行完的BeanDefinitionRegistryPostProcessors
   Set<String> processedBeans = new HashSet<>();

   if (beanFactory instanceof BeanDefinitionRegistry) {
      BeanDefinitionRegistry registry = (BeanDefinitionRegistry) beanFactory;
      //存储beanFactoryPostProcessors中实现了BeanFactoryPostProcessor的类,用于后面执行postProcessBeanFactory方法
      List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
      //存储已经处理的BeanDefinitionRegistryPostProcessor类,用于后面执行postProcessBeanFactory方法
      List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();

      for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
         if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
            BeanDefinitionRegistryPostProcessor registryProcessor =
                  (BeanDefinitionRegistryPostProcessor) postProcessor;
            //如果实现的是BeanDefinitionRegistryPostProcessor 那么直接执行
            registryProcessor.postProcessBeanDefinitionRegistry(registry);
            registryProcessors.add(registryProcessor);
         } else {
            regularPostProcessors.add(postProcessor);
         }
      }

      // Do not initialize FactoryBeans here: We need to leave all regular beans
      // uninitialized to let the bean factory post-processors apply to them!
      // Separate between BeanDefinitionRegistryPostProcessors that implement
      // PriorityOrdered, Ordered, and the rest.
      //当前需要执行的BeanDefinitionRegistryPostProcessor
      List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();

      // First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
      String[] postProcessorNames =
            beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
      for (String ppName : postProcessorNames) {
         if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
            currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
            processedBeans.add(ppName);
         }
      }
      sortPostProcessors(currentRegistryProcessors, beanFactory);
      registryProcessors.addAll(currentRegistryProcessors);
      //完成扫描
      invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
      currentRegistryProcessors.clear();

      // Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
      postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
      for (String ppName : postProcessorNames) {
         if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
            currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
            processedBeans.add(ppName);
         }
      }
      sortPostProcessors(currentRegistryProcessors, beanFactory);
      registryProcessors.addAll(currentRegistryProcessors);
      //调用给出BeanDefinitionRegistryPostProcessor Bean。
      invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
      currentRegistryProcessors.clear();

      // Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
      boolean reiterate = true;
      while (reiterate) {
         reiterate = false;
         postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
         for (String ppName : postProcessorNames) {
            if (!processedBeans.contains(ppName)) {
               currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
               processedBeans.add(ppName);
               reiterate = true;
            }
         }
         sortPostProcessors(currentRegistryProcessors, beanFactory);
         registryProcessors.addAll(currentRegistryProcessors);
         invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
         currentRegistryProcessors.clear();
      }

      // Now, invoke the postProcessBeanFactory callback of all processors handled so far.
      //执行postProcessBeanFactory这个方法
      invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
      //执行通过API放入直接BeanFactoryPostProcessors的哪些 postProcessBeanFactory方法
      invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
   } else {
      // Invoke factory processors registered with the context instance.
      invokeBeanFactoryPostProcessors(beanFactoryPostProcessors, beanFactory);
   }

   // Do not initialize FactoryBeans here: We need to leave all regular beans
   // uninitialized to let the bean factory post-processors apply to them!
   //拿到所有的BeanFactoryPostProcessor 内置的 以及程序员自定义的
   String[] postProcessorNames =
         beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

   // Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
   // Ordered, and the rest.
   List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
   //实现了order接口
   List<String> orderedPostProcessorNames = new ArrayList<>();
   //没有实现order接口
   List<String> nonOrderedPostProcessorNames = new ArrayList<>();
   for (String ppName : postProcessorNames) {
      if (processedBeans.contains(ppName)) {
         // skip - already processed in first phase above
      } else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
         priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
      } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
         orderedPostProcessorNames.add(ppName);
      } else {
         nonOrderedPostProcessorNames.add(ppName);
      }
   }

   // First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
   sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
   invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

   // Next, invoke the BeanFactoryPostProcessors that implement Ordered.
   List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
   for (String postProcessorName : orderedPostProcessorNames) {
      orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   }
   sortPostProcessors(orderedPostProcessors, beanFactory);
   invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

   // Finally, invoke all other BeanFactoryPostProcessors.
   List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
   for (String postProcessorName : nonOrderedPostProcessorNames) {
      nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
   }
   invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

   // Clear cached merged bean definitions since the post-processors might have
   // modified the original metadata, e.g. replacing placeholders in values...
   beanFactory.clearMetadataCache();
}
```

## 3.invokeBeanFactoryPostProcessors 介绍

首先我们分析invokeBeanFactoryPostProcessors 中的两个参数

### 1.beanFactory  

beanFactory在我们new AnnotationConfigApplicationContext()时就初始化了,GenericApplicationContext是AnnotationConfigApplicationContext的父类.

在AbstractApplicationContext#refresh中通过obtainFreshBeanFactory()方法获取到之前创建的Bean工厂.

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\5.png)

然后看看 **DefaultListableBeanFactory** 的继承关系

![](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\6.png)

**DefaultListableBeanFactory实现了BeanDefinitionRegistry接口**

### 2.beanFactoryPostProcessors    

beanFactoryPostProcessors这个参数是通过AbstractApplicationContext#getBeanFactoryPostProcessors()方法传入的.其目的是获取beanFactoryPostProcessors中的后置处理器,而beanFactoryPostProcessors是在调用AbstractApplicationContext#addBeanFactoryPostProcessor方法添加的.

### 3.流程分析

在invokeBeanFactoryPostProcessors 中,虽然代码比较多,很多都是重复的.

1. 执行BeanDefinitionRegistryPostProcessor实现类
   1. 执行程序员加入的BeanDefinitionRegistryPostProcessor中的postProcessBeanDefinitionRegistry方法
   2. 执行系统默认的实现BeanDefinitionRegistryPostProcessor和PriorityOrdered接口的类(其中包括ConfigurationClassPostProcessor),中的postProcessBeanDefinitionRegistry
   3. ConfigurationClassPostProcessor执行过后,会解析@Component相关注解,获取到BeanDefinitionRegistryPostProcessor相关实现类.紧接着执行实现了BeanDefinitionRegistryPostProcessor和Ordered接口类中的postProcessBeanDefinitionRegistry方法
   4. 循环执行剩余的BeanDefinitionRegistryPostProcessor中的postProcessBeanDefinitionRegistry方法
   5. 执行所有BeanDefinitionRegistryPostProcessor实现类中的postProcessBeanFactory方法
   6. 执行程序员加入BeanFactoryPostProcessor的postProcessBeanFactory方法
2. 执行BeanFactoryPostProcessor实现类
   1. 执行实现了BeanFactoryPostProcessor和PriorityOrdered接口类中的postProcessBeanFactory方法
   2. 执行实现了BeanFactoryPostProcessor和Ordered接口类中的postProcessBeanFactory方法
   3. 执行剩余的BeanFactoryPostProcessor中的postProcessBeanFactory方法

以上就是invokeBeanFactoryPostProcessors方法中执行流程,上文中 **程序员加入** 指的是通过 **addBeanFactoryPostProcessor** 

加入到容器中BeanFactoryPostProcessor.

## 4.源码解析

### 1.processedBeans 

```java
// Invoke BeanDefinitionRegistryPostProcessors first, if any.
Set<String> processedBeans = new HashSet<>();//存储已经执行完的BeanDefinitionRegistryPostProcessors
```

processedBeans主要是用于存储,执行完毕的BeanDefinitionRegistryPostProcessors实现类,避免重复执行

### 2.执行BeanDefinitionRegistryPostProcessor

#### 1.regularPostProcessors

```java
//存储已经执行了，并且直接实现了BeanFactoryPostProcessor的类
List<BeanFactoryPostProcessor> regularPostProcessors = new ArrayList<>();
```

前面的判断,当前环境下肯定是true,因为beanFactory就是AnnotationConfigApplicationContext父类构造方法创建的DefaultListableBeanFactory,而DefaultListableBeanFactory实现了BeanDefinitionRegistry接口.

regularPostProcessors存的是BeanFactoryPostProcessor类型,用于后面执行BeanFactoryPostProcessor中的postProcessBeanFactory方法

#### 2.registryProcessors

```java
//待执行的BeanDefinitionRegistryPostProcessor中父接口的方法
List<BeanDefinitionRegistryPostProcessor> registryProcessors = new ArrayList<>();
```

registryProcessors存的是BeanDefinitionRegistryPostProcessor类型,用于后面执行BeanDefinitionRegistryPostProcessor中的postProcessBeanFactory方法

#### 3.执行程序员提供的BeanDefinitionRegistryPostProcessor

```java
for (BeanFactoryPostProcessor postProcessor : beanFactoryPostProcessors) {
   if (postProcessor instanceof BeanDefinitionRegistryPostProcessor) {
      BeanDefinitionRegistryPostProcessor registryProcessor =
            (BeanDefinitionRegistryPostProcessor) postProcessor;
      //如果实现的是BeanDefinitionRegistryPostProcessor 那么直接执行
      registryProcessor.postProcessBeanDefinitionRegistry(registry);
      registryProcessors.add(registryProcessor);
   } else {
      regularPostProcessors.add(postProcessor);
   }
}
```

beanFactoryPostProcessors是获取程序员提供的,也就是我们前面在test中通过addBeanFactoryPostProcessor加入。默认情况下beanFactoryPostProcessors是空，则不会走下面的for循环。

![image-20210331181822329](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210331181822329.png)

for循环中遍历beanFactoryPostProcessors如果发现实现了BeanDefinitionRegistryPostProcessor就直接执行postProcessBeanDefinitionRegistry方法，并且将

添加到registryProcessors。如果仅仅只实现了BeanFactoryPostProcessor接口，那么就添加到regularPostProcessors中.用于后面的执行postProcessBeanFactory方法

#### 4.currentRegistryProcessors

```java
//当前需要执行的BeanDefinitionRegistryPostProcessor
List<BeanDefinitionRegistryPostProcessor> currentRegistryProcessors = new ArrayList<>();
```

currentRegistryProcessors存储当前需要执行的BeanDefinitionRegistryPostProcessor，每次执行完毕后会清空。

#### 5.首先，调用实现PriorityOrdered的BeanDefinitionRegistryPostProcessors

```java
// First, invoke the BeanDefinitionRegistryPostProcessors that implement PriorityOrdered.
String[] postProcessorNames =
      beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
for (String ppName : postProcessorNames) {
   if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
      currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
      processedBeans.add(ppName);
   }
}
sortPostProcessors(currentRegistryProcessors, beanFactory);
registryProcessors.addAll(currentRegistryProcessors);
//完成扫描
invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
currentRegistryProcessors.clear();
```

getBeanNamesForType方法从容器中获取实现了BeanDefinitionRegistryPostProcessor接口的BeanDefinition名称。getBeanNamesForType方法较为复杂，现阶段我们只用了解它的作用即可。

![image-20210401131817493](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210401131817493.png)

可以看到只获取到 org.springframework.context.annotation.internalConfigurationAnnotationProcessor

在AnnotationConfigApplicationContext中，在获取reader的时候就已经注册了一些基本的BeanDefinition,其中包括 org.springframework.context.annotation.internalConfigurationAnnotationProcessor

![image-20210401132624360](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210401132624360.png)

```java
//对 @Configuration @ComponentScans @Component @PropertySource @Import  @Bean 等注解进行处理
if (!registry.containsBeanDefinition(CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME)) {
   RootBeanDefinition def = new RootBeanDefinition(ConfigurationClassPostProcessor.class);
   def.setSource(source);
   beanDefs.add(registerPostProcessor(registry, def, CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME));
}
```

CONFIGURATION_ANNOTATION_PROCESSOR_BEAN_NAME也就是org.springframework.context.annotation.internalConfigurationAnnotationProcessor，对应的类为ConfigurationClassPostProcessor。ConfigurationClassPostProcessor尤为重要，会扫描相关注解，并创建相应的BeanDefinition。ConfigurationClassPostProcessor中实现了PriorityOrdered接口，所以会执行getBean从容器中创建ConfigurationClassPostProcessor，之所以能创建是因为容器中存在ConfigurationClassPostProcessor的BeanDefinition信息。紧接着放入 currentRegistryProcessors中等待执行,同时放入processedBeans中。

sortPostProcessors对currentRegistryProcessors进行排序，紧接着放入到registryProcessors中；最后执行postProcessBeanDefinitionRegistry方法，并清空currentRegistryProcessors。

invokeBeanDefinitionRegistryPostProcessors执行前

![image-20210401133649426](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210401133649426.png)

invokeBeanDefinitionRegistryPostProcessors执行后

![image-20210401133817008](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210401133817008.png)

可以看到多了5个我们加了@Component的BeanDefinition信息.

#### 4.接下来，调用实现Ordered的BeanDefinitionRegistryPostProcessors

```java
// Next, invoke the BeanDefinitionRegistryPostProcessors that implement Ordered.
postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
for (String ppName : postProcessorNames) {
   if (!processedBeans.contains(ppName) && beanFactory.isTypeMatch(ppName, Ordered.class)) {
      currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
      processedBeans.add(ppName);
   }
}
sortPostProcessors(currentRegistryProcessors, beanFactory);
registryProcessors.addAll(currentRegistryProcessors);
//调用给出BeanDefinitionRegistryPostProcessor Bean。
invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
currentRegistryProcessors.clear();
```

![image-20210401134349179](F:\GoodGoodStudent\soundCode\spring-framework\spring-framework-5.1.x\spring-gaohwang-student\src\main\java\com\gaohwang\beanfactorypostprocessors\test\assets\image-20210401134349179.png)

继续调用getBeanNamesForType,这次就会出现三个,其中包含之前的org.springframework.context.annotation.internalConfigurationAnnotationProcessor,以及我们自定的两个BeanDefinitionRegistryPostProcessor接口实现类.

通过processedBeans过滤之前执行过的,并且找到实现了BeanDefinitionRegistryPostProcessor和Ordered接口类,所以myBeanDefinitionRegistryPostProcessorOrder会在这次执行

#### 5.最后，调用所有其他BeanDefinitionRegistryPostProcessor，直到没有其他BeanDefinitionRegistryPostProcessor为止

```java
// Finally, invoke all other BeanDefinitionRegistryPostProcessors until no further ones appear.
boolean reiterate = true;
while (reiterate) {
   reiterate = false;
   postProcessorNames = beanFactory.getBeanNamesForType(BeanDefinitionRegistryPostProcessor.class, true, false);
   for (String ppName : postProcessorNames) {
      if (!processedBeans.contains(ppName)) {
         currentRegistryProcessors.add(beanFactory.getBean(ppName, BeanDefinitionRegistryPostProcessor.class));
         processedBeans.add(ppName);
         reiterate = true;
      }
   }
   sortPostProcessors(currentRegistryProcessors, beanFactory);
   registryProcessors.addAll(currentRegistryProcessors);
   invokeBeanDefinitionRegistryPostProcessors(currentRegistryProcessors, registry);
   currentRegistryProcessors.clear();
}

// Now, invoke the postProcessBeanFactory callback of all processors handled so far.
//执行postProcessBeanFactory这个方法
invokeBeanFactoryPostProcessors(registryProcessors, beanFactory);
//执行通过API放入直接BeanFactoryPostProcessors的哪些 postProcessBeanFactory方法
invokeBeanFactoryPostProcessors(regularPostProcessors, beanFactory);
```

以上就是执行剩余没有执行的BeanDefinitionRegistryPostProcessor接口实现类,但是为什么需要用while循环呢?

我的理解是每当我们执行BeanDefinitionRegistryPostProcessor中的postProcessBeanDefinitionRegistry方法,都可能会在容器中创建一个新的BeanDefinitionRegistryPostProcessor对应的BeanDefinition信息,while循环为了确保,BeanDefinitionRegistryPostProcessor实现类执行完毕.

invokeBeanFactoryPostProcessors执行所有BeanDefinitionRegistryPostProcessor中的postProcessBeanFactory方法,因为BeanDefinitionRegistryPostProcessor 继承了 BeanFactoryPostProcessor.

invokeBeanFactoryPostProcessors执行程序员手动放入的BeanFactoryPostProcessor的postProcessBeanFactory方法,也就是我们通过addBeanFactoryPostProcessor放入的.

### 3.执行BeanFactoryPostProcessor

以下代码比较简单,我们就放在一起分析

```java
// Do not initialize FactoryBeans here: We need to leave all regular beans
// uninitialized to let the bean factory post-processors apply to them!
//拿到所有的BeanFactoryPostProcessor 内置的 以及程序员自定义的
String[] postProcessorNames =
      beanFactory.getBeanNamesForType(BeanFactoryPostProcessor.class, true, false);

// Separate between BeanFactoryPostProcessors that implement PriorityOrdered,
// Ordered, and the rest.
List<BeanFactoryPostProcessor> priorityOrderedPostProcessors = new ArrayList<>();
//实现了order接口
List<String> orderedPostProcessorNames = new ArrayList<>();
//没有实现order接口
List<String> nonOrderedPostProcessorNames = new ArrayList<>();
for (String ppName : postProcessorNames) {
   if (processedBeans.contains(ppName)) {
      // skip - already processed in first phase above
   } else if (beanFactory.isTypeMatch(ppName, PriorityOrdered.class)) {
      priorityOrderedPostProcessors.add(beanFactory.getBean(ppName, BeanFactoryPostProcessor.class));
   } else if (beanFactory.isTypeMatch(ppName, Ordered.class)) {
      orderedPostProcessorNames.add(ppName);
   } else {
      nonOrderedPostProcessorNames.add(ppName);
   }
}

// First, invoke the BeanFactoryPostProcessors that implement PriorityOrdered.
sortPostProcessors(priorityOrderedPostProcessors, beanFactory);
invokeBeanFactoryPostProcessors(priorityOrderedPostProcessors, beanFactory);

// Next, invoke the BeanFactoryPostProcessors that implement Ordered.
List<BeanFactoryPostProcessor> orderedPostProcessors = new ArrayList<>();
for (String postProcessorName : orderedPostProcessorNames) {
   orderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
}
sortPostProcessors(orderedPostProcessors, beanFactory);
invokeBeanFactoryPostProcessors(orderedPostProcessors, beanFactory);

// Finally, invoke all other BeanFactoryPostProcessors.
List<BeanFactoryPostProcessor> nonOrderedPostProcessors = new ArrayList<>();
for (String postProcessorName : nonOrderedPostProcessorNames) {
   nonOrderedPostProcessors.add(beanFactory.getBean(postProcessorName, BeanFactoryPostProcessor.class));
}
invokeBeanFactoryPostProcessors(nonOrderedPostProcessors, beanFactory);

// Clear cached merged bean definitions since the post-processors might have
// modified the original metadata, e.g. replacing placeholders in values...
beanFactory.clearMetadataCache();
```

首先通过getBeanNamesForType从容器中BeanDefinition找到实现了BeanFactoryPostProcessors类名称,将这些分为三类

1. 实现了PriorityOrdered,放入到priorityOrderedPostProcessors
2. 实现了Ordered,放入到orderedPostProcessorNames
3. 剩余的放到nonOrderedPostProcessorNames

下面分阶段执行BeanFactoryPostProcessors中的postProcessBeanFactory中的方法.首先执行priorityOrderedPostProcessors中的,紧接着执行orderedPostProcessorNames的,最后执行nonOrderedPostProcessorNames

# 4.总结

在以上分析中,我们得知Spring在执行BeanFactoryPostProcessors实现类时,会优先执行BeanDefinitionRegistryPostProcessor中的postProcessBeanDefinitionRegistry方法,如果我们通过addBeanFactoryPostProcessor放入的BeanDefinitionRegistryPostProcessor会最先执行,紧接着执行系统内置的(ConfigurationClassPostProcessor),最后会执行我们自定的,也就是使用@Component等相关注解的类.等BeanDefinitionRegistryPostProcessor所有的执行postProcessBeanDefinitionRegistry方法完毕后,才会执行其对应的postProcessBeanFactory方法.BeanDefinitionRegistryPostProcessor所有方法执行完毕后,才会执行BeanFactoryPostProcessors中的postProcessBeanFactory方法.

postProcessBeanDefinitionRegistry可以动态的新增(注册)一些自定义的BeanDefinition信息,而postProcessBeanFactory只能对BeanDefinition进行修改.因此如果需要对Spring进行扩展仅仅只是修改BeanDefinition信息,那么实现BeanFactoryPostProcessors即可,如果需要自己向容器中动态的注册相关BeanDefinition信息那么可实现BeanDefinitionRegistryPostProcessor接口.当然BeanFactoryPostProcessors以及子接口远不止操作BeanDefinition信息这点功能,本文只对BeanFactoryPostProcessors进行浅层次的分析.