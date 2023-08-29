
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
//	PAGE_TITLE	: MagicMRSv2.js
//	Author		: KayKim
//	WRITE_DATE	: 2017.11.20
//	Commant		: javascript function 
////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

// MagicMRSv2 버전
var	szUpdateVersion = "1.0.0.3";

// 중계서버 주소
var	szHostAddress = "http://125.133.65.225";

// 수동설치 파일이
var szSetupFile = "./MagicMRSv2.exe";

// 중계서버 포트
var	szHostPort = "30711";

// 서비스 사이트 URL
var	szSiteInfomation = "www.magicmrs.net";

// 인증서 만료일 체크( 0=만료된 인증서 보임, 1=만료된 인증서 안보임) 
var	szCheckCertExpire = "0";


var bCalled = false;

var bDownload = false;

// 서버주소, 서버포트, 사이트정보, 만료된인증서Hide, 
var	szArguments = "$" + szHostAddress + "$" + szHostPort + "$" + szSiteInfomation + "$" + szCheckCertExpire;

// 화면에 보이게할 정책 설정(설정한 정책의 인증서만 보임). 값이 없을 경우 모든 인증서가 보임
var szOID = "'";
//szOID	+= "1.2.410.200004.5.4.1.1, 1.2.410.200004.5.4.1.2";
//szOID	+= ",1.2.410.200004.5.2.1.1, 1.2.410.200004.5.2.1.2";
//szOID	+= ",1.2.410.200004.5.1.1.5, 1.2.410.200004.5.1.1.7";
//szOID	+= ",1.2.410.200012.1.1.1, 1.2.410.200012.1.1.3";
//szOID	+= ",1.2.410.200012.1.1.7, 1.2.410.200012.1.1.9";
//szOID	+= ",1.2.410.200005.1.1.1, 1.2.410.200005.1.1.5";
//szOID	+= ",1.2.410.200005.1.1.4";
szOID	+= "'";
if (szOID == "''")
	szOID = "|";

var nLimitPWCnt = 5

var port = 31810;
var portRetry = 3;
var portIncrease = 15;
var connectRetry = 5;
var https = true;
var bException = false;


function Reset()
{
	port = 31810;
	portRetry = 3;
	portIncrease = 15;
	connectRetry = 5;
	bCalled = false;
}

function SetDownloadApp()
{
	bDownload = true;
}

function StartLauncher()
{
	bCalled = true;
}

var OS_MAC = 1;
var OS_WINDOWS = 2;
var OS_ETC = 99;

function MM_openBrWindow(moveType) 
{
    console.log("인증서 화면");
	var	width = 450;
	var	height = 610;
	var browser = DetectedBrowser();
	
	if (browser == MICROSOFT_EDGE)
	{
	    console.log("엣지");
		width += 15;
		height += 5;
	}
	else if (browser == UNSUPPORTED_SAFARI)
	{
	    console.log("사파리");
		width += 20;
		height -= 55;
	}
	else if (browser == OPERASOFTWARE_OPERA)
	{
	    console.log("오페라");
		width += 33;
		height += 95;
	}
	else if (browser == GOOGLE_CHROME)
	{
	    console.log("인증서 크롬");
		width += 5;
		height -= 30;
	}
	
	var theURL = './install_popup.html?moveType='+ moveType;
	var	winName = '';
		var sw  = screen.availWidth ;
		var sh  = screen.availHeight ;
			px=(sw - width)/2 ;
			py=(sh - width)/2 ;
	var	features = 'width='+width+', height='+height+', top=' + py + ', left=' + px + ', scrollbars=no,toolbar=no,location=no,directories=no,status=no';
	
	if (DetectedOS() == OS_MAC)
	{
		alert("MAC OS는 지원예정입니다. Windows 계열 OS를 이용해 주시기 바랍니다.");
		return;
	}

	console.log("인증서");
//	window.xmlhttp.open(theURL,winName,features);
	window.open(theURL,winName,features);

}

function DetectedOS()
{
	var strUserAgent = navigator.userAgent.toLowerCase();
	var strPatternMac = /macintosh/;
	var strPatternWindows = /windows/;

	if(strPatternMac.test(strUserAgent) == true)
		return OS_MAC;
	else if(strPatternWindows.test(strUserAgent) == true)
		return OS_WINDOWS;
	else
		return OS_ETC;
}

