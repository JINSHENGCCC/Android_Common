package com.example.bluetoothtool.common;

import java.util.UUID;

public class GlobalDef {
    public static final UUID BT_UUID=UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static final int REQ_CODE_OPEN_BT=1001;
    public static final int REQ_CODE_SELECT_FILE=1002;

    //Activity Handler消息
    public static final int MSG_SHOW_TIP=1;//显示提示
    public static final int MSG_CLEAN_TIP=2;//清空提示
    public static final int MSG_SHOW_TOAST=3;//显示Toast
    public static final int MSG_SHOW_LOAD_DIALOG=4;//显示加载对话框
    public static final int MSG_CLOSE_LOAD_DIALOG=5;//关闭加载对话框
}
