package com.example.bluetoothtool.common;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.bluetoothtool.R;

public class LoadingDialog {
    private static final String TAG=LoadingDialog.class.getSimpleName();
    private static LoadingDialog instance=null;
    private boolean showFlag=false;
    private Context mContext;
    private AlertDialog mDialog=null;
    private ImageView imgIcon;
    private TextView txtTip;


    public LoadingDialog(Context context){
        this.mContext=context;
        initDialog();
    }

    private void initDialog(){
        LayoutInflater inflater=LayoutInflater.from(mContext);
        View mLayout=inflater.inflate(R.layout.dialog_loading,null);
        imgIcon=(ImageView)mLayout.findViewById(R.id.imgIcon);
        txtTip=(TextView)mLayout.findViewById(R.id.txtTip);

        AlertDialog.Builder builder=new AlertDialog.Builder(mContext)
                .setView(mLayout)
                .setCancelable(false);
        mDialog=builder.create();
    }

    /**
     * 显示
     * @param strTip
     */
    public void show(String strTip){
        if(strTip==null){
            strTip="";
        }

        if(mDialog!=null){
            txtTip.setText(strTip);
            if(!showFlag){
                Glide.with(mContext).load(R.drawable.loading_fish).into(imgIcon);
                mDialog.show();
                showFlag=true;
            }
        }
    }

    /**
     * 关闭显示
     */
    public void dismiss(){
        if(mDialog!=null && showFlag){
            mDialog.dismiss();
            showFlag=false;
        }
    }

    /**
     * 是否正在显示
     * @return
     */
    public boolean isShown(){
        return showFlag;
    }
}
