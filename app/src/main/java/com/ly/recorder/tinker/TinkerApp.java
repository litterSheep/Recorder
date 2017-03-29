package com.ly.recorder.tinker;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by ly on 2017/3/27 10:07.
 */

public class TinkerApp extends TinkerApplication {

    public TinkerApp() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.ly.recorder.App",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
