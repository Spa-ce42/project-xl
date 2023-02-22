package com.github.spa_ce42.projectxl3d.event;

import com.github.spa_ce42.projectxl3d.core.Window;

public interface WindowResizeCallback {
    void onResize(Window window, int nWidth, int nHeight);
}
