package io.github.op7418.guizangtv;

/** Only the newly measured firmware gains MFTR0-specific routes. */
final class XiaomiProfile {
    static boolean mftr0(String model, String firmware) {
        return "MiTV-MFTR0".equals(model) && "OS3.0.101.0.UFTMATV".equals(firmware);
    }
    static int hdmiSource(String model, String firmware, int port) {
        int count = mftr0(model, firmware) ? 3 : "MiTV-MTSU0".equals(model) ? 2 : 0;
        return port >= 1 && port <= count ? 22 + port : -1;
    }
}
