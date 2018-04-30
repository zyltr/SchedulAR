/*===============================================================================
Copyright (c) 2016-2017 PTC Inc. All Rights Reserved.

Copyright (c) 2012-2014 Qualcomm Connected Experiences, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other 
countries.
===============================================================================*/

package com.Schedular.ScheduleTargets;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.Schedular.R;
import com.Schedular.Schedule.ScheduleOverlayView;
import com.Schedular.Schedule.Schedules;
import com.Schedular.Schedule.SchedulesRenderer;
import com.vuforia.*;
import com.Schedular.Vuforia.Application.SampleApplicationControl;
import com.Schedular.Vuforia.Application.SampleApplicationException;
import com.Schedular.Vuforia.Application.SampleApplicationSession;
import com.Schedular.Vuforia.Utilities.LoadingDialogHandler;
import com.Schedular.Vuforia.Utilities.SampleApplicationGLView;
import com.Schedular.Vuforia.Utilities.Texture;
import com.Schedular.Vuforia.CameraMenu.SampleAppMenu;
import com.Schedular.Vuforia.CameraMenu.SampleAppMenuGroup;
import com.Schedular.Vuforia.CameraMenu.SampleAppMenuInterface;

import java.util.ArrayList;
import java.util.Vector;


public class ScheduleTargets extends Activity implements SampleApplicationControl, SampleAppMenuInterface {
    final public static int CMD_BACK = -1;
    final public static int CMD_AUTOFOCUS = 2;
    final public static int CMD_FLASH = 3;
    final public static int CMD_CAMERA_FRONT = 4;
    final public static int CMD_CAMERA_REAR = 5;
    final public static int CMD_DATASET_START_INDEX = 6;
    private static final String LOGTAG = "ScheduleTargets";
    SampleApplicationSession vuforiaAppSession;
    LoadingDialogHandler loadingDialogHandler = new LoadingDialogHandler(this);
    boolean mIsDroidDevice = false;
    private DataSet mCurrentDataset;
    private int mCurrentDatasetSelectionIndex = 0;
    private int mStartDatasetsIndex = 0;
    private int mDatasetsNumber = 0;
    private ArrayList<String> mDatasetStrings = new ArrayList<>();
    // Our OpenGL view:
    private SampleApplicationGLView mGlView;
    // Our renderer:
    private ScheduleTargetRenderer mRenderer;
    private GestureDetector mGestureDetector;
    // The textures we will use for rendering:
    private Vector<Texture> mTextures;
    private boolean mSwitchDatasetAsap = false;
    private boolean mFlash = false;
    private boolean mContAutofocus = true;


    // We want to load specific textures from the APK, which we will later use
    // for rendering.
    private View mFocusOptionView;
    private View mFlashOptionView;
    private RelativeLayout mUILayout;
    private SampleAppMenu mSampleAppMenu;
    // Alert Dialog used to display SDK errors
    private AlertDialog mErrorDialog;

    // Called when the activity first starts or the user navigates back to an
    // activity.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOGTAG, "onCreate");
        super.onCreate(savedInstanceState);

        vuforiaAppSession = new SampleApplicationSession(this);

        startLoadingAnimation();

        mDatasetStrings.add("Schedule.xml");

        vuforiaAppSession.initAR(this, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        mGestureDetector = new GestureDetector(this, new GestureListener());

        // Load any sample specific textures:
        mTextures = new Vector<>();
        loadTextures();

        mIsDroidDevice = android.os.Build.MODEL.toLowerCase().startsWith("droid");

    }

    private void loadTextures() {
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBrass.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotBlue.png", getAssets()));
        mTextures.add(Texture.loadTextureFromApk("TextureTeapotRed.png", getAssets()));
    }

    // Called when the activity will start interacting with the user.
    @Override
    protected void onResume() {
        Log.d(LOGTAG, "onResume");
        super.onResume();

        showProgressIndicator(true);

        // This is needed for some Droid devices to force portrait
        if (mIsDroidDevice) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        vuforiaAppSession.onResume();
    }

    // Callback for configuration changes the activity handles itself
    @Override
    public void onConfigurationChanged(Configuration config) {
        Log.d(LOGTAG, "onConfigurationChanged");
        super.onConfigurationChanged(config);

        vuforiaAppSession.onConfigurationChanged();
    }