var MICROSOFT_IE = 10;
var MOZILLA_FF3 = 11;
var APPLE_SAFARI = 12;
var GOOGLE_CHROME = 13;
var OPERASOFTWARE_OPERA = 14;
var MICROSOFT_EDGE = 15;
var UNSUPPORTED_FF = 20;
var UNSUPPORTED_SAFARI = 21;
var UNSUPPORTED_BROWSER = 22;
var browserName = DetectedBrowser();

// 실행된 브라우저 찾기
function DetectedBrowser()
{
	var strAppName = navigator.appName.toLowerCase();
	//alert(strAppName);
	
	var strUserAgent = navigator.userAgent.toLowerCase();
	//alert(strUserAgent);
	if(strAppName == "netscape")
	{
		var strPatternFireFox = /firefox/i;
		var strPatternFireFox3 = /firefox\/3/i;
		var strPatternAppleWebKit = /safari/i;
		var strPatternChrome = /chrome/i;
		var strPatternSafari4 = /Version\/4/i;
		var strPatternSafariMobile = /mobile safari/;
		var strPatternIE11 = /rv:[0-9]+/i;
		var strPatternEdge = /edge/i;
		var strPatternOpra = /opr/i;

		if(strPatternFireFox.test(strUserAgent) == true)
		{
			if(strPatternFireFox3.test(strUserAgent) == true)
			{
				CurrentBrowser  = MOZILLA_FF3;
				return MOZILLA_FF3;
			}
			else
			{
				CurrentBrowser  = UNSUPPORTED_FF;
				return UNSUPPORTED_FF;
			}
		}
		else if(strPatternEdge.test(strUserAgent) == true)
		{
			CurrentBrowser  = MICROSOFT_EDGE;
			return MICROSOFT_EDGE;
		}
		else if(strAppName == "opera" || strPatternOpra.test(strUserAgent) == true)
		{
			CurrentBrowser  = OPERASOFTWARE_OPERA;
			return OPERASOFTWARE_OPERA;
		}
		else if(strPatternAppleWebKit.test(strUserAgent) == true)
		{
			if(strPatternChrome.test(strUserAgent) == true)
			{
				CurrentBrowser  = GOOGLE_CHROME;
				return GOOGLE_CHROME;
			}
			else if(strPatternSafariMobile.test(strUserAgent) == true)
			{
				CurrentBrowser  = UNSUPPORTED_BROWSER;
				return UNSUPPORTED_BROWSER;
			}
			else if(strPatternSafari4.test(strUserAgent) == true)
			{
				CurrentBrowser  = APPLE_SAFARI;
				return APPLE_SAFARI;
			}
			else
			{
				CurrentBrowser  = UNSUPPORTED_SAFARI;
				return UNSUPPORTED_SAFARI;
			}
		}
		else if(strPatternIE11.test(strUserAgent) == true)
		{
				CurrentBrowser  = MICROSOFT_IE;
 				return MICROSOFT_IE;
		}
		else
		{
			CurrentBrowser  = UNSUPPORTED_BROWSER;
			return UNSUPPORTED_BROWSER;
		}
	}
	else
	{
		CurrentBrowser  = MICROSOFT_IE;
		return MICROSOFT_IE;
	}
}

function includeJs(jsFilePath) {
	var js = document.createElement("script");
	
	js.type = "text/javascript";
	js.src = jsFilePath;

	document.body.appendChild(js);
}

var Request = function()
{
    this.getParameter = function( name )
    {
        var rtnval = '';
        var nowAddress = unescape(location.href);
        var parameters = (nowAddress.slice(nowAddress.indexOf('?')+1,nowAddress.length)).split('&');
        for(var i = 0 ; i < parameters.length ; i++)
        {
            var varName = parameters[i].split('=')[0];
            if(varName.toUpperCase() == name.toUpperCase())
            {
                rtnval = parameters[i].split('=')[1];
                break;
            }
        }
        return rtnval;
    }
}
var request = new Request();

