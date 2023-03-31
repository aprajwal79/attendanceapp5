package com.nitap.attende;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Size;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.ttv.face.FaceEngine;
import com.ttv.face.FaceFeature;
import com.ttv.face.FaceFeatureInfo;
import com.ttv.face.FaceResult;
import com.ttv.face.SearchResult;
import com.ttv.facerecog.DBHelper;
import com.ttv.facerecog.FaceEntity;
import com.ttv.facerecog.FaceRectTransformer;
import com.ttv.facerecog.FaceRectView;
import com.ttv.facerecog.MainActivity;
import com.ttv.facerecog.PermissionsDelegate;
import com.ttv.facerecog.R;

import io.fotoapparat.Fotoapparat;
import io.fotoapparat.FotoapparatBuilder;
import io.fotoapparat.preview.Frame;
import io.fotoapparat.selector.LensPositionSelectorsKt;
import io.fotoapparat.view.CameraRenderer;
import io.fotoapparat.view.CameraView;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import kotlin.Metadata;
import kotlin.Unit;
import kotlin.jvm.functions.Function1;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
        mv = {1, 5, 1},
        k = 1,
        d1 = {"\u0000|\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000b\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\t\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0003\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0006\n\u0002\u0010\u0000\n\u0002\b\u0003\u0018\u00002\u00020\u0001:\u0002./B\u0005¢\u0006\u0002\u0010\u0002J\u001e\u0010\u0019\u001a\u00020\f2\u0006\u0010\u001a\u001a\u00020\u001b2\u0006\u0010\u001c\u001a\u00020\u001b2\u0006\u0010\u001d\u001a\u00020\u001bJ\u001a\u0010\u001e\u001a\u0004\u0018\u00010\u001f2\u0006\u0010 \u001a\u00020!2\u0006\u0010\"\u001a\u00020\u0016H\u0002J\u0012\u0010#\u001a\u00020$2\b\u0010%\u001a\u0004\u0018\u00010&H\u0014J\b\u0010'\u001a\u00020$H\u0014J\b\u0010(\u001a\u00020$H\u0014J\b\u0010)\u001a\u00020$H\u0014J\u0018\u0010*\u001a\u00020$2\u0006\u0010+\u001a\u00020\u001b2\u0006\u0010,\u001a\u00020-H\u0002R\u0010\u0010\u0003\u001a\u0004\u0018\u00010\u0004X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0005\u001a\u0004\u0018\u00010\u0006X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0007\u001a\u0004\u0018\u00010\bX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\t\u001a\u0004\u0018\u00010\nX\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u000b\u001a\u00020\fX\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\r\u001a\u00020\u000e8\u0002X\u0083\u0004¢\u0006\u0002\n\u0000R\u0010\u0010\u000f\u001a\u0004\u0018\u00010\u0010X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0011\u001a\u00020\u0012X\u0082\u0004¢\u0006\u0002\n\u0000R\u000e\u0010\u0013\u001a\u00020\u0014X\u0082\u000e¢\u0006\u0002\n\u0000R\u0010\u0010\u0015\u001a\u0004\u0018\u00010\u0016X\u0082\u000e¢\u0006\u0002\n\u0000R\u000e\u0010\u0017\u001a\u00020\u0018X\u0082\u000e¢\u0006\u0002\n\u0000¨\u00060"},
        d2 = {"Lcom/ttv/facerecog/CameraActivity;", "Landroidx/appcompat/app/AppCompatActivity;", "()V", "appCtx", "Landroid/content/Context;", "cameraView", "Lio/fotoapparat/view/CameraView;", "faceRectTransformer", "Lcom/ttv/facerecog/FaceRectTransformer;", "frontFotoapparat", "Lio/fotoapparat/Fotoapparat;", "hasPermission", "", "mHandler", "Landroid/os/Handler;", "mydb", "Lcom/ttv/facerecog/DBHelper;", "permissionsDelegate", "Lcom/ttv/facerecog/PermissionsDelegate;", "recogName", "", "rectanglesView", "Lcom/ttv/facerecog/FaceRectView;", "startVerifyTime", "", "adjustPreview", "frameWidth", "", "frameHeight", "rotation", "adjustPreviewViewSize", "Landroid/view/ViewGroup$LayoutParams;", "previewView", "Landroid/view/View;", "faceRectView", "onCreate", "", "savedInstanceState", "Landroid/os/Bundle;", "onResume", "onStart", "onStop", "sendMessage", "w", "o", "", "FaceRecognizeRunnable", "SampleFrameProcessor", "app_debug"}
)
public final class RecognitionActivity extends AppCompatActivity {
    private final PermissionsDelegate permissionsDelegate = new PermissionsDelegate((Activity)this);
    private boolean hasPermission;
    private Context appCtx;
    private CameraView cameraView;
    private FaceRectView rectanglesView;
    private FaceRectTransformer faceRectTransformer;
    private Fotoapparat frontFotoapparat;
    private long startVerifyTime;
    private DBHelper mydb;
    private String recogName = "";
    void display(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    @SuppressLint({"HandlerLeak"})
    private final Handler mHandler = (Handler)(new Handler() {
        public void handleMessage(@NotNull Message msg) {
            Intrinsics.checkNotNullParameter(msg, "msg");
            int i = msg.what;
            Object var10000;
            if (i == 0) {
                ArrayList drawInfoList = new ArrayList();
                var10000 = msg.obj;
                if (var10000 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.collections.ArrayList<com.ttv.face.FaceResult> /* = java.util.ArrayList<com.ttv.face.FaceResult> */");
                }

                ArrayList detectionResult = (ArrayList)var10000;

                FaceRectView.DrawInfo drawInfo;
                for(Iterator var6 = detectionResult.iterator(); var6.hasNext(); drawInfoList.add(drawInfo)) {
                    FaceResult faceResult = (FaceResult)var6.next();
                    FaceRectTransformer var11 = faceRectTransformer;
                    Intrinsics.checkNotNull(var11);
                    Rect var12 = var11.adjustRect(faceResult.rect);
                    Intrinsics.checkNotNullExpressionValue(var12, "faceRectTransformer!!.adjustRect(faceResult.rect)");
                    Rect rect = var12;
                    drawInfo = null;
                    if (faceResult.liveness == 1) {
                        drawInfo = new FaceRectView.DrawInfo(rect, 0, 0, 1, -16711936, (String)null);
                    } else {
                        drawInfo = new FaceRectView.DrawInfo(rect, 0, 0, 0, -65536, (String)null);
                    }
                }

                FaceRectView var13 = rectanglesView;
                Intrinsics.checkNotNull(var13);
                var13.clearFaceInfo();
                var13 = rectanglesView;
                Intrinsics.checkNotNull(var13);
                var13.addFaceInfo((List)drawInfoList);
            } else if (i == 1) {
                var10000 = msg.obj;
                if (var10000 == null) {
                    throw new NullPointerException("null cannot be cast to non-null type kotlin.Int");
                }

                int verifyResult = (Integer)var10000;
                Intent intent = new Intent();
                intent.putExtra("verifyResult", verifyResult);
                intent.putExtra("verifyName", recogName);
                setResult(-1, intent);

                finish();
            }

        }
    });

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_camera);
         this.appCtx = this.getApplicationContext();
        mydb = new DBHelper(appCtx);

