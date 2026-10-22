package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class HomeReturnPolicyTest {
    @Test public void onlyRealStockHomeTriggersReturn(){
        assertTrue(HomeReturnPolicy.matches("com.mitv.tvhome","com.mitv.tvhome.MainActivityUserMode",false));
        assertFalse(HomeReturnPolicy.matches("com.mitv.tvhome","com.mitv.tvhome.SettingsActivity",false));
        assertFalse(HomeReturnPolicy.matches("com.xiaomi.mitv.tvplayer","com.mitv.tvhome.MainActivity",false));
    }
    @Test public void deliberateStockHomeVisitStaysThere(){assertFalse(HomeReturnPolicy.matches("com.mitv.tvhome","com.mitv.tvhome.MainActivityUserMode",true));}
}
