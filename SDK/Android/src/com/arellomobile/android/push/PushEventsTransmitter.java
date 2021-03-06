//
//  PushEventsTransmitter.java
//
// Pushwoosh Push Notifications SDK
// www.pushwoosh.com
//
// MIT Licensed

package com.arellomobile.android.push;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.arellomobile.android.push.utils.GeneralUtils;

public class PushEventsTransmitter
{
	private static boolean useBroadcast = false;
	
    private static void transmit(final Context context, String stringToShow, String messageKey)
    {
    	transmit(context, stringToShow, messageKey, null);
    }

    private static void transmit(final Context context, String stringToShow, String messageKey, Bundle pushBundle)
    {
        Intent notifyIntent = new Intent(context, MessageActivity.class);
        
        if(pushBundle != null)
        	notifyIntent.putExtras(pushBundle);
        
        notifyIntent.putExtra(messageKey, stringToShow);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(notifyIntent);
    }

	static void onRegistered(final Context context, String registrationId)
	{
		if(useBroadcast)
		{
			String alertString = "Registered. RegistrationId is " + registrationId;
			transmitBroadcast(context, registrationId, PushManager.REGISTER_EVENT);
		}
		else
		{
			transmit(context, registrationId, PushManager.REGISTER_EVENT);
		}
	}

	static void onRegisterError(final Context context, String errorId)
	{
		String alertString = "Register error. Error message is " + errorId;
		transmit(context, errorId, PushManager.REGISTER_ERROR_EVENT);
	}

	private static void transmitBroadcast(Context context, String registrationId, String registerEvent)
	{
		Intent intent = new Intent(PushManager.REGISTER_BROAD_CAST_ACTION);
		intent.putExtra(registerEvent, registrationId);
		
		String packageName = context.getPackageName();
		intent.setPackage(packageName);

		if (GeneralUtils.checkStickyBroadcastPermissions(context))
		{
			context.sendStickyBroadcast(intent);
		}
		else
		{
			Log.w(PushEventsTransmitter.class.getSimpleName(), "No android.permission.BROADCAST_STICKY. Reverting to simple broadcast");
			context.sendBroadcast(intent);
		}
	}

    static void onUnregistered(final Context context, String registrationId)
    {
        //String alertString = "Unregistered. RegistrationId is " + registrationId;
        transmit(context, registrationId, PushManager.UNREGISTER_EVENT);
    }

    static void onUnregisteredError(Context context, String errorId)
    {
        transmit(context, errorId, PushManager.UNREGISTER_ERROR_EVENT);
    }

    static void onMessageReceive(final Context context, String message)
    {
    	onMessageReceive(context, message, null);
    }
    
    static void onMessageReceive(final Context context, String message, Bundle pushBundle)
    {
        transmit(context, message, PushManager.PUSH_RECEIVE_EVENT, pushBundle);
    }
}
