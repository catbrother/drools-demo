package com.desmart.drools.Config;

import com.desmart.drools.util.DroolsUtil;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.KieBuilder;
import org.kie.api.builder.KieFileSystem;
import org.kie.api.builder.KieRepository;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.kie.spring.KModuleBeanFactoryPostProcessor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.io.Resource;

import java.io.IOException;

/**
 * Drools配置类
 *
 * @Author yby
 * @Date 2020/9/3 14:14
 **/
@Configuration
public class DroolsConfig {

    private static final String RULES_PATH = "rules/";
    private final KieServices kieServices = KieServices.Factory.get();

    /**
     * 初始化文件系统
     *
     * @return
     * @throws IOException
     */
    @Bean
    //先去spring工厂查找有没有该类型的对象，有的话就不创建，没有的话就创建，防止存在多个该类型的对象
    @ConditionalOnMissingBean
    public KieFileSystem kieFileSystem() throws IOException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        Resource[] files = resourcePatternResolver.getResources("classpath*:" + RULES_PATH + "/*.*");
        String path = null;
        for (Resource file : files) {
            path = RULES_PATH + file.getFilename();
            kieFileSystem.write(ResourceFactory.newClassPathResource(path, "UTF-8"));
        }
        return kieFileSystem;
    }

    /**
     * 初始化容器
     *
     * @return
     * @throws IOException
     */
    @Bean
    @ConditionalOnMissingBean
    public KieContainer kieContainer() throws IOException {
        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(kieRepository::getDefaultReleaseId);
        KieBuilder kieBuilder = kieServices.newKieBuilder(kieFileSystem());
        kieBuilder.buildAll();
        KieContainer kieContainer = kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
        DroolsUtil.setKieContainer(kieContainer);
        return kieServices.newKieContainer(kieRepository.getDefaultReleaseId());
    }

    /**
     * 初始化kieBase，通过kieBase可以创建session
     *
     * @return
     * @throws IOException
     */
    @Bean
    @ConditionalOnMissingBean
    public KieBase kieBase() throws IOException {
        return kieContainer().getKieBase();
    }

    /**
     * 初始化kieSession
     * @return
     * @throws IOException
     */
    @Bean
    @ConditionalOnMissingBean
    public KieSession kieSession() throws IOException {
        KieSession kieSession = kieContainer().newKieSession();
        DroolsUtil.setKieSession(kieSession);
        return kieContainer().newKieSession();
    }

    /**
     * 创建模块
     *
     * @return
     */
    @Bean
    @ConditionalOnMissingBean
    public KModuleBeanFactoryPostProcessor kiePostProcessor() {
        return new KModuleBeanFactoryPostProcessor();
    }
}
