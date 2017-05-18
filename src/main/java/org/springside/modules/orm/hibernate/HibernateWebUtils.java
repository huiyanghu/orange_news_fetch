    
  
  
  
  
  
  

package org.springside.modules.orm.hibernate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;
import org.springside.modules.orm.PropertyFilter;
import org.springside.modules.utils.ReflectionUtils;

    
  
  
  
  
public class HibernateWebUtils {

    private HibernateWebUtils() {
    }

        
  
  
  
  
  
  
    public static <T, ID> void mergeByCheckedIds(final Collection<T> srcObjects, final Collection<ID> checkedIds,
            final Class<T> clazz) {
        mergeByCheckedIds(srcObjects, checkedIds, clazz, "id");
    }

        
  
  
  
  
  
  
  
  
  
  
    public static <T, ID> void mergeByCheckedIds(final Collection<T> srcObjects, final Collection<ID> checkedIds,
            final Class<T> clazz, final String idName) {

                
        Assert.notNull(srcObjects, "scrObjects不能为空");
        Assert.hasText(idName, "idName不能为空");
        Assert.notNull(clazz, "clazz不能为空");

                                     
        if (checkedIds == null) {
            srcObjects.clear();
            return;
        }

                                         
                                                             
        Iterator<T> srcIterator = srcObjects.iterator();

        try {

            while (srcIterator.hasNext()) {
                T element = srcIterator.next();
                Object id;

                id = PropertyUtils.getProperty(element, idName);

                if (!checkedIds.contains(id)) {
                    srcIterator.remove();
                } else {
                    checkedIds.remove(id);
                }
            }

                                                        
            for (ID id : checkedIds) {
                T obj = clazz.newInstance();

                PropertyUtils.setProperty(obj, idName, id);
                srcObjects.add(obj);
            }
        } catch (Exception e) {
            throw ReflectionUtils.convertToUncheckedException(e);
        }
    }

        
  
  
  
  
  
    public static List<PropertyFilter> buildPropertyFilters(final HttpServletRequest request) {
        return buildPropertyFilters(request, "filter_");
    }

        
  
  
  
  
  
  
  
    @SuppressWarnings("unchecked")
    public static List<PropertyFilter> buildPropertyFilters(final HttpServletRequest request,
            final String filterPrefix) {

        List<PropertyFilter> filterList = new ArrayList<PropertyFilter>();

                                                
        Map<String, Object> filterParamMap = WebUtils.getParametersStartingWith(request, filterPrefix);

                                      
        for (Map.Entry<String, Object> entry : filterParamMap.entrySet()) {
            String filterName = entry.getKey();
            String value;

            if (entry.getValue() instanceof String[]) {
                String[] values = (String[]) entry.getValue();

                value = values[0];
            } else {
                value = (String) entry.getValue();
            }

                                      
            boolean omit = StringUtils.isBlank(value);

            if (!omit) {
                PropertyFilter filter = new PropertyFilter(filterName, value);

                filterList.add(filter);
            }
        }

        return filterList;
    }
}