    // Called when the system is about to start resuming a previous activity.
    @Override
    protected void onPause() {
        Log.d(LOGTAG, "onPause");
        super.onPause();

        if (mGlView != null) {
            mGlView.setVisibility(View.INVISIBLE);
            mGlView.onPause();
        }

        // Turn off the flash
        if (mFlashOptionView != null && mFlash) {
            // OnCheckedChangeListener is called upon changing the checked state
            setMenuToggle(mFlashOptionView, false);
        }

        try {
            vuforiaAppSession.pauseAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }
    }

    // The final call you receive before your activity is destroyed.
    @Override
    protected void onDestroy() {
        Log.d(LOGTAG, "onDestroy");
        super.onDestroy();

        try {
            vuforiaAppSession.stopAR();
        } catch (SampleApplicationException e) {
            Log.e(LOGTAG, e.getString());
        }

        // Unload texture:
        mTextures.clear();
        mTextures = null;

        System.gc();
    }

    // Initializes AR application components.
    private void initApplicationAR() {
        // Create OpenGL ES view:
        int depthSize = 16;
        int stencilSize = 0;
        boolean translucent = Vuforia.requiresAlpha();

        mGlView = new SampleApplicationGLView(this);
        mGlView.init(translucent, depthSize, stencilSize);

        mRenderer = new ScheduleTargetRenderer(this, vuforiaAppSession);
        mRenderer.setTextures(mTextures);
        mGlView.setRenderer(mRenderer);
    }

    private void startLoadingAnimation() {
        mUILayout = (RelativeLayout) View.inflate(this, R.layout.camera_overlay, null);

        mUILayout.setVisibility(View.VISIBLE);
        mUILayout.setBackgroundColor(Color.BLACK);

        // Gets a reference to the loading dialog
        loadingDialogHandler.mLoadingDialogContainer = mUILayout.findViewById(R.id.loading_indicator);

        // Shows the loading indicator at start
        loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);

        // Adds the inflated layout to the view
        addContentView(mUILayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }

    // Methods to load and destroy tracking data.
    @Override
    public boolean doLoadTrackersData() {
        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset == null)
            mCurrentDataset = objectTracker.createDataSet();

        if (mCurrentDataset == null)
            return false;

        if (!mCurrentDataset.load(
                mDatasetStrings.get(mCurrentDatasetSelectionIndex), STORAGE_TYPE.STORAGE_APPRESOURCE))
            return false;

        if (!objectTracker.activateDataSet(mCurrentDataset))
            return false;

        int numTrackables = mCurrentDataset.getNumTrackables();
        for (int count = 0; count < numTrackables; count++) {
            Trackable trackable = mCurrentDataset.getTrackable(count);

            String name = "Current Dataset : " + trackable.getName();
            trackable.setUserData(name);
            Log.d(LOGTAG, "UserData:Set the following user data " + trackable.getUserData());
        }

