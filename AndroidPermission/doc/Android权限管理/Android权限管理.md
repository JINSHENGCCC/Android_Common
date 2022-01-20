#<center>Android权限管理
---
##一、基本介绍
&#8195;&#8195;Android安全架构规定：默认情况下，任何应用都没有权限执行对其他应用、操作系统或用户有不利影响的任何操作。这包括读写用户的私有数据（如联系人或电子邮件等）、读写其他应用的文件、执行网络访问、使设备保持唤醒状态等等。  
&#8195;&#8195;应用权限基于系统安全功能，并有助于 Android 支持与用户隐私相关的以下目标：  

* 控制：用户可以控制他们与应用分享的数据。  
* 透明度：用户可以了解应用使用了哪些数据以及应用为何访问相关数据。  
* 数据最小化：应用仅能访问和使用用户调用的特定任务或操作所需的数据。

###权限类型
&#8195;&#8195;Android将权限分为不同的类型，包括安装时权限、运行时权限和特殊权限。每种权限类型都指明了当系统授予应用该权限后，应用可以访问的受限数据范围以及应用可以执行的受限操作范围。

####1、安装时权限
&#8195;&#8195;安装时权限会授予应用对受限数据的受限访问权限，并允许应用执行对系统或其他应用只有最低影响的受限操作。如果在应用中声明了安装时权限，系统会在用户允许该应用安装时自动授予相应权限。  
&#8195;&#8195;安装时权限分为多个子类型，包括普通权限和签名权限。

* **普通权限**  
此类权限允许访问超出应用沙盒的数据和执行超出应用沙盒的操作。但是，这些数据和操作对用户隐私及对其他应用的操作带来的风险非常小。系统会为普通权限分配“normal”保护级别。

* **签名权限**  
当应用声明了其他应用已定义的签名权限时，如果两个应用使用同一个签名文件进行签名，系统会在安装时向前者授予该权限。否则，系统无法向前者授予该权限。系统会为签名权限分配“signature”保护级别。

####2、运行时权限
&#8195;&#8195;运行时权限也称为危险权限，此类权限会授予应用对受限数据的额外访问权限，并允许应用执行对系统和其他应用具有更严重影响的受限操作。所以需要在运行时请求权限，然后才能访问受限数据或执行受限操作。当应用请求运行时权限时，系统会弹框显示运行时权限申请提示。许多运行时权限会访问用户私有数据，这是一种特殊的受限数据，其中包含可能比较敏感的信息。例如，位置信息和联系信息就属于用户私人数据。系统会为运行时权限分配“dangerous”保护级别。  
&#8195;&#8195;运行时权限机制是Android 6.0（M）的新特性，因此不同Android版本的设备以及应用设置的targetSdkVersion都会影响到应用申请危险权限时的表现。

* 在Android6.0版本之前的设备上使用时，应用还是使用旧的权限系统，危险权限还是在应用安装时就会进行申请，不管targetSdkVersion是否大于23。  
* 在Android6.0版本及更高版本的设备上使用时，则会根据应用中设置的targetSdkVersion进行判断：1）若targetSdkVersion低于23，则继续使用旧规则，危险权限在安装时进行申请；2）若targetSdkVersion大于等于23时，则应用需要在运行时进行权限动态申请。

_注：从Android 6.0（Marshmallow，API 23）开始，用户可以在任何时候撤销应用的某个权限，即使应用的targetSdkVersion小于23。因此必须确保在请求权限失败后，应用还能表现良好。_

&#8195;&#8195;如果设备运行的是Android 6.0或更高版本，并且应用的targetSdkVersion是23或更高版本，则当用户请求危险权限时系统会发生以下行为：

