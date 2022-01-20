package com.demo.permissiondemo;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG="PermissionDemo";
    private Button btnApplySingle,btnApplyMultiple;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:{//显示Toast
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
        initView();
    }

    public void initView(){
        btnApplySingle=(Button)findViewById(R.id.btnApplySingle);
        btnApplySingle.setOnClickListener(this);
        btnApplyMultiple=(Button)findViewById(R.id.btnApplyMultiple);
        btnApplyMultiple.setOnClickListener(this);
    }

    private void showToast(String strTip){
        Log.i(TAG,strTip);
        Message msg=Message.obtain();
        msg.what=1;
        msg.obj=strTip;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btnApplySingle:{
                applyForSinglePermission();
                break;
            }
            case R.id.btnApplyMultiple:{
                applyForMultiplePermissions();
                break;
            }
        }
    }

    private String PM_SINGLE=Manifest.permission.WRITE_EXTERNAL_STORAGE;
    //申请单个权限
    public void applyForSinglePermission(){
        Log.i(TAG,"applyForSinglePermission");
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23){
                //判断是否拥有权限
                int nRet=ContextCompat.checkSelfPermission(this,PM_SINGLE);
                Log.i(TAG,"checkSelfPermission nRet="+nRet);
                if(nRet!= PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"进行权限申请...");
                    ActivityCompat.requestPermissions(this,new String[]{PM_SINGLE},10000);
                }
                else{
                    showToast("权限已授权");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private String[] PM_MULTIPLE={
            Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.CAMERA,Manifest.permission.WRITE_CONTACTS
    };
    //申请多个权限
    public void applyForMultiplePermissions(){
        Log.i(TAG,"applyForMultiplePermissions");
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23){
                ArrayList<String> pmList=new ArrayList<>();
                //获取当前未授权的权限列表
                for(String permission:PM_MULTIPLE){
                    int nRet=ContextCompat.checkSelfPermission(this,permission);
                    Log.i(TAG,"checkSelfPermission nRet="+nRet);
                    if(nRet!= PackageManager.PERMISSION_GRANTED){
                        pmList.add(permission);
                    }
                }

                if(pmList.size()>0){
                    Log.i(TAG,"进行权限申请...");
                    String[] sList=pmList.toArray(new String[0]);
                    ActivityCompat.requestPermissions(this,sList,10000);
                }
                else{
                    showToast("全部权限都已授权");
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        try{
            ArrayList<String> requestList=new ArrayList<>();//允许询问列表
            ArrayList<String> banList=new ArrayList<>();//禁止列表
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    Log.i(TAG,"【"+permissions[i]+"】权限授权成功");
                }
                else{
                    //判断是否允许重新申请该权限
                    boolean nRet=ActivityCompat.shouldShowRequestPermissionRationale(this,permissions[i]);
                    Log.i(TAG,"shouldShowRequestPermissionRationale nRet="+nRet);
                    if(nRet){//允许重新申请
                        requestList.add(permissions[i]);
                    }
                    else{//禁止申请
                        banList.add(permissions[i]);
                    }
                }
            }

            //优先对禁止列表进行判断
            if(banList.size()>0){//告知该权限作用，要求手动授予权限
                showFinishedDialog();
            }
            else if(requestList.size()>0){//告知权限的作用，并重新申请
                showTipDialog(requestList);
            }
            else{
                showToast("权限授权成功");
            }
        }catch (Exception e){
            e.printStackTrace();
            showToast("权限申请回调中发生异常");
        }
    }

    public void showFinishedDialog(){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("警告")
                .setMessage("请前往设置中打开相关权限，否则功能无法正常运行！")
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

    public void showTipDialog(ArrayList<String> pmList){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("提示")
                .setMessage("【"+pmList.toString()+"】权限为应用必要权限，请授权")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String[] sList=pmList.toArray(new String[0]);
                        //重新申请该权限
                        ActivityCompat.requestPermissions(MainActivity.this,sList,10000);
                    }
                })
                .create();
        dialog.show();
    }
}
