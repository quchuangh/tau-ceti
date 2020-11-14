//package com.chuang.tauceti.tools.pendding.rest;
//
//import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinition;
//import org.springframework.beans.factory.config.BeanDefinitionHolder;
//import org.springframework.beans.factory.support.AbstractBeanDefinition;
//import org.springframework.beans.factory.support.BeanDefinitionRegistry;
//import org.springframework.beans.factory.support.GenericBeanDefinition;
//import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
//import org.springframework.core.type.filter.AnnotationTypeFilter;
//import org.springframework.core.type.filter.AssignableTypeFilter;
//
//import java.lang.annotation.Annotation;
//import java.util.Arrays;
//import java.util.Set;
//
//public class RemoteApiScanner extends ClassPathBeanDefinitionScanner {
//
//    private Class<? extends Annotation> annotationClass;
//
//    private Class<?> markerInterface;
//
//    private RemoteApiFactoryBean remoteApiFactoryBean = new RemoteApiFactoryBean();
//
//    public RemoteApiScanner(BeanDefinitionRegistry registry) {
//        super(registry);
//    }
//
//    @Override
//    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
//        Set<BeanDefinitionHolder> beanDefinitions = super.doScan(basePackages);
//
//        if (beanDefinitions.isEmpty()) {
//            logger.warn("No Remote Api was found in '" + Arrays.toString(basePackages) + "' package. Please check your configuration.");
//        } else {
//            processBeanDefinitions(beanDefinitions);
//        }
//
//        return beanDefinitions;
//    }
//
//    private void processBeanDefinitions(Set<BeanDefinitionHolder> beanDefinitions) {
//        GenericBeanDefinition definition;
//        for (BeanDefinitionHolder holder : beanDefinitions) {
//            definition = (GenericBeanDefinition) holder.getBeanDefinition();
//
//            if (logger.isDebugEnabled()) {
//                logger.debug("Creating RemoteApiBean with name '" + holder.getBeanName()
//                        + "' and '" + definition.getBeanClassName() + "' remoteApiClass");
//            }
//
//            // the remoteApi interface is the original class of the bean
//            // but, the actual class of the bean is RemoteApiFactoryBean
//            definition.getConstructorArgumentValues().addGenericArgumentValue(definition.getBeanClassName()); // issue #59
//            definition.setBeanClass(this.remoteApiFactoryBean.getClass());
//
//            if (logger.isDebugEnabled()) {
//                logger.debug("Enabling autowire by type for RemoteApiFactoryBean with name '" + holder.getBeanName() + "'.");
//            }
//            definition.setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
//        }
//    }
//
//    public void registerFilters() {
//        boolean acceptAllInterfaces = true;
//
//        // if specified, use the given annotation and / or marker interface
//        if (this.annotationClass != null) {
//            addIncludeFilter(new AnnotationTypeFilter(this.annotationClass));
//            acceptAllInterfaces = false;
//        }
//
//        // override AssignableTypeFilter to ignore matches on the actual marker interface
//        if (this.markerInterface != null) {
//            addIncludeFilter(new AssignableTypeFilter(this.markerInterface) {
//                @Override
//                protected boolean matchClassName(String className) {
//                    return false;
//                }
//            });
//            acceptAllInterfaces = false;
//        }
//
//        if (acceptAllInterfaces) {
//            // default include filter that accepts all classes
//            addIncludeFilter((metadataReader, metadataReaderFactory) -> true);
//        }
//
//        // exclude package-info.java
//        addExcludeFilter((metadataReader, metadataReaderFactory) -> {
//            String className = metadataReader.getClassMetadata().getClassName();
//            return className.endsWith("package-info");
//        });
//    }
//    /**
//     * {@inheritDoc}
//     */
//    @Override
//    protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
//        return beanDefinition.getMetadata().isInterface() && beanDefinition.getMetadata().isIndependent();
//    }
//
//    /**
//     * 添加失败日志
//     * {@inheritDoc}
//     */
//    @Override
//    protected boolean checkCandidate(String beanName, BeanDefinition beanDefinition) {
//        if (super.checkCandidate(beanName, beanDefinition)) {
//            return true;
//        } else {
//            logger.warn("Skipping RemoteApiFactoryBean with name '" + beanName
//                    + "' and '" + beanDefinition.getBeanClassName() + "' remoteApiClass"
//                    + ". Bean already defined with the same name!");
//            return false;
//        }
//    }
//
//    public void setRemoteApiFactoryBean(RemoteApiFactoryBean remoteApiFactoryBean) {
//        this.remoteApiFactoryBean = (remoteApiFactoryBean != null ? remoteApiFactoryBean : new RemoteApiFactoryBean());
//    }
//
//    public void setAnnotationClass(Class<? extends Annotation> annotationClass) {
//        this.annotationClass = annotationClass;
//    }
//
//    public void setMarkerInterface(Class<?> markerInterface) {
//        this.markerInterface = markerInterface;
//    }
//
//}