        display("Recognition Activity");
        this.cameraView = findViewById(R.id.camera_view);
        //this.appCtx = com.nitap.attende.MainActivity.context;
        View var10001 = this.findViewById(R.id.camera_view);
        if (var10001 == null) {
            throw new NullPointerException("null cannot be cast to non-null type io.fotoapparat.view.CameraView");
        } else {
            display("A");
            this.cameraView = (CameraView)var10001;
            var10001 = this.findViewById(R.id.rectanglesView);
            if (var10001 == null) {

                throw new NullPointerException("null cannot be cast to non-null type com.ttv.facerecog.FaceRectView");
            } else {
                display("B");
                this.rectanglesView = (FaceRectView)var10001;
                this.mydb = new DBHelper(this.appCtx);
                this.hasPermission = this.permissionsDelegate.hasPermissions();
                if (this.hasPermission) {
                    display("C");
                    CameraView var10000 = this.cameraView;
                   // CameraView var10000 = findViewById(R.id.camera_view);
                    Intrinsics.checkNotNull(var10000);

                    var10000.setVisibility(View.VISIBLE);
                    display("D");
                } else {
                    this.permissionsDelegate.requestPermissions();
                }

                FotoapparatBuilder var2 = Fotoapparat.Companion.with((Context)appCtx);
                CameraView var10002 = this.cameraView;
                Intrinsics.checkNotNull(var10002);
                this.frontFotoapparat = var2.into((CameraRenderer)var10002).lensPosition(LensPositionSelectorsKt.front()).frameProcessor((Function1)(new SampleFrameProcessor())).previewResolution((Function1)null).build();
                display("E");
            }
        }
    }

    protected void onStart() {
        super.onStart();
        if (this.hasPermission) {
            Fotoapparat var10000 = this.frontFotoapparat;
            Intrinsics.checkNotNull(var10000);
            var10000.start();
        }

    }

    protected void onStop() {
        super.onStop();
        if (this.hasPermission) {
            try {
                Fotoapparat var10000 = this.frontFotoapparat;
                Intrinsics.checkNotNull(var10000);
                var10000.stop();
            } catch (Exception var2) {
                var2.printStackTrace();
            }
        }

    }

    protected void onResume() {
        super.onResume();
        if (this.permissionsDelegate.hasPermissions() && !this.hasPermission) {
            this.hasPermission = true;
            CameraView var10000 = this.cameraView;
            Intrinsics.checkNotNull(var10000);
            var10000.setVisibility(View.VISIBLE);
            Fotoapparat var1 = this.frontFotoapparat;
            Intrinsics.checkNotNull(var1);
            var1.start();
        } else {
            this.permissionsDelegate.requestPermissions();
        }

    }

    public final boolean adjustPreview(int frameWidth, int frameHeight, int rotation) {
        if (this.faceRectTransformer == null) {
            Size frameSize = new Size(frameWidth, frameHeight);
            CameraView var10000 = this.cameraView;
            Intrinsics.checkNotNull(var10000);
            if (var10000.getMeasuredWidth() == 0) {
                return false;
            } else {
                CameraView var10001 = this.cameraView;
                Intrinsics.checkNotNull(var10001);
                View var6 = (View)var10001;
                FaceRectView var10002 = this.rectanglesView;
                Intrinsics.checkNotNull(var10002);
                this.adjustPreviewViewSize(var6, var10002);
                int var10003 = frameSize.getWidth();
                int var10004 = frameSize.getHeight();
                CameraView var10005 = this.cameraView;
                Intrinsics.checkNotNull(var10005);
                int var5 = var10005.getLayoutParams().width;
                CameraView var10006 = this.cameraView;
                Intrinsics.checkNotNull(var10006);
                this.faceRectTransformer = new FaceRectTransformer(var10003, var10004, var5, var10006.getLayoutParams().height, rotation, 0, false, false, false);
                return true;
            }
        } else {
            return true;
        }
    }

    private final ViewGroup.LayoutParams adjustPreviewViewSize(View previewView, FaceRectView faceRectView) {
        ViewGroup.LayoutParams layoutParams = previewView.getLayoutParams();
        int measuredWidth = previewView.getMeasuredWidth();
        int measuredHeight = previewView.getMeasuredHeight();
        layoutParams.width = measuredWidth;
        layoutParams.height = measuredHeight;
        faceRectView.getLayoutParams().width = measuredWidth;
        faceRectView.getLayoutParams().height = measuredHeight;
        return layoutParams;
    }

    private final void sendMessage(int w, Object o) {
        Message message = new Message();
        message.what = w;
        message.obj = o;
        this.mHandler.sendMessage(message);
    }

    // $FF: synthetic method
    public static final void access$setAppCtx$p(RecognitionActivity $this, Context var1) {
        $this.appCtx = var1;
    }

    // $FF: synthetic method
    public static final void access$setFaceRectTransformer$p(RecognitionActivity $this, FaceRectTransformer var1) {
        $this.faceRectTransformer = var1;
    }

    // $FF: synthetic method
    public static final void access$setRectanglesView$p(RecognitionActivity $this, FaceRectView var1) {
        $this.rectanglesView = var1;
    }

    @Metadata(
            mv = {1, 5, 1},
            k = 1,
            d1 = {"\u00004\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0018\u0002\n\u0002\b\u0005\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0006\b\u0086\u0004\u0018\u00002!\u0012\u0013\u0012\u00110\u0002¢\u0006\f\b\u0003\u0012\b\b\u0004\u0012\u0004\b\b(\u0005\u0012\u0004\u0012\u00020\u00060\u0001j\u0002`\u0007B\u0005¢\u0006\u0002\u0010\bJ\u0011\u0010\u0016\u001a\u00020\u00062\u0006\u0010\u0005\u001a\u00020\u0002H\u0096\u0002R\u001c\u0010\t\u001a\u0004\u0018\u00010\nX\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u000b\u0010\f\"\u0004\b\r\u0010\u000eR\"\u0010\u000f\u001a\n\u0012\u0004\u0012\u00020\u0011\u0018\u00010\u0010X\u0086\u000e¢\u0006\u000e\n\u0000\u001a\u0004\b\u0012\u0010\u0013\"\u0004\b\u0014\u0010\u0015¨\u0006\u0017"},
            d2 = {"Lcom/ttv/facerecog/CameraActivity$SampleFrameProcessor;", "Lkotlin/Function1;", "Lio/fotoapparat/preview/Frame;", "Lkotlin/ParameterName;", "name", "frame", "", "Lio/fotoapparat/util/FrameProcessor;", "(Lcom/ttv/facerecog/CameraActivity;)V", "frExecutor", "Ljava/util/concurrent/ExecutorService;", "getFrExecutor", "()Ljava/util/concurrent/ExecutorService;", "setFrExecutor", "(Ljava/util/concurrent/ExecutorService;)V", "frThreadQueue", "Ljava/util/concurrent/LinkedBlockingQueue;", "Ljava/lang/Runnable;", "getFrThreadQueue", "()Ljava/util/concurrent/LinkedBlockingQueue;", "setFrThreadQueue", "(Ljava/util/concurrent/LinkedBlockingQueue;)V", "invoke", "app_debug"}
    )
    public final class SampleFrameProcessor implements Function1 {
        @Nullable
        private LinkedBlockingQueue frThreadQueue = new LinkedBlockingQueue(1);
        @Nullable
        private ExecutorService frExecutor;

        @Nullable
        public final LinkedBlockingQueue getFrThreadQueue() {
            return this.frThreadQueue;
        }

        public final void setFrThreadQueue(@Nullable LinkedBlockingQueue var1) {
            this.frThreadQueue = var1;
        }

        @Nullable
        public final ExecutorService getFrExecutor() {
            return this.frExecutor;
        }

        public final void setFrExecutor(@Nullable ExecutorService var1) {
            this.frExecutor = var1;
        }

        public void invoke(@NotNull Frame frame) {
            Intrinsics.checkNotNullParameter(frame, "frame");
            List var10000 = FaceEngine.getInstance(appCtx).detectFace(frame.getImage(), frame.getSize().width, frame.getSize().height);
            Intrinsics.checkNotNullExpressionValue(var10000, "FaceEngine.getInstance(a…width, frame.size.height)");
            List faceResults = var10000;
            Collection var3 = (Collection)faceResults;
            if (var3.size() > 0) {
                FaceEngine.getInstance(appCtx).livenessProcess(frame.getImage(), frame.getSize().width, frame.getSize().height, faceResults);
                LinkedBlockingQueue var4 = this.frThreadQueue;
                Intrinsics.checkNotNull(var4);
                if (var4.remainingCapacity() > 0) {
                    ExecutorService var5 = this.frExecutor;
                    Intrinsics.checkNotNull(var5);
                    var5.execute((Runnable)(new FaceRecognizeRunnable(frame.getImage(), frame.getSize().width, frame.getSize().height, faceResults)));
                }
            }

            if (adjustPreview(frame.getSize().width, frame.getSize().height, frame.getRotation())) {
                sendMessage(0, faceResults);
            }

        }

        // $FF: synthetic method
        // $FF: bridge method
        public Object invoke(Object var1) {
            this.invoke((Frame)var1);
            return Unit.INSTANCE;
        }

        public SampleFrameProcessor() {
            this.frExecutor = (ExecutorService)(new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, (BlockingQueue)this.frThreadQueue, (ThreadFactory)null));
        }
    }

    @Metadata(
            mv = {1, 5, 1},
            k = 1,
            d1 = {"\u0000*\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0010\u0012\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\n\u0002\u0010 \n\u0002\u0018\u0002\n\u0002\b\r\n\u0002\u0010\u0002\n\u0000\b\u0086\u0004\u0018\u00002\u00020\u0001B+\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0005\u0012\f\u0010\u0007\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\u0002\u0010\nJ\b\u0010\u0016\u001a\u00020\u0017H\u0016R\u0017\u0010\u000b\u001a\b\u0012\u0004\u0012\u00020\t0\b¢\u0006\b\n\u0000\u001a\u0004\b\f\u0010\rR\u0011\u0010\u000e\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u000f\u0010\u0010R\u0011\u0010\u0011\u001a\u00020\u0003¢\u0006\b\n\u0000\u001a\u0004\b\u0012\u0010\u0013R\u0011\u0010\u0014\u001a\u00020\u0005¢\u0006\b\n\u0000\u001a\u0004\b\u0015\u0010\u0010¨\u0006\u0018"},
            d2 = {"Lcom/ttv/facerecog/CameraActivity$FaceRecognizeRunnable;", "Ljava/lang/Runnable;", "nv21Data_", "", "width_", "", "height_", "faceResults_", "", "Lcom/ttv/face/FaceResult;", "(Lcom/ttv/facerecog/CameraActivity;[BIILjava/util/List;)V", "faceResults", "getFaceResults", "()Ljava/util/List;", "height", "getHeight", "()I", "nv21Data", "getNv21Data", "()[B", "width", "getWidth", "run", "", "app_debug"}
    )
    public final class FaceRecognizeRunnable implements Runnable {
        @NotNull
        private final byte[] nv21Data;
        private final int width;
        private final int height;
        @NotNull
        private final List faceResults;

        @NotNull
        public final byte[] getNv21Data() {
            return this.nv21Data;
        }

        public final int getWidth() {
            return this.width;
        }

        public final int getHeight() {
            return this.height;
        }

        @NotNull
        public final List getFaceResults() {
            return this.faceResults;
        }

        public void run() {
            if (startVerifyTime == 0L) {
                startVerifyTime = System.currentTimeMillis();
            }

            boolean exists = false;

            try {
                FaceEngine.getInstance(appCtx).extractFeature(this.nv21Data, this.width, this.height, false, this.faceResults);
                SearchResult var10000 = FaceEngine.getInstance(appCtx).searchFaceFeature(new FaceFeature(((FaceResult)this.faceResults.get(0)).feature));
                Intrinsics.checkNotNullExpressionValue(var10000, "FaceEngine.getInstance(a…eResults.get(0).feature))");
                SearchResult result = var10000;
                if (result.getMaxSimilar() > 0.82F) {
                    Iterator var4 = MainActivity.Companion.getUserLists().iterator();

                    while(var4.hasNext()) {
                        FaceEntity user = (FaceEntity)var4.next();
                        int var6 = user.user_id;
                        FaceFeatureInfo var10001 = result.getFaceFeatureInfo();
                        Intrinsics.checkNotNull(var10001);
                        if (var6 == var10001.getSearchId() && ((FaceResult)this.faceResults.get(0)).liveness == 1) {
                            exists = true;
                            RecognitionActivity var7 = RecognitionActivity.this;
                            String var8 = user.userName;
                            Intrinsics.checkNotNullExpressionValue(var8, "user.userName");
                            var7.recogName = var8;
                        }
                    }
                }
            } catch (Exception var5) {
            }

            if (exists) {
                sendMessage(1, 1);
            } else if (System.currentTimeMillis() - startVerifyTime > (long)10000) {
                sendMessage(1, 0);
            }

        }

        public FaceRecognizeRunnable(@NotNull byte[] nv21Data_, int width_, int height_, @NotNull List faceResults_) {
            super();
            Intrinsics.checkNotNullParameter(nv21Data_, "nv21Data_");
            Intrinsics.checkNotNullParameter(faceResults_, "faceResults_");

            this.nv21Data = nv21Data_;
            this.width = width_;
            this.height = height_;
            this.faceResults = faceResults_;
        }
    }
}
