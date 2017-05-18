package org.springside.modules.orm.hibernate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.impl.CriteriaImpl;
import org.hibernate.transform.ResultTransformer;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;
import org.springside.modules.orm.Page;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.orm.PropertyFilter.MatchType;
import org.springside.modules.utils.ReflectionUtils;

//@Repository
public class HibernateDao<T, PK extends Serializable> extends SimpleHibernateDao<T, PK> {

	public HibernateDao() {
		super();
	}

	public HibernateDao(final SessionFactory sessionFactory, final Class<T> entityClass) {
		super(sessionFactory, entityClass);
	}

	public Page<T> getAll(final Page<T> page) {
		return findPage(page);
	}

	@SuppressWarnings("unchecked")
	public Page<T> findPage(final Page<T> page, final String hql, final Object... values) {

		Assert.notNull(page, "page不能为空");

		Query q = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);

			page.setTotalCount(totalCount);
		}

		setPageParameter(q, page);

		List result = q.list();

		page.setResult(result);
		return page;
	}

	@SuppressWarnings("unchecked")
	public Page<T> findPage(final Page<T> page, final String hql, final Map<String, Object> values) {

		Assert.notNull(page, "page不能为空");

		Query q = createQuery(hql, values);

		if (page.isAutoCount()) {
			long totalCount = countHqlResult(hql, values);

			page.setTotalCount(totalCount);
		}

		setPageParameter(q, page);

		List result = q.list();

		page.setResult(result);
		return page;
	}

	@SuppressWarnings("unchecked")
	public Page<T> findPage(final Page<T> page, final Criterion... criterions) {

		Assert.notNull(page, "page不能为空");

		Criteria c = createCriteria(criterions);

		if (page.isAutoCount()) {
			int totalCount = countCriteriaResult(c);

			page.setTotalCount(totalCount);
		}

		setPageParameter(c, page);

		List result = c.list();

		page.setResult(result);
		return page;
	}

	protected Query setPageParameter(final Query q, final Page<T> page) {

		q.setFirstResult(page.getFirst() - 1);
		q.setMaxResults(page.getPageSize());
		return q;
	}

	protected Criteria setPageParameter(final Criteria c, final Page<T> page) {

		c.setFirstResult(page.getFirst() - 1);
		c.setMaxResults(page.getPageSize());

		if (page.isOrderBySetted()) {
			String[] orderByArray = StringUtils.split(page.getOrderBy(), ',');
			String[] orderArray = StringUtils.split(page.getOrder(), ',');

			Assert.isTrue(orderByArray.length == orderArray.length, "分页多重排序参数中,排序字段与排序方向的个数不相等");

			for (int i = 0; i < orderByArray.length; i++) {
				if (Page.ASC.equals(orderArray[i])) {
					c.addOrder(Order.asc(orderByArray[i]));
				} else {
					c.addOrder(Order.desc(orderByArray[i]));
				}
			}
		}

		return c;
	}

	protected int countHqlResult(final String hql, final Object... values) {

		Long count = 0L;
		String fromHql = hql;

		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			count = findUnique(countHql, values);
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}

		return count.intValue();
	}

	protected int countHqlResult(final String hql, final Map<String, Object> values) {

		Integer count = 0;
		String fromHql = hql;

		fromHql = "from " + StringUtils.substringAfter(fromHql, "from");
		fromHql = StringUtils.substringBefore(fromHql, "order by");

		String countHql = "select count(*) " + fromHql;

		try {
			count = findUnique(countHql, values);
		} catch (Exception e) {
			throw new RuntimeException("hql can't be auto count, hql is:" + countHql, e);
		}

		return count;
	}

	@SuppressWarnings("unchecked")
	protected int countCriteriaResult(final Criteria c) {

		CriteriaImpl impl = (CriteriaImpl) c;

		Projection projection = impl.getProjection();
		ResultTransformer transformer = impl.getResultTransformer();

		List<CriteriaImpl.OrderEntry> orderEntries = null;

		try {
			orderEntries = (List) ReflectionUtils.getFieldValue(impl, "orderEntries");

			ReflectionUtils.setFieldValue(impl, "orderEntries", new ArrayList());
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		int totalCount = (Integer) c.setProjection(Projections.rowCount()).uniqueResult();

		c.setProjection(projection);

		if (projection == null) {
			c.setResultTransformer(CriteriaSpecification.ROOT_ENTITY);
		}

		if (transformer != null) {
			c.setResultTransformer(transformer);
		}

		try {
			ReflectionUtils.setFieldValue(impl, "orderEntries", orderEntries);
		} catch (Exception e) {
			logger.error("不可能抛出的异常:{}", e.getMessage());
		}

		return totalCount;
	}

	public List<T> findBy(final String propertyName, final Object value, final MatchType matchType) {

		Criterion criterion = buildPropertyFilterCriterion(propertyName, value, value.getClass(), matchType);

		return find(criterion);
	}

	public List<T> find(List<PropertyFilter> filters) {

		Criterion[] criterions = buildPropertyFilterCriterions(filters);

		return find(criterions);
	}

	public Page<T> findPage(final Page<T> page, final List<PropertyFilter> filters) {

		Criterion[] criterions = buildPropertyFilterCriterions(filters);

		return findPage(page, criterions);
	}

	protected Criterion[] buildPropertyFilterCriterions(final List<PropertyFilter> filters) {

		List<Criterion> criterionList = new ArrayList<Criterion>();

		for (PropertyFilter filter : filters) {
			if (!filter.isMultiProperty()) {
				Criterion criterion = buildPropertyFilterCriterion(filter.getPropertyName(), filter.getPropertyValue(), filter.getPropertyType(), filter.getMatchType());

				criterionList.add(criterion);
			} else {
				Disjunction disjunction = Restrictions.disjunction();

				for (String param : filter.getPropertyNames()) {
					Criterion criterion = buildPropertyFilterCriterion(param, filter.getPropertyValue(), filter.getPropertyType(), filter.getMatchType());

					disjunction.add(criterion);
				}

				criterionList.add(disjunction);
			}
		}

		return criterionList.toArray(new Criterion[criterionList.size()]);
	}

	protected Criterion buildPropertyFilterCriterion(final String propertyName, final Object propertyValue, final Class<?> propertyType, final MatchType matchType) {

		Assert.hasText(propertyName, "propertyName不能为空");

		Criterion criterion = null;

		try {

			Object realValue = ReflectionUtils.convertValue(propertyValue, propertyType);

			if (MatchType.EQ.equals(matchType)) {
				criterion = Restrictions.eq(propertyName, realValue);
			}

			if (MatchType.LIKE.equals(matchType)) {
				criterion = Restrictions.like(propertyName, (String) realValue, MatchMode.ANYWHERE);
			}

			if (MatchType.LE.equals(matchType)) {
				criterion = Restrictions.le(propertyName, realValue);
			}

			if (MatchType.LT.equals(matchType)) {
				criterion = Restrictions.lt(propertyName, realValue);
			}

			if (MatchType.GE.equals(matchType)) {
				criterion = Restrictions.ge(propertyName, realValue);
			}

			if (MatchType.GT.equals(matchType)) {
				criterion = Restrictions.gt(propertyName, realValue);
			}
		} catch (Exception e) {
			throw ReflectionUtils.convertToUncheckedException(e);
		}

		return criterion;
	}

	public boolean isPropertyUnique(final String propertyName, final Object newValue, final Object oldValue) {

		if (newValue == null || newValue.equals(oldValue)) {
			return true;
		}

		Object object = findUniqueBy(propertyName, newValue);

		return (object == null);
	}
}
