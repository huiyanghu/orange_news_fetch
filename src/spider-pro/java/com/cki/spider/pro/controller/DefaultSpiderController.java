   
  
  
  
  
  
  

package com.cki.spider.pro.controller;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;

import org.jboss.netty.util.internal.ExecutorUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cki.spider.pro.Spider;
import com.cki.spider.pro.controller.filter.SimilarUrlDetector;
import com.cki.spider.pro.controller.filter.HashBasedDuplicateDetector;
import com.cki.spider.pro.util.NamedThreadFactory;

   
  
  
 
  
@SuppressWarnings("unchecked")
public class DefaultSpiderController implements SpiderController {

                                                                                                          
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final Spider spider;

    private LinkedBlockingQueue<SpiderTaskContext> taskQueue;

    private final SpiderUrlQueue defaultFetchUrlQueue;

    private final LinkResolver defaultLinkExtractor;

    private Executor fetchExecutor;

    private Executor taskDispatchExecutor;

    private volatile boolean terminate;

                                                                                                          
    public DefaultSpiderController(Spider spider, int maxTaskInQueue) {

        this.spider = spider;
        this.taskQueue = new LinkedBlockingQueue<SpiderTaskContext>(maxTaskInQueue);
        this.fetchExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("FetchController"));
        this.taskDispatchExecutor = Executors.newSingleThreadExecutor(new NamedThreadFactory("FetchTaskDispatcher"));
        this.defaultFetchUrlQueue = new MemBasedSpiderUrlQueue();
        this.defaultLinkExtractor = new AHrefBasedLinkResolver();
        this.terminate = false;
    }

    public void init() {

        this.fetchExecutor.execute(new Runnable() {

            @Override
            public void run() {

                while (true) {

                    if (Thread.currentThread().isInterrupted()) {
                        logger.info("feed spider thread interrupted.");
                        return;
                    }

                    try {
                        InternalSpiderUrl fetchUrl = defaultFetchUrlQueue.take();

                        SpiderTaskContext taskContext = fetchUrl.getTaskContext();

                        spider.fetch(fetchUrl, taskContext.getFetchUrlListener(), taskContext.getFetchUrlFilter());

                    } catch (InterruptedException e) {
                        logger.info("feed spider thread interrupted.");
                        return;
                    }

                }
            }

        });

        this.taskDispatchExecutor.execute(new Runnable() {

            @Override
            public void run() {

                while (true) {
                    if (Thread.currentThread().isInterrupted()) {
                        logger.info("task dispatcher thread interrupted.");
                        return;
                    }

                    try {
                        SpiderTaskContext taskContext = taskQueue.take();

                        taskContext.execute();

                    } catch (InterruptedException e) {
                        logger.info("task dispatcher thread interrupted.");
                        return;
                    }

                }
            }
        });
    }

    @Override
    public Future<TaskStatistics> submit(SpiderTask task, SpiderTaskFilter filter, SpiderTaskListener listener) {

        return submit(task, new HashBasedDuplicateDetector(), this.defaultLinkExtractor, this.defaultFetchUrlQueue,
                      filter, listener);

    }

    @Override
    public Future<TaskStatistics> submit(SpiderTask task, SimilarUrlDetector similarUrlDetector,
            LinkResolver linkExtractor, SpiderUrlQueue fetchUrlQueue, SpiderTaskFilter filter,
            SpiderTaskListener listener) {

        if (this.terminate) {
            throw new IllegalStateException("fetchController already terminated.");
        }

        SpiderTaskContext context = new SpiderTaskContext(task, filter, listener, similarUrlDetector, linkExtractor,
                                       this.defaultFetchUrlQueue);

        try {
            taskQueue.put(context);
        } catch (InterruptedException e) {
            return null;
        }

        return context.getFuture();
    }

    @Override
    public void termiate() {

        if (this.terminate) {
            return;
        }

        ExecutorUtil.terminate(this.taskDispatchExecutor, this.fetchExecutor);

        this.spider.terminate();

        logger.info("fetchController terminated.");

    }

                                                                                                          

}
