package io.github.op7418.guizangtv;

import java.util.ArrayList;
import java.util.List;

/** Only append our exact service; never replace other accessibility choices. */
final class StandbyRecoveryPolicy {
    static String appendService(String current, String own) {
        List<String> entries = new ArrayList<>();
        if (current != null && !current.equals("null")) {
            for (String entry : current.split(":")) {
                if (!entry.isEmpty()) entries.add(entry);
            }
        }
        if (!entries.contains(own)) entries.add(own);
        return String.join(":", entries);
    }
}