function getXMLHttp() 
{
	if (window.XMLHttpRequest) 
	{
		var xmlhttps;
		
		if (bException)
			return new ActiveXObject("Microsoft.XMLHTTP");

		try {
			xmlhttps = new XMLHttpRequest();
		}
		catch(e)
		{
			try	{
					xmlhttps = new ActiveXObject("Msxml2.HTTP");
			}
			catch(f)
			{
				xmlhttps = new ActiveXObject("Microsoft.XMLHTTP");
			}
		}
		return xmlhttps;
	} else {
		var xmlhttps = new Array("MSXML2.XMLHttp.7.0", "MSXML2.XMLhttp.6.0",
					"MSXML2.XMLhttp.5.0", "MSXML2.XMLhttp.4.0",
					"MSXML2.XMLhttp.3.0", "MSXML2.XMLhttp", "Microsoft.XMLHttp");

		for ( var i = 0; i < xmlhttps.length; i++) {
			try {
				var req = new ActiveXObject(xmlhttps[i]);
				return req;
			} catch (e) { }
		}
	}
}

function recvInstallCheck(xmlhttp)
{
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
	{	
		var jobjResponseData;
		
		try {
			try {
				jobjResponseData = JSON.parse(xmlhttp.responseText);
			} catch(e) {
				includeJs("./json2.js");
				
				jobjResponseData = JSON.parse(xmlhttp.responseText);
			}
		} catch( e ) {
			port += portIncrease;
			portRetry--;
			return installCheck();
		}
		if (jobjResponseData.OPCODE == "R1001" && jobjResponseData.UpdateFlag == "FALSE")
		{
			OnInstallResult(true);
			return true;
		}else{
			
			if (jobjResponseData.OPCODE == "R1001" && jobjResponseData.UpdateFlag == "TRUE")
			{
				if (bDownload == false)
				{
					document.location.href = szSetupFile;
					bDownload = true;
				}
				OnInstallResult(false);
				return false;
			}
			else
			{
				port += portIncrease;
				portRetry--;
				return installCheck();
			}
		}
	}
	else if (xmlhttp.readyState == 4 && xmlhttp.status == 0)
	{
		if (connectRetry <= 0)
		{	
			if (bCalled)
			{
				port += portIncrease;
				portRetry--;
			}
			connectRetry = 5;
		}
		
		if (!bCalled)
		{
			StartLauncher();
		}
		
		installCheck();
		connectRetry--;
	}
}

// MagicMRSv2가 설치되어 있는지 체크
function installCheck()
{
	var	sync = true;
	
	var xmlhttp = getXMLHttp();
	try 
	{	
		if (portRetry <= 0)
		{
			if (bDownload == false)
			{
				document.location.href = szSetupFile;
				bDownload = true;
			}
			OnInstallResult(false);
			return false;
		}
		
		function handlerInstall()
		{
			recvInstallCheck(xmlhttp);
		}
		
		if (https)
			xmlhttp.open("POST","https://127.0.0.1:"+(port+10000), sync);
		else
			xmlhttp.open("POST","http://localhost:"+port, sync);
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");
		
		if (sync)
			xmlhttp.onreadystatechange = handlerInstall;
		//xmlhttp.timeout = 1000;
		xmlhttp.send("OPCODE=Q1001&version="+szUpdateVersion);
		
		if (sync == false)
		{
			recvInstallCheck(xmlhttp);
		}
		
	}catch(e) {
		bException = true;
		if (bCalled)
		{
			port += portIncrease;
			portRetry--;
		}
		StartLauncher();
		return installCheck();
	}

}

function RecvExportCertificate(xmlhttp)
{
	if (xmlhttp.readyState == 4 && xmlhttp.status == 200)
	{
		var jobjResponseData;
		
		try {
			try {
				jobjResponseData = JSON.parse(xmlhttp.responseText);
			} catch(e) {
				includeJs("./json2.js");
				
				jobjResponseData = JSON.parse(xmlhttp.responseText);
			}
			
			if (jobjResponseData.OPCODE == "R1002")
			{
				return true;
			}
			else
			{
				return false;
			}
		} catch( e ) {

		}
	}

}

var windowWidth = window.outerWidth;
var windowHeight = window.outerHeight;
	
if(windowWidth == null || typeof(windowWidth) == "undefined")
{
	try
	{
		windowWidth = document.body.clientWidth;
		windowHeight = document.body.clientHeight;
	}
	catch(e)
	{	
		if(windowWidth == null || typeof(windowWidth) == "undefined")
		{
			windowWidth = document.documentElement.clientWidth;
			windowHeight = document.documentElement.clientHeight;
		}
	}
}

