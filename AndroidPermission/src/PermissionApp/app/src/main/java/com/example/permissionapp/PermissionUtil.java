package com.example.permissionapp;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.ArraySet;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * author:chenjs
 */
public class PermissionUtil {
    private static final String TAG=PermissionUtil.class.getSimpleName();
    private static final boolean LOG_FLAG=true;//日志标识

    //日历
    private static final String[] Group_Calendar={
            Manifest.permission.READ_CALENDAR,Manifest.permission.WRITE_CALENDAR
    };
    //照相机
    private static final String[] Group_Camera={
            Manifest.permission.CAMERA
    };
    //通讯录
    private static final String[] Group_Contacts={
            Manifest.permission.WRITE_CONTACTS,Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.READ_CONTACTS
    };
    //定位
    private static final String[] Group_Location={
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
    };
    //麦克风
    private static final String[] Group_Microphone={
            Manifest.permission.RECORD_AUDIO
    };
    //电话
    private static final String[] Group_Phone={
            Manifest.permission.READ_PHONE_STATE,Manifest.permission.CALL_PHONE,
            Manifest.permission.READ_CALL_LOG,Manifest.permission.WRITE_CALL_LOG,
            Manifest.permission.ADD_VOICEMAIL,Manifest.permission.USE_SIP,
            Manifest.permission.PROCESS_OUTGOING_CALLS
    };
    //传感器
    private static final String[] Group_Sensors={
            Manifest.permission.BODY_SENSORS
    };
    //短信
    private static final String[] Group_Sms={
            Manifest.permission.READ_SMS,Manifest.permission.SEND_SMS,
            Manifest.permission.RECEIVE_SMS,Manifest.permission.RECEIVE_MMS,
            Manifest.permission.RECEIVE_WAP_PUSH
    };
    //存储
    private static final String[] Group_Storage={
            Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private static Map<String,String[]> m_PermissionGroupList=null;
    private static Map<String,String> m_PermissionsMappingList=null;
    static{
        initMap();
    }

    /**
     * 通过权限组名来申请一组权限
     * @param context
     * @param permissionGroupName
     * @param requestCode
     * @param listener
     */
    public static void requestByGroupName(Activity context, String permissionGroupName,int requestCode,OnPermissionsListener listener){
        requestByGroupName(context, new String[]{permissionGroupName}, requestCode, listener);
    }

    /**
     * 通过权限组名来申请多组权限
     * @param context Activity上下文
     * @param pgNameArray 多个要申请的权限组名称
     * @param requestCode 请求码
     * @param listener 回调接口
     */
    public static void requestByGroupName(Activity context, String[] pgNameArray,int requestCode,OnPermissionsListener listener){
        showLog("requestByPermissionGroup");
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23 && pgNameArray!=null){
                String[] permissionsList=getAppPermissionsList(context);//应用权限列表
                ArrayList<String> targetList=new ArrayList<>();
                if(permissionsList==null || permissionsList.length==0){
                    showLog("获得权限列表为空");
                    return;
                }

                for(String groupName:pgNameArray){
                    ArrayList<String> tmpPermissionList=isPermissionDeclared(permissionsList,groupName);
                    if(tmpPermissionList==null){//未找到
                        showLog("未找到["+groupName+"]中的权限");
                        continue;
                    }

                    for(int i=0;i<tmpPermissionList.size();i++){
                        //判断是否拥有权限
                        int nRet=ContextCompat.checkSelfPermission(context,tmpPermissionList.get(i));
                        if(nRet!= PackageManager.PERMISSION_GRANTED){
                            targetList.add(tmpPermissionList.get(i));
                        }
                    }
                }

                if(targetList.size()>0){
                    showLog("进行以下权限申请:"+targetList.toString());
                    String[] sList=targetList.toArray(new String[0]);
                    ActivityCompat.requestPermissions(context,sList,requestCode);
                }
                else{
                    showLog("全部权限都已授权");
                    if(listener!=null){
                        listener.onPermissionsOwned();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 通过权限名来申请一组权限
     * @param context
     * @param permission
     * @param requestCode
     * @param listener
     */
    public static void requestByPermissionName(Activity context, String permission,int requestCode,OnPermissionsListener listener){
        requestByPermissionName(context, new String[]{permission}, requestCode, listener);
    }

    /**
     * 通过权限名来申请多组权限
     * @param context Activity上下文
     * @param permissionArray 多个要申请的权限名称
     * @param requestCode 请求码
     * @param listener 回调接口
     */
    public static void requestByPermissionName(Activity context, String[] permissionArray,int requestCode,OnPermissionsListener listener){
        showLog("requestPermissions");
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23 && permissionArray!=null){
                ArrayList<String> targetList=new ArrayList<>();
                for(String strPermission:permissionArray){
                    //判断是否拥有权限
                    int nRet=ContextCompat.checkSelfPermission(context,strPermission);
                    if(nRet!= PackageManager.PERMISSION_GRANTED){
                        targetList.add(strPermission);
                    }
                }

                if(targetList.size()>0){
                    showLog("进行以下权限申请:"+targetList.toString());
                    String[] sList=targetList.toArray(new String[0]);
                    ActivityCompat.requestPermissions(context,sList,requestCode);
                }
                else{
                    showLog("全部权限都已授权");
                    if(listener!=null){
                        listener.onPermissionsOwned();
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 针对申请权限时的用户操作进行处理
     * @param context
     * @param permissions 申请的权限
     * @param grantResults 各权限的授权状态
     * @param listener 回调接口
     * @param controlFlag 控制标识，用于判断当响应禁止列表后，是否继续处理可再申请列表(避免出现同时处理禁止列表和可再申请列表，互相干扰，比如弹出两个提示框)
     */
    public static void onRequestPermissionsResult(Activity context,String[] permissions, int[] grantResults,OnPermissionsListener listener,boolean controlFlag) {
        try{
            ArrayList<String> requestList=new ArrayList<>();//可再申请列表
            ArrayList<String> banList=new ArrayList<>();//禁止列表
            for(int i=0;i<permissions.length;i++){
                if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
                    showLog("["+permissions[i]+"]权限授权成功");
                }
                else{
                    boolean nRet=ActivityCompat.shouldShowRequestPermissionRationale(context,permissions[i]);
                    //Log.i(TAG,"shouldShowRequestPermissionRationale nRet="+nRet);
                    if(nRet){//允许重新申请
                        requestList.add(permissions[i]);
                    }
                    else{//禁止申请
                        banList.add(permissions[i]);
                    }
                }
            }

            do{
                //优先对禁止列表进行判断
                if(banList.size()>0){
                    if(listener!=null){
                        listener.onPermissionsForbidden(permissions,grantResults,banList);
                    }
                    if(!controlFlag){//对禁止列表处理后，且控制标识为false，则跳过对可再申请列表的处理
                        break;
                    }
                }
                if(requestList.size()>0){
                    if(listener!=null){
                        listener.onPermissionsDenied(permissions,grantResults,requestList);
                    }
                }
                if(banList.size()==0 && requestList.size()==0){
                    showLog("权限授权成功");
                    if(listener!=null){
                        listener.onPermissionsSucceed();
                    }
                }
            }while (false);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 判断权限状态
     * @param context
     * @param permission 权限名
     * @return
     */
    public static boolean checkPermission(Context context,String permission){
        try{
            //如果操作系统SDK级别在23之上（android6.0），就进行动态权限申请
            if(Build.VERSION.SDK_INT>=23){
                int nRet= ContextCompat.checkSelfPermission(context,permission);
                showLog("checkSelfPermission nRet="+nRet);

                return nRet==PackageManager.PERMISSION_GRANTED? true : false;
            }
            return true;
        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 获得当前应用清单中的权限列表
     * @param context 应用上下文
     * @return
     */
    public static String[] getAppPermissionsList(Context context){
        try{
            PackageManager packageManager = context.getApplicationContext().getPackageManager();
            String packageName=context.getApplicationContext().getPackageName();
            String[] array = packageManager.getPackageInfo(packageName, PackageManager.GET_PERMISSIONS).requestedPermissions;
            return array;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断权限列表中是否声明了指定权限组中的权限
     * @param permissionList 权限列表
     * @param permissionGroup 权限组名
     * @return 存在则返回找到的权限组权限，否则返回null
     */
    public static ArrayList<String> isPermissionDeclared(String[] permissionList, String permissionGroup){
        try{
            if(permissionList!=null && permissionGroup!=null){
                String[] pmGroup=m_PermissionGroupList.get(permissionGroup);
                if(pmGroup!=null){
                    ArrayList<String> arrayList=new ArrayList<>();
                    //遍历
                    for(int i=0;i<pmGroup.length;i++){
                        String strPermission=pmGroup[i];
                        for(int j=0;j< permissionList.length;j++){
                            if(strPermission.equals(permissionList[j])){//找到指定权限组中的权限
                                arrayList.add(strPermission);
                                break;
                            }
                        }
                    }
                    if(arrayList.size()==0){
                        return null;
                    }
                    return arrayList;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获得传入的权限名列表对应的中文名称
     * @param permissionList 权限名列表
     * @return 集合
     */
    public static Set<String> getPermissionsNameByChinese(String[] permissionList){
        try{
            if(permissionList!=null){
                HashSet<String> nameSet=new HashSet<>();//确保集合元素不重复
                String tmpName;
                for(String strPermission : permissionList){
                    tmpName=m_PermissionsMappingList.get(strPermission);
                    if(tmpName!=null){
                        nameSet.add(tmpName);
                    }
                }
                return nameSet;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    private static void initMap(){
        if(m_PermissionGroupList==null){
            m_PermissionGroupList=new HashMap<>();
            m_PermissionGroupList.put(Manifest.permission_group.CALENDAR,Group_Calendar);
            m_PermissionGroupList.put(Manifest.permission_group.CAMERA,Group_Camera);
            m_PermissionGroupList.put(Manifest.permission_group.CONTACTS,Group_Contacts);
            m_PermissionGroupList.put(Manifest.permission_group.LOCATION,Group_Location);
            m_PermissionGroupList.put(Manifest.permission_group.MICROPHONE,Group_Microphone);
            m_PermissionGroupList.put(Manifest.permission_group.PHONE,Group_Phone);
            m_PermissionGroupList.put(Manifest.permission_group.SENSORS,Group_Sensors);
            m_PermissionGroupList.put(Manifest.permission_group.SMS,Group_Sms);
            m_PermissionGroupList.put(Manifest.permission_group.STORAGE,Group_Storage);
        }

        if(m_PermissionsMappingList==null){
            m_PermissionsMappingList=new HashMap<>();
            //日历
            for(String strPermission : Group_Calendar){
                m_PermissionsMappingList.put(strPermission,"日历");
            }
            //照相机
            for(String strPermission : Group_Camera){
                m_PermissionsMappingList.put(strPermission,"摄像头");
            }
            //通讯录
            for(String strPermission : Group_Contacts){
                m_PermissionsMappingList.put(strPermission,"通讯录");
            }
            //定位
            for(String strPermission : Group_Location){
                m_PermissionsMappingList.put(strPermission,"位置");
            }
            //麦克风
            for(String strPermission : Group_Microphone){
                m_PermissionsMappingList.put(strPermission,"麦克风");
            }
            //电话
            for(String strPermission : Group_Phone){
                m_PermissionsMappingList.put(strPermission,"电话");
            }
            //传感器
            for(String strPermission : Group_Sensors){
                m_PermissionsMappingList.put(strPermission,"传感器");
            }
            //短信
            for(String strPermission : Group_Sms){
                m_PermissionsMappingList.put(strPermission,"短信");
            }
            //存储
            for(String strPermission : Group_Storage){
                m_PermissionsMappingList.put(strPermission,"存储");
            }
        }
    }

    private static void showLog(String str){
        if(LOG_FLAG){
            Log.i(TAG,str);
        }
    }

    public interface OnPermissionsListener {
        /**
         * 权限都已拥有时的处理
         */
        void onPermissionsOwned();
        /**
         * 权限被禁止时的处理
         * @param permissions 申请的全部权限
         * @param grantResults 各权限的授权状态
         * @param pmList 禁止申请的权限列表
         */
        void onPermissionsForbidden(String[] permissions, int[] grantResults,ArrayList<String> pmList);
        /**
         * 权限被拒绝时的处理
         * @param permissions
         * @param grantResults
         * @param pmList 可再申请的权限列表
         */
        void onPermissionsDenied(String[] permissions, int[] grantResults,ArrayList<String> pmList);
        /**
         * 权限申请成功时的处理
         */
        void onPermissionsSucceed();
    }
}
