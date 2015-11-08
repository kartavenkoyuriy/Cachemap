public class CacheMapImpl<K, V> implements CacheMap {

    private long timeToLive = 0;
    private Entry head;
    private Entry tail;

    static class Entry<K, V> {
        private K key;
        private V value;
        private Entry<K, V> prev;
        private Entry<K, V> next;
        private long currentEntryDate;

        //@param key may not be null //in tests

        public Entry(K key, V value, Entry<K, V> prev, Entry<K, V> next, long currentEntryDate) {
            this.key = key;
            this.value = value;
            this.prev = prev;
            this.next = next;
            this.currentEntryDate = currentEntryDate;
        }
    }

    @Override
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() {
        long defaultTimeToLive = 60000;
        return timeToLive == 0 ? defaultTimeToLive : timeToLive;
    }

    @Override
    public V put(Object key, Object value) {
        clearExpired();
        if (head == null) {
            head = tail = new Entry(key, value, null, null, Clock.getTime());
            return null;
        } else {
            Entry temp = new Entry(key, value, tail, null, Clock.getTime());
            tail.next = temp;
            tail = temp;
            return (V) tail.prev;
        }
    }

    @Override
    public void clearExpired() {
        if (head == null) {
            return;
        }

        Entry temp = head;
        while (temp != null) {
            if (Clock.getTime() - temp.currentEntryDate > getTimeToLive()) {
                if (head == tail) {
                    head = null;
                } else {
                    if(temp == head){
                        head = head.next;
                    }
                    if (temp == tail){
                        tail = tail.prev;
                    }
                    if (temp.prev != null){
                        temp.prev.next = temp.next;
                    }
                    if (temp.next != null){
                        temp.next.prev = temp.prev;
                    }
                }
            }
            temp = temp.next;
        }
    }

    @Override
    public void clear() {
        head = tail = null;
    }

    @Override
    public boolean containsKey(Object key) {
        clearExpired();

        Entry temp = head;
        while (temp != null) {
            if (temp.key.equals(key)) {
                return true;
            }
            temp = temp.next;
        }

        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        clearExpired();

        Entry temp = head;
        while (temp != null) {
            if (temp.value.equals(value)) {
                return true;
            }
            temp = temp.next;
        }

        return false;
    }

    @Override
    public Object get(Object key) {
        clearExpired();

        Entry temp = head;
        while (temp != null) {
            if (temp.key.equals(key)) {
                return temp.value;
            }
            temp = temp.next;
        }

        return null;
    }

    @Override
    public boolean isEmpty() {
        clearExpired();

        return head == null;
    }

    @Override
    public Object remove(Object key) {
        clearExpired();

        Entry temp = head;
        while (temp != null) {
            if (temp.key.equals(key)) {
                V oldValue = (V) temp.value;
                if(temp == head){
                    head = head.next;
                }
                if (temp == tail){
                    tail = tail.prev;
                }
                if (temp.prev != null){
                    temp.prev.next = temp.next;
                }
                if (temp.next != null){
                    temp.next.prev = temp.prev;
                }
                return oldValue;
            }
            temp = temp.next;
        }

        return null;
    }

    @Override
    public int size() {
        clearExpired();

        int count = 0;
        Entry temp = head;
        while (temp != null) {
            count++;
            temp = temp.next;
        }

        return count;
    }
}
