package co.chatsdk.ui.chat;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.theartofdev.edmodo.cropper.CropImage;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.GlideEngine;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import org.greenrobot.greendao.annotation.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import co.chatsdk.core.session.ChatSDK;
import co.chatsdk.core.utils.ActivityResultPushSubjectHolder;
import co.chatsdk.core.utils.PermissionRequestHandler;
import co.chatsdk.ui.R;
import co.chatsdk.ui.chat.options.MediaType;
import co.chatsdk.ui.utils.Cropper;
import id.zelory.compressor.Compressor;
import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.disposables.Disposable;

import static android.app.Activity.RESULT_OK;

/**
 * Created by benjaminsmiley-andrews on 23/05/2017.
 */

public class MediaSelector {

    public static final int CHOOSE_PHOTO = 100;
    public static final int TAKE_VIDEO = 101;
    public static final int CHOOSE_VIDEO = 102;

    public static final int SELECTION_MAX_SIZE = 5;

    protected Disposable disposable;
    protected SingleEmitter<List<File>> emitter;
    protected int width = ChatSDK.config().imageMaxThumbnailDimension;
    protected int height = ChatSDK.config().imageMaxThumbnailDimension;

    protected CropType cropType = CropType.Rectangle;

    public enum CropType {
        None,
        Rectangle,
        Square,
        Circle,
    }

    public Single<List<File>> startActivity (Activity activity, MediaType type) {
        return startActivity(activity, type, null);
    }

    public Single<List<File>> startActivity (Activity activity, MediaType type, CropType cropType) {

        if (cropType != null) {
            this.cropType = cropType;
        }

        Single<List<File>> single = null;

        if (type.isEqual(MediaType.ChoosePhoto)) {
            single = startChooseMediaActivity(activity, MimeType.ofImage(), this.cropType, true);
        }

        if (type.isEqual(MediaType.TakeVideo)) {
            single = startTakeVideoActivity(activity);
        }
        if (type.isEqual(MediaType.ChooseVideo)) {
            single = startChooseVideoActivity(activity);
        }

        if (single != null) {
            return PermissionRequestHandler.requestImageMessage(activity).andThen(single);
        }
        return Single.error(new Throwable(activity.getString(R.string.error_launching_activity)));
    }

    public Single<List<File>> startChooseMediaActivity(Activity activity, Set<MimeType> mimeTypeSet, CropType cropType, boolean multiSelectEnabled) {
        return startChooseMediaActivity(activity, mimeTypeSet, cropType, multiSelectEnabled, 0, 0);
    }

    public Single<List<File>> startChooseMediaActivity(Activity activity, Set<MimeType> mimeTypeSet, CropType cropType, boolean multiSelectEnabled, int width, int height) {
        return Single.create(emitter -> {

            this.emitter = emitter;
            this.cropType = cropType;

            if (disposable != null) {
                disposable.dispose();
            }

            if (width != 0) {
                this.width = width;
            }
            if (height != 0) {
                this.height = height;
            }

            disposable = ActivityResultPushSubjectHolder.shared().subscribe(activityResult -> {
                handleResult(activity, activityResult.requestCode, activityResult.resultCode, activityResult.data);
            });

            Matisse.from(activity)
                    .choose(mimeTypeSet)
                    .captureStrategy(new CaptureStrategy(false, activity.getPackageName() + ".contentprovider", "images"))
                    .theme(R.style.Matisse_Zhihu)
                    .capture(true)
                    .maxSelectable(multiSelectEnabled ? SELECTION_MAX_SIZE : 1)
                    .thumbnailScale(1)
                    .imageEngine(new GlideEngine())
                    .forResult(CHOOSE_PHOTO);
        });
    }

    public Single<List<File>> startTakeVideoActivity (Activity activity) {
        return Single.create(emitter -> {
            MediaSelector.this.emitter = emitter;

            Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (!startActivityForResult(activity, intent, TAKE_VIDEO)) {
                notifyError(new Exception(activity.getString(R.string.unable_to_start_activity)));
            }
        });
    }

    public Single<List<File>> startChooseVideoActivity (Activity activity) {
        return Single.create(emitter -> {
            MediaSelector.this.emitter = emitter;

            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            if (!startActivityForResult(activity, intent, CHOOSE_VIDEO)) {
                notifyError(new Exception(activity.getString(R.string.unable_to_start_activity)));
            }
        });
    }

