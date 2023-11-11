package com.igrium.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.igrium.collections.PerceptibleList;
import com.igrium.collections.PerceptibleSet;

public class PerceptibleCollectionTest {
    
    @Test
    public void TestPerceptibleSet() {
        new PerceptableSetTest().runTest();
    }

    public static class PerceptableSetTest {
        private PerceptibleSet<String> set = new PerceptibleSet<>(new HashSet<>(), this::onAdded, this::onRemoved);

        String itemAdded = null;
        Object itemRemoved = null;

        void onAdded(String item) {
            itemAdded = item;
        }

        void onRemoved(Object item) {
            itemRemoved = item;
        }

        public void runTest() {
            set.add("testItem1");
            assertEquals("testItem1", itemAdded);

            set.add("testItem2");
            assertEquals("testItem2", itemAdded);

            set.remove("testItem1");
            assertEquals("testItem1", itemRemoved);
        }
    }

    public static class PerceptibleIteratorTest {
        private PerceptibleList<String> list = new PerceptibleList<>(new ArrayList<>(), this::onAdded, this::onRemoved);

        String itemAdded = null;
        List<Object> itemsRemoved = new LinkedList<>();

        void onAdded(String item) {
            itemAdded = item;
        }

        void onRemoved(Object item) {
            itemsRemoved.add(item);
        }

        @Test
        public void testPerceptibleIterator() {
            list.add("Hi i am a test");
            list.add("Hi i not a test");
            list.add("Hi i am also a test");

            Assertions.assertEquals("Hi i am also a test", itemAdded);
            
            list.removeIf(str -> str.contains("am"));
            Assertions.assertArrayEquals(new Object[] {"Hi i am a test", "Hi i am also a test"}, itemsRemoved.toArray());

            Assertions.assertArrayEquals(new Object[] {"Hi i not a test"}, list.toArray());
        }
    }
}
