function setCookie(cname,cvalue,exdays) {
    var d = new Date();
    d.setTime(d.getTime() + (exdays*24*60*60*1000));
    var expires = "expires=" + d.toGMTString();
    document.cookie = cname+"="+cvalue+"; "+expires;
}

function getCookie(cname) {
    var name = cname + "=";
    var ca = document.cookie.split(';');
    for(var i=0; i<ca.length; i++) {
        var c = ca[i];
        while (c.charAt(0)==' ') c = c.substring(1);
        if (c.indexOf(name) == 0) {
            return c.substring(name.length, c.length);
        }
    }
    return "";
}


function checkCookie() {

    var user=getCookie("username");

    if (user != "") {
 //       alert("both not emepty");
        var array = user.split(".");
    if(array[0] != "" && array[1] != ""){
        document.getElementById("user").value = array[0];
        document.getElementById("pass").value = array[1];
        document.getElementById("box").checked = true;
    } else{
      document.getElementById("box").checked = false;
     }
  }
}


function enter(){
   if(document.getElementById("user").value != "" && document.getElementById("pass").value != ""){
      if(document.getElementById("box").checked){
        setCookie("username", document.getElementById("user").value+"."+document.getElementById("pass").value , 30);
        window.location.assign("search.html");
      } else{
        setCookie("username", "." , 30);
        window.location.assign("search.html");
        }
    } else{
//both empty
if(document.getElementById("user").value == "" && document.getElementById("pass").value == "")
      alert("please fill your username and password to login");
else{
//username
if(document.getElementById("user").value == "")
      alert("please fill your username to login");
//password
if(document.getElementById("pass").value == "")
      alert("please fill your password to login");
}
    }
}