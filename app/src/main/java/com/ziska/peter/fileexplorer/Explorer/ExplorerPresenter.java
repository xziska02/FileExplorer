package com.ziska.peter.fileexplorer.Explorer;

import android.graphics.Color;

import com.ziska.peter.fileexplorer.Model.MyFile;
import com.ziska.peter.fileexplorer.R;
import com.ziska.peter.fileexplorer.Utils.Util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ExplorerPresenter implements ExplorerContract.Presenter {

    private ExplorerActivity mView;
    private MyFile currentDir = null;
    private ArrayList<MyFile> mMyFiles;
    private ArrayList<String> mPreviousFolders;         //Store folder paths
    private boolean isMultiChoiceEnabled = false;
    private int selectedItems = 0;
    private int mDelay = 1500;                    //Simulating delay for fetching data
    private CompositeDisposable mDisposable;

    public ExplorerPresenter() {
        mPreviousFolders = new ArrayList<>();
        mDisposable = new CompositeDisposable();
    }

    @Override
    public void loadData(String path, boolean changeCurDir) {
        if (mMyFiles == null) {
            mMyFiles = new ArrayList<>();
        }

        if (mDisposable.isDisposed()) {
            mDisposable = new CompositeDisposable();
        }

        if (changeCurDir) {
            currentDir = new MyFile(path);
        }

        mDisposable.add(loadFiles()
                .subscribeOn(Schedulers.io())
                .delay(mDelay, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(getFiles()));

    }

    private Observable<ArrayList<MyFile>> loadFiles() {
        ArrayList<MyFile> loadedFiles = new ArrayList<>();

        if (currentDir.listFiles() == null) {
            return Observable.just(loadedFiles);
        }

        for (File file : currentDir.listFiles()) {
            MyFile myFile = new MyFile(file.getAbsolutePath());
            loadedFiles.add(myFile);
        }
        return Observable.just(loadedFiles);
    }

    private DisposableObserver<ArrayList<MyFile>> getFiles() {
        return new DisposableObserver<ArrayList<MyFile>>() {

            @Override
            public void onNext(ArrayList<MyFile> myFiles) {
                if (!mMyFiles.equals(myFiles)) {
                    mMyFiles.clear();
                    mMyFiles.addAll(myFiles);
                }

                if (myFiles.size() == 0) {
                    mView.showNoFilesImage();
                    mView.showLoading(false);
                    return;
                }
                mDelay = 0;       //I don't want to have delay while changing directory
            }

            @Override
            public void onError(Throwable e) {
                mView.showNoFilesImage();
            }

            @Override
            public void onComplete() {
                if (isMultiChoiceEnabled()) {
                    mView.showNumSelectedItems(selectedItems);
                }
                mView.showCurrentDirTitle(currentDir.getName());
                mView.slideUpRecyclerAnimation();
                mView.showLoading(false);
            }

            @Override
            protected void onStart() {
                mView.showLoading(true);
            }
        };
    }

    @Override
    public void changeDirectory(String dir) {
        mMyFiles = null;
        mPreviousFolders.add(currentDir.getAbsolutePath());
        loadData(dir, true);
        mView.slideUpRecyclerAnimation();
        mView.showCurrentDirTitle(currentDir.getName());
    }

    @Override
    public void unMarkSelectedFiles() {
        for (MyFile file : mMyFiles) {
            if (file.isSelected()) {
                file.setSelected(false);
            }
        }
        selectedItems = 0;
        mView.hideCAB();
    }

    @Override
    public void deleteSelectedFiles() {
        if (!currentDir.getParentFile().canWrite()) {
            mView.showSnack("You cannot delete data here");
            unMarkSelectedFiles();
            return;
        }
        ArrayList<MyFile> removedFiles = new ArrayList<>();
        for (MyFile file : mMyFiles) {

            if (file.isSelected()) {
                file.delete();
                removedFiles.add(file);
            }


        }
        mMyFiles.removeAll(removedFiles);
        mView.showSnack(Integer.toString(removedFiles.size()) + " Files Deleted");
        mView.refreshView();
        mView.hideCAB();
    }

    @Override
    public int getFilesRowsCount() {
        return mMyFiles.size();
    }

    @Override
    public void onBindFilesRowViewAtPosition(int position, FileAdapter.MyViewHolder viewHolder) {
        MyFile file = mMyFiles.get(position);
        viewHolder.setTextView(file.getName());
        if (file.isSelected()) {
            viewHolder.setSelectedItemView(mView.getResources().getColor(R.color.selectedItemBackground));
        } else {
            viewHolder.setSelectedItemView(Color.WHITE);
        }
        if (file.isDirectory()) {
            viewHolder.setFolderImage();
        }
    }

    @Override
    public void handleClickOnRowItem(int position) {
        MyFile clickedItem = mMyFiles.get(position);
        if (!clickedItem.isDirectory()) {
            try {
                Util.openFile(mView.getApplicationContext(), clickedItem.getPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            changeDirectory(clickedItem.getAbsolutePath());
        }
    }

    @Override
    public void attachView(ExplorerActivity view) {
        mView = view;
    }

    @Override
    public void detach() {
        if (mDisposable != null && !mDisposable.isDisposed()) {
            mDisposable.dispose();
        }
        mView = null;
    }

    @Override
    public boolean isMultiChoiceEnabled() {
        return isMultiChoiceEnabled;
    }

    @Override
    public void setMultiChoiceEnabled(boolean multiChoiceEnabled) {
        isMultiChoiceEnabled = multiChoiceEnabled;
    }

    @Override
    public void enableCAB() {
        mView.showCAB();
    }

    @Override
    public void selectFile(int index) {
        selectedItems++;
        mMyFiles.get(index).setSelected(true);
        mView.showNumSelectedItems(selectedItems);
    }

    @Override
    public void deselectFile(int index) {
        selectedItems--;
        if (selectedItems == 0) {    //no items are selected, we can hide CAB
            mView.hideCAB();
        }
        mMyFiles.get(index).setSelected(false);
        mView.showNumSelectedItems(selectedItems);
    }

    @Override
    public boolean isFileSelected(int index) {
        return mMyFiles.get(index).isSelected();
    }

    @Override
    public boolean existsPreviousDirectory() {
        mView.hideNoFilesImage();
        if (mPreviousFolders.size() == 0) {
            return false;               //no more previous directories
        }
        int lastItemID = mPreviousFolders.size() - 1;
        loadData(mPreviousFolders.get(lastItemID), true);
        mPreviousFolders.remove(lastItemID);
        return true;
    }
}
