package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {
    private final Comparator<T> comp;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comp = c;
    }

    public T max() {
        if (size() == 0) {
            return null;
        }
        var it = iterator();
        var maxElem = it.next();
        while (it.hasNext()) {
            var nextElem = it.next();
            if (comp.compare(nextElem, maxElem) > 0) {
                maxElem = nextElem;
            }
        }

        return maxElem;
    }

    public T max(Comparator<T> c) {
        if (size() == 0) {
            return null;
        }
        var it = iterator();
        var maxElem = it.next();
        while (it.hasNext()) {
            var nextElem = it.next();
            if (c.compare(nextElem, maxElem) > 0) {
                maxElem = nextElem;
            }
        }

        return maxElem;
    }
}
