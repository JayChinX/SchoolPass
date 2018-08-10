// Generated code. Do not modify!
package com.yuanding.schoolpass;

import com.zhy.m.permission.*;

public class A_Main_Acy$$PermissionProxy implements PermissionProxy<A_Main_Acy> {
@Override
 public void grant(A_Main_Acy source , int requestCode) {
switch(requestCode) {case 11:source.requestSdcardSuccess();break;case 12:source.requestRecordAudioSuccess();break;case 13:source.requestCameraSuccess();break;case 14:source.requestPhoneStateSuccess();break;}  }
@Override
 public void denied(A_Main_Acy source , int requestCode) {
switch(requestCode) {case 11:source.requestSdcardFailed();break;case 12:source.requestRecordAudioFailed();break;case 13:source.requestCameraFailed();break;case 14:source.requestPhoneStateFailed();break;}  }
@Override
 public void rationale(A_Main_Acy source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
