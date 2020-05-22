package sdk.chat;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.core.view.View;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.chatsdk.android.app.R;
import firestream.chat.events.EventType;
import firestream.chat.namespace.Fire;
import io.reactivex.disposables.Disposable;

public class MainActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 123;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private TextView userId;
    private EditText recipientId;
    private EditText message;
    private Button sendBtn;

    List<AuthUI.IdpConfig> providers = Arrays.asList(
            new AuthUI.IdpConfig.EmailBuilder().build(),
            new AuthUI.IdpConfig.GoogleBuilder().build()
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userId = (TextView) findViewById(R.id.userId);
        recipientId = (EditText) findViewById(R.id.recipientId);
        message = (EditText) findViewById(R.id.message);
        sendBtn = (Button) findViewById(R.id.sendBtn);

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user!=null){

                    userId.setText(user.getUid());
                }
                else{
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(providers)
                                    .build(),
                            RC_SIGN_IN);
                }
            }

        };

        Disposable d = Fire.stream().getSendableEvents().getMessages().subscribe(messageEvent -> {
            if(messageEvent.typeIs(EventType.Added)){
                new AlertDialog.Builder(this).setTitle("New Message").setMessage(messageEvent.get().toTextMessage().getText()).show();
            }
        });

    }

    @Override
    protected void onResume() {

        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause(){

        super.onPause();
        firebaseAuth.removeAuthStateListener(authStateListener);
    }

    public void signOut(android.view.View view) {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        finish();
                    }
                });
    }

    public void sendMsg(android.view.View view) {

       Fire.stream().sendMessageWithText(recipientId.getText().toString(), message.getText().toString()).subscribe(()->{
           //handle sucess
       }, throwable ->{
           //handle error
       });

    }
}
