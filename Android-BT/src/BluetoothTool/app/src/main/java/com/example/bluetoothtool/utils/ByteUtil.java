package com.example.bluetoothtool.utils;

public class ByteUtil {
    /**
     * 判断字节数组中是否每一字节都与指定字节相同
     * @param array
     * @param mByte
     * @return
     */
    public static boolean isBytesEuqal(byte[] array,byte mByte){
        for(int i=0;i<array.length;i++){
            if(array[i]!=mByte){
                return false;
            }
        }
        return true;
    }

    /**
     * 将int转换为byte数组(低位在前，高位在后)
     * @param length	int数值
     * @return	byte数组
     */
    public static byte[] intToBytesByLittleEndian(int length){
        byte[] byteArray = new byte[4];
        byteArray[3] = (byte)(length >>> 24);
        byteArray[2] = (byte)(length >>> 16);
        byteArray[1] = (byte)(length >>> 8);
        byteArray[0] = (byte)(length);
        return byteArray;
    }

    /**
     * 将byte数组转为int(低位在前，高位在后)
     * @param data
     * @return
     */
    public static int bytesToIntByLittleEndian(byte[] data){
        try{
            int ret=0;
            ret|=(byte)data[0]&0xFF;
            ret|=(((byte)data[1]&0xFF)<<8);
            ret|=(((byte)data[2]&0xFF)<<16);
            ret|=(((byte)data[3]&0xFF)<<24);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public static int bytesToIntByLittleEndian(byte[] data,int offset){
        try{
            int ret=0;
            ret|=(byte)data[offset]&0xFF;
            ret|=(((byte)data[offset+1]&0xFF)<<8);
            ret|=(((byte)data[offset+2]&0xFF)<<16);
            ret|=(((byte)data[offset+3]&0xFF)<<24);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 将int转换为byte数组(高位在前，低位在后)
     * @param length	int数值
     * @return	byte数组
     */
    public static byte[] intToBytesByBigEndian(int length){
        byte[] byteArray = new byte[4];
        byteArray[0] = (byte)(length >>> 24);
        byteArray[1] = (byte)(length >>> 16);
        byteArray[2] = (byte)(length >>> 8);
        byteArray[3] = (byte)(length);
        return byteArray;
    }

    /**
     * 将byte数组转为int(高位在前，低位在后)
     * @param data
     * @return
     */
    public static int bytesToIntByBigEndian(byte[] data){
        try{
            int ret=0;
            ret|=(byte)data[3]&0xFF;
            ret|=(((byte)data[2]&0xFF)<<8);
            ret|=(((byte)data[1]&0xFF)<<16);
            ret|=(((byte)data[0]&0xFF)<<24);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public static int bytesToIntByBigEndian(byte[] data,int offset){
        try{
            int ret=0;
            ret|=(byte)data[offset+3]&0xFF;
            ret|=(((byte)data[offset+2]&0xFF)<<8);
            ret|=(((byte)data[offset+1]&0xFF)<<16);
            ret|=(((byte)data[offset]&0xFF)<<24);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * 将int转换为byte数组(数组长度为2，高位在前，低位在后)
     * @param length	int数值
     * @return	byte数组
     */
    public static byte[] intToTwoBytesByBigEndian(int length){
        byte[] byteArray = new byte[2];
        byteArray[0] = (byte)(length >>> 8);
        byteArray[1] = (byte)(length);
        return byteArray;
    }

    /**
     * 将byte数组(数组长度为2)转为int(高位在前，低位在后)
     * @param data
     * @return
     */
    public static int twoBytesToIntByBigEndian(byte[] data){
        try{
            int ret=0;
            ret|=(byte)data[1]&0xFF;
            ret|=(((byte)data[0]&0xFF)<<8);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    public static int twoBytesToIntByBigEndian(byte[] data,int offset){
        try{
            int ret=0;
            ret|=(byte)data[offset+1]&0xFF;
            ret|=(((byte)data[offset]&0xFF)<<8);
            return ret;
        }
        catch(Exception e){
            e.printStackTrace();
            return -1;
        }
    }

    /**
     * @name: 字节合并
     * @function: 将拆分后的字节数组，以每两个字节合并为一个字节 如：byte[] {0x35, 0x3B, 0x34, 0x35 }
     * –> byte[]{0x5B, 0×45}
     * @param bData ---待合并的byte[]数据
     * @return合并后的数据
     */
    public static byte[] combineBytes(byte[] bData){
        if (bData == null || bData.length%2!=0){
            return null;
        }
        int nLen = bData.length;
        int i = 0;
        int j = 0;
        byte[] bReturnData = new byte[nLen / 2];
        for (i = 0; i < nLen; i = i + 2){
            j = i / 2;
            bReturnData[j] = (byte) (((bData[i] << 4) & 0xF0) | (bData[i + 1] & 0x0F));
        }

        return bReturnData;
    }

    /**
     * @name: 字节拆分
     * @function: 将一个byte拆分成两个 如：byte[]{0x2B,0x45} –> byte[]{0x32,0x3B,0x34,0x45}
     * @param bData ---待分割的字符串数据
     * @return 拆分后的数据
     */
    public static byte[] divideBytes(byte[] bData){
        if (bData == null){
            return null;
        }

        int nLen = bData.length;
        byte[] bReturnData = new byte[2 * nLen];

        for (int i = 0; i < nLen; i++) {
            bReturnData[2 * i] = (byte) (((bData[i] & 0xF0) >> 4) + 0x30);
            bReturnData[2 * i + 1] = (byte) ((bData[i] & 0x0F) + 0x30);
        }

        return bReturnData;
    }

    /**
     * 比较两个字符数组的指定区段字符是否完全相同
     * @param arrayA 数组A
     * @param posA  比较区段的起始索引号
     * @param arrayB 数组B
     * @param posB 比较区段的起始索引号
     * @param length 比较区段的字节数
     * @return 相同返回true, 否则返回false
     */
    boolean arrayCompare(byte[] arrayA, int posA, byte[] arrayB, int posB, int length )
    {
        //判断参数是否有效
        if( null == arrayA || null == arrayB || arrayA.length <= posA
                || arrayB.length <= posB || length <= 0 )
        {
            return false;
        }

        //如果超出了数组长度，则肯定是不相等的
        if( posA + length > arrayA.length || posB + length > arrayB.length )
        {
            return false;
        }

        //逐个字节比较，如果完全相等，则返回true
        for( int i = 0; i < length; i++ )
        {
            if( arrayA[i+posA] != arrayB[i+posB])
            {
                return false;//有一个字符不相同，则返回false
            }
        }
        return true;//完全相同，返回true
    }

    /**
     * 获取最早出现的分隔符两边的数据
     * 例如：test|func|param拆分后得到test和func|param两部分
     * @param bSrc 数据
     * @param bSeperatorChar 分隔符
     * @return
     */
    public static byte[][] getByteArrayBeforeSeperator(byte[] bSrc, byte bSeperatorChar)
    {
        byte[] flag = new byte[1];
        flag[0] = bSeperatorChar;
        return getArrayBySeperatorArray(bSrc, flag);
    }


    //获取分隔符数组两边数据
    public static byte[][] getArrayBySeperatorArray(byte[] bSrc, byte[] bSeperatorChar)
    {
        byte[][] retdata = new byte[2][];
        retdata[0] = null;
        retdata[1] = null;

        //处理源数据为空情况
        if (bSrc == null)
        {
            return retdata;
        }
        //处理分隔数据为空境况
        if (bSeperatorChar == null)
        {
            retdata[0] = bSrc.clone();
            return retdata;
        }

        //处理源数据长度小于分隔数据长度的情况
        if (bSrc.length < bSeperatorChar.length)
        {
            retdata[0] = bSrc.clone();
            return retdata;
        }

        int i = 0;
        int j = 0;
        //遍历源数据并查找分隔数据
        for (i = 0; i < bSrc.length; ++i)
        {
            //如果剩余数据小于分隔数据则比较结束
            if (bSrc.length-i < bSeperatorChar.length)
            {
                retdata[0] = bSrc.clone();
                return retdata;
            }

            //比较数据是否相同
            for (j = 0; j < bSeperatorChar.length; ++j)
            {
                if ( bSrc[i+j] != bSeperatorChar[j] )
                {
                    break;
                }
            }

            //找到指定的字符串
            if ( j == bSeperatorChar.length )
            {
                if (i > 0)
                {
                    retdata[0] = new byte[i];
                    System.arraycopy(bSrc, 0, retdata[0], 0, i);
                }

                int len = bSrc.length - bSeperatorChar.length - i;
                if (len > 0)
                {
                    retdata[1] = new byte[len];
                    System.arraycopy(bSrc, i+bSeperatorChar.length, retdata[1], 0, len);
                }
                return retdata;
            }

        }
        retdata[0] = bSrc.clone();
        return retdata;
    }

    /**
     * 计算指定字节数据的异或校验值
     * @param datas 数据
     * @param offset 起始位置
     * @param len 长度
     * @return
     */
    public static byte doXor(byte[] datas,int offset,int len) {
        byte errRet=0x00;
        try{
            if(datas==null || offset<0 || len<=0 || datas.length<=offset ||
                    datas.length<(offset+len)){
                return errRet;
            }

            int nPos=offset;
            byte bRet = datas[nPos];
            nPos++;
            while((nPos-offset)<len){
                bRet ^=datas[nPos];
                nPos++;
            }
            return bRet;
        }catch (Exception e){
            e.printStackTrace();
        }
        return errRet;
    }
}
