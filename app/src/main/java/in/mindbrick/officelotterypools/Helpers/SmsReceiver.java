package in.mindbrick.officelotterypools.Helpers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by swarajpal on 19-04-2016.
 */
public class SmsReceiver extends BroadcastReceiver {

    //private static SmsListener mListener;
    private static SmsListener mListener;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data  = intent.getExtras();

        Object[] pdus = (Object[]) data.get("pdus");

        for(int i=0;i<pdus.length;i++){
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdus[i]);

            String sender = smsMessage.getDisplayOriginatingAddress();
            //You must check here if the sender is your provider and not another one with same text.

           // String messageBody = smsMessage.getMessageBody();

            String senderAddress = smsMessage.getDisplayOriginatingAddress();
            String message = smsMessage.getDisplayMessageBody();
            Log.e("senderAddress",senderAddress+"--"+message);

            if (senderAddress.toLowerCase().contains("REACHD".toLowerCase())) {
                Log.e("sms", "SMS is  for our app!");
                mListener.messageReceived(message);
                return;
            } else {
                Log.e("sms", "SMS is not for our app!");
            }



            //Pass on the text to our listener.

        }

    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}
