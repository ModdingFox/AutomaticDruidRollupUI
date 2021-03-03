package com.druid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class zookeeper {
	private static final Logger log = LoggerFactory.getLogger(zookeeper.class);
	
	private ZooKeeper zooKeeperConnection = null;
	private String zookeeperHosts;
	private String rootZNode;
	private String configRootZNode;
	
	public zookeeper(String zookeeperHosts, String rootZNode, String configRootZNode) {
		this.zookeeperHosts = zookeeperHosts;
		this.rootZNode = rootZNode;
		this.configRootZNode = configRootZNode;
	}
	
    private void zooKeeperConnect(String hostStringIn)
    {
    	zooKeeperConnection = null;
    	CountDownLatch connectionLatch = new CountDownLatch(1);
    	
    	try
    	{
    		zooKeeperConnection = new ZooKeeper(hostStringIn, 2000, new Watcher() {
			    public void process(WatchedEvent we)
			    {
			        if (we.getState() == KeeperState.SyncConnected) { connectionLatch.countDown(); }
			    }
			});
			
			connectionLatch.await();
			
			if(zooKeeperConnection.exists(rootZNode, false) == null) {
				zooKeeperConnection.create(rootZNode, null, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
		}
    	catch (IOException | InterruptedException | KeeperException e) { log.error("Zookeeper Error: " + hostStringIn, e); }
    }
    
    private void zookeeperDisconnect()
	{
		if(zooKeeperConnection != null)
		{
			try { zooKeeperConnection.close(); }
			catch (InterruptedException e) { log.error("InterruptedException", e); }
		}
		else { log.error("Attempted to close a null zookeeper connection"); }
	}
	
	public String zookeeperGet(String nodeName)
	{
		zooKeeperConnect(zookeeperHosts);
		byte[] resultBytes = null;
		String resultString = null;
		
		try {
			if(zooKeeperConnection.exists(configRootZNode + "/" + nodeName, false) != null) {
			    resultBytes = zooKeeperConnection.getData(configRootZNode + "/" + nodeName, null, null);
			    resultString = new String(resultBytes, "UTF-8");
			}
		}
		catch (KeeperException e) { log.error("KeeperException",  e); }
		catch (InterruptedException e) { log.error("InterruptedException",  e); }
		catch (UnsupportedEncodingException e) { log.error("UnsupportedEncodingException",  e); }
		zookeeperDisconnect();
		return resultString;
	}
	
	public void zookeeperSet(String nodeName, String nodeData)
	{
		zooKeeperConnect(zookeeperHosts);
		byte[] nodeDataBytes = nodeData.getBytes();
		
		try {
			if(zooKeeperConnection.exists(configRootZNode + "/" + nodeName, false) == null) {
				zooKeeperConnection.create(configRootZNode + "/" + nodeName, nodeDataBytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			} else {
				zooKeeperConnection.setData(configRootZNode + "/" + nodeName, nodeDataBytes, -1);
			}
		}
		catch (KeeperException e) { log.error("KeeperException",  e); }
		catch (InterruptedException e) { log.error("InterruptedException",  e); }
		zookeeperDisconnect();
	}
	
	public void zookeeperDelete(String nodeName)
	{
		zooKeeperConnect(zookeeperHosts);
		try {
			zooKeeperConnection.delete(configRootZNode + "/" + nodeName, -1);
		}
		catch (KeeperException e) { log.error("KeeperException",  e); }
		catch (InterruptedException e) { log.error("InterruptedException",  e); }
		zookeeperDisconnect();
	}
	
	public List<String> getDruidHostList(String serviceName, String target) {
		zooKeeperConnect(zookeeperHosts);
		
		String druidComponentZNodePath = rootZNode + "/" + serviceName + ":" + target;
		List<String> result = new ArrayList<>();
		
		try {
			if(zooKeeperConnection.exists(druidComponentZNodePath, false) != null) {
				for(String zookeeperChild : zooKeeperConnection.getChildren(druidComponentZNodePath, false)) {
					byte[] resultBytes = zooKeeperConnection.getData(druidComponentZNodePath + "/" + zookeeperChild, null, null);
					String resultString = new String(resultBytes, "UTF-8");
					JSONObject resultJson = new JSONObject(resultString);
					result.add(resultJson.getString("address") + ":" + resultJson.getInt("port"));
				}
			}
		}
		catch (KeeperException e) { log.error("KeeperException",  e); }
		catch (InterruptedException e) { log.error("InterruptedException",  e); }
		catch (UnsupportedEncodingException e) { log.error("UnsupportedEncodingException",  e); }
		
		zookeeperDisconnect();
		
		return result;
	}
}
