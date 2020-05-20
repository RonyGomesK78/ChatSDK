package firestream.chat.interfaces;

import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import firestream.chat.Config;
import firestream.chat.chat.Chat;
import firestream.chat.chat.User;
import firestream.chat.events.ConnectionEvent;
import firestream.chat.events.Event;
import firestream.chat.firebase.rx.MultiQueueSubject;
import firestream.chat.firebase.service.Paths;
import firestream.chat.message.Message;
import firestream.chat.message.Sendable;
import firestream.chat.types.ContactType;
import firestream.chat.types.DeliveryReceiptType;
import firestream.chat.types.InvitationType;
import firestream.chat.types.PresenceType;
import firestream.chat.types.TypingStateType;
import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Predicate;

public interface IFireStream extends IAbstractChat {

    void initialize(Context context, @Nullable Config config);
    void initialize(Context context);
    boolean isInitialized();

    /**
     * @return authenticated user
     */
    User currentUser();

    /**
     * @return id of authenticated user
     */
    String currentUserId();

    // Messages

    /**
     * Send a delivery receipt to a user. If delivery receipts are enabled,
     * a 'received' status will be returned as soon as a message type delivered
     * and then you can then manually send a 'read' status when the user
     * actually reads the message
     * @param userId - the recipient user id
     * @param type - the status getTypingStateType
     * @return - subscribe to get a completion, error update from the method
     */
    Completable sendDeliveryReceipt(String userId, DeliveryReceiptType type, String messageId);
    Completable sendDeliveryReceipt(String userId, DeliveryReceiptType type, String messageId, @Nullable Consumer<String> newId);

    Completable sendInvitation(String userId, InvitationType type, String id);
    Completable sendInvitation(String userId, InvitationType type, String groupId, @Nullable Consumer<String> newId);

    Completable send(String toUserId, Sendable sendable);
    Completable send(String toUserId, Sendable sendable, @Nullable Consumer<String> newId);

    /**
     * Messages can always be deleted locally. Messages can only be deleted remotely
     * for recent messages. Specifically, when the client connects, it will add a
     * message listener to get an update for "new" messages. By default, we listen
     * to messages that were added after we last sent a message or a received delivery
     * receipt. This is the dateOfLastDeliveryReceipt. A client will only pick up
     * remote delivery receipts if the date of delivery is after this date.
     * @param sendable to be deleted
     * @return completion
     */
    Completable deleteSendable (Sendable sendable);
    Completable deleteSendable (String sendableId);
    Completable sendPresence(String userId, PresenceType type);
    Completable sendPresence(String userId, PresenceType type, @Nullable Consumer<String> newId);

    Completable sendMessageWithText(String userId, String text);
    Completable sendMessageWithText(String userId, String text, @Nullable Consumer<String> newId);

    Completable sendMessageWithBody(String userId, HashMap<String, Object> body);
    Completable sendMessageWithBody(String userId, HashMap<String, Object> body, @Nullable Consumer<String> newId);


    /**
     * Send a typing indicator update to a user. This should be sent when the user
     * starts or stops typing
     * @param userId - the recipient user id
     * @param type - the status getTypingStateType
     * @return - subscribe to get a completion, error update from the method
     */
    Completable sendTypingIndicator(String userId, TypingStateType type);
    Completable sendTypingIndicator(String userId, TypingStateType type, @Nullable Consumer<String> newId);

    // Blocked

    Completable block(User user);
    Completable unblock(User user);
    ArrayList<User> getBlocked();
    boolean isBlocked(User user);

    // Contacts

    Completable addContact(User user, ContactType type);
    Completable removeContact(User user);
    ArrayList<User> getContacts();

    // Chats

    Single<Chat> createChat(@Nullable String name, @Nullable String imageURL, User... users);
    Single<Chat> createChat(@Nullable String name, @Nullable String imageURL, @Nullable HashMap<String, Object> customData, User... users);
    Single<Chat> createChat(@Nullable String name, @Nullable String imageURL, List<? extends User> users);
    Single<Chat> createChat(@Nullable String name, @Nullable String imageURL, @Nullable HashMap<String, Object> customData, List<? extends User> users);

    /**
     * Leave the chat. When you leave, you will be removed from the
     * chat's roster
     * @param chat to leave
     * @return completion
     */
    Completable leaveChat(IChat chat);

    /**
     * Join the chat. To join you must already be in the chat roster
     * @param chat to join
     * @return completion
     */
    Completable joinChat(IChat chat);

    IChat getChat(String chatId);
    List<IChat> getChats();

    // Events

    MultiQueueSubject<Event<Chat>> getChatEvents();
    MultiQueueSubject<Event<User>> getBlockedEvents();
    MultiQueueSubject<Event<User>> getContactEvents();
    Observable<ConnectionEvent> getConnectionEvents();

    Completable markReceived(String fromUserId, String sendableId);
    Completable markRead(String fromUserId, String sendableId);

    /**
     * If you set the
     * @param filter
     */
    void setMarkReceivedFilter(Predicate<Event<? extends Sendable>> filter);

    /**
     * Mute notifications for a user
     * @param user to mute
     * @return completion
     */
    Completable mute(User user);

    /**
     * Mute notifications until a future date
     * @param user to mute
     * @param until to mute until
     * @return completion
     */
    Completable mute(User user, Date until);

    /**
     * Unmute notifications for a user
     * @param user to unmute
     * @return completion
     */
    Completable unmute(User user);

    /**
     * Use this method to find out if the user is muted and until when
     * @param user to check
     * @return date or null if not muted
     */
    Date mutedUntil(User user);

    /**
     * Is a user muted?
     * @param user to mute
     * @return true / false
     */
    boolean muted(User user);
}
