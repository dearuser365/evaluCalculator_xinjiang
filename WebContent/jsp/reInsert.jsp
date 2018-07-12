<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />

<link rel="stylesheet" href="js/My97DatePicker/skin/WdatePicker.css" type="text/css" media="all" />
<style>
body{
	text-rendering: auto;
    color: initial;
    letter-spacing: normal;
    word-spacing: normal;
    text-transform: none;
    text-indent: 0px;
    text-shadow: none;
    text-align: start;
    font: 13.3333px Arial;
    font-style: normal;
    font-variant-ligatures: normal;
    font-variant-caps: normal;
    font-variant-numeric: normal;
    font-weight: normal;
    font-stretch: normal;
    font-size: 13.3333px;
    line-height: normal;
    font-family: Arial;
}
</style>
<script type="text/javascript" src="js/jquery-1.8.3.js"></script>
<script type="text/javascript" src="js/My97DatePicker/WdatePicker.js"></script>
<script>
var calculateMap = <%=request.getAttribute("calculateMap")%>;
var insertorMap = <%=request.getAttribute("insertorMap")%>;
var dateStr = '<%=request.getAttribute("dateStr")%>';
var monthStr = '<%=request.getAttribute("monthStr")%>';
var organList = <%=request.getAttribute("organList")%>;

function calculatorNewIndexAjax(thiz){
	$.ajax({
		url : "reInsertFileController/calculatorNewIndexAjax.do",
		type : 'post',
		data:{dateStr:thiz.prev().val()},
		cache : false,
		success : function(data) {
			alert("计算新指标成功！");
		},
		error: function(){
		}
	});
}

//重算按钮ajax
function reCalculateAllAjax(thiz){
	console.log(thiz.prev().val());
	$.ajax({
		url : "reInsertFileController/reCalculateAllAjax.do",
		data:{dateStr:thiz.prev().val()},
		type : 'post',
		cache : false,
		success : function(data) {
			alert("全部重算成功！");
		},
		error: function(){
		}
	});
}

//调用拓扑校验
function checkTopoAjax(thiz){
	$.ajax({
		url : "reInsertFileController/checkTopoAjax.do",
		data:{dateStr:thiz.prev().val()},
		type : 'post',
		cache : false,
		success : function(data) {
			alert("校验拓扑程序执行成功！");
		},
		error: function(){
		}
	});
}

$(document).ready(function(){
	for (var i=0;i<calculateMap.length;i++) {
		var calculator = calculateMap[i];
		var option = '<option value="'+calculator.className+'">'+calculator.name+'</option>';
		$("#singleCalculateSelect").append($(option));
		if (calculator.isUnevalu) {
			$("#unevalueCalculateSelect").append($(option));
		}
	}
	for (var i=0;i<insertorMap.length;i++) {
		var insertor = insertorMap[i];
		var option = '<option value="'+insertor.className+'">'+insertor.name+'</option>';
		$("#insertSingleDetailAjaxSelect").append($(option));
	}
	//初始化置位organList
	for (var i = 0; i < organList.length; i++) {
		var organ = organList[i];
		$("#organCodeSelect").append($('<option value="'+organ.code+'">'+organ.name+'</option>'));
	}
	
	//重算按钮
	$("#reCalculateAllAjaxBtn").click(function(){
		reCalculateAllAjax($(this));
	});
	//拓扑校验按钮
	$("#checkTopoAjaxBtn").click(function(){
		checkTopoAjax($(this));
	});
	$("#calculatorNewIndexAjaxBtn").click(function(){
		calculatorNewIndexAjax($(this));
	});
	
	//单个指标计算
	$("#calculatorSingleIndexAjaxBtn").click(function(){
		var selectedValue = $("#singleCalculateSelect").val();
		if (selectedValue == -1) {
			alert("请选择一个计算指标。");
		} else {
			var option = $(this).find("option:selected");
			console.log(option.text());
			$.ajax({
				url : "reInsertFileController/calculatorSingleIndexAjax.do",
				type : 'post',
				data : {"className":selectedValue, dateStr:$(this).prev().val()},
				cache : false,
				success : function(data) {
					alert("重算单个指标成功！");
				},
				error: function(){
				}
			});
		}
	});
	
	//单类Dms文件解析
	$("#singleFileParserAjaxBtn").click(function(){
		var selectedValue = $("#singleFileParserSelect").val();
		if (selectedValue == -1) {
			alert("请选择一类文件。");
		} else {
			var option = $(this).find("option:selected");
			console.log(option.text());
			var name = option.text();
			$.ajax({
				url : "reInsertFileController/parseSingleDmsFileAjax.do",
				type : 'post',
				data : {"datatype":selectedValue},
				cache : false,
				success : function(data) {
					alert("解析文件入库成功！");
				},
				error: function(){
				}
			});
		}
	});
	
	//Dms单类明细入库
	$("#insertSingleDetailAjaxBtn").click(function(){
		var selectedValue = $("#insertSingleDetailAjaxSelect").val();
		if (selectedValue == -1) {
			alert("请选择一类明细。");
		} else {
			var option = $(this).find("option:selected");
			console.log(option.text());
			var name = option.text();
			$.ajax({
				url : "reInsertFileController/insertSingleDetailAjax.do",
				type : 'post',
				data : {"className":selectedValue, dateStr:$(this).prev().val()},
				cache : false,
				success : function(data) {
					alert("录入明细成功！");
				},
				error: function(){
				}
			});
		}
	});
	
	//计算免考核
	$("#calculatorUnevaluAjaxBtn").click(function(){
		var selectedValue = $("#unevalueCalculateSelect").val();
			var option = $(this).find("option:selected");
			console.log(option.text());
			$('#calculatorUnevaluAjaxBtn').attr("disabled",true); 
			$.ajax({
				url : "reInsertFileController/calculatorUnevaluAjax.do",
				type : 'post',
				data : {"className":selectedValue, dateStr:$(this).prev().val()},
				cache : false,
				success : function(data) {
					$('#calculatorUnevaluAjaxBtn').attr("disabled",false); 
					alert("计算免考核成功！");
				},
				error: function(){
				}
			});
	});
	
	//Pms台账入库
	$("#parseGpmsFileAjaxBtn").click(function(){
		$.ajax({
			url : "reInsertFileController/parseGpmsFileAjax.do",
			type : 'post',
			cache : false,
			success : function(data) {
				alert("录入PMS台账成功！");
			},
			error: function(){
			}
		});
	});
	
	//全部Dms文件解析
	$("#parseDmsFileAjaxBtn").click(function(){
		$.ajax({
			url : "reInsertFileController/parseDmsFileAjax.do",
			type : 'post',
			cache : false,
			success : function(data) {
				alert("录入DMS台账成功！");
			},
			error: function(){
			}
		});
	});
	
	//设置/取消免考核
	$(".setFlagAjaxBtn").click(function(){
		var flag = $(this).attr("data-type");
		var startDate = $("#startDate")[0].value;
		var endDate = $("#endDate")[0].value;
		var organCode = $("#organCodeSelect").val();
		var code = $("#codeSelect").val();
		var kxIds = "";
		var ypIds = "";
		var codename = $("#codeSelect").find("option:selected").text();
		
		if (code == -1) {
			alert("请选择一个指标");
			return false;
		}
		$.ajax({
			url : "reInsertFileController/setFlagAjax.do",
			type : 'post',
			data : {
				"startDate": startDate,
				"endDate": endDate,
				"organCode": organCode,
				"code" : code,
				"flag" : flag,
				"ypIds" : ypIds,
				"kxIds" : kxIds,
				"codename":codename
			},
			cache : false,
			success : function(data) {
				alert(data.msg);
			},
			error: function(){
			}
		});
	});
	
	$("input[name='date']").val(dateStr);
	$("input[name='mdate']").val(monthStr);
});
</script>
</head>
<body>
<hr>
	<input type="text" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">	
	<input id="reCalculateAllAjaxBtn" type="button" value="全部重算"/>
