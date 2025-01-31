package com.vgu.dungluong.cardscannerapp.ui.splash;

import android.os.Bundle;

import com.vgu.dungluong.cardscannerapp.BR;
import com.vgu.dungluong.cardscannerapp.R;
import com.vgu.dungluong.cardscannerapp.ViewModelProviderFactory;
import com.vgu.dungluong.cardscannerapp.databinding.ActivitySplashBinding;
import com.vgu.dungluong.cardscannerapp.ui.base.BaseActivity;
import com.vgu.dungluong.cardscannerapp.ui.main.MainActivity;
import com.vgu.dungluong.cardscannerapp.utils.AppLogger;
import com.vgu.dungluong.cardscannerapp.utils.CommonUtils;
import com.vgu.dungluong.cardscannerapp.utils.ContactUtils;
import com.vgu.dungluong.cardscannerapp.utils.PermissionUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;

import static com.vgu.dungluong.cardscannerapp.utils.AppConstants.CODE_PERMISSIONS_REQUEST;
import static com.vgu.dungluong.cardscannerapp.utils.AppConstants.DATA_PATH;
import static com.vgu.dungluong.cardscannerapp.utils.AppConstants.MODELDATA;
import static com.vgu.dungluong.cardscannerapp.utils.AppConstants.MODEL_PATH;
import static com.vgu.dungluong.cardscannerapp.utils.AppConstants.TESSDATA;

/**
 * Created by Dung Luong on 09/07/2019
 */
public class SplashActivity extends BaseActivity<ActivitySplashBinding, SplashViewModel>
        implements SplashNavigator{

    @Inject
    ViewModelProviderFactory mViewModelProviderFactory;

    private SplashViewModel mSplashViewModel;

    private ActivitySplashBinding mActivitySplashBinding;

    public static final String TAG = SplashActivity.class.getSimpleName();

    @Override
    public int getBindingVariable() {
        return BR.viewModel;
    }

    @Override
    public int getLayoutId() {
        return R.layout.activity_splash;
    }

    @Override
    public SplashViewModel getViewModel() {
        mSplashViewModel = ViewModelProviders.of(this, mViewModelProviderFactory).get(SplashViewModel.class);
        return mSplashViewModel;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySplashBinding = getViewDataBinding();
        mSplashViewModel.setNavigator(this);
        mActivitySplashBinding.setViewModel(mSplashViewModel);
        if(checkPermission()){
            openMainActivity();
        }
    }

    @Override
    public void openMainActivity() {
        startActivity(MainActivity.newIntent(this));
        finish();
    }

    @Override
    public void prepareTesseract() {
        try {
            prepareDirectory(DATA_PATH + TESSDATA);
            //prepareDirectory(MODEL_PATH + MODELDATA);
        } catch (Exception e) {
            e.printStackTrace();
        }

        copyDataFiles(DATA_PATH, TESSDATA);
        //copyDataFiles(MODEL_PATH, MODELDATA);
    }

    /**
     * Prepare directory on external storage
     *
     * @param path
     * @throws Exception
     */
    private void prepareDirectory(String path) {

        File dir = new File(path);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                AppLogger.e( "ERROR: Creation of directory " + path + " failed, check does Android Manifest have permission to write to external storage.");
            }
        } else {
            AppLogger.i("Created directory " + path);
        }
    }

    /**
     * Copy tessdata files (located on assets/tessdata) to destination directory
     *
     * @param path - name of directory with .traineddata files
     */
    private void copyDataFiles(String path, String folder) {
        try {
            String fileList[] = getAssets().list(folder);

            for (String fileName : fileList) {
                AppLogger.i(fileName);
                // open file within the assets folder
                // if it is not already there copy it to the sdcard
                String pathToDataFile = path + folder + "/" + fileName;

                InputStream in = getAssets().open(folder+ "/" + fileName);

                OutputStream out = new FileOutputStream(pathToDataFile);

                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;

                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                in.close();
                out.close();

                AppLogger.i("Copied " + fileName + "to " + pathToDataFile);

            }
        } catch (IOException e) {
            AppLogger.i( "Unable to copy files to " + e.toString());
        }
    }

    /**
     * Callback received when a permissions request has been completed
     */
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if(requestCode == CODE_PERMISSIONS_REQUEST){
            if(PermissionUtils.verifyPermissions(grantResults)){
                showMessage(getString(R.string.camera_grant));
                getViewModel().setIsLoading(true);
                prepareTesseract();
                openMainActivity();
            } else {
                CommonUtils.dialogConfiguration(this,
                        getString(R.string.request_permissions_title),
                        getString(R.string.permission_not_grant_message),
                        false)
                        .setPositiveButton(android.R.string.yes, ((dialog, which) -> restart())).show();
            }
        }
    }
}
