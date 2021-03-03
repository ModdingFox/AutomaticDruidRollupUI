var granularities = [ "NONE", "SECOND", "MINUTE", "FIVE_MINUTE", "TEN_MINUTE", "FIFTEEN_MINUTE", "THIRTY_MINUTE", "HOUR", "SIX_HOUR", "DAY" ];

function clearDruidDataSourceRules(){
    var druidDataSourceRules = $('#druidDataSourceRules');
    
    var druidDataSourceRulesHeaderThead = document.createElement("thead");
    var druidDataSourceRulesTBody = document.createElement("tbody");
    var druidDataSourceRulesHeaderTRow = document.createElement("tr");
    var period = document.createElement("th");
    var segmentGranularity = document.createElement("th");
    var queryGranularity = document.createElement("th");
    var position = document.createElement("th");
    
    period.innerHTML = "Period";
    segmentGranularity.innerHTML = "Segment Granularity";
    queryGranularity.innerHTML = "Query Granularity";
    position.innerHTML = "Position";
    druidDataSourceRulesTBody.id = "druidDataSourceRulesTBody";
    
    druidDataSourceRulesHeaderTRow.appendChild(period);
    druidDataSourceRulesHeaderTRow.appendChild(segmentGranularity);
    druidDataSourceRulesHeaderTRow.appendChild(queryGranularity);
    druidDataSourceRulesHeaderTRow.appendChild(position);
    druidDataSourceRulesHeaderThead.appendChild(druidDataSourceRulesHeaderTRow);
    druidDataSourceRules.empty();
    druidDataSourceRules.append(druidDataSourceRulesHeaderThead);
    druidDataSourceRules.append(druidDataSourceRulesTBody);
}

function addDruidDataSourceRulesRow(periodIn, segmentGranularityIn, queryGranularityIn, ruleNumberIn) {
    var druidDataSourceRulesTBody = $('#druidDataSourceRulesTBody');
    var druidDataSourceRulesBodyTRow = document.createElement("tr");
    
    var period = document.createElement("td");
    var segmentGranularity = document.createElement("td");
    var queryGranularity = document.createElement("td");
    var position = document.createElement("td");

    var periodInput = document.createElement("input");
    periodInput.id = "periodInput-" + ruleNumberIn;
    periodInput.value = periodIn;
    
    var segmentGranularitySelect = document.createElement("select");
    segmentGranularitySelect.id = "segmentGranularitySelect-" + ruleNumberIn;
    var queryGranularitySelect = document.createElement("select");
    queryGranularitySelect.id = "queryGranularitySelect-" + ruleNumberIn;
    
    granularities.forEach(async function(granularity) {
	var option = document.createElement("option");
	option.value = granularity;
	option.innerHTML = granularity;
	segmentGranularitySelect.appendChild(option);
        option = document.createElement("option");
        option.value = granularity;
        option.innerHTML = granularity;
	queryGranularitySelect.appendChild(option);
    });
    
    segmentGranularitySelect.value = segmentGranularityIn;
    queryGranularitySelect.value = queryGranularityIn;
   
    var positionUp = document.createElement("button");
    positionUp.innerHTML = "Up";
    positionUp.setAttribute("onclick", "MovePositionUp(" + ruleNumberIn + ")");
    var positionDown = document.createElement("button");
    positionDown.innerHTML = "Down";
    positionDown.setAttribute("onclick", "MovePositionDown(" + ruleNumberIn + ")"); 
    var positionDelete = document.createElement("button");
    positionDelete.innerHTML = "Delete";
    positionDelete.setAttribute("onclick", "DeletePosition(" + ruleNumberIn + ")");
    
    period.appendChild(periodInput);
    segmentGranularity.appendChild(segmentGranularitySelect);
    queryGranularity.appendChild(queryGranularitySelect);
    position.innerHTML = ruleNumberIn;
    
    druidDataSourceRulesBodyTRow.appendChild(period);
    druidDataSourceRulesBodyTRow.appendChild(segmentGranularity);
    druidDataSourceRulesBodyTRow.appendChild(queryGranularity);
    druidDataSourceRulesBodyTRow.appendChild(positionUp);
    druidDataSourceRulesBodyTRow.appendChild(positionDown);
    druidDataSourceRulesBodyTRow.appendChild(positionDelete);
    druidDataSourceRulesTBody.append(druidDataSourceRulesBodyTRow);
}

function buildTable(jsonIn) {
    var jsonPayload = JSON.parse(jsonIn);
    var ruleNumber = 0;
    clearDruidDataSourceRules();
    jsonPayload.forEach(function(row) {
        addDruidDataSourceRulesRow(row['Period'], row['segmentGranularity'], row['queryGranularity'], ruleNumber);
	ruleNumber = ruleNumber + 1;
    });
}

