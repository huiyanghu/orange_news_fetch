package com.it7890.orange.service;

import com.avos.avoscloud.AVObject;

import javax.annotation.Resource;
import java.util.List;


@Resource(name="grabListRuleService")
public interface GrabListRuleService {

	List<AVObject> findGrabListRules();
}
