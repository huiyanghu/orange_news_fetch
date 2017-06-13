package com.it7890.orange.service.impl;

import com.avos.avoscloud.AVObject;
import com.it7890.orange.dao.GrabListRuleDao;
import com.it7890.orange.service.GrabListRuleService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Astro on 17/6/12.
 */
public class GrabListRuleServiceImpl implements GrabListRuleService {

	@Resource
	private GrabListRuleDao grabListRuleDao;

	@Override
	public List<AVObject> findGrabListRules() {
		return grabListRuleDao.findGrabListRules();
	}
}
