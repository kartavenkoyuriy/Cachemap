public class CacheMapImpl<K, V> implements CacheMap {

    private long timeToLive = 0;
    private long defaultTimeToLive = 60000;
    private Entry<K, V>[] entries;//linkedlist?

    static class Entry<K, V> {//linkedlist?
        K key;
        V value;
        long currentEntryDate;

        public Entry(K key, V value, long currentEntryDate) {
            this.key = key;
            this.value = value;
            this.currentEntryDate = currentEntryDate;
        }

        public K getKey() {
            return key;
        }

        public V getValue() {
            return value;
        }

        public long getCurrentEntryDate() {
            return currentEntryDate;
        }

        public void setValue(V value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object obj) {
            Entry<K, V> newEntry = (Entry<K, V>) obj;
            return (this.key.equals(newEntry.key) && this.value.equals(newEntry.value));
        }
    }


    @Override
    public void setTimeToLive(long timeToLive) {
        this.timeToLive = timeToLive;
    }

    @Override
    public long getTimeToLive() {
        return timeToLive == 0 ? defaultTimeToLive : timeToLive;
    }

    @Override
    public V put(Object key, Object value) {
//        clearExpired();
        Entry entry = new Entry(key, value, Clock.getTime());
        if (entries == null) {
            entries = new Entry[4];
            entries[0] = entry;
        } else {
            if(entries[entries.length - 1] != null){
                Entry<V,K>[] oldEntries = (Entry<V, K>[]) entries;//Arrays.copy
                entries = new Entry[entries.length * 2];
                for (int j = 0; j < entries.length; j++) {
                    if (oldEntries[j] != null){
                        entries[j] = (Entry<K, V>) oldEntries[j];
                    } else {
                        entries[j] = null;//continue
                    }
                }
            }
            for (int i = 0; i < entries.length; i++) {
                if(entries[i] == null){
                    entries[i] = entry;
                    return (i - 1 < 0) ? null : entries[i - 1].getValue();
                }
            }
        }
        return null;
    }

    @Override
    public void clearExpired() {
        if(entries == null){
            return;
        }
        for (int i = 0; i < entries.length; i++) {
            if(entries[i] == null){
                continue;
            }
            System.out.println("entries[i].getCurrentEntryDate():" + entries[i].getCurrentEntryDate());
            System.out.println("getTimeToLive():" + getTimeToLive());
            System.out.println("entries[i].getCurrentEntryDate() - currentTime > getTimeToLive():" + (entries[i].getCurrentEntryDate() < getTimeToLive()));
            if(entries[i].getCurrentEntryDate() < getTimeToLive()){
                entries[i] = null;
            }
        }
    }

    @Override
    public void clear() {
        for (int i = 0; i < entries.length; i++) {
            entries[i] = null;
        }
    }

    @Override
    public boolean containsKey(Object key) {
//        clearExpired();
        for (int i = 0; i < entries.length; i++) {
            if(entries[i].getKey().equals(key)){
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
//        clearExpired();
        for (int i = 0; i < entries.length; i++) {
            if(entries[i].getValue().equals(value)){
                return true;
            }
        }
        return false;
    }

    @Override
    public Object get(Object key) {
        //clearExpired();
        for (int i = 0; i < entries.length; i++) {
            if(entries[i] == null){
                return null;
            }
            if(entries[i].getKey().equals(key)){
                return entries[i].getValue();
            }
        }
        return null;
    }

    @Override
    public boolean isEmpty() {
//        clearExpired();
        for (int i = 0; i < entries.length; i++) {
            if(entries[i] != null){
                return false;
            }
        }
        return true;
    }

    @Override
    public Object remove(Object key) {
        for (int i = 0; i < entries.length; i++) {
            if(entries[i].getKey().equals(key)){
                entries[i] = null;
                return (i - 1 < 0) ? null : entries[i - 1].getValue();
            }
        }
        return null;
    }

    @Override
    public int size() {
        clearExpired();
        int size = 0;
        for (int i = 0; i < entries.length; i++) {
            if(entries[i] != null){
                size++;
            }
        }
        return size;
    }

    public static void main(String[] args) {
        CacheMapImpl cacheMap = new CacheMapImpl();
        cacheMap.put(1, "asd");
        System.out.println(cacheMap.size());
        System.out.println(cacheMap.get(1));
        cacheMap.setTimeToLive(1);
        System.out.println(cacheMap.size());
    }

}