function updateJsonPayloadValues() {
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var jsonPayload = JSON.parse(druidDataSourceRulesJson.val());
    var newJsonPayload = [];
    ruleNumberIn = 0;
    jsonPayload.forEach(function(currentPayload) {
	currentPayload["Period"] = $("#periodInput-" + ruleNumberIn).val();
	currentPayload["segmentGranularity"] = $("#segmentGranularitySelect-" + ruleNumberIn).val();
	currentPayload["queryGranularity"] = $("#queryGranularitySelect-" + ruleNumberIn).val();
	newJsonPayload.push(currentPayload);
	ruleNumberIn = ruleNumberIn + 1;
    });
    newJsonPayloadString = JSON.stringify(newJsonPayload);
    $('#druidDataSourceRulesJson').val(newJsonPayloadString);
}

function MovePositionUp(currentPosition) {
    updateJsonPayloadValues();
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var jsonPayload = JSON.parse(druidDataSourceRulesJson.val());
    var newPosition = currentPosition - 1;
    
    if (currentPosition > 0){
        var tmpObject = jsonPayload[newPosition];
	jsonPayload[newPosition] = jsonPayload[currentPosition];
	jsonPayload[currentPosition] = tmpObject;
	newJsonPayload = JSON.stringify(jsonPayload);
	$('#druidDataSourceRulesJson').val(newJsonPayload);
	buildTable(newJsonPayload);
    }
}

function MovePositionDown(currentPosition) {
    updateJsonPayloadValues();
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var jsonPayload = JSON.parse(druidDataSourceRulesJson.val());
    var newPosition = currentPosition + 1;

    if (newPosition < jsonPayload.length){
        var tmpObject = jsonPayload[newPosition];
        jsonPayload[newPosition] = jsonPayload[currentPosition];
        jsonPayload[currentPosition] = tmpObject;
        newJsonPayload = JSON.stringify(jsonPayload);
        $('#druidDataSourceRulesJson').val(newJsonPayload);
        buildTable(newJsonPayload);
    }
}

function DeletePosition(currentPosition) {
    updateJsonPayloadValues();
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var jsonPayload = JSON.parse(druidDataSourceRulesJson.val());
    jsonPayload.splice(currentPosition, 1);
    newJsonPayload = JSON.stringify(jsonPayload);
    $('#druidDataSourceRulesJson').val(newJsonPayload);
    buildTable(newJsonPayload);
}

function AddPosition() {
    updateJsonPayloadValues();
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var jsonPayload = JSON.parse(druidDataSourceRulesJson.val());
    var newElement = { "Period": "", "segmentGranularity": granularities[0], "queryGranularity": granularities[0] };
    jsonPayload.push(newElement);
    newJsonPayload = JSON.stringify(jsonPayload);
    $('#druidDataSourceRulesJson').val(newJsonPayload);
    buildTable(newJsonPayload);
}

function SaveRule() {
    updateJsonPayloadValues();
    var druidDataSource = $('#druidDataSource');
    var druidDataSourceRulesJsonVal = $('#druidDataSourceRulesJson').val();
    jQuery.ajax({
        type: 'POST',
        url: "/setNodeData?nodeName=" + druidDataSource.val(),
	data: druidDataSourceRulesJsonVal,
	dataType: "json",
        processData: false,
        contentType: "application/json",
        success: function (response) {
	    alert("Rule Saved");
            jQuery.ajax({
                type: 'GET',
                url: "/getNodeData?nodeName=" + druidDataSource.val(),
                processData: false,
                contentType: false,
                success: function (response) {
		    alert("Rule Saved")
                    $('#druidDataSourceRulesJson').val(response);
                    buildTable(response);
                },
		error: function (response) {
		    alert("Rule Save Failed");
                }
            });
        }
    });
}

function DeleteRule() {
    var druidDataSource = $('#druidDataSource');
    jQuery.ajax({
        type: 'GET',
        url: "/deleteNodeData?nodeName=" + druidDataSource.val(),
        processData: false,
        contentType: false,
        success: function (response) {
            jQuery.ajax({
                type: 'GET',
                url: "/getNodeData?nodeName=" + druidDataSource.val(),
                processData: false,
                contentType: false,
                success: function (response) {
                    $('#druidDataSourceRulesJson').val(response);
                    buildTable(response);
                }
            });
        }
    });
}

$( document ).ready(function() {
    var druidDataSourceRulesJson = $('#druidDataSourceRulesJson');
    var druidDataSource = $('#druidDataSource');
    var druidDataSourceRules = $('#druidDataSourceRules');
    
    druidDataSource.change(function() {
        jQuery.ajax({
            type: 'GET',
            url: "/getNodeData?nodeName=" + druidDataSource.val(),
            processData: false,
            contentType: false,
            success: function (response) {
		$('#druidDataSourceRulesJson').val(response);
		buildTable(response);
            }
        });
    });

    jQuery.ajax({
        type: 'GET',
        url: "/druidDataSources",
        processData: false,
        contentType: false,
        success: function (response) {
            var getReturn = JSON.parse(response);
	    getReturn.forEach(async function(dataSource) {
                druidDataSource.append($('<option></option>').val(dataSource).html(dataSource));
	    });
	    druidDataSource.val("_default").change();
        }
    });
});
