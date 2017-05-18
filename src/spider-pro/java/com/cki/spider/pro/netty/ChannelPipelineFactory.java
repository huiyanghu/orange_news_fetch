package com.cki.spider.pro.netty;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.example.securechat.SecureChatSslContextFactory;
import org.jboss.netty.handler.codec.http.HttpChunkAggregator;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.ssl.SslHandler;

import com.cki.spider.pro.SpiderConfig;

public class ChannelPipelineFactory {

	private SpiderConfig config;

	public ChannelPipelineFactory(SpiderConfig config) {
		this.config = config;
	}

	public ChannelPipeline getPipeline(boolean ssl, ChannelHandler handler) {

		ChannelPipeline pipeline = Channels.pipeline();

		if (ssl) {
			SSLEngine engine = SecureChatSslContextFactory.getServerContext().createSSLEngine();

			engine.setUseClientMode(true);
			pipeline.addLast("ssl", new SslHandler(engine));
		}

		pipeline.addLast("codec", new HttpClientCodec(4096, 8192, config.getRequestChunkSize()));

		pipeline.addLast("inflater", new HttpContentDecompressor());

		pipeline.addLast("aggregator", new HttpChunkAggregator(config.getMaxContentLength()));

		pipeline.addLast("handler", handler);

		return pipeline;
	}

	public SpiderConfig getConfig() {
		return config;
	}

}
