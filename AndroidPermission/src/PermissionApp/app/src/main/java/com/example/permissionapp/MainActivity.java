package com.example.permissionapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG="PermissionApp";

    private TextView txtTip;
    private Button btnRequest1_1,btnRequest1_2,btnRequest2_1,btnRequest2_2,btnRequest3_1;
    private Button btnGetState;
    private Activity mContext;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case 1:{//显示提示信息
                    String strTip=msg.obj.toString();
                    txtTip.append(strTip+"\n");
                    int offset=txtTip.getLineCount()*txtTip.getLineHeight();//获得文本高度
                    if(offset>txtTip.getHeight()){
                        txtTip.scrollTo(0,offset-txtTip.getHeight());
                    }
                    break;
                }
                case 2:{//清空提示
                    txtTip.setText("");
                    txtTip.scrollTo(0,0);
                    break;
                }
                case 3:{//显示Toast
                    String strTip=msg.obj.toString();
                    Toast.makeText(MainActivity.this,strTip,Toast.LENGTH_SHORT).show();
                    break;
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=MainActivity.this;
        initView();
    }

    private void initView(){
        txtTip=(TextView)findViewById(R.id.txtTip);
        txtTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        txtTip.setText("");
        txtTip.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                cleanTip();
                return false;
            }
        });

        btnRequest1_1=(Button)findViewById(R.id.btnRequest1_1);
        btnRequest1_1.setOnClickListener(this);
        btnRequest1_2=(Button)findViewById(R.id.btnRequest1_2);
        btnRequest1_2.setOnClickListener(this);
        btnRequest2_1=(Button)findViewById(R.id.btnRequest2_1);
        btnRequest2_1.setOnClickListener(this);
        btnRequest2_2=(Button)findViewById(R.id.btnRequest2_2);
        btnRequest2_2.setOnClickListener(this);
        btnRequest3_1=(Button)findViewById(R.id.btnRequest3_1);
        btnRequest3_1.setOnClickListener(this);
        btnGetState=(Button)findViewById(R.id.btnGetState);
        btnGetState.setOnClickListener(this);
    }

    private void showTip(String strTip){
        Message message=Message.obtain();
        message.what=1;
        message.obj=strTip;
        mHandler.sendMessage(message);
    }

    private void cleanTip(){
        mHandler.sendEmptyMessage(2);
    }

    private void showToast(String strTip){
        Message message=Message.obtain();
        message.what=3;
        message.obj=strTip;
        mHandler.sendMessage(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnRequest1_1:{//申请日历权限
                requestPermission1_1();
                break;
            }
            case R.id.btnRequest1_2:{//申请摄像头权限
                requestPermission1_2();
                break;
            }
            case R.id.btnRequest2_1:{//申请通讯录+定位权限
                requestPermission2_1();
                break;
            }
            case R.id.btnRequest2_2:{//申请麦克风+电话权限
                requestPermission2_2();
                break;
            }
            case R.id.btnRequest3_1:{//申请传感器+短信+存储权限
                requestPermission3_1();
                break;
            }
            case R.id.btnGetState:{//获取权限状态
                getPermissionsState();
                break;
            }
        }
    }

    /********************************通过权限组名申请一组权限 例子********************************************/
    private final int RequestCode1_1 =10000;
    private PermissionUtil.OnPermissionsListener mListener1_1=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {
            showTip("该权限已拥有");
        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被禁止："+pmList.toString());
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被拒绝授权："+pmList.toString());
        }

        @Override
        public void onPermissionsSucceed() {
            showTip("权限申请成功");
        }
    };
    public void requestPermission1_1(){
        String pgName=Manifest.permission_group.CALENDAR;
        showTip("进行日历权限申请...");
        PermissionUtil.requestByGroupName(mContext,pgName, RequestCode1_1,mListener1_1);
    }

    /********************************通过权限名申请单个权限 例子********************************************/
    private final int RequestCode1_2 =10001;
    private PermissionUtil.OnPermissionsListener mListener1_2=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {
            showTip("该权限已拥有");
        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被禁止："+pmList.toString());
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("警告")
                    .setMessage("请前往设置中手动打开摄像头权限，否则功能无法正常运行！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 一般情况下如果用户不授权的话，功能是无法运行的，做退出处理
                            finish();
                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被拒绝授权："+pmList.toString());
        }

        @Override
        public void onPermissionsSucceed() {
            showTip("权限申请成功");
        }
    };
    public void requestPermission1_2(){
        String permissionName=Manifest.permission.CAMERA;
        showTip("进行摄像头权限申请...");
        PermissionUtil.requestByPermissionName(mContext,permissionName, RequestCode1_2,mListener1_2);
    }

    /********************************通过权限组名申请多组权限 例子********************************************/
    private final int RequestCode2_1 =10002;
    private PermissionUtil.OnPermissionsListener mListener2_1=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {
            showTip("该权限已拥有");
        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被禁止："+pmList.toString());
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被拒绝授权："+pmList.toString());
            //重新请求权限
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage(pmList.toString()+"权限为应用必要权限，请授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] sList=pmList.toArray(new String[0]);
                            //重新申请权限,通过权限名的方式申请多组权限
                            PermissionUtil.requestByPermissionName(mContext,sList, RequestCode2_1,mListener2_1);
                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsSucceed() {
            showTip("权限申请成功");
        }
    };
    public void requestPermission2_1(){
        String[] pgName=new String[]{Manifest.permission_group.CONTACTS,Manifest.permission_group.LOCATION};
        showTip("进行[通讯录+定位]权限申请...");
        PermissionUtil.requestByGroupName(mContext,pgName, RequestCode2_1,mListener2_1);
    }

    /********************************通过权限名申请多组权限 例子********************************************/
    private final int RequestCode2_2 =10003;
    private PermissionUtil.OnPermissionsListener mListener2_2=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {
            showTip("该权限已拥有");
        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被禁止："+pmList.toString());
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("警告")
                    .setMessage("请前往设置中手动打开"+pmList.toString()+"权限！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被拒绝授权："+pmList.toString());
            //重新请求权限
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage("【"+pmList.toString()+"】权限为应用必要权限，请授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] sList=pmList.toArray(new String[0]);
                            //重新申请权限,通过权限名的方式申请多组权限
                            PermissionUtil.requestByPermissionName(mContext,sList, RequestCode2_2,mListener2_2);
                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsSucceed() {
            showTip("权限申请成功");
        }
    };
    public void requestPermission2_2(){
        String[] permissionArray=new String[]{
                Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
                Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG,
                Manifest.permission.ADD_VOICEMAIL,Manifest.permission.USE_SIP,
                Manifest.permission.PROCESS_OUTGOING_CALLS,Manifest.permission.RECORD_AUDIO
        };
        showTip("进行[麦克风+电话]权限申请...");
        PermissionUtil.requestByPermissionName(mContext,permissionArray, RequestCode2_2,mListener2_2);
    }

    /********************************通过权限组名申请多组权限 例子********************************************/
    private final int RequestCode3_1 =10004;
    private PermissionUtil.OnPermissionsListener mListener3_1=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {
            showTip("该权限已拥有");
        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被禁止："+pmList.toString());
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("警告")
                    .setMessage("请前往设置中手动打开"+pmList.toString()+"权限！")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            showTip("以下权限被拒绝授权："+pmList.toString());
            //重新请求权限
            AlertDialog dialog = new AlertDialog.Builder(mContext)
                    .setTitle("提示")
                    .setMessage("【"+pmList.toString()+"】权限为应用必要权限，请授权")
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String[] sList=pmList.toArray(new String[0]);
                            //重新申请权限,通过权限名的方式申请多组权限
                            PermissionUtil.requestByPermissionName(mContext,sList, RequestCode3_1,mListener3_1);
                        }
                    })
                    .create();
            dialog.show();
        }

        @Override
        public void onPermissionsSucceed() {
            showTip("权限申请成功");
        }
    };
    public void requestPermission3_1(){
        String[] pgArray=new String[]{
                Manifest.permission_group.SENSORS,Manifest.permission_group.SMS,Manifest.permission_group.STORAGE
        };
        showTip("进行[传感器+短信+存储]权限申请...");
        PermissionUtil.requestByGroupName(mContext,pgArray, RequestCode3_1,mListener3_1);
    }


    public void getPermissionsState(){
        String[] arrayPm=PermissionUtil.getAppPermissionsList(this);
        String strContent="";
        boolean state=false;
        if(arrayPm!=null){
            strContent="应用权限列表如下：\n";
            for(int i=0;i<arrayPm.length;i++){
                state=PermissionUtil.checkPermission(this,arrayPm[i]);
                strContent+=arrayPm[i]+" : "+state+"\n";
            }
        }
        else{
            strContent="获取应用权限列表为空";
        }
        showTip(strContent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){//可以针对不同的权限申请操作进行不同处理，也可以统一以相同方式处理（不对requestCode进行判断）
            case RequestCode1_1:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener1_1,true);
                break;
            }
            case RequestCode1_2:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener1_2,true);
                break;
            }
            case RequestCode2_1:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener2_1,true);
                break;
            }
            case RequestCode2_2:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener2_2,true);
                break;
            }
            case RequestCode3_1:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener3_1,false);
                break;
            }
        }
    }
}