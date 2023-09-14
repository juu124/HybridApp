## 라이브러리
* 기능 명
* 라이브러리 명
* Constant 변수 명
  - 사용하고 있는 파일 목록

#### 앱 위변조
* TrustAppAAB5.0.0.6.jar
* USE_TRUST_APP_DREAM
  - TrustAppManager.java
  - TrustAppUI.java
  - rimeCoreAndroid3.0.2.9

#### 인증서 (서명, 삭제, 인증서 비밀번호 변경, 본인 확인, XML 서명, 비대칭키 암/복호화, EnvelopedData 암/복호화, 대칭키 암/복호화, Hash 생성, Base
  64 생성, 인증서 상세 정보, PEX Export, PFX Import, 개인키 암호화, UCPID Request Info)
* MagicXSign_1.0.9.2.jar
* USE_XSIGN_DREAM
  - XSignCertPolicy.java
  - XSignTester.java
  - XSignCertPolicy.java
  - MainActivity.java
  - XSignBaseActivity.java
  - XSignCertDetailActivity.java
  - XSignCertListActivity.java
  - XSignMainActivity.java
  - XSignSettingActivity.java
  - XSignUcpidActivity.java
  - ProcessCertificate.java

#### 웹 플러그 인
* XSignWebPlugin_Client_v1.4.4
* USE_XSIGN_PLUGIN_DREAM
  - IntroActivity.java
  - MainActivity.java

#### 백신
* (라이브러리 모듈) magic_mvaccine
* USE_VACCINE_DREAM
  - MagicResult.java
  - MagicVaccine.java
  - MagicmVaccineListener.java
  - ScanData.java
  - MagicNullInstance.java
  - MagicmVaccine.java
  - MagicConnectivityException.java
  - MagicIOEception.java
  - MagicInntegrityCheckException.java
  - MagicIOException.java
  - MagicIntegrityCheckException.java
  - MagicLicenseExpireException.java
  - MagicLicenseInvalidException.java
  - MagicLicenseKeyNotFoundException.java
  - MagicLicenseOverException.java
  - MagicTimeoutException.java
  - MagicNullInstance.java
  - MagicNullPointerException.java
  - MagicTimeoutException.java
  - MagicmVaccineOptions.java
  - VaccineManager.java

#### push 기능
* morpheus_push_library_5.2.0.6.aar
* USE_PUSH_FIRBASE
  - PushManager.java
  - PushConstants.java
  - PushLog.java
  - PushUtils.java
  - PushManager.java
  - PushHandler.java
  - Logger.java
  - PushConstants.java
  - PushConstantsEx.java
  - PushUtils.java
  - PushManager.java
  - PushConstants.java
  - PushLog.java
  - PushUtils.java

* 보안 키보드
* (라이브러리 모듈) magicvkeypad
* USE_MAGIC_KEYPAD_DREAM
  - MagicVKeypadType.java
  - MagicVKeypad.java
  - MagicVKeypadOnClickInterface.java
  - MagicVKeypadResult.java
  - MagicVKeypadType.java

#### 구간 암호화
* MagicSE2.jar

#### jniLibs
  * 앱 위변조
    - libAAPlusToolkit4.2.0.3.so
  * 구간 암호화
    - libMagicSEv2.so
    - libDSToolkitV30Jni.so
    - libMagicCrypto.so
  * FIDO
    - Lib_Magic_FIDO

#### 라이브러리 모듈
  - magicmrsv2lib

#### Not Used library

- commons-lang-2.5
- jcaos-client-1.4.7.8
- MagicMRS // 인증서 이동 서비스 (pc, 스마트폰에 저장된 인증서를 다른 PC 또는 스마트 폰에서 사용할 수 있도록 별도 저장 매체 없이 인증서 이동 장) (
  내보내기 / 가져오기)
- MDCPdfViewer
- morpheus_push_library_5.2.0.6
- netty-common-4.0.19.Final
- universal-image-loader-1.9.3

## 기능 테스트 주의할 점

#### 백신 & 앱 위변조 검사

- 현재 다른 샘플 앱의 라이브러리(백신 & 앱 위변조 검사)를 가져왔기 때문에 동시에 작동하기 어려움
- 테스트 할때 Constant에서 각 맞는 라이브러리를 true 상태로 변경
- application ID를 변경한 후 빌드하기

## 보안 키보드(가상 키보드)

#### 보안 키보드 기능 사용시 준비

- build.gradle(module)에서 apply plugin: 'com.google.gms.google-services' 부분 해제
- build.gradle(module)에서 applicationID 를 "com.dreamsecurity.example.magicvkeypad"로 변경하기 (해당 패키지의
  키보드 라이브러리가 없기 때문에 샘플 키보드 앱의 라이브러리 사용)
- build.gradle(project)에서 id 'com.google.gms.google-services' version '4.3.15' apply false 주석 해제
- google-services.json 파일 복사해서 다른 파일에 붙여놓고 해당 프로젝트 안에 있는 json 파일은 삭제 및 이름변경
- MagicVKeyPadSettings.java에서 strLicense 값 변경(해당 applicationId에 맞는 값 넣기)
- 앱 build 하기 전 단말에 해당 앱 삭제하고 build 하기

#### 보안 키보드 이슈

- 풀 모드 보안키보드에서 android.support.v4.app.FragmentActivity를 사용하는데, 오래된 내용이라서 새 라이브러리를 받아야한다.
- 현재 가상키패드는 '문자 키패드 HALF 모드, 숫자 키패드 HALF 모드'만 지원한다. (나머지 풀모드는 지원하지 않음)

## PUSH

#### PUSH 기능 사용시 준비

- push 기능은 서울헬스케어 라이선스를 사용하기 때문에 해당 패키지를 kr.go.seoul.healthcare로 바꿔야한다.