    protected boolean startActivityForResult (Activity activity, Intent intent, int tag) {
        if (disposable == null && intent.resolveActivity(activity.getPackageManager()) != null) {
            disposable = ActivityResultPushSubjectHolder.shared().subscribe(activityResult -> handleResult(activity, activityResult.requestCode, activityResult.resultCode, activityResult.data));
            activity.startActivityForResult(intent, tag);
            return true;
        }
        return false;
    }

    protected void processPickedImage(Activity activity, List<Uri> uris) throws Exception {
        if (!ChatSDK.config().imageCroppingEnabled || cropType == CropType.None || uris.size() > 1) {

            ArrayList<File> files = new ArrayList<File>();
            for (Uri uri: uris) {
                File imageFile = fileFromURI(uri, activity, MediaStore.Images.Media.DATA);
                if (imageFile != null) {
                    File compressed = new Compressor(ChatSDK.shared().context())
                            .setMaxHeight(width)
                            .setMaxWidth(height)
                            .compressToFile(imageFile);
                    files.add(compressed);
                }
            }
            // New
            handleImageFiles(activity, files);
        }
        else {
            Uri uri = uris.get(0);
            if (cropType == CropType.Circle) {
                Cropper.startCircleActivity(activity, uri);
            }
            else if (cropType == CropType.Square) {
                Cropper.startSquareActivity(activity, uri);
            }
            else {
                Cropper.startActivity(activity, uri);
            }
        }
    }

    public static File fileFromURI (Uri uri, Activity activity, String column) {
        File file = null;
        if (uri.getPath() != null) {
            file = new File(uri.getPath());
        }
        if (file != null && file.length() > 0) {
            return file;
        }
        else {
            // Try to get it another way for this kind of URL
            // content://media/external ...
            String [] filePathColumn = { column };
            Cursor cursor = activity.getContentResolver().query(uri, filePathColumn,null, null, null);
            if (cursor != null) {
                cursor.moveToFirst();
                String fileURI = cursor.getString(cursor.getColumnIndex(filePathColumn[0]));
                return new File(fileURI);
            }
        }
        return null;
    }


    protected void processCroppedPhoto(Activity activity, int resultCode, Intent data) throws Exception {

        CropImage.ActivityResult result = CropImage.getActivityResult(data);

        if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE || resultCode == Activity.RESULT_CANCELED) {
            throw new Exception(result.getError());
        }
        else if (resultCode == RESULT_OK) {
            try {
                handleImageFiles(activity, new File(result.getUri().getPath()));
            }
            catch (NullPointerException e){
                notifyError(new Exception(activity.getString(R.string.unable_to_fetch_image)));
            }
        }

    }

    public void handleImageFiles (Activity activity, File... files) {
        handleImageFiles(activity, Arrays.asList(files));
    }

    public void handleImageFiles (Activity activity, List<File> files) {

        // Scanning the messageImageView so it would be visible in the gallery images.
        if (ChatSDK.config().saveImagesToDirectory) {
            for (File file: files) {
                ChatSDK.shared().fileManager().addFileToGallery(file);
            }
        }
        notifySuccess(files);
    }

    public void handleResult (Activity activity, int requestCode, int resultCode, Intent intent) throws Exception {

        if (resultCode == RESULT_OK) {
            if (requestCode == CHOOSE_PHOTO) {
                List<Uri> result = Matisse.obtainResult(intent);
                processPickedImage(activity, result);
            }
            else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                processCroppedPhoto(activity, resultCode, intent);
            }
            else if (requestCode == TAKE_VIDEO || requestCode == CHOOSE_VIDEO) {
                Uri videoUri = intent.getData();
                notifySuccess(fileFromURI(videoUri, activity, MediaStore.Video.Media.DATA));
            }
            else {
                notifyError(new Exception(activity.getString(R.string.error_processing_image)));
            }
        } else {
            notifyError(new Exception(""));
        }
    }

    protected void notifySuccess (@NotNull File... file) {
        notifySuccess(Arrays.asList(file));
    }

    protected void notifySuccess (@NotNull List<File> files) {
        if (emitter != null) {
            emitter.onSuccess(files);
        }
        clear();
    }

    protected void notifyError (@NotNull Throwable throwable) {
        if (emitter != null) {
            emitter.onError(throwable);
        }
        clear();
    }

    public void clear () {
        emitter = null;
        disposable.dispose();
        disposable = null;
    }
}
