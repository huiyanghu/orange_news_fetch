package com.cki.filter.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FilterConfig {

	@Value("${project.base}")
	private String projectBase;
	@Value("${filter.server.debug}")
	private boolean debug = false;
	@Value("${filter.dump.path}")
	private String dumpPath;

	private boolean replicateOn;

	private int replicationPort;

	private boolean master;

	private boolean slave;

	private String masterAddress;

	public boolean isDebug() {
		return debug;
	}

	public void setDebug(boolean debug) {
		this.debug = debug;
	}

	public String getDumpPath() {
		return projectBase + dumpPath;
	}

	public void setDumpPath(String dumpPath) {
		this.dumpPath = dumpPath;
	}

	public boolean isReplicateOn() {
		return replicateOn;
	}

	public void setReplicateOn(boolean replicateOn) {
		this.replicateOn = replicateOn;
	}

	public int getReplicationPort() {
		return replicationPort;
	}

	public void setReplicationPort(int replicationPort) {
		this.replicationPort = replicationPort;
	}

	public boolean isMaster() {
		return master;
	}

	public void setMaster(boolean master) {
		this.master = master;
	}

	public boolean isSlave() {
		return slave;
	}

	public void setSlave(boolean slave) {
		this.slave = slave;
	}

	public String getMasterAddress() {
		return masterAddress;
	}

	public void setMasterAddress(String masterAddress) {
		this.masterAddress = masterAddress;
	}

}
