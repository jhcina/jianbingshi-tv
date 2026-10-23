package io.github.op7418.guizangtv;
import org.junit.Test;
import static org.junit.Assert.*;
public class DockScrollTest {
    @Test public void shelvesFitSmallAndLargeViewportsWithoutShrinkingTiles(){
        for(int available:new int[]{544,864,1184})for(int count=1;count<=9;count++){
            int content=DockScroll.contentWidth(count,144,12);
            int shelf=DockScroll.shelfWidth(available,content,12),viewport=shelf-24;
            assertTrue(shelf<=available);
            if(content<=viewport)assertEquals(content+24,shelf);
            int offset=0;
            for(int i=0;i<count;i++){
                int left=i*156,right=left+144;
                offset=DockScroll.reveal(offset,left,right,viewport,content);
                assertTrue(left>=offset);assertTrue(right<=offset+viewport);
            }
            for(int i=count-1;i>=0;i--){
                int left=i*156,right=left+144;
                offset=DockScroll.reveal(offset,left,right,viewport,content);
                assertTrue(left>=offset);assertTrue(right<=offset+viewport);
            }
            assertEquals(0,offset);
        }
        assertEquals(636,DockScroll.shelfWidth(864,DockScroll.contentWidth(4,144,12),12));
    }

    @Test public void sixFitAndNineKeepTheFocusedTileVisibleInBothDirections(){
        assertEquals(0,DockScroll.reveal(0,710,842,860,842));
        int offset=0;
        for(int i=0;i<9;i++){
            int left=i*142,right=left+132;
            offset=DockScroll.reveal(offset,left,right,860,1268);
            assertTrue(left>=offset);assertTrue(right<=offset+860);
        }
        assertEquals(408,offset);
        for(int i=8;i>=0;i--){
            int left=i*142,right=left+132;
            offset=DockScroll.reveal(offset,left,right,860,1268);
            assertTrue(left>=offset);assertTrue(right<=offset+860);
        }
        assertEquals(0,offset);
    }
    @Test public void restoringLastItemAndShrinkingListClampsScroll(){
        assertEquals(408,DockScroll.reveal(0,1136,1268,860,1268));
        assertEquals(0,DockScroll.reveal(408,710,842,860,842));
    }
}
