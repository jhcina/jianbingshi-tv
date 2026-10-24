package io.github.op7418.guizangtv;

import org.junit.Test;
import static org.junit.Assert.*;

public class BootStartPolicyTest {
    @Test public void newBootCanEnter() {
        assertNull(BootStartPolicy.skipReason(true, true, 20, 19, 75000));
    }
    @Test public void disabledCannotEnterEvenDuringBoot() {
        assertNotNull(BootStartPolicy.skipReason(true, false, 20, 19, 75000));
    }
    @Test public void unsupportedModelCannotEnter() {
        assertNotNull(BootStartPolicy.skipReason(false, true, 20, 19, 75000));
    }
    @Test public void duplicateCannotTakeFocusFromHomeOrHdmi() {
        assertNotNull(BootStartPolicy.skipReason(true, true, 20, 20, 76000));
    }
    @Test public void unknownBootIsSkipped() {
        assertNotNull(BootStartPolicy.skipReason(true, true, -1, -1, 75000));
    }
    @Test public void lateDeliveryCannotInterruptUse() {
        assertNull(BootStartPolicy.skipReason(true, true, 20, 19, 600000));
        assertNotNull(BootStartPolicy.skipReason(true, true, 20, 19, 600001));
    }
    @Test public void invalidClockIsSkipped() {
        assertNotNull(BootStartPolicy.skipReason(true, true, 20, 19, -1));
    }
    @Test public void previousBootSuccessIsNotCurrentEvidence() {
        assertEquals("仅有此前开机记录，本次尚未记录启动调用", BootStartPolicy.evidenceSummary(21,20,20));
    }
    @Test public void launchRequestDoesNotProveVisibleDesktop() {
        assertEquals("本次开机已收到启动调用，尚未确认桌面显示", BootStartPolicy.evidenceSummary(21,21,20));
    }
    @Test public void currentConfirmationDoesNotClaimStandbyPersistence() {
        assertEquals("本次开机已确认进入桌面；待机保持仍需另测", BootStartPolicy.evidenceSummary(21,21,21));
    }
    @Test public void missingOrLegacyEvidenceRemainsUnknown() {
        assertEquals("无法读取本次开机编号，结果待确认", BootStartPolicy.evidenceSummary(-1,-1,-1));
        assertEquals("尚未记录启动调用，不能确认自动进入", BootStartPolicy.evidenceSummary(21,-1,-1));
        assertEquals("仅有此前开机记录，本次尚未记录启动调用", BootStartPolicy.evidenceSummary(21,20,-1));
    }
}
