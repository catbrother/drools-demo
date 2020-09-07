package com.desmart.drools.util;

import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilder;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.io.ResourceFactory;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * Drools工具类，存储了对应的静态方法和静态属性，供其他的地方获取和更新
 *
 * @Author yby
 * @Date 2020/9/6 16:13
 **/
public class DroolsUtil {

    private static KieContainer kieContainer;

    private static KieSession kieSession;

    public static KieContainer getKieContainer() {
        return kieContainer;
    }

    public static void setKieContainer(KieContainer kieContainer) {
        DroolsUtil.kieContainer = kieContainer;
        kieSession = kieContainer.newKieSession();
    }

    public static KieSession getKieSession() {
        return kieSession;
    }

    public static void setKieSession(KieSession kieSession) {
        DroolsUtil.kieSession = kieSession;
    }

    /**
     * 从数据库中取出规则存入内存
     *
     * @param droolsString
     * @throws UnsupportedEncodingException
     */
    public static void reloadDB(String droolsString) throws UnsupportedEncodingException {
        KieSession tempKieSession = null;
        KnowledgeBuilder kb = KnowledgeBuilderFactory.newKnowledgeBuilder();
        //从数据库查询所有规则存到kieSession
        //。。。
        //String drl = "";
        kb.add(ResourceFactory.newByteArrayResource(droolsString.getBytes("utf-8")), ResourceType.DRL);
        if (kb.hasErrors()) {
            String errorMessage = kb.getErrors().toString();
            System.out.println("规则语法异常---\n" + errorMessage);
        }
        InternalKnowledgeBase kBase = KnowledgeBaseFactory.newKnowledgeBase();
        kBase.addPackages(kb.getKnowledgePackages());
        tempKieSession = kBase.newKieSession();
        DroolsUtil.setKieSession(tempKieSession);
    }

    /**
     * 把伪代码字符串转成drools字符串
     *
     * @param pseudoCode
     * @return
     */
    public static List<String> convertPseudoCodeToDroolsCode(String pseudoCode) {
        List<String> droolsCodeList = new ArrayList<>();
        //以结束字样分割伪代码为规则段
        String[] paragraphs = pseudoCode.split("结束");
        for (int i = 0; i < paragraphs.length; i++) {
            String droolsCode = "";
            //添加drools规则方言为java
            droolsCode += "dialect \"java\"";
            //System.out.println("paragraphs = " + paragraphs[i]);
            //以\n字样分割伪代码段为行格式（\\u005C为\的转义符）
            String[] lines = paragraphs[i].split("\\u005Cn");
            for (int j = 0; j < lines.length; j++) {
                //定义规则名
                if (lines[j].startsWith("规则名")) {
                    //System.out.println("lines = " + lines[j]);
                    droolsCode += " rule \"" + lines[j].replace("规则名(", "").replace(")", "") + "\"";
                    break;
                }
            }
            for (int j = 0; j < lines.length; j++) {
                //定义左手右定则
                if (lines[j].startsWith("如果")) {
                    //System.out.println("lines = " + lines[j]);
                    // {转义
                    //以{字样分割伪代码行为左右手定则代码（\\u007B为{的转义符）
                    String[] lrhs = lines[j].split("\\u007B");
                    droolsCode += " when $count : " + lrhs[0].replace("如果(", "").substring(0, lrhs[0].replace("如果(", "").length() - 1).replace("整型", "Integer").replace("&(线路匹配结果集)", "$count");
                    droolsCode += " then System.out.println(\"" + lrhs[1].replace("}", "").replace(";", "").trim() + "\")" + ";";
                    break;
                }
            }
            droolsCode += " end";
            droolsCodeList.add(droolsCode);
        }
        //System.out.println("droolsCodeList = " + droolsCodeList);
        return droolsCodeList;
    }
}
