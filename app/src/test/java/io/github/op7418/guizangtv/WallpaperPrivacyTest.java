package io.github.op7418.guizangtv;
import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;
public class WallpaperPrivacyTest {
    @Test public void defaultRotationNeverEntersPersonalPhotos(){
        boolean[] personal={false,false,false,true,true};
        assertEquals(0,WallpaperPrivacy.next(2,personal));
        assertEquals(3,WallpaperPrivacy.next(4,personal));
    }
    @Test public void aSinglePrivatePhotoStaysSelected(){assertEquals(2,WallpaperPrivacy.next(2,new boolean[]{false,false,true}));}
    @Test public void normalLocalNameResolvesInsideStore()throws Exception{
        File base=new File("build/test-private-store");assertEquals(base.getCanonicalFile(),WallpaperPrivacy.resolve(base,"wallpaper_05.jpg").getParentFile());
    }
    @Test(expected=java.io.IOException.class)public void parentTraversalIsRefused()throws Exception{WallpaperPrivacy.resolve(new File("build/store"),"../portrait.jpg");}
    @Test(expected=java.io.IOException.class)public void absolutePathIsRefused()throws Exception{WallpaperPrivacy.resolve(new File("build/store"),"C:/portrait.jpg");}
}
