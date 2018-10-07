package com.ziska.peter.fileexplorer.Model;

import java.io.File;

public class MyFile extends File {

    private boolean isSelected = false;

    public MyFile(String path) {
        super(path);
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }
}
