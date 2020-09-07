package com.desmart.drools;

import com.desmart.drools.domain.People;
import com.desmart.drools.service.DroolsService;
import com.desmart.drools.util.DroolsUtil;
import com.desmart.drools.util.ReadTxtUtil;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.junit.jupiter.api.Test;
import org.kie.api.runtime.KieSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.List;

@SpringBootTest
class DroolsApplicationTests {

    @Autowired
    DroolsService droolsService;

    @Test
    void contextLoads() throws IOException {
        //从文件中读取伪代码转成字符串
        String pseudoCode = ReadTxtUtil.readTxt(new File("D:\\pseudoCode\\demo.txt"));
        //System.out.println("pseudoCode = " + pseudoCode);
        List<String> strings = DroolsUtil.convertPseudoCodeToDroolsCode(pseudoCode);
        if (!strings.isEmpty()) {
            strings.forEach(string -> {
                try {
                    DroolsUtil.reloadDB(string);
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            });
        }
        People people = new People();
        people.setName("杨秉毅");
        people.setAge(100);
        people.setSex("男");
        //调用方法
        Integer count = droolsService.count("杨秉毅", 100, "男");
        KieSession kieSession = DroolsUtil.getKieSession();
        kieSession.insert(count);
        kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("rule02"));
    }
}
