package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class StandbyRecoveryPolicyTest {
    @Test public void keepsOtherServicesAndOrder() {
        assertEquals("reader/.Service:input/.Service:ours/.Service",StandbyRecoveryPolicy.appendService("reader/.Service:input/.Service","ours/.Service"));
    }
    @Test public void doesNotDuplicateOwnService() {
        assertEquals("ours/.Service:reader/.Service",StandbyRecoveryPolicy.appendService("ours/.Service:reader/.Service","ours/.Service"));
    }
    @Test public void missingSettingsDoNotBecomePackageNames() {
        assertEquals("ours/.Service",StandbyRecoveryPolicy.appendService(null,"ours/.Service"));
        assertEquals("ours/.Service",StandbyRecoveryPolicy.appendService("null","ours/.Service"));
        assertEquals("ours/.Service",StandbyRecoveryPolicy.appendService("::","ours/.Service"));
    }
}
