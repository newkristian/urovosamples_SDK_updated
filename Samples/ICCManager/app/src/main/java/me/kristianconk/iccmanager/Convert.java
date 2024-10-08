package me.kristianconk.iccmanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class Convert {
    /**
     * Check if a (hex) string is pure hex (0-9, A-F, a-f) and 16 byte
     * (32 chars) long. If not show an error Toast in the context.
     *
     * @param hexString The string to check.
     * @param context   The Context in which the Toast will be shown.
     * @return True if sting is hex an 16 Bytes long, False otherwise.
     */
    public static boolean isHexAnd16Byte(String hexString, Context context) {
        if (hexString.matches("[0-9A-Fa-f]+") == false) {
            // Error, not hex.
            Toast.makeText(context, "Error: Data must be in hexadecimal(0-9 and A-F)",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public static boolean isHex(String hexString, Context context) {
        if (hexString.matches("[0-9A-Fa-f]+") == false || hexString.length() % 2 != 0) {
            // Error, not hex.
            Toast.makeText(context, "Error: Data must be in hexadecimal(0-9 and A-F)",
                    Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    /**
     * Convert a string of hex data into a byte array.
     * Original author is:
     *
     * @param s The hex string to convert
     * @return An array of bytes with the values of the string.
     */
    public static byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        try {
            for (int i = 0; i < len; i += 2) {
                data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                        + Character.digit(s.charAt(i + 1), 16));
            }
        } catch (Exception e) {
            Log.d("debug", "Argument(s) for hexStringToByteArray(String s)"
                    + "was not a hex string");
        }
        return data;
    }

    public static String bytesToHexString(byte[] src, int offset, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = offset; i < length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /*
     * Convert byte[] to hex
     * string. Here we can convert byte to int and then use integer.tohexstring (int) to convert to hexadecimal string
     * @param src byte[] data
     * @return hex string
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (int i = 0; i < src.length; i++) {
            int v = src[i] & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    @SuppressLint("DefaultLocale")
    public static void hexDump(View view, byte startAddr, byte[] src, int length) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return;
        }

        int padLen = (int) (8 * Math.ceil(length / 8.0));
        byte[] tmp = new byte[padLen];

        int modLen = length % 8;

        if (view instanceof EditText) {
            System.arraycopy(src, 0, tmp, 0, length);
            ((EditText) view).append("Memory Data: " + "\n");

            for (int i = 0; i < padLen; i++) {
                int v = tmp[i] & 0xFF;
                String hv = Integer.toHexString(v);
                if (hv.length() < 2) {
                    stringBuilder.append(0);
                }
                stringBuilder.append(hv);
                stringBuilder.append(" ");
                if (((i + 1) * 3) % 24 == 0) {
                    StringBuilder sb = new StringBuilder("");
                    String sAddr = Integer.toHexString((startAddr + i / 8 * 8) & 0xFF);
                    if (sAddr.length() < 2) {
                        sb.append(0);
                    }
                    sb.append(sAddr);

                    ((EditText) view).append(sb.toString().toUpperCase() + ": ");
                    if (i != padLen - 1) {
                        ((EditText) view).append(stringBuilder.subSequence(((i + 1) * 3 / 24 - 1) * 24, (i + 1) * 3).toString().toUpperCase() + "\n");
                    } else if (i == padLen - 1) {
                        if (modLen == 0) {
                            ((EditText) view).append(stringBuilder.subSequence(((i + 1) * 3 / 24 - 1) * 24, (i + 1) * 3).toString().toUpperCase() + "\n");
                        } else {
                            ((EditText) view).append(stringBuilder.subSequence(((i + 1) * 3 / 24 - 1) * 24, (i + 1 - (8 - modLen)) * 3).toString().toUpperCase() + "\n");
                        }
                    }
                }
            }


            ((EditText) view).append("\n");
        }
    }

    /*
     *
     * BCD code string converted to int value
     *
     * */
    public static int BcdToInt(byte[] sBcdBuf, int iBcdLen) {
        int iValue = 0, i = 0;
        byte temp;

        if (iBcdLen <= 0)
            return 0;

        while (i < iBcdLen) {
            temp = sBcdBuf[i];
            iValue = iValue * 100 + bcd_to_byte(temp);
            i++;
        }


        return iValue;
    }

    public static byte abcd_to_asc(byte ucBcd) {
        byte ucAsc;

        ucBcd &= 0x0f;
        if (ucBcd <= 9)
            ucAsc = (byte) (ucBcd + (byte) ('0'));
        else
            ucAsc = (byte) (ucBcd + (byte) ('A') - (byte) 10);
        return (ucAsc);
    }

    public static void BcdToAsc(byte[] sAscBuf, byte[] sBcdBuf, int iAscLen) {
        int i, j;

        j = 0;
        for (i = 0; i < iAscLen / 2; i++) {
            sAscBuf[j] = (byte) ((sBcdBuf[i] & 0xf0) >> 4);
            sAscBuf[j] = abcd_to_asc(sAscBuf[j]);
            j++;
            sAscBuf[j] = (byte) (sBcdBuf[i] & 0x0f);
            sAscBuf[j] = abcd_to_asc(sAscBuf[j]);
            j++;
        }
        if ((iAscLen % 2) != 0) {
            sAscBuf[j] = (byte) ((sBcdBuf[i] & 0xf0) >> 4);
            sAscBuf[j] = abcd_to_asc(sAscBuf[j]);
        }
    }

    /*
     *
     * One byte BCD code converted to int value
     *
     * */
    public static byte bcd_to_byte(byte ucBcd) {
        byte temp, temp2;
        temp = ucBcd;
        temp >>= 4;
        temp &= 0x0f;
        temp *= 10;

        temp2 = ucBcd;
        temp2 &= 0x0f;

        return (byte) (temp + temp2);
        //return (((ucBcd >> 4) & 0x0f) * 10 + (ucBcd & 0x0f));
    }
}
