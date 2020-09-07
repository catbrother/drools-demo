package com.desmart.drools.service;

import com.desmart.drools.domain.People;
import com.desmart.drools.util.DroolsUtil;
import org.drools.core.base.RuleNameStartsWithAgendaFilter;
import org.kie.api.runtime.KieSession;
import org.springframework.stereotype.Service;

/**
 * 方法
 *
 * @Author yby
 * @Date 2020/9/6 18:50
 **/
@Service
public class DroolsService {

    /**
     * 得到符合规则的数量
     *
     * @param name
     * @param age
     * @param sex
     * @return
     */
    public Integer count(String name, Integer age, String sex) {
        People people = new People();
        people.setName(name);
        people.setAge(age);
        people.setSex(sex);
        KieSession kieSession = DroolsUtil.getKieSession();
        kieSession.insert(people);
        int count = kieSession.fireAllRules(new RuleNameStartsWithAgendaFilter("rule01"));
        return count;
    }
}
