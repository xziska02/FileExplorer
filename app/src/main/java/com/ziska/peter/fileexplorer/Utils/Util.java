package com.ziska.peter.fileexplorer.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ActionMode;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.ziska.peter.fileexplorer.Explorer.ExplorerContract;
import com.ziska.peter.fileexplorer.R;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class Util {

    private final static int mGridItemLayoutWidth = 80;

    public static int calculateNoOfColumns(Context context) {

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int noOfColumns = (int) (dpWidth / mGridItemLayoutWidth);
        return noOfColumns;
    }

    public static ActionMode.Callback setCallBack(final ExplorerContract.View view, final ExplorerContract.Presenter mPresenter) {

        ActionMode.Callback mActionModeCallbacks = new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                mode.getMenuInflater().inflate(R.menu.menu_context_action_bar, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                mPresenter.deleteSelectedFiles();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mPresenter.setMultiChoiceEnabled(false);
                mPresenter.unMarkSelectedFiles();
                view.refreshView();
            }
        };
        return mActionModeCallbacks;
    }
}



