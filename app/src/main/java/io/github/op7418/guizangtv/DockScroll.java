package io.github.op7418.guizangtv;

/** Keep the selected tile fully within the fixed shelf without shrinking its icon. */
final class DockScroll {
    static int contentWidth(int count, int tile, int gap) {
        return Math.max(0,count)*tile+Math.max(0,count-1)*gap;
    }
    static int shelfWidth(int available, int content, int padding) {
        return Math.max(0,Math.min(available,content+2*padding));
    }
    static int reveal(int current, int left, int right, int viewport, int content) {
        int maximum=Math.max(0,content-viewport);
        int target=Math.max(0,Math.min(current,maximum));
        if(left<target)target=left;
        else if(right>target+viewport)target=right-viewport;
        return Math.max(0,Math.min(target,maximum));
    }
}
