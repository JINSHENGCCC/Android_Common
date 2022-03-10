package com.example.bluetoothtool.utils;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.SystemClock;
import android.util.Log;

import java.lang.reflect.Method;

public class BluetoothUtil {
    private static final String TAG=BluetoothUtil.class.getSimpleName();

    /**
     * 设置蓝牙可见
     * @param adapter
     * @param timeout 超时为0时，永久可见
     */
    public static void setDiscoverableTimeout(BluetoothAdapter adapter, int timeout) {
        //BluetoothAdapter adapter=BluetoothAdapter.getDefaultAdapter();
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, timeout);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE,timeout);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭蓝牙可见
     * @param adapter
     */
    public static void closeDiscoverableTimeout(BluetoothAdapter adapter) {
        try {
            Method setDiscoverableTimeout = BluetoothAdapter.class.getMethod("setDiscoverableTimeout", int.class);
            setDiscoverableTimeout.setAccessible(true);
            Method setScanMode =BluetoothAdapter.class.getMethod("setScanMode", int.class,int.class);
            setScanMode.setAccessible(true);

            setDiscoverableTimeout.invoke(adapter, 1);
            setScanMode.invoke(adapter, BluetoothAdapter.SCAN_MODE_CONNECTABLE,1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 蓝牙配对绑定
     * @param dev
     * @return
     */
    public static boolean createBond( BluetoothDevice dev ){
        try{
            Method createBondMethod = BluetoothDevice.class.getMethod("createBond");
            Boolean ret = (Boolean)createBondMethod.invoke( dev );
            return ret.booleanValue();
        }
        catch(Exception e ){
            e.printStackTrace();
        }
        return false;
    }

    /*设置匹配密码*/
	public static boolean setPasskey(BluetoothDevice dev, int passkey)
	{
		try
		{
			Method setKeyMethod = BluetoothDevice.class.getMethod("setPasskey", int.class );
			Boolean ret = (Boolean)setKeyMethod.invoke( dev, passkey );
			return ret.booleanValue();
		}catch( Exception e )
		{
			e.printStackTrace();
		}
		return false;
	}

    /**
     * 设置PIN码
     * @param dev
     * @param pin
     * @return
     */
    public static boolean setPin( BluetoothDevice dev, byte[] pin)
    {
        try
        {
            Method setPinMethod = BluetoothDevice.class.getMethod("setPin", byte[].class );
            Boolean ret = (Boolean)setPinMethod.invoke( dev, pin );
            return ret.booleanValue();
        }catch( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消用户输入
     * @param dev
     * @return
     */
    public static boolean cancelPairingUserInput(BluetoothDevice dev)
    {
        try
        {
            Method cancelInputMethod = BluetoothDevice.class.getMethod("cancelPairingUserInput" );
            Boolean ret = (Boolean)cancelInputMethod.invoke( dev );
            return ret.booleanValue();
        }catch( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }


    public static boolean setPairingConfirmation(BluetoothDevice dev, boolean confirm)
    {
        try
        {
            Method confirmMethod = BluetoothDevice.class.getMethod("setPairingConfirmation", boolean.class );
            Boolean ret = (Boolean)confirmMethod.invoke( dev, confirm );
            return ret.booleanValue();
        }catch( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 蓝牙解除配对
     * @param dev
     * @return
     */
    public static boolean removeBond(BluetoothDevice dev){
        try{
            Method removeBondMethod = BluetoothDevice.class.getMethod("removeBond" );
            Boolean ret = (Boolean)removeBondMethod.invoke( dev );
            return ret.booleanValue();
        }
        catch( Exception e ){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消蓝牙配对
     * @param dev
     * @return
     */
    public static boolean cancelBondProcess(BluetoothDevice dev)
    {
        try
        {
            Method cancelBondMethod = BluetoothDevice.class.getMethod("cancelBondProcess" );
            Boolean ret = (Boolean)cancelBondMethod.invoke( dev );
            return ret.booleanValue();
        }catch( Exception e )
        {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 打开蓝牙
     * @param btAdapter 本地蓝牙适配器
     * @param timeout 轮询蓝牙状态超时时间
     * @return
     */
    public static boolean openBluetooth(BluetoothAdapter btAdapter,int timeout){
        try{
            if (btAdapter == null) {
                Log.e(TAG, "BluetoothAdapter is null,该设备不支持蓝牙功能!");
                return false;
            }

            if(timeout<=0){
                return false;
            }

            if( !btAdapter.isEnabled()) {
                Log.d(TAG,"当前蓝牙未打开，正在打开蓝牙中...");
                long start = System.currentTimeMillis();
                long TimeOut=timeout*1000;
                btAdapter.enable();
                while (btAdapter.getState() == BluetoothAdapter.STATE_TURNING_ON
                        || btAdapter.getState() != BluetoothAdapter.STATE_ON) {
                    //若打开超时,则返回失败
                    if(System.currentTimeMillis()-start > TimeOut){
                        Log.d(TAG,"打开蓝牙超时!");
                        return false;
                    }
                    Thread.sleep(100);
                }
                Log.d(TAG,"打开蓝牙成功");
            }
            else
            {
                Log.d(TAG,"蓝牙已打开");
            }
            return true;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 设置设备蓝牙名称
     * @param mBluetoothAdapter
     * @param name
     */
    public static boolean setBluetoothName(BluetoothAdapter mBluetoothAdapter,String name){
        if(mBluetoothAdapter == null ){
            Log.i(TAG,"bluetoothAdapter is null");
            return false;
        }

        if(!mBluetoothAdapter.isEnabled()){
            Log.i(TAG,"bluetooth is not on");
            return false;
        }

        Log.i(TAG,"now bluetooth name is "+mBluetoothAdapter.getName());
        Log.i(TAG,"the name for set is "+name);

        if(name == null || "".equals(name)){
            Log.i(TAG,"the bluetoothName for set is null");
            return false;
        }

        //名称已相同
        if(mBluetoothAdapter.getName().equals(name)){
            Log.i(TAG,"bluetooth name is already same");
            return true;
        }

        //设置蓝牙方法返回成功  且  获取的蓝牙名称与设置的蓝牙名称一致
        if (mBluetoothAdapter.setName(name)) {
            SystemClock.sleep(100);
            if(mBluetoothAdapter.getName().equals(name)){
                Log.i(TAG,"set bluetooth name success");
                return true;
            }
            else{
                Log.i(TAG,"set bluetooth name success,but the name what getted is not same");
            }
        }else{
            //修改失败
            Log.i(TAG,"set bluetooth name fail");
        }

        return false;
    }
}
