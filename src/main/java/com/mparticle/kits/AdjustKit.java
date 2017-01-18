package com.mparticle.kits;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.adjust.sdk.Adjust;
import com.adjust.sdk.AdjustConfig;
import com.adjust.sdk.AdjustReferrerReceiver;
import com.adjust.sdk.LogLevel;
import com.mparticle.MParticle;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p/>
 * Embedded implementation of the Adjust SDK
 * <p/>
 */
public class AdjustKit extends KitIntegration implements KitIntegration.ActivityListener{

    private static final String APP_TOKEN = "appToken";

    @Override
    public Object getInstance() {
        return Adjust.getDefaultInstance();
    }

    @Override
    public String getName() {
        return "Adjust";
    }

    @Override
    protected List<ReportingMessage> onKitCreate(Map<String, String> settings, Context context) {
        boolean production = MParticle.Environment.Production.equals(MParticle.getInstance().getEnvironment());

        AdjustConfig config = new AdjustConfig(getContext(),
                getSettings().get(APP_TOKEN),
                production ? AdjustConfig.ENVIRONMENT_PRODUCTION : AdjustConfig.ENVIRONMENT_SANDBOX);

        if (!production){
            config.setLogLevel(LogLevel.VERBOSE);
        }
        config.setEventBufferingEnabled(false);
        Adjust.onCreate(config);
        return null;
    }

    @Override
    public void setInstallReferrer(Intent intent) {
        new AdjustReferrerReceiver().onReceive(getContext(), intent);
    }

    @Override
    public List<ReportingMessage> onActivityCreated(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityStarted(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityResumed(Activity activity) {
        Adjust.onResume();
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
            new ReportingMessage(this, ReportingMessage.MessageType.APP_STATE_TRANSITION, System.currentTimeMillis(), null)
        );
        return messageList;
    }

    @Override
    public List<ReportingMessage> onActivityPaused(Activity activity) {
        Adjust.onPause();
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.APP_STATE_TRANSITION, System.currentTimeMillis(), null)
        );
        return messageList;
    }

    @Override
    public List<ReportingMessage> onActivityStopped(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        return null;
    }

    @Override
    public List<ReportingMessage> onActivityDestroyed(Activity activity) {
        return null;
    }

    @Override
    public List<ReportingMessage> setOptOut(boolean optOutStatus) {
        Adjust.setEnabled(!optOutStatus);
        List<ReportingMessage> messageList = new LinkedList<ReportingMessage>();
        messageList.add(
                new ReportingMessage(this, ReportingMessage.MessageType.OPT_OUT, System.currentTimeMillis(), null)
                .setOptOut(optOutStatus)
        );
        return messageList;
    }
}