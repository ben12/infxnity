// Copyright (C) 2017 Benoît Moreau (ben.12)
// 
// This software may be modified and distributed under the terms
// of the MIT license.  See the LICENSE file for details.
package com.ben12.infxnity.binding;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import org.junit.Test;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;

/**
 * @author Benoît Moreau (ben.12)
 */
public class IFXContentBindingTest
{
    @Test
    public void observabelListInitializationTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        assertEquals(Arrays.asList("value1", "value2", "value3", "value4"), collection2);
    }

    @Test
    public void listInitializationTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final List<String> collection2 = new ArrayList<>();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        assertEquals(Arrays.asList("value1", "value2", "value3", "value4"), collection2);
    }

    @Test
    public void addElementsTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        final AtomicReference<ListChangeListener.Change<? extends String>> change = new AtomicReference<>(null);
        collection2.addListener((ListChangeListener<String>) c -> {
            change.set(c);
        });

        collection1.addAll(2, Arrays.asList(new Model("value2bis"), new Model("value2ter")));

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertFalse("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 2, change.get().getFrom());
        assertEquals("to", 4, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList("value2bis", "value2ter"), change.get().getAddedSubList());
        assertFalse("Has more change", change.get().next());

        assertEquals(Arrays.asList("value1", "value2", "value2bis", "value2ter", "value3", "value4"), collection2);
    }

    @Test
    public void removeElementsTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        final AtomicReference<ListChangeListener.Change<? extends String>> change = new AtomicReference<>(null);
        collection2.addListener((ListChangeListener<String>) c -> {
            change.set(c);
        });

        collection1.subList(1, 3).clear();

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertFalse("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 1, change.get().getFrom());
        assertEquals("to", 1, change.get().getTo());
        assertEquals("Removed", Arrays.asList("value2", "value3"), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals(Arrays.asList("value1", "value4"), collection2);
    }

    @Test
    public void replaceElementsTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        final AtomicReference<ListChangeListener.Change<? extends String>> change = new AtomicReference<>(null);
        collection2.addListener((ListChangeListener<String>) c -> {
            change.set(c);
        });

        collection1.set(1, new Model("value2bis"));

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 1, change.get().getFrom());
        assertEquals("to", 2, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList("value2bis"), change.get().getAddedSubList());
        assertEquals("Removed", Arrays.asList("value2"), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals(Arrays.asList("value1", "value2bis", "value3", "value4"), collection2);
    }

    @Test
    public void permuteElementsTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value2"),
                                                                                    new Model("value4"),
                                                                                    new Model("value1"),
                                                                                    new Model("value3"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        // Permutation can be generated only by calling sort, so IFXContentBinding generate a remove then an add

        final List<ListChangeListener.Change<? extends String>> change = new ArrayList<>(2);
        collection2.addListener((ListChangeListener<String>) c -> {
            if (change.isEmpty())
            {
                assertEquals(Collections.emptyList(), collection2);
            }
            change.add(c);
        });

        collection1.sort(Comparator.comparing(Model::getText));

        assertEquals("Change event", 2, change.size());

        assertTrue("Has change", change.get(0).next());
        assertFalse("Was added", change.get(0).wasAdded());
        assertTrue("Was removed", change.get(0).wasRemoved());
        assertFalse("Was update", change.get(0).wasUpdated());
        assertFalse("Was permuted", change.get(0).wasPermutated());
        assertEquals("from", 0, change.get(0).getFrom());
        assertEquals("to", 0, change.get(0).getTo());
        assertEquals("Removed", Arrays.asList("value2", "value4", "value1", "value3"), change.get(0).getRemoved());
        assertFalse("Has more change", change.get(0).next());

        assertTrue("Has change", change.get(1).next());
        assertTrue("Was added", change.get(1).wasAdded());
        assertFalse("Was removed", change.get(1).wasRemoved());
        assertFalse("Was update", change.get(1).wasUpdated());
        assertFalse("Was permuted", change.get(1).wasPermutated());
        assertEquals("from", 0, change.get(1).getFrom());
        assertEquals("to", 4, change.get(1).getTo());
        assertEquals("Added sub-list", Arrays.asList("value1", "value2", "value3", "value4"),
                     change.get(1).getAddedSubList());
        assertFalse("Has more change", change.get(1).next());

        assertEquals(Arrays.asList("value1", "value2", "value3", "value4"), collection2);
    }

    @Test
    public void updateElementsTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(m -> new ObservableValue[] {
                m.textProperty() });
        collection1.setAll(new Model("value1"), new Model("value2"), new Model("value3"), new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);

        final AtomicReference<ListChangeListener.Change<? extends String>> change = new AtomicReference<>(null);
        collection2.addListener((ListChangeListener<String>) c -> {
            change.set(c);
        });

        collection1.get(1).setText("value2bis");

        assertNotNull("Change event", change.get());
        assertTrue("Has change", change.get().next());
        assertTrue("Was added", change.get().wasAdded());
        assertTrue("Was removed", change.get().wasRemoved());
        assertFalse("Was update", change.get().wasUpdated());
        assertFalse("Was permuted", change.get().wasPermutated());
        assertEquals("from", 1, change.get().getFrom());
        assertEquals("to", 2, change.get().getTo());
        assertEquals("Added sub-list", Arrays.asList("value2bis"), change.get().getAddedSubList());
        assertEquals("Removed", Arrays.asList("value2"), change.get().getRemoved());
        assertFalse("Has more change", change.get().next());

        assertEquals(Arrays.asList("value1", "value2bis", "value3", "value4"), collection2);
    }

    @Test
    public void unbindTest()
    {
        final ObservableList<Model> collection1 = FXCollections.observableArrayList(new Model("value1"),
                                                                                    new Model("value2"),
                                                                                    new Model("value3"),
                                                                                    new Model("value4"));
        final ObservableList<String> collection2 = FXCollections.observableArrayList();

        IFXContentBinding.bind(collection2, collection1, Model::getText);
        IFXContentBinding.unbind(collection2, collection1);

        final AtomicReference<ListChangeListener.Change<? extends String>> change = new AtomicReference<>(null);
        collection2.addListener((ListChangeListener<String>) c -> {
            change.set(c);
        });

        collection1.addAll(2, Arrays.asList(new Model("value2bis"), new Model("value2ter")));

        assertNull("Change event", change.get());

        assertEquals(Arrays.asList("value1", "value2", "value3", "value4"), collection2);
    }

    private static final class Model
    {
        private final StringProperty text = new SimpleStringProperty();

        public Model(final String pText)
        {
            setText(pText);
        }

        public final StringProperty textProperty()
        {
            return text;
        }

        public final String getText()
        {
            return textProperty().get();
        }

        public final void setText(final String text)
        {
            textProperty().set(text);
        }

        @Override
        public String toString()
        {
            return "text:" + text.get();
        }
    }
}
