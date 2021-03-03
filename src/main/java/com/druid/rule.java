package com.druid;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

import org.json.JSONObject;

public class rule {
	@NotBlank(message = "Period cannot be blank")
	@Pattern(regexp = "^P\\d+[M|D|Y]$", message = "Period must match Pattern ^P\\d+[M|D|Y]$")
	private String period;
	
	@NotBlank(message = "segmentGranularity cannot be blank")
	@Pattern(regexp = "^(NONE|SECOND|MINUTE|FIVE_MINUTE|TEN_MINUTE|FIFTEEN_MINUTE|THIRTY_MINUTE|HOUR|SIX_HOUR|DAY)$", message = "segmentGranularity must be one of NONE, SECOND, MINUTE, FIVE_MINUTE, TEN_MINUTE, FIFTEEN_MINUTE, THIRTY_MINUTE, HOUR, SIX_HOUR, or DAY")
	private String segmentGranularity;
	
	@NotBlank(message = "queryGranularity cannot be blank")
	@Pattern(regexp = "^(NONE|SECOND|MINUTE|FIVE_MINUTE|TEN_MINUTE|FIFTEEN_MINUTE|THIRTY_MINUTE|HOUR|SIX_HOUR|DAY)$", message = "queryGranularity must be one of NONE, SECOND, MINUTE, FIVE_MINUTE, TEN_MINUTE, FIFTEEN_MINUTE, THIRTY_MINUTE, HOUR, SIX_HOUR, or DAY")
	private String queryGranularity;
	
	rule(@JsonProperty("Period") String period, @JsonProperty("segmentGranularity") String segmentGranularity, @JsonProperty("queryGranularity") String queryGranularity) {
		this.period = period;
		this.segmentGranularity = segmentGranularity;
		this.queryGranularity = queryGranularity;
	}
	
	public String getPeriod() { return period; }
	public String getSegmentGranularity() { return segmentGranularity; }
	public String getQueryGranularity() { return queryGranularity; }
	public JSONObject getJSONObject() {
		JSONObject result = new JSONObject();
		result.put("Period", period);
		result.put("segmentGranularity", segmentGranularity);
		result.put("queryGranularity", queryGranularity);
		return result;
	}
}