* 如果应用请求一个已经在其清单文件中列出的危险权限，并且应用当前没有拥有该权限组的任何权限，那么系统就会向用户显示一个对话框询问用户是否授权，该对话框会描述应用想要访问的权限组而不是组内的特定权限。例如，如果一个应用请求READ\_CONTACTS权限，系统会弹出对话框告知用户应用需要访问设备的联系人，如果用户允许授权，那么系统将授予应用所需的权限。  
* 如果应用请求一个已经在其清单文件中列出的危险权限，并且应用当前已经拥有了该权限组的其它危险权限，系统会立即授予该权限而不需要通知用户。例如，如果一个应用之前已经请求过并已经被授予了READ\_CONTACTS权限，那么之后它请求WRITE\_CONTACTS时系统将立即授予该权限。

> 权限组概念：  
> &#8195;&#8195;根据设备的功能和特性，权限被分为权限组。系统以权限组的形式来处理权限请求，一个权限组可能对应Manifest中申请的几个权限。例如，STORAGE权限组包括READ\_EXTERNAL\_STORAGE和WRITE\_EXTERNAL\_STORAGE权限。权限组的方式让用户更容易理解权限，和更方便处理APP的权限请求，防止过多的复杂的授予单独的权限。  
> &#8195;&#8195;任何权限都可以属于一个权限组，包括正常权限和应用自定义的权限。但权限组仅当权限危险时才影响用户体验。可以忽略正常权限的权限组。

**危险权限以及对应分组如下：**

![][01]

####3、特殊权限
&#8195;&#8195;特殊权限与特定的应用操作相对应。只有平台和原始设备制造商 (OEM) 可以定义特殊权限。此外，如果平台和 OEM 想要防止有人执行功能特别强大的操作（例如通过其他应用绘图），通常会定义特殊权限。系统会为特殊权限分配“appop”保护级别。在Android 6.0以后，只有系统应用才能够使用这些特殊权限。

<br/>
<br/>
<br/>
##二、动态权限申请
###1、主要使用方法
**(1)ContextCompat.checkSelfPermission**  
&#8195;&#8195;检查应用是否具有某个危险权限，如果应用具有此权限，方法将返回 PackageManager.PERMISSION\_GRANTED；如果应用不具有此权限，方法将返回 PackageManager.PERMISSION\_DENIED。

**(2)ActivityCompat.requestPermissions**  
&#8195;&#8195;应用可以通过这个方法动态申请权限，调用后会弹出一个对话框提示用户授权所申请的权限组。

**(3)ActivityCompat.shouldShowRequestPermissionRationale**  
&#8195;&#8195;检查此权限上次是否被拒绝过。如果应用之前请求过此权限但用户拒绝了请求，但可继续请求，则此方法将返回 true。而以下几种情况均返回false： 1）用户从未申请过该权限；2）用户在过去拒绝了权限请求，并在权限请求系统对话框中选择了 Don't ask again 选项；3）用户允许了该权限申请请求；4）设备规范禁止应用具有该权限。  
&#8195;&#8195;因此单独使用该方法去做判断，是没用的，应该在请求权限回调中使用。

_注：不同手机系统对于权限处理方面可能存在差异，部分手机系统在弹出授权申请时选择拒绝就默认了不再弹出，此时调用此方法返回false。_

**(4)onRequestPermissionsResult**  
&#8195;&#8195;当应用请求权限时，系统将向用户显示一个对话框。当用户响应时，系统将调用应用的 onRequestPermissionsResult() 方法，向其传递用户响应，处理对应的场景。

###2、调用流程
**(1)在AndroidManifest.xml中对要申请的权限进行申明。**

	<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"></uses-permission>
	<uses-permission android:name="android.permission.CAMERA"></uses-permission>
	<uses-permission android:name="android.permission.WRITE_CONTACTS"></uses-permission>
	<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION"></uses-permission>

**(2)申请权限前对权限进行检查，若应用不具有该权限，则进行权限申请；若一次申请多个权限，则应该逐个进行权限检查，最后对未授权的权限进行统一申请。**

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


**(3)重写onRequestPermissionsResult回调方法，对用户的权限授权操作进行监听处理。**

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


