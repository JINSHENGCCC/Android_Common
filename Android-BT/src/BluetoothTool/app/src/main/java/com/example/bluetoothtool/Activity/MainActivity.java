package com.example.bluetoothtool.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.bluetoothtool.R;
import com.example.bluetoothtool.utils.PermissionUtil;

import java.util.ArrayList;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    private Button btnServer,btnClient;
    private Activity mContext;

    //权限申请回调
    private PermissionUtil.OnPermissionsListener mPermissionsListener=new PermissionUtil.OnPermissionsListener() {
        @Override
        public void onPermissionsOwned() {

        }

        @Override
        public void onPermissionsForbidden(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            Set<String> nameSet=PermissionUtil.getPermissionsNameByChinese(pmList.toArray(new String[0]));
            if(nameSet!=null && nameSet.size()>0){
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("警告")
                        .setMessage("请前往设置中手动授予"+nameSet.toString()+"权限，否则功能无法正常运行！")
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
        }

        @Override
        public void onPermissionsDenied(String[] permissions, int[] grantResults, ArrayList<String> pmList) {
            Set<String> nameSet=PermissionUtil.getPermissionsNameByChinese(pmList.toArray(new String[0]));
            if(nameSet!=null && nameSet.size()>0){
                //重新请求权限
                AlertDialog dialog = new AlertDialog.Builder(mContext)
                        .setTitle("提示")
                        .setMessage(nameSet.toString()+"权限为应用必要权限，请授权")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] sList=pmList.toArray(new String[0]);
                                //重新申请权限,通过权限名的方式申请多组权限
                                PermissionUtil.requestByPermissionName(mContext,sList, 10000,mPermissionsListener);
                            }
                        })
                        .create();
                dialog.show();
            }
        }

        @Override
        public void onPermissionsSucceed() {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mContext=this;
        initView();

        //权限申请
        String[] pgList=new String[]{Manifest.permission_group.LOCATION,Manifest.permission_group.STORAGE};
        PermissionUtil.requestByGroupName(mContext,pgList,10000,mPermissionsListener);
    }

    private void initView(){
        btnServer=(Button)findViewById(R.id.btnServer);
        btnClient=(Button)findViewById(R.id.btnClient);

        btnServer.setOnClickListener(this);
        btnClient.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnServer:{
                openActivity(BTServerActivity.class.getName());
                break;
            }
            case R.id.btnClient:{
                openActivity(BTClientActivity.class.getName());
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionUtil.onRequestPermissionsResult(this,permissions,grantResults,mPermissionsListener,false);
    }

    public void openActivity(String activityName){
        Intent intent=new Intent();
        intent.setClassName(MainActivity.this,activityName);
        startActivity(intent);
    }
}