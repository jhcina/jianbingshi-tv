package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class PhotoLayoutTest {
    @Test public void portraitRemainsComplete() { assertArrayEquals(new int[]{720,1080}, PhotoLayout.fit(2000,3000,1920,1080)); }
    @Test public void panoramaRemainsComplete() { assertArrayEquals(new int[]{1920,480}, PhotoLayout.fit(8000,2000,1920,1080)); }
    @Test public void hugeInputIsBoundedBeforeDecode() { assertEquals(4,PhotoLayout.sample(7952,5304)); }
    @Test(expected=IllegalArgumentException.class) public void rejectDecompressionBomb() { PhotoLayout.sample(50000,50000); }
    @Test(expected=IllegalArgumentException.class) public void rejectInvalidImage() { PhotoLayout.sample(-1,0); }
    @Test public void galleryNeverCrossesPrivacyBoundary() {
        boolean[] scope={false,false,true,true};
        assertEquals(0,PhotoLayout.next(1,scope,1));
        assertEquals(3,PhotoLayout.next(2,scope,-1));
        assertEquals(2,PhotoLayout.next(3,scope,1));
    }
}
