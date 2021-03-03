package com.druid;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClient.ResponseSpec;

@Validated
@RestController
public class restController  {
	private static final Logger log = LoggerFactory.getLogger(restController.class);
	
	@Value("${zookeeperHosts}")
	private String zookeeperHosts;
	@Value("${rootZNode}")
	private String rootZNode;
	@Value("${configRootZNode}")
	private String configRootZNode;
	
    @RequestMapping("/getNodeData")
    public String getNodeData(@RequestParam @NotBlank(message = "Must provide nodeName") String nodeName) {
    	zookeeper ZookeeperConnection = new zookeeper(zookeeperHosts, rootZNode, configRootZNode);
    	String resultData = ZookeeperConnection.zookeeperGet(nodeName);
    	if(resultData == null) { 
            return (new JSONArray()).toString();
    	} else {
    	    return ZookeeperConnection.zookeeperGet(nodeName);
    	}
    }
    
    @PostMapping(value = "/setNodeData", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String setNodeData(@RequestParam @NotBlank(message = "Must provide nodeName") String nodeName, @RequestBody List<@Valid rule> nodeData) {
    	zookeeper ZookeeperConnection = new zookeeper(zookeeperHosts, rootZNode, configRootZNode);
    	JSONArray nodeDataJSONArray = new JSONArray();
    	nodeData.forEach(jsonNode -> nodeDataJSONArray.put(jsonNode.getJSONObject()));
    	ZookeeperConnection.zookeeperSet(nodeName, nodeDataJSONArray.toString());
    	return getNodeData(nodeName);
    }
    
    @RequestMapping("/deleteNodeData")
    public String deleteNodeData(@RequestParam @NotBlank(message = "Must provide nodeName") String nodeName) {
    	zookeeper ZookeeperConnection = new zookeeper(zookeeperHosts, rootZNode, configRootZNode);
    	ZookeeperConnection.zookeeperDelete(nodeName);
    	return getNodeData(nodeName);
    }
    
    @RequestMapping("/druidDataSources")
    public String druidDataSources() {   
    	zookeeper ZookeeperConnection = new zookeeper(zookeeperHosts, rootZNode, configRootZNode);
    	
    	for(String druidBroker : ZookeeperConnection.getDruidHostList("druid", "router")) {
        	WebClient webClient = WebClient.create("http://" + druidBroker);
        	ResponseSpec responseSpec = webClient.get().uri("/druid/coordinator/v1/datasources").accept(MediaType.APPLICATION_JSON).retrieve();
        	
        	try {
            	ResponseEntity<String[]> responseEntity = responseSpec.toEntity(String[].class).block();
            	
            	if (responseEntity.getStatusCode() == HttpStatus.OK) {
            	    JSONArray nodeDataJSONArray = new JSONArray(responseEntity.getBody());
        		    nodeDataJSONArray.put("_default");
        		    return nodeDataJSONArray.toString();
            	}
        	} catch (Exception e) { log.error("Exception", e); }
    	}
    	
    	return "[]";
    }
}
