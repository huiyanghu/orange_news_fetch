package com.it7890.orange.service.impl;

import com.avos.avoscloud.AVObject;
import com.it7890.orange.dao.GrabDetailRuleDao;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.service.GrabDetailRuleService;

import javax.annotation.Resource;
import java.util.List;

/**
 * Created by Astro on 17/6/12.
 */
public class GrabDetailRuleServiceImpl implements GrabDetailRuleService {

	@Resource
	private GrabDetailRuleDao grabDetailRuleDao;


	@Override
	public List<AVObject> findGrabDetailRules() {
		return grabDetailRuleDao.findGrabDetailRules();
	}
}
