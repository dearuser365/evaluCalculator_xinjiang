<%@page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<script type="text/javascript" src="../js/jquery-1.8.3.js"></script>
<script>
function demoAjax(){
	$.ajax({
		url : "../demoController/demoAjax.do",
		type : 'post',
		data : {"demo1" : "demo1", "demo2" : "demo2"},
		cache : false,
		success : function(data) {
			if (data.success) {
				alert(data.attributes.demo);
				alert(JSON.stringify(data.attributes.userList));
			} else {
				alert("获取失败");
			}
		},
		error: function(){
		}
	});
}

//websocket
var wsUri = "ws://localhost:8080/SSM/kdwebsocket.ws";
var ws = null;
function closeServer(){
	if (ws) {
		ws.close();
	}
}
function startServer() {
	ws = new WebSocket(wsUri);

	// WebSocket握手完成，连接成功的回调
	// 有个疑问，按理说new WebSocket的时候就会开始连接了，如果在设置onopen以前连接成功，是否还会触发这个回调
	ws.onopen = function() { 
		writeToScreen('Opened!'); 
    };

    // 收到服务器发送的文本消息, event.data表示文本内容
	ws.onmessage = function(event) { 
		writeToScreen('Receive message: ' + event.data); 
	};

	// 关闭WebSocket的回调
	ws.onclose = function() {
		writeToScreen('Closed!'); 
	};
}

function sendMyMessage() {
	var textMessage = document.getElementById('textMessage').value;
	if(ws != null && textMessage != '') {
		ws.send(textMessage);
	}
}
function writeToScreen(message) {
	var pre = document.createElement("p");
	pre.style.wordWrap = "break-word";
	pre.innerHTML = message;
	output.appendChild(pre);
	
	output.scrollTop = output.scrollHeight;
}
//window.addEventListener("load", init, false);
$(document).ready(function(){
	startServer();
});
</script>
</head>
<body>
	<input type="button" value="AjaxDemo" onclick="demoAjax()"/>
	
	<div>
		向服务器发送<input type="text" id="textMessage" size="20" />
		<input type="button" onclick="sendMyMessage()" value="发送">
		<input type="button" onclick="closeServer()" value="关闭">
		<div id="output" style="width:800px;height:600px;background:#ededed;overflow-y:auto;"></div>
	</div>
</body>
</html>