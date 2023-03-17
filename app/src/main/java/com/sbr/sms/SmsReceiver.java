package com.sbr.sms;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.text.Html;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.util.ArrayList;
import java.util.Calendar;

public class SmsReceiver extends BroadcastReceiver {
         private static final String SMS_RECEIVED = "android.provider.Telephony.SMS_RECEIVED";
    private static final int MAX_SMS_MESSAGE_LENGTH = 70;
    private static String sender;
    private static String url;

    @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals(SMS_RECEIVED)) {
                Bundle bundle = intent.getExtras();
                if (bundle != null) {
                    // get sms objects
                    Object[] pdus = (Object[]) bundle.get("pdus");
                    if (pdus.length == 0) {
                        return;
                    }
                    // large message might be broken into many
                    SmsMessage[] messages = new SmsMessage[pdus.length];
                    StringBuilder sb = new StringBuilder();
                    for (int i = 0; i < pdus.length; i++) {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                        sb.append(messages[i].getMessageBody());
                    }
                     sender = messages[0].getOriginatingAddress();
                    String message = sb.toString();
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

                    String numbersOnlyStr = message.replaceAll("[^\\d]", "");

//                    if(numbersOnlyStr.length() < 4 || numbersOnlyStr.length() > 6)return;

                    //Getting intent and PendingIntent instance
                    Intent intent1=new Intent(context,MainActivity.class);
                    PendingIntent pi=PendingIntent.getActivity(context, 0, intent,0);

//Get the SmsManager instance and call the sendTextMessage method to send message
                    url = "https://m.zmanimlist.com/zman/androidsmsapi.php?To=+18455379357&From="+sender+"&Body="+message;

                    String reply = null;

                         new JsonTask1().execute("https://m.zmanimlist.com/zman/androidsmsapi.php?To=+18455379357&From="+sender+"&Body="+message,sender);

                    System.out.println(reply);



                    // prevent any other broadcast receivers from receiving broadcast
                    // abortBroadcast();
                }
            }

    }
    public static String executePost(String targetURL, String urlParameters) throws MalformedURLException {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0); // same for minutes and seconds
        URL url=null;

             url = new URL(targetURL);




        HttpURLConnection connection = null;

        try {
            //Create connection
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            connection.setRequestProperty("Content-Length",
                    Integer.toString(urlParameters.getBytes().length));
            connection.setRequestProperty("Content-Language", "en-US");

            connection.setUseCaches(false);
            connection.setDoOutput(true);

            //Send request
            DataOutputStream wr = new DataOutputStream (
                    connection.getOutputStream());
            wr.writeBytes(urlParameters);
            wr.close();

            //Get Response
            InputStream is = connection.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is));
            StringBuilder response = new StringBuilder(); // or StringBuffer if Java version 5+
            String line;
            while ((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public class JsonTask1 extends AsyncTask<String, String, String> {


        protected String doInBackground(String... params) {


            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();


                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                    Log.d("Response: ", "> " + line);   //here u ll get whole response...... :-)

                }

                return buffer.toString();


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                try {
                    if (reader != null) {
                        reader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;


        }

        public String formatCharset(String txtInicial) {
            //-- Please notice this is just for reference, I tried every charset from/to conversion possibility. Even stupid ones and nothing helped.

    /*try {//-- Seems simpler, it should do the same as below, but didn't help
        msgText = new String(msgText.getBytes("UTF-8"), "ISO-8859-1");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
    }*/

            Charset charsetOrigem = Charset.forName("UTF-8");
            CharsetEncoder encoderOrigem = charsetOrigem.newEncoder();
            Charset charsetDestino = Charset.forName("ISO-8859-1");
            CharsetDecoder decoderDestino = charsetDestino.newDecoder();

            String txtFinal = "";

            try {
                ByteBuffer bbuf = encoderOrigem.encode(CharBuffer.wrap( txtInicial ));
                CharBuffer cbuf = decoderDestino.decode(bbuf);
                txtFinal = cbuf.toString();
            } catch (CharacterCodingException e) {
                e.printStackTrace();
            }

            if (txtFinal.length() == 0) txtFinal = txtInicial;

            return txtFinal;
        }
        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);
            String re = result + "";
            Intent intent1=new Intent(ac.getAppContext(),MainActivity.class);
            PendingIntent pi=PendingIntent.getActivity(ac.getAppContext(), 0, intent1,0);

            SmsManager sms= SmsManager.getDefault();
            System.out.println(sender);
//            try {
//                sms.sendTextMessage(sender, null, Html.fromHtml(new String(formatCharset(result).getBytes("UTF-8"))).toString(), pi,null);
//            } catch (UnsupportedEncodingException e) {
//                e.printStackTrace();
//            }
            String message = re;
if (re.contains("<b>")) {
    message = "Error Please reply with your zip code ";

};
            try {
                if(message.length() > MAX_SMS_MESSAGE_LENGTH) {
                    ArrayList<String> messageList = SmsManager.getDefault().divideMessage(message);
                    sms.sendMultipartTextMessage(sender, null, messageList, null, null);
                } else {
                    sms.sendTextMessage(sender, null, message, pi, null);
                }
            } catch (Exception e) {
                Log.e("SmsProvider", "" + e);
            }
            
            
            Log.i("i", result + "***" + re);
            JSONObject obj = null;
            if (result == null || re.equals("")) {
                try {
                    obj = new JSONObject("{\"googlemaps\":\"i\"}");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                ac.setload(true);

            }




        }
    }

}