        return true;
    }

    @Override
    public boolean doUnloadTrackersData() {
        // Indicate if the trackers were unloaded correctly
        boolean result = true;

        TrackerManager trackerManager = TrackerManager.getInstance();
        ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
        if (objectTracker == null)
            return false;

        if (mCurrentDataset != null && mCurrentDataset.isActive()) {
            if (objectTracker.getActiveDataSet(0).equals(mCurrentDataset) && !objectTracker.deactivateDataSet(mCurrentDataset)) {
                result = false;
            } else if (!objectTracker.destroyDataSet(mCurrentDataset)) {
                result = false;
            }

            mCurrentDataset = null;
        }

        return result;
    }

    @Override
    public void onVuforiaResumed() {
        if (mGlView != null) {
            mGlView.setVisibility(View.VISIBLE);
            mGlView.onResume();
        }
    }

    @Override
    public void onVuforiaStarted() {
        mRenderer.updateConfiguration();

        if (mContAutofocus) {
            // Set camera focus mode
            if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO)) {
                // If continuous autofocus mode fails, attempt to set to a different mode
                if (!CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO)) {
                    CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);
                }

                // Update Toggle state
                setMenuToggle(mFocusOptionView, false);
            } else {
                // Update Toggle state
                setMenuToggle(mFocusOptionView, true);
            }
        } else {
            setMenuToggle(mFocusOptionView, false);
        }

        showProgressIndicator(false);
    }

    public void showProgressIndicator(boolean show) {
        if (loadingDialogHandler != null) {
            if (show) {
                loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.SHOW_LOADING_DIALOG);
            } else {
                loadingDialogHandler.sendEmptyMessage(LoadingDialogHandler.HIDE_LOADING_DIALOG);
            }
        }
    }

    @Override
    public void onInitARDone(SampleApplicationException exception) {

        if (exception == null) {
            initApplicationAR();

            mRenderer.setActive(true);

            // Now add the GL surface view. It is important
            // that the OpenGL ES surface view gets added
            // BEFORE the camera is started and video
            // background is configured.
            addContentView(mGlView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));;

            vuforiaAppSession.startAR(CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_DEFAULT);

            // Sets the UILayout to be drawn in front of the camera
            mUILayout.bringToFront();

            // Sets the layout background to transparent
            mUILayout.setBackgroundColor(Color.TRANSPARENT);

            mSampleAppMenu = new SampleAppMenu(this, this, "Schedule Targets", mGlView, mUILayout, null);
            setSampleAppMenuSettings();

        } else {
            Log.e(LOGTAG, exception.getString());
            showInitializationErrorMessage(exception.getString());
        }
    }

    // Shows initialization error messages as System dialogs
    public void showInitializationErrorMessage(String message) {
        final String errorMessage = message;
        runOnUiThread(new Runnable() {
            public void run() {
                if (mErrorDialog != null) {
                    mErrorDialog.dismiss();
                }

                // Generates an Alert Dialog to show the error message
                AlertDialog.Builder builder = new AlertDialog.Builder(ScheduleTargets.this);
                builder.setMessage(errorMessage);
                builder.setTitle(getString(R.string.INIT_ERROR));
                builder.setCancelable(false);
                builder.setIcon(0);
                builder.setPositiveButton(getString(R.string.button_OK), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                });

                mErrorDialog = builder.create();
                mErrorDialog.show();
            }
        });
    }

    private String stringForStatus (int status)
    {
        switch (status)
        {
            case TrackableResult.STATUS.DETECTED: {
                return "DETECTED";
            }
            case TrackableResult.STATUS.EXTENDED_TRACKED:
            {
                return "EXTENDED TRACKED";
            }
            case TrackableResult.STATUS.LIMITED:
            {
                return "LIMITED";
            }
            case TrackableResult.STATUS.NO_POSE:
            {
                return "NO POSE";
            }
            case TrackableResult.STATUS.TRACKED:
            {
                return "TRACKED";
            }
            default:
                return "DEFAULT";

        }
    }

    private String stringForStatusInfo (int statusInfo)
    {
        switch (statusInfo)
        {
            case TrackableResult.STATUS_INFO.EXCESSIVE_MOTION:
            {
                return "EXCESSIVE MOTION";
            }
            case TrackableResult.STATUS_INFO.INITIALIZING:
            {
                return "INITIALIZING";
            }
            case TrackableResult.STATUS_INFO.INSUFFICIENT_FEATURES:
            {
                return "INSUFFICIENT FEATURES";
            }
            case TrackableResult.STATUS_INFO.NORMAL:
            {
                return "NORMAL";
            }
            case TrackableResult.STATUS_INFO.UNKNOWN:
            {
                return "UNKNOWN";
            }
            default:
                return "DEFAULT";
        }
    }

    // TODO -> EXPERIMENTAL
    int currentTrackableId = 0;
    private static int mTextureSize = 768;
    Trackable currentTrackable;
    Texture mTexture;

    public void createProductTexture ()
    {
        // Generates a View to display the schedule data
        ScheduleOverlayView productView = new ScheduleOverlayView(ScheduleTargets.this);

        // Sets the layout params
        productView.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT));

        // Sets View measure - This size should be the same as the
        // texture generated to display the overlay in order for the
        // texture to be centered in screen
        productView.measure(View.MeasureSpec.makeMeasureSpec(mTextureSize, View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(mTextureSize, View.MeasureSpec.EXACTLY));

        // updates layout size
        productView.layout(0, 0, productView.getMeasuredWidth(), productView.getMeasuredHeight());

        // Draws the View into a Bitmap. Note we are allocating several
        // large memory buffers thus attempt to clear them as soon as
        // they are no longer required:
        Bitmap bitmap = Bitmap.createBitmap(mTextureSize, mTextureSize, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(bitmap);
        productView.draw(c);

        // Allocate int buffer for pixel conversion and copy pixels
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        int[] data = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(data, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());

        // Generates the Texture from the int buffer
       mTexture = Texture.loadTextureFromIntBuffer(data, width, height);
    }

    public Texture getProductTexture ()
    {
        if ( mTexture == null )
            createProductTexture();

        return mTexture;
    }

    // TODO -> END

    @Override
    public void onVuforiaUpdate(State state) {
        int numberOfTrackableResults = state.getNumTrackableResults();
        for (int index = 0; index < numberOfTrackableResults; ++index)
        {
            TrackableResult result = state.getTrackableResult(index);
            Trackable trackable = result.getTrackable();

            if ( currentTrackableId != trackable.getId() )
            {
                currentTrackable = trackable;
                currentTrackableId = trackable.getId();
                Log.d(LOGTAG, "Initialize Tracker's Texture Here");
            }

            String status = stringForStatus(result.getStatus());
            String statusInfo = stringForStatusInfo(result.getStatusInfo());
            Log.d(LOGTAG, "Status : " + status + " | Status Info : " + statusInfo);
        }

        if (mSwitchDatasetAsap) {
            mSwitchDatasetAsap = false;
            TrackerManager trackerManager = TrackerManager.getInstance();
            ObjectTracker objectTracker = (ObjectTracker) trackerManager.getTracker(ObjectTracker.getClassType());
            if (objectTracker == null || mCurrentDataset == null || objectTracker.getActiveDataSet(0) == null) {
                Log.d(LOGTAG, "Failed to swap datasets");
                return;
            }

            doUnloadTrackersData();
            doLoadTrackersData();
        }
    }

    @Override
    public boolean doInitTrackers() {
        // Indicate if the trackers were initialized correctly
        boolean result = true;

        TrackerManager trackerManager = TrackerManager.getInstance();
        Tracker tracker;

        // Trying to initialize the image tracker
        tracker = trackerManager.initTracker(ObjectTracker.getClassType());
        if (tracker == null) {
            Log.e(LOGTAG, "Tracker not initialized. Tracker already initialized or the camera is already started");
            result = false;
        } else {
            Log.i(LOGTAG, "Tracker successfully initialized");
        }
        return result;
    }

    @Override
    public boolean doStartTrackers() {
        // Indicate if the trackers were started correctly
        Tracker objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.start();

        return true;
    }

    @Override
    public boolean doStopTrackers() {
        // Indicate if the trackers were stopped correctly
        Tracker objectTracker = TrackerManager.getInstance().getTracker(ObjectTracker.getClassType());
        if (objectTracker != null)
            objectTracker.stop();

        return true;
    }

    @Override
    public boolean doDeinitTrackers() {
        // Indicate if the trackers were deinitialized correctly
        TrackerManager trackerManager = TrackerManager.getInstance();
        trackerManager.deinitTracker(ObjectTracker.getClassType());

        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // Process the Gestures
        if (mSampleAppMenu != null && mSampleAppMenu.processEvent(event))
            return true;

        return mGestureDetector.onTouchEvent(event);
    }

    // This method sets the menu's settings
    private void setSampleAppMenuSettings() {
        SampleAppMenuGroup group;

        group = mSampleAppMenu.addGroup("", false);
        group.addTextItem(getString(R.string.menu_back), -1);

        mFocusOptionView = group.addSelectionItem(getString(R.string.menu_contAutofocus), CMD_AUTOFOCUS, mContAutofocus);
        mFlashOptionView = group.addSelectionItem(getString(R.string.menu_flash), CMD_FLASH, false);

        CameraInfo ci = new CameraInfo();
        boolean deviceHasFrontCamera = false;
        boolean deviceHasBackCamera = false;
        for (int i = 0; i < Camera.getNumberOfCameras(); i++) {
            Camera.getCameraInfo(i, ci);
            if (ci.facing == CameraInfo.CAMERA_FACING_FRONT)
                deviceHasFrontCamera = true;
            else if (ci.facing == CameraInfo.CAMERA_FACING_BACK)
                deviceHasBackCamera = true;
        }

        if (deviceHasBackCamera && deviceHasFrontCamera) {
            group = mSampleAppMenu.addGroup(getString(R.string.menu_camera), true);
            group.addRadioItem(getString(R.string.menu_camera_front), CMD_CAMERA_FRONT, false);
            group.addRadioItem(getString(R.string.menu_camera_back), CMD_CAMERA_REAR, true);
        }

        group = mSampleAppMenu.addGroup(getString(R.string.menu_datasets), true);
        mStartDatasetsIndex = CMD_DATASET_START_INDEX;
        mDatasetsNumber = mDatasetStrings.size();

        group.addRadioItem("Schedule", mStartDatasetsIndex, true);

        mSampleAppMenu.attachMenu();
    }

    private void setMenuToggle(View view, boolean value) {
        // OnCheckedChangeListener is called upon changing the checked state
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            ((Switch) view).setChecked(value);
        } else {
            ((CheckBox) view).setChecked(value);
        }
    }

    @Override
    public boolean menuProcess(int command) {

        boolean result = true;

        switch (command) {
            case CMD_BACK:
                finish();
                break;

            case CMD_FLASH:
                result = CameraDevice.getInstance().setFlashTorchMode(!mFlash);

                if (result) {
                    mFlash = !mFlash;
                } else {
                    showToast(getString(mFlash ? R.string.menu_flash_error_off : R.string.menu_flash_error_on));
                    Log.e(LOGTAG, getString(mFlash ? R.string.menu_flash_error_off : R.string.menu_flash_error_on));
                }
                break;

            case CMD_AUTOFOCUS:

                if (mContAutofocus) {
                    result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_NORMAL);

                    if (result) {
                        mContAutofocus = false;
                    } else {
                        showToast(getString(R.string.menu_contAutofocus_error_off));
                        Log.e(LOGTAG, getString(R.string.menu_contAutofocus_error_off));
                    }
                } else {
                    result = CameraDevice.getInstance().setFocusMode(
                            CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                    if (result) {
                        mContAutofocus = true;
                    } else {
                        showToast(getString(R.string.menu_contAutofocus_error_on));
                        Log.e(LOGTAG, getString(R.string.menu_contAutofocus_error_on));
                    }
                }

                break;

            case CMD_CAMERA_FRONT:
            case CMD_CAMERA_REAR:

                // Turn off the flash
                if (mFlashOptionView != null && mFlash) {
                    setMenuToggle(mFlashOptionView, false);
                }

                vuforiaAppSession.stopCamera();

                vuforiaAppSession.startAR(command == CMD_CAMERA_FRONT ? CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_FRONT : CameraDevice.CAMERA_DIRECTION.CAMERA_DIRECTION_BACK);

                break;

            default:
                if (command >= mStartDatasetsIndex && command < mStartDatasetsIndex + mDatasetsNumber) {
                    mSwitchDatasetAsap = true;
                    mCurrentDatasetSelectionIndex = command - mStartDatasetsIndex;
                }
                break;
        }

        return result;
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }

    // Process Single Tap event to trigger autofocus
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        // Used to set autofocus one second after a manual focus is triggered
        private final Handler autofocusHandler = new Handler();

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }


        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            boolean result = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_TRIGGERAUTO);
            if (!result)
                Log.e("SingleTapUp", "Unable to trigger focus");

            // Generates a Handler to trigger continuous auto-focus
            // after 1 second
            autofocusHandler.postDelayed(new Runnable() {
                public void run() {
                    if (mContAutofocus) {
                        final boolean autofocusResult = CameraDevice.getInstance().setFocusMode(CameraDevice.FOCUS_MODE.FOCUS_MODE_CONTINUOUSAUTO);

                        if (!autofocusResult)
                            Log.e("SingleTapUp", "Unable to re-enable continuous auto-focus");
                    }
                }
            }, 1000L);

            return true;
        }
    }
}
