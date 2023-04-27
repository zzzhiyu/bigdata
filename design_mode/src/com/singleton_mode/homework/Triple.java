package com.singleton_mode.homework;

public class Triple {
    private static Triple[] triples;
    private final int id;

    private Triple(int id) {
        this.id = id;
    }

    public static Triple getInstance(int id) {
        // 不存在id直接返回null
        if (id < 0 || 3 <= id) {
            return null;
        }
        if (triples == null) {
            synchronized (Triple.class) {
                if (triples == null) {
                    triples = new Triple[3];
                }
            }
        }
        if (triples[id] == null) {
            synchronized (Triple.class) {
                if (triples[id] == null) {
                    triples[id] = new Triple(id);
                }
            }
        }
        return triples[id];
    }

    public int getId() {
        return id;
    }
}
