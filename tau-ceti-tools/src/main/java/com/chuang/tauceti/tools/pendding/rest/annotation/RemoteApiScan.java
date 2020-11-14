//package com.chuang.tauceti.tools.pendding.rest.annotation;
//
//import com.chuang.tauceti.tools.pendding.rest.RemoteApiFactoryBean;
//import com.chuang.tauceti.tools.pendding.rest.RemoteApiScannerRegistrar;
//import org.springframework.beans.factory.support.BeanNameGenerator;
//import org.springframework.context.annotation.Import;
//
//import java.lang.annotation.*;
//
//@Retention(RetentionPolicy.RUNTIME)
//@Target(ElementType.TYPE)
//@Documented
//@Import(RemoteApiScannerRegistrar.class)
//public @interface RemoteApiScan {
//
//    /**
//     * Alias for the {@link #basePackages()} attribute. Allows for more concise
//     * annotation declarations e.g.:
//     */
//    String[] value() default {};
//
//    /**
//     * Base packages to scan for RemoteApi interfaces. Note that only interfaces
//     * with at least one method will be registered; concrete classes will be
//     * ignored.
//     */
//    String[] basePackages() default {};
//
//    /**
//     * Type-safe alternative to {@link #basePackages()} for specifying the packages
//     * to scan for annotated components. The package of each class specified will be scanned.
//     * <p>Consider creating a special no-op marker class or interface in each package
//     * that serves no purpose other than being referenced by this attribute.
//     */
//    Class<?>[] basePackageClasses() default {};
//
//    /**
//     * The {@link BeanNameGenerator} class to be used for naming detected components
//     * within the Spring container.
//     */
//    Class<? extends BeanNameGenerator> nameGenerator() default BeanNameGenerator.class;
//
//    /**
//     * This property specifies the annotation that the scanner will search for.
//     * <p>
//     * The scanner will register all interfaces in the base package that also have
//     * the specified annotation.
//     * <p>
//     * Note this can be combined with markerInterface.
//     */
//    Class<? extends Annotation> annotationClass() default RestRemoteApi.class;
//
//    /**
//     * This property specifies the parent that the scanner will search for.
//     * <p>
//     * The scanner will register all interfaces in the base package that also have
//     * the specified interface class as a parent.
//     * <p>
//     * Note this can be combined with annotationClass.
//     */
//    Class<?> markerInterface() default Class.class;
//
//    /**
//     * Specifies a custom RemoteApiFactoryBean to return a remoteApi proxy as spring bean.
//     *
//     */
//    Class<? extends RemoteApiFactoryBean> factoryBean() default RemoteApiFactoryBean.class;
//}
