package com.it7890.orange.service;

import com.avos.avoscloud.AVObject;
import com.it7890.orange.entity.GrabDetailRule;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Astro on 17/6/12.
 */
@Resource(name="grabDetailRuleService")
public interface GrabDetailRuleService {

	List<AVObject> findGrabDetailRules();
}
