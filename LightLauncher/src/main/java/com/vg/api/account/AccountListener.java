package com.vg.api.account;

import com.vg.api.VGData;

/**
 * Created by huadong on 11/19/13.
 */
public interface AccountListener {
    void onLogIn(VGData.User user);
    void onLogOut();
}
