package io.github.op7418.guizangtv;

import java.io.File;
import java.io.IOException;

/** Local photos stay in a separate runtime store and never enter default rotation. */
final class WallpaperPrivacy {
    static File resolve(File folder,String name) throws IOException {
        if(folder==null||name==null||!name.matches("[A-Za-z0-9_-]{1,80}\\.(jpg|jpeg|png|webp)"))throw new IOException("Invalid wallpaper filename");
        File file=new File(folder,name).getCanonicalFile();
        if(!file.getParentFile().equals(folder.getCanonicalFile()))throw new IOException("Wallpaper outside local store");
        return file;
    }
    static int next(int current,boolean[] privateImages){
        if(privateImages.length==0)return 0;
        int start=WallpaperCycle.index(current,privateImages.length);
        for(int i=1;i<=privateImages.length;i++){
            int next=(start+i)%privateImages.length;
            if(privateImages[next]==privateImages[start])return next;
        }
        return start;
    }
}
