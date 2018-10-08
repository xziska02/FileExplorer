package com.ziska.peter.fileexplorer.Explorer;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.animation.LayoutAnimationController;
import android.widget.ProgressBar;

import com.ziska.peter.fileexplorer.R;
import com.ziska.peter.fileexplorer.Settings.SettingsActivity;
import com.ziska.peter.fileexplorer.Utils.FileUtil;
import com.ziska.peter.fileexplorer.Utils.PermissionUtil;
import com.ziska.peter.fileexplorer.Utils.Util;


public class ExplorerActivity extends AppCompatActivity implements ExplorerContract.View {

    private static final int MY_PERMISSION = 1;
    private ExplorerContract.Presenter mPresenter;
    private FileAdapter mFileAdapter;
    private RecyclerView mFileRecyclerView;
    private ActionMode.Callback mActionModeCallbacks;
    private ProgressBar mProgressBar;
    private ActionMode mMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_explorer);
        mProgressBar = findViewById(R.id.progressBar);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        PreferenceManager.setDefaultValues(this, R.xml.preference, false);

        if (PermissionUtil.isPermissionGranted(this)) {
            initApp();
        } else {
            PermissionUtil.requestPermission(this);
        }
    }

    private void initApp() {
        attachPresenter();
        mActionModeCallbacks = Util.setCallBack(this,mPresenter);
        if (mPresenter.isMultiChoiceEnabled()) {
            showCAB();
        }
        setFileRecycler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_file_explorer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        if (id == R.id.action_refresh) {
            hideNoFilesImage();
            mPresenter.loadData(FileUtil.getDefaultPreferencePath(this), true);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void attachPresenter() {
        mPresenter = (ExplorerContract.Presenter) getLastCustomNonConfigurationInstance();
        boolean isFirstLoad = false;
        if (mPresenter == null) {
            mPresenter = new ExplorerPresenter();
            isFirstLoad = true;
        }
        mPresenter.attachView(this);
        mPresenter.loadData(FileUtil.getDefaultPreferencePath(this), isFirstLoad);
    }

    @Override
    public Object onRetainCustomNonConfigurationInstance() {
        return mPresenter;
    }

    @Override
    protected void onDestroy() {
        if (mPresenter != null)
            mPresenter.detach();
        super.onDestroy();
    }

    @Override
    public void showLoading(boolean isLoading) {
        if (isLoading)
            mProgressBar.setVisibility(View.VISIBLE);
        else
            mProgressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void showSnack(String msg) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, msg, Snackbar.LENGTH_LONG).show();
    }

    @Override
    public void hideCAB() {
        mMode.finish();
    }

    @Override
    public void showCAB() {
        mMode = ((AppCompatActivity) this).startSupportActionMode(mActionModeCallbacks);
    }

    @Override
    public void showNoFilesImage() {
        (findViewById(R.id.no_file_image_layout)).setVisibility(View.VISIBLE);
    }

    @Override
    public void hideNoFilesImage() {
        (findViewById(R.id.no_file_image_layout)).setVisibility(View.INVISIBLE);
    }

    @Override
    public void slideUpRecyclerAnimation() {

        LayoutAnimationController controller = AnimationUtils
                .loadLayoutAnimation(mFileRecyclerView.getContext(), R.anim.layout_slide_up);
        mFileRecyclerView.setLayoutAnimation(controller);
        mFileRecyclerView.getAdapter().notifyDataSetChanged();
        mFileRecyclerView.scheduleLayoutAnimation();
    }

    @Override
    public void showCurrentDirTitle(String title) {
        getSupportActionBar().setTitle(title);
    }

    @Override
    public void showNumSelectedItems(int num) {
        String title = Integer.toString(num) + " items selected";
        mMode.setTitle(title);
    }

    @Override
    public void refreshView() {
        mFileAdapter.notifyDataSetChanged();
    }

    private void setFileRecycler() {
        mFileRecyclerView = findViewById(R.id.fileRecyclerView);
        mFileAdapter = new FileAdapter(mPresenter);

        if (getApplicationContext().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mFileRecyclerView.setLayoutManager(new GridLayoutManager(this, 1));
        } else {
            mFileRecyclerView.setLayoutManager(new GridLayoutManager(this, Util.calculateNoOfColumns(this)));
        }
        mFileRecyclerView = findViewById(R.id.fileRecyclerView);
        mFileRecyclerView.setAdapter(mFileAdapter);
    }

    @Override
    public void onBackPressed() {
        if (!mPresenter.existsPreviousDirectory()) {
            super.onBackPressed();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == MY_PERMISSION){
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initApp();
            } else {
                finish();
            }
        }
    }

}
