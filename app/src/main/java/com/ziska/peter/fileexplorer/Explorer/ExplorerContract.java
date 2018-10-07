package com.ziska.peter.fileexplorer.Explorer;

public interface ExplorerContract {

    interface View {

        void showLoading(boolean isLoading);

        void refreshView();

        void showSnack(String msg);

        void hideCAB();

        void showCAB();

        void showNoFilesImage();

        void hideNoFilesImage();

        void slideUpRecyclerAnimation();

        void showCurrentDirTitle(String title);

        void showNumSelectedItems(int num);

    }

    interface Presenter {

        void loadData(String path, boolean changeCurDir);

        void changeDirectory(String dir);

        void unMarkSelectedFiles();

        void deleteSelectedFiles();

        void attachView(ExplorerActivity view);

        void detach();

        boolean isMultiChoiceEnabled();

        void setMultiChoiceEnabled(boolean multiChoiceEnabled);

        void enableCAB();

        void selectFile(int index);

        void deselectFile(int index);

        boolean isFileSelected(int index);

        boolean existsPreviousDirectory();

        int getFilesRowsCount();

        void onBindFilesRowViewAtPosition(int position, FileAdapter.MyViewHolder viewHolder);

        void handleClickOnRowItem(int position);
    }

}
