package com.example.bluetoothtool.Activity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bluetoothtool.R;
import com.example.bluetoothtool.common.GlobalDef;
import com.example.bluetoothtool.common.LoadingDialog;
import com.example.bluetoothtool.utils.BluetoothUtil;
import com.example.bluetoothtool.utils.FileDigest;
import com.example.bluetoothtool.utils.StringUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BTServerActivity extends BaseActivity{
    private static final String TAG="BTServerLog";
    private int[] mBtnArray=new int[]{
        R.id.btnOpenBt,R.id.btnCloseBt,R.id.btnOpenDiscovery,R.id.btnCloseDiscovery,
        R.id.btnCloseClient,R.id.btnClearTip,R.id.btnSendData
    };
    private RadioGroup rgDataType;
    private EditText edtData;
    private Button btnServerControl;

    private BluetoothBroadcastReceiver mBluetoothBroadcastReceiver;
    private volatile boolean mServerRunningFlag=false;//服务端是否正在运行
    //socket服务
    private BluetoothServerSocket mServerSocket;
    //当前连接的客户端socket列表
    private List<BluetoothSocket> mSocketList;
    //客户端socket处理线程池
    private ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_server);

        mContext=this;
        mBluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        initView();
        loadingDialog=new LoadingDialog(mContext);
        //注册广播
        mBluetoothBroadcastReceiver=new BluetoothBroadcastReceiver();
        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_PAIRING_REQUEST);
        mContext.registerReceiver(mBluetoothBroadcastReceiver,filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext.unregisterReceiver(mBluetoothBroadcastReceiver);
    }

    public void initView(){
        //提示栏
        txtTip=(TextView)findViewById(R.id.txtTip);
        txtTip.setText("");
        txtTip.setMovementMethod(ScrollingMovementMethod.getInstance());
        edtData=(EditText)findViewById(R.id.edtData);
        edtData.setText("");

        rgDataType=(RadioGroup)findViewById(R.id.rgDataType);
        rgDataType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rBtnStr:{
                        mCurDataType=DATATYPE_STR;
                        break;
                    }
                    case R.id.rBtnHex:{
                        mCurDataType=DATATYPE_HEX;
                        break;
                    }
                }
            }
        });
        rgDataType.check(R.id.rBtnStr);

        for(int tempId : mBtnArray){
            Button btnTemp=(Button)findViewById(tempId);
            btnTemp.setOnClickListener(this);
        }
        btnServerControl=(Button)findViewById(R.id.btnServerControl);
        btnServerControl.setOnClickListener(this);
        mServerRunningFlag=false;
        btnServerControl.setText("启动服务端");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnOpenBt:{
                openBluetooth();
                break;
            }
            case R.id.btnCloseBt:{
                closeBluetooth();
                break;
            }
            case R.id.btnOpenDiscovery:{
                openDiscovery();
                break;
            }
            case R.id.btnCloseDiscovery:{
                closeDiscovery();
                break;
            }
            case R.id.btnClearTip:{
                cleanTip();
                break;
            }
            case R.id.btnServerControl:{
                controlServer();
                break;
            }
            case R.id.btnCloseClient:{
                closeAllConnection();
                break;
            }
            case R.id.btnSendData:{
                sendData();
                break;
            }
        }
    }

    /**
     * 控制服务端的开关
     */
    public void controlServer(){
        if(mServerRunningFlag){//当前服务正在运行中
            //关闭服务端
            stopServer();
        }
        else{//服务未运行
            //开启服务端
            runServer();
        }
    }

    /**
     * 运行服务端
     */
    public void runServer(){
        if(mServerRunningFlag){
            showTip("服务端正在运行，请勿重复启动!");
            return;
        }

        try{
            mSocketList=new LinkedList<BluetoothSocket>();
            mExecutorService= Executors.newCachedThreadPool();

            mServerSocket=mBluetoothAdapter.listenUsingRfcommWithServiceRecord("BluetoothTool", GlobalDef.BT_UUID);
            mServerRunningFlag=true;
            btnServerControl.setText("关闭服务端");

            showTip("蓝牙服务端成功启动");
            new Thread(){
                @Override
                public void run(){
                    try{
                        BluetoothSocket socket=null;
                        while(mServerRunningFlag){
                            socket=mServerSocket.accept();
                            mSocketList.add(socket);
                            mExecutorService.execute(new SocketThread(socket));
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }catch(IOException e){
            e.printStackTrace();
            showTip("服务端启动出现异常");
            Log.e(TAG,"runServer IOException");
        }
    }

    /**
     * 停止服务端运行
     */
    public void stopServer(){
        if(!mServerRunningFlag){
            showTip("服务端未运行!");
            return;
        }
        //断开全部连接
        closeAllConnection();
        mServerRunningFlag=false;
        try{
            mSocketList=null;
            mExecutorService.shutdownNow();
            mExecutorService=null;

            mServerSocket.close();
            mServerSocket=null;
        }catch (Exception e){
            e.printStackTrace();
        }
        btnServerControl.setText("启动服务端");
        showTip("成功关闭蓝牙服务端");
    }

    /**
     * 关闭当前所有连接
     */
    public void closeAllConnection(){
        if(!mServerRunningFlag){
            showTip("服务端未运行!");
            return;
        }

        if(mSocketList!=null){
            if(mSocketList.size()==0){
                showTip("当前未存在蓝牙客户端连接!");
                return;
            }
            showTip("进行蓝牙客户端连接断开操作...");
            for(int i=0;i<mSocketList.size();i++){
                try{
                    BluetoothSocket tmpSocket=mSocketList.get(i);
                    tmpSocket.close();
                }catch (Exception e){
                    e.printStackTrace();
                }
                SystemClock.sleep(200);
            }
            mSocketList.clear();

        }
    }

    /**
     * 将编辑框内的数据发送给所有客户端
     */
    public void sendData(){
        if(!mServerRunningFlag){
            showTip("服务端未运行!");
            return;
        }

        if(mSocketList.size()==0){
            showTip("当前无客户端连接!");
            return;
        }

        final String strData=edtData.getText().toString();
        if(strData==null || strData.trim().equals("")){
            showTip("编辑框不能为空!");
            return;
        }

        try{
            byte[] bData=null;
            if(mCurDataType==DATATYPE_STR){
                bData=strData.getBytes();
            }
            else{
                bData=StringUtil.hexStringToBytes(strData);
            }

            if(bData==null){
                showTip("编辑框内的数据不符合要求!");
                return;
            }

            for(int i=0;i<mSocketList.size();i++){
                BluetoothSocket socket=mSocketList.get(i);
                writeData(socket,bData,0,bData.length);
            }
            showTip("数据发送成功");
        }catch (Exception e){
            e.printStackTrace();
        }
        /*new Thread(){
            @Override
            public void run(){

            }
        }.start();*/
    }

    /**
     * 写入数据
     * @param data
     * @param offset
     * @param len
     */
    public void writeData(BluetoothSocket socket,byte[] data,int offset,int len){
        if (socket == null || data == null || offset<0 || len<=0 || (len+offset)>data.length) {
            Log.e(TAG,"BT writeData params fail");
            return;
        }

        try {
            OutputStream mOut=socket.getOutputStream();

            byte[] buffer=new byte[1024];
            int nPos=offset;
            while((nPos-offset)<len){
                Arrays.fill(buffer,(byte)0x00);
                if((len+offset-nPos)>=buffer.length){
                    System.arraycopy(data,nPos,buffer,0,buffer.length);
                    mOut.write(buffer);
                    nPos+=buffer.length;
                }
                else{
                    int last=len+offset-nPos;
                    System.arraycopy(data,nPos,buffer,0,last);
                    mOut.write(buffer,0,last);
                    nPos+=last;
                }
            }
            mOut.flush();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    class SocketThread extends Thread {
        private BluetoothSocket mSocket;
        private BluetoothDevice mDevice;
        private InputStream mIn;
        private OutputStream mOut;
        private boolean isOpen = false;
        private byte[] mRecBuffer=new byte[1024*10];
        private int mRecPos=0;

        private int mFilePos=0;

        public SocketThread(BluetoothSocket socket) {
            try {
                this.mSocket = socket;
                mDevice=socket.getRemoteDevice();
                mIn = socket.getInputStream();
                mOut = socket.getOutputStream();
                isOpen = true;
                showTip(getDevInfo(mDevice)+"成功连接");
                Log.d(TAG, "a socket thread create");
            } catch (IOException e) {
                e.printStackTrace();
                Log.e(TAG, "create SocketThread fail");
            }

        }

        public String getDevInfo(BluetoothDevice bluetoothDevice){
            String strName="unknown";
            String strMac="unknown";
            if(bluetoothDevice!=null){
                strName=bluetoothDevice.getName();
                strMac=bluetoothDevice.getAddress();
            }
            String info="["+strName+","+strMac+"]";
            return info;
        }

        @Override
        public void run() {
            int readLen=0;
            byte[] buffer=new byte[1024];
            try{
                while(isOpen){
                    readLen=mIn.read(buffer);
                    if(readLen>0){
                        System.arraycopy(buffer,0,mRecBuffer,mRecPos,readLen);
                        mRecPos+=readLen;

                        while(true){
                            if(mIn.available()>0){//若流中有数据，则读取
                                readLen=mIn.read(buffer);
                                if((mRecPos+readLen)>mRecBuffer.length){//超出缓冲区
                                    showTip("读取数据超出缓冲区，将已读取的数据输出：");
                                    showTip("Receive hex data = "+ StringUtil.bytesToHexString(mRecBuffer,0,mRecPos));
                                    showTip("Receive string data = "+new String(mRecBuffer,0,mRecPos).trim());
                                    mRecPos=0;
                                    Arrays.fill(mRecBuffer,(byte)0x00);
                                }

                                System.arraycopy(buffer,0,mRecBuffer,mRecPos,readLen);
                                mRecPos+=readLen;
                            }
                            else{//若流中无数据
                                if(mRecPos>0){//说明此时数据读取完毕，将内容输出
                                    showTip("Receive hex data = "+StringUtil.bytesToHexString(mRecBuffer,0,mRecPos));
                                    showTip("Receive string data = "+new String(mRecBuffer,0,mRecPos).trim());

                                    //收到请求文件传输指令
                                    if(mRecBuffer[0]==0x01 && mRecBuffer[mRecPos-1]==0x04){
                                        String strInfoJson=new String(mRecBuffer,1,mRecPos-2);
                                        JSONObject infoJson=new JSONObject(strInfoJson);
                                        String fileName=infoJson.getString("FileName");
                                        long totalSize=infoJson.getLong("FileSize");//文件总大小
                                        String fileMD5=infoJson.getString("MD5");

                                        mFilePos=0;//已传输大小
                                        int nRate=0;//已传输的百分比

                                        File recFile=new File(RECEIVE_FILE_PATH+fileName);
                                        if(recFile.exists()){
                                            recFile.delete();
                                        }

                                        showLoadingDialog("正在接收文件数据..."+nRate+"%");
                                        //循环接收文件数据并写入文件
                                        while(true){
                                            readLen=mIn.read(buffer);
                                            if(readLen>0){
                                                writeFile(fileName,buffer,0,readLen);
                                                mFilePos+=readLen;
                                                //获得当前百分比
                                                int tmpRate=getPercent(mFilePos,totalSize);
                                                if(tmpRate!=nRate){
                                                    nRate=tmpRate;
                                                    showLoadingDialog("正在接收文件数据..."+nRate+"%");
                                                }
                                                if(mFilePos>=totalSize){
                                                    break;
                                                }
                                            }
                                            else{
                                                break;
                                            }
                                        }

                                        if(!recFile.exists()){
                                            showTip("接收文件失败");
                                            byte[] retData="Receive file fail!".getBytes();
                                            writeData(mSocket,retData,0,retData.length);
                                        }
                                        else{
                                            String nMD5= FileDigest.getFileMD5(recFile);
                                            if(fileMD5.equals(nMD5)){
                                                showTip("文件接收成功");
                                                byte[] retData="Server receive file success!".getBytes();
                                                writeData(mSocket,retData,0,retData.length);
                                            }
                                            else{
                                                showTip("文件校验失败");
                                                byte[] retData="File check fail!".getBytes();
                                                writeData(mSocket,retData,0,retData.length);
                                            }
                                        }
                                        dismissLoadingDialog();
                                    }

                                    mRecPos=0;
                                    Arrays.fill(mRecBuffer,(byte)0x00);
                                }
                                break;
                            }
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                dismissLoadingDialog();
                release();
            }
        }

        public void release(){
            Log.d(TAG,"A socketThread release");
            try{
                if(isOpen){
                    showTip(getDevInfo(mDevice)+"断开连接");
                }
                isOpen=false;
                if(mOut!=null){
                    try{
                        mOut.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mOut=null;
                }
                if(mIn!=null){
                    try{
                        mIn.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mIn=null;
                }
                if(mSocket!=null){
                    mSocketList.remove(mSocket);
                    try{
                        mSocket.close();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    mSocket=null;
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }


    /**
     * 向指定文件追加写数据
     * @param fileName
     * @param data
     * @param offset
     * @param length
     */
    public void writeFile(String fileName,byte[] data,int offset,int length){
        FileOutputStream fOut=null;
        try{
            File dir=new File(RECEIVE_FILE_PATH);
            if(!dir.exists()){
                dir.mkdir();
            }

            String filePath=RECEIVE_FILE_PATH+fileName;
            File nFile=new File(filePath);
            if(!nFile.exists()){
                nFile.createNewFile();
            }

            fOut=new FileOutputStream(nFile,true);

            byte[] buffer=new byte[1024];
            int nPos=offset;
            while((nPos-offset)<length){
                Arrays.fill(buffer,(byte)0x00);
                if((length+offset-nPos)>=buffer.length){
                    System.arraycopy(data,nPos,buffer,0,buffer.length);
                    fOut.write(buffer);
                    nPos+=buffer.length;
                }
                else{
                    int last=length+offset-nPos;
                    System.arraycopy(data,nPos,buffer,0,last);
                    fOut.write(buffer,0,last);
                    nPos+=last;
                }
            }
            fOut.flush();
        }
        catch (Exception e){
            e.printStackTrace();
            showTip("写本地文件发生异常");
        }
        finally {
            if(fOut!=null){
                try {
                    fOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class BluetoothBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent){
            String action=intent.getAction();
            Log.d(TAG,"Action received is "+action);
            if(BluetoothAdapter.ACTION_STATE_CHANGED.equals(action)){
                int blueState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blueState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
                        Log.i(TAG,"onReceive---------STATE_TURNING_ON");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        Log.i(TAG,"onReceive---------STATE_ON");
                        showTip("蓝牙当前状态：ON");
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        Log.i(TAG,"onReceive---------STATE_TURNING_OFF");
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        Log.i(TAG,"onReceive---------STATE_OFF");
                        showTip("蓝牙当前状态：OFF");
                        break;
                }
            }
            /*else if (BluetoothDevice.ACTION_PAIRING_REQUEST.equals(action)) {
                // 获得设备, 配对类型、随机配对密码
                BluetoothDevice btDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                String address = btDevice.getAddress();// 地址
                int type = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_VARIANT, BluetoothDevice.ERROR);
                int passkey = intent.getIntExtra(BluetoothDevice.EXTRA_PAIRING_KEY, BluetoothDevice.ERROR);
                Log.i(TAG, "Bluetooth ACTION_PAIRING_REQUEST type:" + type + ", key:" + passkey);

                if(type == BluetoothDevice.PAIRING_VARIANT_PIN){
                    Log.i(TAG, "Pin配对");
                    String strPin="0123";
                    Log.i(TAG,"Pin："+strPin);
                    byte[] byPin = strPin.getBytes();
                    btDevice.setPin(byPin);
                    try{
                        BluetoothUtil.cancelPairingUserInput(btDevice);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if (type == BluetoothDevice.PAIRING_VARIANT_PASSKEY_CONFIRMATION) {
                    Log.i(TAG, "开始密钥配对");
                    try{
                        BluetoothUtil.setPairingConfirmation(btDevice,true);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else if(type == 3){//PAIRING_VARIANT_CONSENT
                    Log.i(TAG, "PAIRING_VARIANT_CONSENT 开始配对");
                }
                else {
                    Log.i(TAG, "无法与\"" + address + "\"进行配对:未支持的配对方式");
                }
            }*/
        }
    }
}