<br/>
<br/>
<br/>
##三、PermissionUtil工具类
&#8195;&#8195;这个类是自己封装的一个Android权限申请的工具类，可以通过权限组名或权限名来对多个权限进行申请，并将对用户授权操作的处理逻辑封装起来，可以通过传入回调接口，针对用户授权情况来定义不同的响应处理。

**PermissionUtil实现:**

	import android.Manifest;
	import android.app.Activity;
	import android.content.Context;
	import android.content.pm.PackageManager;
	import android.os.Build;
	import android.util.Log;

	import androidx.core.app.ActivityCompat;
	import androidx.core.content.ContextCompat;

	import java.util.ArrayList;
	import java.util.HashMap;
	import java.util.Map;

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
    	public static void requestByGroupName(Activity context, String permissionGroupName,int 	requestCode,OnPermissionsListener listener){
    	    requestByGroupName(context, new String[]{permissionGroupName}, requestCode, listener);
    	}
	
    	/**
    	 * 通过权限组名来申请多组权限
    	 * @param context Activity上下文
    	 * @param pgNameArray 多个要申请的权限组名称
    	 * @param requestCode 请求码
    	 * @param listener 回调接口
    	 */
    	public static void requestByGroupName(Activity context, String[] pgNameArray,int 	requestCode,OnPermissionsListener listener){
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
    	public static void requestByPermissionName(Activity context, String permission,int 	requestCode,OnPermissionsListener listener){
    	    requestByPermissionName(context, new String[]{permission}, requestCode, listener);
    	}

    	/**
    	 * 通过权限名来申请多组权限
    	 * @param context Activity上下文
    	 * @param permissionArray 多个要申请的权限名称
    	 * @param requestCode 请求码
    	 * @param listener 回调接口
    	 */
    	public static void requestByPermissionName(Activity context, String[] permissionArray,int 	requestCode,OnPermissionsListener listener){
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
    	public static void onRequestPermissionsResult(Activity context,String[] permissions, int[] 	grantResults,OnPermissionsListener listener,boolean controlFlag) {
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
    	        String[] array = packageManager.getPackageInfo(packageName,PackageManager.GET_PERMISSIONS).requestedPermissions;
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


**使用方式可以参考以下例子：**


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
		//通过权限组名来申请指定权限
        PermissionUtil.requestByGroupName(mContext,pgArray, RequestCode3_1,mListener3_1);
    }

	@Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){//可以针对不同的权限申请操作进行不同处理，也可以统一以相同方式处理（不对requestCode进行判断）
            case RequestCode3_1:{
                PermissionUtil.onRequestPermissionsResult(mContext,permissions,grantResults,mListener3_1,false);
                break;
            }
        }
    }

具体的工具类以及演示工程放在Github上，感兴趣的同学可以了解一下。如果有什么问题，欢迎来一起讨论，共同进步。  

GitHub地址：[https://github.com/JINSHENGCCC/Android_Common/tree/master/AndroidPermission/src](https://github.com/JINSHENGCCC/Android_Common/tree/master/AndroidPermission/src)



<br/>
<br/>
<br/>
##四、参考
1. [Android 中的权限](https://developer.android.google.cn/guide/topics/permissions/overview "Android 中的权限")  
2. [秒懂Android开发之权限总结](https://zhuanlan.zhihu.com/p/158899172 "秒懂Android开发之权限总结")  
3. [Android权限管理详解](https://blog.csdn.net/shangmingchao/article/details/70312824 "Android权限管理详解")  
4. [Android 运行时权限处理](https://www.cnblogs.com/2eggs/p/9516044.html "Android 运行时权限处理")  
5. [Android 6.0动态权限申请](https://www.jianshu.com/p/2fe4fb3e8ce0 "Android 6.0动态权限申请")  
6. [Android6.0危险权限列表](https://blog.csdn.net/xxdw1992/article/details/89811370 "Android6.0危险权限列表")  





[01]: ./pic/1.png  




