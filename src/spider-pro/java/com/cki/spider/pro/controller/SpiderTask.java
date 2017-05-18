   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import java.io.Serializable;

import java.util.Collection;
import java.util.Collections;

import com.cki.spider.pro.SpiderUrl;

   
  
  
 
  
public class SpiderTask implements Serializable {

    private static final long serialVersionUID = 2826714252549686577L;

                                                                                                                                         

       
  
  
    private SpiderUrl seed;

       
  
  
    private TraverseStrategy traverseStrategy;

       
  
  
    private DomainBreadth domainBreadth;

       
  
  
    private Collection<String> assignedDomains = null;

       
  
  
    private int fetchDepth;

       
  
  
    private long maxFetched;

                                                                                                                                       

    public SpiderTask() {

        this.traverseStrategy = TraverseStrategy.BREADTH_FIRST;
        this.domainBreadth = DomainBreadth.CURRENT_COMAIN;
        this.assignedDomains = Collections.emptyList();
    }

                                                                                                                                           
    public SpiderUrl getSeed() {
        return seed;
    }

    public void setSeed(SpiderUrl seed) {
        this.seed = seed;
    }

    public TraverseStrategy getTraverseStrategy() {
        return traverseStrategy;
    }

    public void setTraverseStrategy(TraverseStrategy traverseStrategy) {
        this.traverseStrategy = traverseStrategy;
    }

    public int getFetchDepth() {
        return fetchDepth;
    }

    public void setFetchDepth(int fetchDepth) {
        this.fetchDepth = fetchDepth;
    }

    public long getMaxFetched() {
        return maxFetched;
    }

    public void setMaxFetched(long maxFetched) {
        this.maxFetched = maxFetched;
    }

    public DomainBreadth getDomainBreadth() {
        return domainBreadth;
    }

    public void setDomainBreadth(DomainBreadth domainBreadth) {
        this.domainBreadth = domainBreadth;
    }

    public Collection<String> getAssignedDomains() {
        return assignedDomains;
    }

    public void setAssignedDomains(Collection<String> assignedDomains) {
        this.assignedDomains = assignedDomains;
    }

}
