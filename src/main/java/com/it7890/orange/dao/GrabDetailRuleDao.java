package com.it7890.orange.dao;

import com.avos.avoscloud.AVCloudQueryResult;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.it7890.orange.entity.GrabDetailRule;
import com.it7890.orange.entity.GrabListRule;
import com.it7890.orange.util.StringUtil;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class GrabDetailRuleDao {

	public List<AVObject> findGrabDetailRules() {
		List<AVObject> grabDetailRules = new ArrayList<>();
		try {
			AVCloudQueryResult queryResult = AVQuery.doCloudQuery("select * from GrabDetailRule limit 1000", GrabListRule.class);
			grabDetailRules = (List<AVObject>) queryResult.getResults();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return grabDetailRules;
	}

	public AVObject getGrabDetailRuleByObjectId(String objectId) {
		AVObject grabDetail = null;
		if (StringUtil.isNotEmpty(objectId)) {
			try {
				AVCloudQueryResult queryResult = AVQuery.doCloudQuery("select include grabListRuleObj, * from GrabDetailRule where objectId = ?", GrabListRule.class, objectId);
				List<AVObject> grabDetailRules = (List<AVObject>) queryResult.getResults();
				if (null != grabDetailRules && grabDetailRules.size() > 0) {
					grabDetail = grabDetailRules.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return grabDetail;
	}

	public AVObject getGrabDetailRuleByLid(String listRuleId) {
		AVObject grabDetail = null;
		if (StringUtil.isNotEmpty(listRuleId)) {
			try {
				AVCloudQueryResult queryResult = AVQuery.doCloudQuery("select * from GrabDetailRule where grabListRuleObj = pointer('GrabListRule', ?)", GrabListRule.class, listRuleId);
				List<AVObject> grabDetailRules = (List<AVObject>) queryResult.getResults();
				if (null != grabDetailRules && grabDetailRules.size() > 0) {
					grabDetail = grabDetailRules.get(0);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return grabDetail;
	}
}
