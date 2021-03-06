package firestream.chat.firebase.firestore;

import androidx.annotation.Nullable;

import com.google.firebase.database.annotations.NotNull;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import firestream.chat.firebase.rx.Optional;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Single;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class RXFirestore implements Action {

    protected ListenerRegistration lr;

    public Observable<DocumentSnapshot> on(DocumentReference ref) {
        return Observable.create((ObservableOnSubscribe<DocumentSnapshot>) emitter -> {
            lr = ref.addSnapshotListener((snapshot, e) -> {
                if (e != null) {
                    emitter.onError(e);
                } else if (snapshot != null) {
                    emitter.onNext(snapshot);
                }
            });
        }).doOnDispose(this).subscribeOn(Schedulers.io());
    }

    public Observable<DocumentChange> on(@NotNull Query ref) {
        return Observable.create((ObservableOnSubscribe<DocumentChange>) emitter -> {
            lr = ref.addSnapshotListener((snapshot, e) -> {
                if (snapshot != null) {
                    for (DocumentChange dc : snapshot.getDocumentChanges()) {
                        emitter.onNext(dc);
                    }
                }
            });
        }).doOnDispose(this).subscribeOn(Schedulers.io());
    }

    public Single<String> add(CollectionReference ref, Object data) {
        return add(ref, data, null);
    }

    public Single<String> add(CollectionReference ref, Object data, @Nullable Consumer<String> newId) {
        return Single.create((SingleOnSubscribe<String>) emitter -> {
            final DocumentReference docRef = ref.document();
            if (newId != null) {
                newId.accept(docRef.getId());
            }
            docRef.set(data).addOnSuccessListener(aVoid -> emitter.onSuccess(docRef.getId())).addOnFailureListener(emitter::onError);
        }).subscribeOn(Schedulers.io());
    }

    public Completable delete(DocumentReference ref) {
        return Completable.create(emitter -> ref.delete()
                .addOnSuccessListener(aVoid -> emitter.onComplete())
                .addOnFailureListener(emitter::onError))
                .subscribeOn(Schedulers.io());
    }

    public Completable set(DocumentReference ref, Object data) {
        return Completable.create(emitter -> ref.set(data)
                .addOnSuccessListener(aVoid -> emitter.onComplete())
                .addOnFailureListener(emitter::onError))
                .subscribeOn(Schedulers.io());
    }

    public Completable update(DocumentReference ref, HashMap<String, Object> data) {
        return Completable.create(emitter -> ref.update(data)
                .addOnSuccessListener(aVoid -> emitter.onComplete())
                .addOnFailureListener(emitter::onError))
                .subscribeOn(Schedulers.io());
    }

    public Single<Optional<QuerySnapshot>> get(Query ref) {
        return Single.create((SingleOnSubscribe<Optional<QuerySnapshot>>) emitter -> ref.get().addOnSuccessListener(snapshots -> {
            if (snapshots != null) {
                emitter.onSuccess(new Optional<>(snapshots));
            } else {
                emitter.onSuccess(new Optional<>());
            }
        }).addOnFailureListener(emitter::onError))
                .subscribeOn(Schedulers.io());
    }

    public Single<Optional<DocumentSnapshot>> get(DocumentReference ref) {
        return Single.create((SingleOnSubscribe<Optional<DocumentSnapshot>>)emitter -> ref.get().addOnSuccessListener(snapshot -> {
            if (snapshot != null && snapshot.exists() && snapshot.getData() != null) {
                emitter.onSuccess(new Optional<>(snapshot));
            } else {
                emitter.onSuccess(new Optional<>());
            }
        }).addOnFailureListener(emitter::onError))
                .subscribeOn(Schedulers.io());
    }

    @Override
    public void run() throws Exception {
        if (lr != null) {
            lr.remove();
        }
    }
}