<!-- <hr>
	<input type="text" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">
	<input id="calculatorNewIndexAjaxBtn" type="button" value="计算新指标"/> -->
<hr>
单个指标计算：
<select id="singleCalculateSelect">
	<option value=-1>--请选择一个指标--</option>
</select>
<input type="text" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">
<input id="calculatorSingleIndexAjaxBtn" type="button" value="计算指标"/>
<hr>
单类文件解析：
<select id="singleFileParserSelect">
	<option value=-1>--请选择一类文件--</option>
	<option value="DMS@DmsBusFileInsertor">母线台账文件(_bus.xml)</option>
	<option value="DMS@DmsCbFileInsertor">开关台账文件(_cb.xml)</option>
	<option value="DMS@DmsDisFileInsertor">刀闸台账列表文件(_dsc.xml)</option>
	<option value="DMS@DmsSubsFileInsertor">变电站列表文件(_substation.xml)</option>	
	<option value="DMS@DmsTransFileInsertor">配变台账文件(_transformer.xml)</option>	
</select>
<input id="singleFileParserAjaxBtn" type="button" value="解析"/>
<input id="parseDmsFileAjaxBtn" type="button" value="全部解析"/>
<hr>
DMS单个明细入库：
<select id="insertSingleDetailAjaxSelect">
	<option value=-1>--请选择一个指标--</option>
</select>
<input type="text" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">
<input id="insertSingleDetailAjaxBtn" type="button" value="入库"/>
<hr>
PMS台账入库：
<input id="parseGpmsFileAjaxBtn" type="button" value="入库"/>
<hr>
计算免考核:
<select id="unevalueCalculateSelect">
	<option value=-1>--全部--</option>
</select>
<input type="text" name="mdate" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMM',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">
<input id="calculatorUnevaluAjaxBtn" type="button" value="重算"/>
<hr>
配置flag位:
<select id="organCodeSelect" style="height:22px">
	<option value="all">全省</option>
</select>
<select id="codeSelect" style="height:22px">
	<option value=-1>--请选择一个指标--</option>
	<option value="_devfine">设备平均完整率</option>
</select>
<input type="text" id="startDate" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'#F{$dp.$D(\'endDate\')}'})" readonly="readonly">
	&nbsp;&nbsp;至&nbsp;&nbsp;
<input type="text" id="endDate" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1},',minDate:'#F{$dp.$D(\'startDate\')}'})" readonly="readonly">&nbsp;&nbsp;	
<input class="setFlagAjaxBtn" data-type="1" type="button" value="设为免考核"/>
<input class="setFlagAjaxBtn" data-type="0" type="button" value="消除免考核"/>
<hr>
	<input type="text" name="date" class="Wdate" onfocus="WdatePicker({dateFmt:'yyyyMMdd',maxDate:'%y-%M-{%d-1}'})" readonly="readonly">	
	<input id="checkTopoAjaxBtn" type="button" value="校验拓扑程序"/>
<!-- <hr>
	<input type=button onclick="window.open('http://10.35.2.204:34240/KH_dbToFile')" value="获取xml文件界面跳转"> -->
</body>
</html>