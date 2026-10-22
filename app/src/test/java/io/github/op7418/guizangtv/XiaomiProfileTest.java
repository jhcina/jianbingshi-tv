package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class XiaomiProfileTest {
    @Test public void measuredFirmwareHasExactlyThreeHdmiPorts(){
        assertEquals(23,XiaomiProfile.hdmiSource("MiTV-MFTR0","OS3.0.101.0.UFTMATV",1));
        assertEquals(25,XiaomiProfile.hdmiSource("MiTV-MFTR0","OS3.0.101.0.UFTMATV",3));
        assertEquals(-1,XiaomiProfile.hdmiSource("MiTV-MFTR0","OS3.0.101.0.UFTMATV",4));
    }
    @Test public void otherFirmwareAndOtherBrandsDoNotInheritRoutes(){
        assertEquals(-1,XiaomiProfile.hdmiSource("MiTV-MFTR0","other",1));
        assertEquals(-1,XiaomiProfile.hdmiSource("TCL","OS3.0.101.0.UFTMATV",1));
    }
    @Test public void originalProfileKeepsItsTwoPorts(){assertEquals(24,XiaomiProfile.hdmiSource("MiTV-MTSU0","",2));assertEquals(-1,XiaomiProfile.hdmiSource("MiTV-MTSU0","",3));}
}
