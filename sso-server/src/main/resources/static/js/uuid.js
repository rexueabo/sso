function guid() {
    return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
    });
}

function getuuid(){
	var name = "uuid";
	var uuid = getCookie(name);
	if(uuid == null || uuid = ""){
		uuid =  guid();
		setCookie(name, uuid)
	}
	return uuid;
	
}

function getCookie(name)
{
var arr,reg=new RegExp("(^| )"+name+"=([^;]*)(;|$)");
if(arr=document.cookie.match(reg))
return unescape(arr[2]);
else
return null;
}

/设置cookie
function setCookie(cname, cvalue) {
    document.cookie = cname + "=" + cvalue + "; ";
}