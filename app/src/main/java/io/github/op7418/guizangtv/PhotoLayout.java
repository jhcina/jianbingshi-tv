package io.github.op7418.guizangtv;

/** Fits the entire photograph; never crops a face to fill the television. */
final class PhotoLayout {
    static int[] fit(int width, int height, int boxWidth, int boxHeight) {
        if (width <= 0 || height <= 0 || boxWidth <= 0 || boxHeight <= 0)
            throw new IllegalArgumentException("Invalid image dimensions");
        double scale = Math.min((double) boxWidth / width, (double) boxHeight / height);
        return new int[]{Math.max(1, (int) Math.round(width * scale)),
                Math.max(1, (int) Math.round(height * scale))};
    }
    static int sample(int width, int height) {
        if (width <= 0 || height <= 0 || (long) width * height > 120_000_000L)
            throw new IllegalArgumentException("Unsupported image dimensions");
        int sample = 1;
        while (Math.max(width, height) / sample > 2560) sample *= 2;
        return sample;
    }
    static int next(int current, boolean[] personal, int direction) {
        if (personal.length == 0) return 0;
        int start = Math.floorMod(current, personal.length);
        for (int i = 1; i <= personal.length; i++) {
            int index = Math.floorMod(start + (direction < 0 ? -i : i), personal.length);
            if (personal[index] == personal[start]) return index;
        }
        return start;
    }
}
