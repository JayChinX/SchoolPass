// Generated code. Do not modify!
package com.yuanding.schoolpass;

import com.zhy.m.permission.*;

public class B_Contact_Main_In_School$$PermissionProxy implements PermissionProxy<B_Contact_Main_In_School> {
@Override
 public void grant(B_Contact_Main_In_School source , int requestCode) {
switch(requestCode) {case 4:source.requestSdcardSuccess();break;}  }
@Override
 public void denied(B_Contact_Main_In_School source , int requestCode) {
switch(requestCode) {case 4:source.requestSdcardFailed();break;}  }
@Override
 public void rationale(B_Contact_Main_In_School source , int requestCode) {
switch(requestCode) {}  }
@Override
 public boolean needShowRationale(int requestCode) {
switch(requestCode) {}
return false;  }

}
