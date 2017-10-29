// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.collections;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;

/**
 * @author Benoît Moreau (ben.12)
 */
public class ObservableListAggregationTest
{
    @Test
    public void emptyInitializationTest()
    {
        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>();

        assertEquals("size", 0, listAggregation.size());
    }

    @Test
    public void arrayInitializationTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList();
        final ObservableList<Integer> list4 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3,
                list4);

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(5), listAggregation.get(5));
    }

    @Test
    public void listInitializationTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList();
        final ObservableList<Integer> list4 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(
                Arrays.asList(list1, list2, list3, list4));

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(5), listAggregation.get(5));
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getNegativeIndexTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(1);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(
                Arrays.asList(list1, list2));

        listAggregation.get(-1);
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void getTooBigIndexTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3, 4);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(
                Arrays.asList(list1, list2));

        listAggregation.get(5);
    }

    @Test
    public void listInsertionTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        listAggregation.getLists().add(1, FXCollections.observableArrayList(3));

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertFalse("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 3, change.get().getFrom());
        assertEquals("to", 4, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList(3), change.get().getAddedSubList());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(5), listAggregation.get(5));
    }

    @Test
    public void listRemoveTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        listAggregation.getLists().remove(1);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertFalse("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 3, change.get().getFrom());
        assertEquals("to", 3, change.get().getTo());
        assertEquals("Removed", Arrays.asList(3), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 5, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(4), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(5), listAggregation.get(4));
    }

    @Test
    public void listReplaceTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableList<Integer> list2bis = FXCollections.observableArrayList(6, 7);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        listAggregation.getLists().set(1, list2bis);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 3, change.get().getFrom());
        assertEquals("to", 5, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList(6, 7), change.get().getAddedSubList());
        assertEquals("Removed", Arrays.asList(3), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 7, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(6), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(7), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(4), listAggregation.get(5));
        assertEquals("get(6)", Integer.valueOf(5), listAggregation.get(6));
    }

    @Test
    public void addElementsTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        list2.addAll(6, 7);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertFalse("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 4, change.get().getFrom());
        assertEquals("to", 6, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList(6, 7), change.get().getAddedSubList());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 8, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(6), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(7), listAggregation.get(5));
        assertEquals("get(6)", Integer.valueOf(4), listAggregation.get(6));
        assertEquals("get(7)", Integer.valueOf(5), listAggregation.get(7));
    }

    @Test
    public void removeElementsTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        list3.remove(1);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertFalse("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 5, change.get().getFrom());
        assertEquals("to", 5, change.get().getTo());
        assertEquals("Removed", Arrays.asList(5), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 5, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
    }

    @Test
    public void replaceElementsTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(0, 1, 2);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        list1.setAll(6, 7, 8);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 0, change.get().getFrom());
        assertEquals("to", 3, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList(6, 7, 8), change.get().getAddedSubList());
        assertEquals("Removed", Arrays.asList(0, 1, 2), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(6), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(7), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(8), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(5), listAggregation.get(5));
    }

    @Test
    public void permuteElementsTest()
    {
        final ObservableList<Integer> list1 = FXCollections.observableArrayList(1, 2, 0);
        final ObservableList<Integer> list2 = FXCollections.observableArrayList(3);
        final ObservableList<Integer> list3 = FXCollections.observableArrayList(4, 5);

        final ObservableListAggregation<Integer> listAggregation = new ObservableListAggregation<>(list1, list2, list3);
        final AtomicReference<Change<? extends Integer>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<Integer>) c -> {
            change.set(c);
        });

        list1.sort(Integer::compareTo);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertFalse("Was added", change.get().wasAdded());
        assertFalse("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertTrue("Was permuted", change.get().wasPermutated());
        assertEquals("from", 0, change.get().getFrom());
        assertEquals("to", 3, change.get().getTo());
        assertEquals("permitation(0)", 1, change.get().getPermutation(0));
        assertEquals("permitation(1)", 2, change.get().getPermutation(1));
        assertEquals("permitation(2)", 0, change.get().getPermutation(2));
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", Integer.valueOf(0), listAggregation.get(0));
        assertEquals("get(1)", Integer.valueOf(1), listAggregation.get(1));
        assertEquals("get(2)", Integer.valueOf(2), listAggregation.get(2));
        assertEquals("get(3)", Integer.valueOf(3), listAggregation.get(3));
        assertEquals("get(4)", Integer.valueOf(4), listAggregation.get(4));
        assertEquals("get(5)", Integer.valueOf(5), listAggregation.get(5));
    }

    @Test
    public void updateElementsTest()
    {
        final IntegerProperty int0 = new SimpleIntegerProperty(0);
        final IntegerProperty int1 = new SimpleIntegerProperty(1);
        final IntegerProperty int2 = new SimpleIntegerProperty(2);
        final IntegerProperty int3 = new SimpleIntegerProperty(3);
        final IntegerProperty int4 = new SimpleIntegerProperty(4);
        final IntegerProperty int5 = new SimpleIntegerProperty(5);

        final ObservableList<IntegerProperty> list1 = FXCollections.observableList(Arrays.asList(int0, int1, int2),
                                                                                   e -> new Observable[] { e });
        final ObservableList<IntegerProperty> list2 = FXCollections.observableList(Arrays.asList(int3),
                                                                                   e -> new Observable[] { e });
        final ObservableList<IntegerProperty> list3 = FXCollections.observableList(Arrays.asList(int4, int5),
                                                                                   e -> new Observable[] { e });

        final ObservableListAggregation<IntegerProperty> listAggregation = new ObservableListAggregation<>(list1, list2,
                list3);
        final AtomicReference<Change<? extends IntegerProperty>> change = new AtomicReference<>(null);
        listAggregation.addListener((ListChangeListener<IntegerProperty>) c -> {
            change.set(c);
        });

        int4.set(6);

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertFalse("Was added", change.get().wasAdded());
        assertFalse("Was removed", change.get().wasRemoved());
        assertTrue("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 4, change.get().getFrom());
        assertEquals("to", 5, change.get().getTo());
        assertFalse("Has more change", change.get().next());

        assertEquals("size", 6, listAggregation.size());
        assertEquals("get(0)", 0, listAggregation.get(0).get());
        assertEquals("get(1)", 1, listAggregation.get(1).get());
        assertEquals("get(2)", 2, listAggregation.get(2).get());
        assertEquals("get(3)", 3, listAggregation.get(3).get());
        assertEquals("get(4)", 6, listAggregation.get(4).get());
        assertEquals("get(5)", 5, listAggregation.get(5).get());
    }
}
