package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class IconArtworkTest {
    @Test public void knownIdentitiesMapWithoutMatchingUnrelatedPackages(){
        assertEquals("yi_icon_zspace",IconArtwork.name("com.zentraedi.zspace.tv"));
        assertEquals("yi_icon_bilibili",IconArtwork.name("com.xiaodianshi.tv.yst"));
        assertEquals("yi_icon_hongguo",IconArtwork.name("cn.hongguo.tv"));
        assertEquals("yi_icon_settings",IconArtwork.name("control:settings"));
        assertNull(IconArtwork.name("cn.hongguo.tv.unrelated"));assertNull(IconArtwork.name("hdmi:1"));
    }
    @Test public void sampledDecodeKeepsEnoughPixelsWithBoundedMemory(){
        for(int target:new int[]{64,128,192,256}){
            int sample=IconArtwork.sample(1254,1254,target),size=1254/sample;
            assertTrue(size>=target);assertTrue(size<target*2);
        }
        assertEquals(1,IconArtwork.sample(48,48,192));
        assertEquals(1,IconArtwork.sample(1254,1254,0));
    }
}
