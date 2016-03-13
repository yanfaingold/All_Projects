function startsearch() {

    	if(document.getElementById("r1").checked)
              window.location.assign("https://www.google.co.il/search?q="+document.getElementById("text").value);

    	if(document.getElementById("r2").checked)
              window.location.assign("https://search.yahoo.com/search;_ylt=AwrBT7c7h1xV3a0AiOBXNyoA;_ylc=X1MDMjc2NjY3OQRfcgMyBGZyA2oEZ3ByaWQDbDM5LkEyMTFRdEdReG1COV94cEtZQQRuX3JzbHQDMARuX3N1Z2cDMARvcmlnaW4Dc2VhcmNoLnlhaG9vLmNvbQRwb3MDMARwcXN0cgMEcHFzdHJsAwRxc3RybAMxNgRxdWVyeQN3aHl5eXl5eXl5eXl5eXl5BHRfc3RtcAMxNDMyMTI3Mjk2?p="+document.getElementById("text").value);          
          
    	if(document.getElementById("r3").checked)
              window.location.assign("http://www.ask.com/web?q="+document.getElementById("text").value);
          
        if (document.getElementById("r4").checked)
              window.location.assign("https://www.bing.com/search?q="+document.getElementById("text").value);
               
    	if(document.getElementById("r5").checked)
              window.location.assign("http://search.aol.com/aol/search?s_it=searchbox.webhome&v_t=na&q="+document.getElementById("text").value);

	if(document.getElementById("r6").checked)
              window.location.assign("http://search.walla.co.il/?q="+document.getElementById("text").value);
          

}