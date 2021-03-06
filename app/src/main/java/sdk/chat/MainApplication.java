package sdk.chat;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.multidex.MultiDex;
import androidx.multidex.MultiDexApplication;

import com.google.android.exoplayer2.upstream.LoaderErrorThrower;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.PhoneAuthProvider;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageHolders;

import org.pmw.tinylog.Level;
import org.pmw.tinylog.Logger;

import cafe.adriel.androidaudioconverter.AndroidAudioConverter;
import co.chatsdk.android.app.R;
import co.chatsdk.core.avatar.gravatar.GravatarAvatarGenerator;
import co.chatsdk.core.dao.Message;
import co.chatsdk.core.events.NetworkEvent;
import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.session.Configuration;
import co.chatsdk.core.types.MessageType;
import co.chatsdk.firebase.FirebaseNetworkAdapter;
import co.chatsdk.firebase.file_storage.FirebaseFileStorageModule;
import co.chatsdk.firebase.push.FirebasePushModule;
import co.chatsdk.firebase.ui.FirebaseUIModule;
import co.chatsdk.firestream.FireStreamNetworkAdapter;
import co.chatsdk.profile.pictures.ProfilePicturesModule;
import co.chatsdk.ui.BaseInterfaceAdapter;
import co.chatsdk.ui.activities.LoginActivity;
import firestream.chat.Config;
import firestream.chat.FireStream;
import firestream.chat.events.EventType;
import firestream.chat.namespace.Fire;
import firestream.chat.test.TestScript;
import io.reactivex.disposables.Disposable;
import sdk.chat.test.DummyData;

/**
 * Created by Ben Smiley on 6/8/2014.
 */
public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

       String rootPath = "micro_test_tester03";
       // TestScript.run(this,rootPath);
        try {

            Config firestreamConfig = new Config();
            try {
                firestreamConfig.setRoot(rootPath);
                firestreamConfig.startListeningFromLastSentMessageDate = false;
                firestreamConfig.listenToMessagesWithTimeAgo = Config.TimePeriod.days(7);
                firestreamConfig.database = Config.DatabaseType.Realtime;
                firestreamConfig.deleteMessagesOnReceipt = false;
                firestreamConfig.deliveryReceiptsEnabled = false;
            } catch (Exception e) {
                e.printStackTrace();
            }

            Fire.stream().initialize(this, firestreamConfig);
           // ChatSDK.initialize(this, config, FireStreamNetworkAdapter.class, BaseInterfaceAdapter.class);

            Logger.getConfiguration().level(Level.DEBUG).activate();


        }
        catch (Exception e) {
            e.printStackTrace();
            Logger.debug("Error");
        }

    }

}
