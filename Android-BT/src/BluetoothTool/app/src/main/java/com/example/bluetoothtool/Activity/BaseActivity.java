package com.example.bluetoothtool.Activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothtool.common.GlobalDef;
import com.example.bluetoothtool.common.LoadingDialog;
import com.example.bluetoothtool.utils.BluetoothUtil;

import java.text.NumberFormat;

public class BaseActivity extends AppCompatActivity implements View.OnClickListener{
    public static final String RECEIVE_FILE_PATH="/sdcard/BluetoothRec/";
    public static final int DATATYPE_STR=0;
    public static final int DATATYPE_HEX=1;
    protected Context mContext;
    protected TextView txtTip;
    protected int mCurDataType=DATATYPE_STR;//当前选择的发送数据类型
    protected BluetoothAdapter mBluetoothAdapter;
    protected LoadingDialog loadingDialog=null;

    private Handler mBaseHandler=new Handler(Looper.myLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case GlobalDef.MSG_SHOW_TIP:{
                    if(txtTip!=null){
                        String strTip=msg.obj.toString();
                        txtTip.append(strTip+"\n");
                        int offset=txtTip.getLineCount()*txtTip.getLineHeight();
                        if(offset>txtTip.getHeight()){
                            txtTip.scrollTo(0,offset-txtTip.getHeight());
                        }
                    }
                    break;
                }
                case GlobalDef.MSG_CLEAN_TIP:{
                    if(txtTip!=null){
                        txtTip.setText("");
                        txtTip.scrollTo(0,0);
                    }
                    break;
                }
                case GlobalDef.MSG_SHOW_TOAST:{
                    String strTip=msg.obj.toString();
                    if(mContext!=null){
                        Toast.makeText(mContext,strTip,Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case GlobalDef.MSG_SHOW_LOAD_DIALOG:{
                    String strTip=msg.obj.toString();
                    if(loadingDialog!=null){
                        loadingDialog.show(strTip);
                    }
                    break;
                }
                case GlobalDef.MSG_CLOSE_LOAD_DIALOG:{
                    if(loadingDialog!=null){
                        loadingDialog.dismiss();
                    }
                    break;
                }
            }
        }
    };

    public void showTip(String strTip){
        Message msg=Message.obtain();
        msg.what=GlobalDef.MSG_SHOW_TIP;
        msg.obj=strTip;
        mBaseHandler.sendMessage(msg);
    }

    public void cleanTip(){
        mBaseHandler.sendEmptyMessage(GlobalDef.MSG_CLEAN_TIP);
    }

    public void showToast(String strTip){
        Message msg=Message.obtain();
        msg.what=GlobalDef.MSG_SHOW_TOAST;
        msg.obj=strTip;
        mBaseHandler.sendMessage(msg);
    }

    /**
     * 显示（或更新）加载对话框
     * @param strTip
     */
    public void showLoadingDialog(String strTip){
        Message message=Message.obtain();
        message.what=GlobalDef.MSG_SHOW_LOAD_DIALOG;
        message.obj=strTip;
        mBaseHandler.sendMessage(message);
    }

    /**
     * 关闭加载对话框
     */
    public void dismissLoadingDialog(){
        mBaseHandler.sendEmptyMessage(GlobalDef.MSG_CLOSE_LOAD_DIALOG);
    }

    @Override
    public void onClick(View v) {

    }

    /**
     * 打开蓝牙
     */
    public void openBluetooth(){
        if(mBluetoothAdapter==null){
            showTip("当前设备不支持蓝牙功能！");
            return;
        }

        if(mBluetoothAdapter.isEnabled()){
            showTip("蓝牙已打开");
            return;
        }

        Intent intent=new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(intent,GlobalDef.REQ_CODE_OPEN_BT);
    }

    /**
     * 关闭蓝牙
     */
    public void closeBluetooth(){
        if(mBluetoothAdapter==null){
            showTip("当前设备不支持蓝牙功能！");
            return;
        }
        if(!mBluetoothAdapter.isEnabled()){
            showTip("当前蓝牙未打开");
            return;
        }

        mBluetoothAdapter.disable();
    }

    /**
     * 打开蓝牙可见性
     */
    public void openDiscovery(){
        if(mBluetoothAdapter==null){
            showTip("当前设备不支持蓝牙功能！");
            return;
        }
        if(!mBluetoothAdapter.isEnabled()){
            showTip("当前蓝牙未打开");
            return;
        }

        //启动修改蓝牙可见性的Intent
        //Intent intent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        //设置蓝牙可见性的时间，默认持续时间为120秒，每个请求的最长持续时间上限为300秒
        //intent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 120);
        //startActivity(intent);

        BluetoothUtil.setDiscoverableTimeout(mBluetoothAdapter,0);
        showTip("当前设备蓝牙可见性永久开启");
    }

    /**
     * 关闭蓝牙可见性
     */
    public void closeDiscovery(){
        if(mBluetoothAdapter==null){
            showTip("当前设备不支持蓝牙功能！");
            return;
        }
        if(!mBluetoothAdapter.isEnabled()){
            showTip("当前蓝牙未打开");
            return;
        }

        BluetoothUtil.closeDiscoverableTimeout(mBluetoothAdapter);
        showTip("当前设备蓝牙可见性关闭");
    }


    public int getPercent(double a,double b){
        if(b==0){
            return -1;
        }

        double result=a/b*100;
        int iRet=(int)result;
        return iRet;
    }

    /*@Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GlobalDef.REQ_CODE_OPEN_BT){
            if(resultCode == Activity.RESULT_OK){
                showTip("蓝牙打开成功");
            }
            else{
                showTip("蓝牙打开失败");
            }
        }
    }*/
}