if(windowWidth == null || typeof(windowWidth) == "undefined")
{
	windowWidth = 0;
	windowHeight = 0;
}

function MoveCertificate( sujectDN, moveType )
{	
	var		sync = true;
	var		szParam = "$"+moveType+szArguments;
	
	// ServiceType, SubjectDN, 
	if( sujectDN != null && moveType == 10001)
		szParam += "$" + sujectDN;
	else
		szParam += "$|";
	
	// 키보드보안, 인증서도메인(1:NPKI,2:GPKI/EPKI,0:모두), OID, 인증서 비밀번호 재시도 횟수
	szParam += "$NULL$1$"+szOID+"$"+nLimitPWCnt+"$"+windowWidth+"$"+windowHeight;

	var xmlhttp = getXMLHttp();
	try 
	{
		function handlerMoveCert()
		{
			RecvExportCertificate(xmlhttp);
		}
		
		if (https)
			xmlhttp.open("POST","https://127.0.0.1:"+(port+10000), sync);
		else
			xmlhttp.open("POST","http://localhost:"+port, sync);
		xmlhttp.setRequestHeader("Content-Type", "application/x-www-form-urlencoded");

		if (sync)
			xmlhttp.onreadystatechange = handlerMoveCert;
		//xmlhttp.timeout = 1000;
		
		var Base64={_keyStr:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/=",encode:function(e){var t="";var n,r,i,s,o,u,a;var f=0;e=Base64._utf8_encode(e);while(f<e.length){n=e.charCodeAt(f++);r=e.charCodeAt(f++);i=e.charCodeAt(f++);s=n>>2;o=(n&3)<<4|r>>4;u=(r&15)<<2|i>>6;a=i&63;if(isNaN(r)){u=a=64}else if(isNaN(i)){a=64}t=t+this._keyStr.charAt(s)+this._keyStr.charAt(o)+this._keyStr.charAt(u)+this._keyStr.charAt(a)}return t},decode:function(e){var t="";var n,r,i;var s,o,u,a;var f=0;e=e.replace(/[^A-Za-z0-9\+\/\=]/g,"");while(f<e.length){s=this._keyStr.indexOf(e.charAt(f++));o=this._keyStr.indexOf(e.charAt(f++));u=this._keyStr.indexOf(e.charAt(f++));a=this._keyStr.indexOf(e.charAt(f++));n=s<<2|o>>4;r=(o&15)<<4|u>>2;i=(u&3)<<6|a;t=t+String.fromCharCode(n);if(u!=64){t=t+String.fromCharCode(r)}if(a!=64){t=t+String.fromCharCode(i)}}t=Base64._utf8_decode(t);return t},_utf8_encode:function(e){e=e.replace(/\r\n/g,"\n");var t="";for(var n=0;n<e.length;n++){var r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r)}else if(r>127&&r<2048){t+=String.fromCharCode(r>>6|192);t+=String.fromCharCode(r&63|128)}else{t+=String.fromCharCode(r>>12|224);t+=String.fromCharCode(r>>6&63|128);t+=String.fromCharCode(r&63|128)}}return t},_utf8_decode:function(e){var t="";var n=0;var r=c1=c2=0;while(n<e.length){r=e.charCodeAt(n);if(r<128){t+=String.fromCharCode(r);n++}else if(r>191&&r<224){c2=e.charCodeAt(n+1);t+=String.fromCharCode((r&31)<<6|c2&63);n+=2}else{c2=e.charCodeAt(n+1);c3=e.charCodeAt(n+2);t+=String.fromCharCode((r&15)<<12|(c2&63)<<6|c3&63);n+=3}}return t}}
		szParam = Base64.encode(szParam);
		xmlhttp.send("OPCODE=Q1002&Param="+szParam);
		
		if (sync == false)
		{
			recvInstallCheck(xmlhttp);
		}

	}catch(e) {
		bException = true;
		if (connectRetry <= 0)
		{
			connectRetry--;
			return false;
		}
		return MoveCertificate(sujectDN, moveType);
	}

}

function ExportCertificate( sujectDN )
{
    console.log("인증서 내보내기");
	MoveCertificate(sujectDN, 10001);
}

function ImportCertificate( )
{
    console.log("인증서 가져오기");
	MoveCertificate("", 10000);
}