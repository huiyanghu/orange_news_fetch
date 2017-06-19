package com.it7890.orange.dao;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.it7890.orange.entity.GrabListRule;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GrabListRuleDao {

	private static final Logger logger = LogManager.getLogger(GrabListRuleDao.class);

	public List<AVObject> findGrabListRules() {
		List<AVObject> grabListRules = new ArrayList<>();
		try {
			AVCloudQueryResult queryResult = AVQuery.doCloudQuery("select * from GrabListRule limit 1000", GrabListRule.class);
			grabListRules = (List<AVObject>) queryResult.getResults();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return grabListRules;
	}
}
